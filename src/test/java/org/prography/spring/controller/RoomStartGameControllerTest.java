package org.prography.spring.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.prography.spring.common.BussinessException;
import org.prography.spring.domain.Room;
import org.prography.spring.domain.User;
import org.prography.spring.dto.request.StartGameRequest;
import org.prography.spring.fixture.dto.UserDtoFixture;
import org.prography.spring.fixture.setup.RoomSetup;
import org.prography.spring.fixture.setup.UserRoomSetUp;
import org.prography.spring.fixture.setup.UserSetup;
import org.prography.spring.service.RoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.prography.spring.common.ApiResponseCode.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureRestDocs
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class RoomStartGameControllerTest {

    private static final String BASE_URL = "/room";

    @Autowired
    private MockMvc mockMvc;

    @SpyBean
    private RoomService roomService;

    @Autowired
    private UserSetup userSetup;

    @Autowired
    private UserRoomSetUp userRoomSetUp;

    @Autowired
    private RoomSetup roomSetup;

    @Test
    @DisplayName("정상적으로 호스트가 방에서 게임을 시작하면, 성공 응답이 반환된다")
    void startGame_Success() throws Exception {
        //given
        Long fakerId = 1L;
        Long hostFakerId = 2L;
        User host = userSetup.setUpUser(hostFakerId);
        User guest = userSetup.setUpUser(fakerId);
        Room room = roomSetup.setUpRoom(host);

        userRoomSetUp.setUpUserRoom(host, room);
        userRoomSetUp.setUpUserRoom(guest, room);

        StartGameRequest startGameRequest = UserDtoFixture.startGameRequest(host.getId());

        doNothing()
                .when(roomService)
                .startGameById(room.getId(), startGameRequest);

        //when
        ResultActions resultActions = mockMvc.perform(
                RestDocumentationRequestBuilders.put(BASE_URL + "/start/{roomId}", room.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(startGameRequest))
        );

        //then
        resultActions
                .andExpect(jsonPath("$.code").value(SUCCESS.getCode()))
                .andExpect(jsonPath("$.message").value(SUCCESS.getMessage()))
                .andDo(print())
                .andDo(document("RoomControllerTest/startGame_Success",
                        pathParameters(
                                parameterWithName("roomId").description("방 ID")
                        ),
                        requestFields(
                                fieldWithPath("userId").description("유저 ID")
                        ),
                        responseFields(
                                fieldWithPath("code").description("응답 코드"),
                                fieldWithPath("message").description("응답 메시지")
                        )));
    }

    @Test
    @DisplayName("존재하지 않는 방에서 게임을 시작하려는 경우, 실패 응답이 반환된다")
    void startGame_Fail_RoomNotExist() throws Exception {
        //given
        Long fakerId = 1L;
        Long hostFakerId = 2L;
        Long notExistRoomId = 100L;
        User guest = userSetup.setUpUser(fakerId);
        User host = userSetup.setUpUser(hostFakerId);
        Room room = roomSetup.setUpRoom(host);

        userRoomSetUp.setUpUserRoom(guest, room);
        userRoomSetUp.setUpUserRoom(host, room);

        StartGameRequest startGameRequest = UserDtoFixture.startGameRequest(host.getId());

        doThrow(new BussinessException(BAD_REQUEST))
                .when(roomService)
                .startGameById(notExistRoomId, startGameRequest);

        //when
        ResultActions resultActions = mockMvc.perform(
                RestDocumentationRequestBuilders.put(BASE_URL + "/start/{roomId}", notExistRoomId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(startGameRequest)
                        )
        );

        //then
        resultActions
                .andExpect(jsonPath("$.code").value(BAD_REQUEST.getCode()))
                .andExpect(jsonPath("$.message").value(BAD_REQUEST.getMessage()))
                .andDo(print())
                .andDo(document("RoomControllerTest/startGame_Fail_RoomNotExist",
                        pathParameters(
                                parameterWithName("roomId").description("방 ID")
                        ),
                        requestFields(
                                fieldWithPath("userId").description("유저 ID")
                        ),
                        responseFields(
                                fieldWithPath("code").description("응답 코드"),
                                fieldWithPath("message").description("응답 메시지")
                        )));
    }

    @Test
    @DisplayName("방의 상태가 대기 상태가 아닌 경우, 게임 시작이 실패하고 실패 응답이 반환된다")
    void startGame_Fail_RoomStatusNotWait() throws Exception {
        //given
        Long fakerId = 1L;
        Long hostFakerId = 2L;
        User guest = userSetup.setUpUser(fakerId);
        User host = userSetup.setUpUser(hostFakerId);
        Room room = roomSetup.notWaitStatusRoom(host);

        userRoomSetUp.setUpUserRoom(guest, room);
        userRoomSetUp.setUpUserRoom(host, room);

        StartGameRequest startGameRequest = UserDtoFixture.startGameRequest(host.getId());

        doThrow(new BussinessException(BAD_REQUEST))
                .when(roomService)
                .startGameById(room.getId(), startGameRequest);

        //when
        ResultActions resultActions = mockMvc.perform(
                RestDocumentationRequestBuilders.put(BASE_URL + "/start/{roomId}", room.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(startGameRequest)
                        )
        );

        //then
        resultActions
                .andExpect(jsonPath("$.code").value(BAD_REQUEST.getCode()))
                .andExpect(jsonPath("$.message").value(BAD_REQUEST.getMessage()))
                .andDo(print())
                .andDo(document("RoomControllerTest/startGame_Fail_RoomStatusNotWait",
                        pathParameters(
                                parameterWithName("roomId").description("방 ID")
                        ),
                        requestFields(
                                fieldWithPath("userId").description("유저 ID")
                        ),
                        responseFields(
                                fieldWithPath("code").description("응답 코드"),
                                fieldWithPath("message").description("응답 메시지")
                        )));

    }

    @Test
    @DisplayName("게임 시작 요청을 한 유저가 방의 호스트가 아닌 경우, 게임 시작이 실패하고 실패 응답이 반환된다")
    void startGame_Fail_NotHost() throws Exception {
        //given
        Long fakerId = 1L;
        Long hostFakerId = 2L;
        User guest = userSetup.setUpUser(fakerId);
        User host = userSetup.setUpUser(hostFakerId);
        Room room = roomSetup.setUpRoom(host);
        userRoomSetUp.setUpUserRoom(guest, room);

        StartGameRequest startGameRequest = UserDtoFixture.startGameRequest(guest.getId());

        doThrow(new BussinessException(BAD_REQUEST))
                .when(roomService)
                .startGameById(room.getId(), startGameRequest);

        //when
        ResultActions resultActions = mockMvc.perform(
                RestDocumentationRequestBuilders.put(BASE_URL + "/start/{roomId}", room.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(startGameRequest)
                        )
        );

        //then
        resultActions
                .andExpect(jsonPath("$.code").value(BAD_REQUEST.getCode()))
                .andExpect(jsonPath("$.message").value(BAD_REQUEST.getMessage()))
                .andDo(print())
                .andDo(document("RoomControllerTest/startGame_Fail_NotHost",
                        pathParameters(
                                parameterWithName("roomId").description("방 ID")
                        ),
                        requestFields(
                                fieldWithPath("userId").description("유저 ID")
                        ),
                        responseFields(
                                fieldWithPath("code").description("응답 코드"),
                                fieldWithPath("message").description("응답 메시지")
                        )));
    }

    @Test
    @DisplayName("방의 정원이 방의 타입에 맞게 모두 찬 상태가 아닌 경우, 게임 시작이 실패하고 실패 응답이 반환된다")
    void startGame_Fail_RoomNotFull() throws Exception {
        //given
        Long hostFakerId = 1L;
        User host = userSetup.setUpUser(hostFakerId);
        Room room = roomSetup.setUpRoom(host);

        StartGameRequest startGameRequest = UserDtoFixture.startGameRequest(host.getId());

        doThrow(new BussinessException(BAD_REQUEST))
                .when(roomService)
                .startGameById(room.getId(), startGameRequest);

        //when
        ResultActions resultActions = mockMvc.perform(
                RestDocumentationRequestBuilders.put(BASE_URL + "/start/{roomId}", room.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(startGameRequest)
                        )
        );

        //then
        resultActions
                .andExpect(jsonPath("$.code").value(BAD_REQUEST.getCode()))
                .andExpect(jsonPath("$.message").value(BAD_REQUEST.getMessage()))
                .andDo(print())
                .andDo(document("RoomControllerTest/startGame_Fail_RoomNotFull",
                        pathParameters(
                                parameterWithName("roomId").description("방 ID")
                        ),
                        requestFields(
                                fieldWithPath("userId").description("유저 ID")
                        ),
                        responseFields(
                                fieldWithPath("code").description("응답 코드"),
                                fieldWithPath("message").description("응답 메시지")
                        )));
    }

    @Test
    @DisplayName("게임 시작 요청이 서버 오류로 실패하면, 서버 오류 응답이 반환된다")
    void startGame_Fail_ServerError() throws Exception {
        //given
        Long fakerId = 1L;
        Long hostFakerId = 2L;
        User guest = userSetup.setUpUser(fakerId);
        User host = userSetup.setUpUser(hostFakerId);
        Room room = roomSetup.setUpRoom(host);

        userRoomSetUp.setUpUserRoom(guest, room);
        userRoomSetUp.setUpUserRoom(host, room);

        StartGameRequest startGameRequest = UserDtoFixture.startGameRequest(host.getId());

        doThrow(new BussinessException(SEVER_ERROR))
                .when(roomService)
                .startGameById(any(), any());

        //when
        ResultActions resultActions = mockMvc.perform(
                RestDocumentationRequestBuilders.put(BASE_URL + "/start/{roomId}", room.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(startGameRequest)
                        )
        );

        //then
        resultActions
                .andExpect(jsonPath("$.code").value(SEVER_ERROR.getCode()))
                .andExpect(jsonPath("$.message").value(SEVER_ERROR.getMessage()))
                .andDo(print())
                .andDo(document("RoomControllerTest/startGame_Fail_ServerError",
                        pathParameters(
                                parameterWithName("roomId").description("방 ID")
                        ),
                        requestFields(
                                fieldWithPath("userId").description("유저 ID")
                        ),
                        responseFields(
                                fieldWithPath("code").description("응답 코드"),
                                fieldWithPath("message").description("응답 메시지")
                        )));
    }
}
