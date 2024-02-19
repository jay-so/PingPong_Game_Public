package org.prography.spring.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.prography.spring.common.BussinessException;
import org.prography.spring.domain.Room;
import org.prography.spring.domain.User;
import org.prography.spring.dto.request.ExitRoomRequest;
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
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.prography.spring.common.ApiResponseCode.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureRestDocs
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class RoomExitControllerTest {

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
    @DisplayName("정상적으로 일반 유저가 생성된 방에서 나가면, 성공 응답이 반환된다")
    void exitRoom_Success() throws Exception {
        //given
        Long fakerId = 1L;
        Long hostFakerId = 2L;
        User guest = userSetup.setUpUser(fakerId);
        User host = userSetup.setUpUser(hostFakerId);
        Room room = roomSetup.setUpRoom(host);
        userRoomSetUp.setUpUserRoom(guest, room);
        userRoomSetUp.setUpUserRoom(host, room);

        ExitRoomRequest exitRoomRequest = UserDtoFixture.exitRoomRequest(guest.getId());

        doNothing()
                .when(roomService)
                .exitRoomById(room.getId(), exitRoomRequest);

        //when
        ResultActions resultActions = mockMvc.perform(
                post(BASE_URL + "/out/{roomId}", room.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(exitRoomRequest))
        );

        //then
        resultActions
                .andExpect(jsonPath("$.code").value(SUCCESS.getCode()))
                .andExpect(jsonPath("$.message").value(SUCCESS.getMessage()))
                .andDo(print())
                .andDo(document("RoomControllerTest/exitRoom_Success",
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
    @DisplayName("정상적으로 호스트가 방에서 나가면, 성공 응답을 반환한다")
    void exitRoom_Success_HostExit() throws Exception {
        //given
        Long fakerId = 1L;
        Long hostFakerId = 2L;
        User guest = userSetup.setUpUser(fakerId);
        User host = userSetup.setUpUser(hostFakerId);
        Room room = roomSetup.setUpRoom(host);
        userRoomSetUp.setUpUserRoom(guest, room);
        userRoomSetUp.setUpUserRoom(host, room);

        ExitRoomRequest exitRoomRequest = UserDtoFixture.exitRoomRequest(host.getId());

        doNothing()
                .when(roomService)
                .exitRoomById(room.getId(), exitRoomRequest);

        //when
        ResultActions resultActions = mockMvc.perform(
                post(BASE_URL + "/out/{roomId}", room.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(exitRoomRequest))
        );

        //then
        resultActions
                .andExpect(jsonPath("$.code").value(SUCCESS.getCode()))
                .andExpect(jsonPath("$.message").value(SUCCESS.getMessage()))
                .andDo(print())
                .andDo(document("RoomControllerTest/exitRoom_Success_HostExit",
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
    @DisplayName("방 나기기 요청에 잘못된 값이 들어오면, 실패 응답이 반환된다")
    void exitRoom_Fail_BadRequest() throws Exception {
        //givn
        Long fakerId = 1L;
        Long hostFakerId = 2L;
        User guest = userSetup.setUpUser(fakerId);
        User host = userSetup.setUpUser(hostFakerId);
        Room room = roomSetup.setUpRoom(host);
        userRoomSetUp.setUpUserRoom(guest, room);

        ExitRoomRequest exitRoomRequest = UserDtoFixture.exitRoomRequest(-1L);

        doThrow(new BussinessException(BAD_REQUEST))
                .when(roomService)
                .exitRoomById(room.getId(), exitRoomRequest);

        //when
        ResultActions resultActions = mockMvc.perform(
                post(BASE_URL + "/out/{roomId}", room.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(exitRoomRequest))
        );

        //then
        resultActions
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.code").value(BAD_REQUEST.getCode()))
                .andExpect(jsonPath("$.message").value(BAD_REQUEST.getMessage()))
                .andDo(print())
                .andDo(document("RoomControllerTest/exitRoom_Fail_BadRequest",
                        pathParameters(
                                parameterWithName("roomId").description("방 ID")
                        ),
                        requestFields(
                                fieldWithPath("userId").description("유저 ID")
                        ),
                        responseFields(
                                fieldWithPath("code").description("응답 코드"),
                                fieldWithPath("message").description("응답 메시지")
                        )
                ));
    }

    @Test
    @DisplayName("유저가 방에 참가하지 않은 상태에서 나가려고 하면, 실패 응답이 반환된다")
    void exitRoom_Fail_UserNotJoinedRoom() throws Exception {
        //given
        Long fakerId = 1L;
        Long hostFakerId = 2L;
        User guest = userSetup.setUpUser(fakerId);
        User host = userSetup.setUpUser(hostFakerId);
        Room room = roomSetup.setUpRoom(host);

        ExitRoomRequest exitRoomRequest = UserDtoFixture.exitRoomRequest(guest.getId());

        doThrow(new BussinessException(BAD_REQUEST))
                .when(roomService)
                .exitRoomById(room.getId(), exitRoomRequest);

        //when
        ResultActions resultActions = mockMvc.perform(
                post(BASE_URL + "/out/{roomId}", room.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(exitRoomRequest))
        );

        //then
        resultActions
                .andExpect(jsonPath("$.code").value(BAD_REQUEST.getCode()))
                .andExpect(jsonPath("$.message").value(BAD_REQUEST.getMessage()))
                .andDo(print())
                .andDo(document("RoomControllerTest/exitRoom_Fail_UserNotJoinedRoom",
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
    @DisplayName("방 상태가 대기 상태가 아닌 경우, 유저가 방에서 나가려고 하면, 실패 응답이 반환된다")
    void exitRoom_Fail_RoomStatusIsNotWait() throws Exception {
        //given
        Long fakerId = 1L;
        Long hostFakerId = 2L;
        User guest = userSetup.setUpUser(fakerId);
        User host = userSetup.setUpUser(hostFakerId);
        Room room = roomSetup.notWaitStatusRoom(host);
        userRoomSetUp.setUpUserRoom(guest, room);
        userRoomSetUp.setUpUserRoom(host, room);

        ExitRoomRequest exitRoomRequest = UserDtoFixture.exitRoomRequest(guest.getId());

        doThrow(new BussinessException(BAD_REQUEST))
                .when(roomService)
                .exitRoomById(room.getId(), exitRoomRequest);

        //when
        ResultActions resultActions = mockMvc.perform(
                post(BASE_URL + "/out/{roomId}", room.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(exitRoomRequest))
        );

        //then
        resultActions
                .andExpect(jsonPath("$.code").value(BAD_REQUEST.getCode()))
                .andExpect(jsonPath("$.message").value(BAD_REQUEST.getMessage()))
                .andDo(print())
                .andDo(document("RoomControllerTest/exitRoom_Fail_RoomStatusIsNotWait",
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
    @DisplayName("방이 존재하지 않는 경우, 유저가 방에서 나가려고 하면, 실패 응답이 반환된다")
    void exitRoom_Fail_RoomNotExist() throws Exception {
        //given
        Long fakerId = 1L;
        Long hostFakerId = 2L;
        Long notExistRoomId = 100L;

        User host = userSetup.setUpUser(hostFakerId);
        User guest = userSetup.setUpUser(fakerId);
        Room room = roomSetup.setUpRoom(host);
        userRoomSetUp.setUpUserRoom(host, room);

        ExitRoomRequest exitRoomRequest = UserDtoFixture.exitRoomRequest(guest.getId());

        doThrow(new BussinessException(BAD_REQUEST))
                .when(roomService)
                .exitRoomById(notExistRoomId, exitRoomRequest);

        //when
        ResultActions resultActions = mockMvc.perform(
                post(BASE_URL + "/out/{roomId}", room.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(exitRoomRequest))
        );

        //then
        resultActions
                .andExpect(jsonPath("$.code").value(BAD_REQUEST.getCode()))
                .andExpect(jsonPath("$.message").value(BAD_REQUEST.getMessage()))
                .andDo(print())
                .andDo(document("RoomControllerTest/exitRoom_Fail_RoomNotExist",
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
    @DisplayName("유저가 생성된 방에서 나갈 때, 서버 오류가 발생하면, 서버 오류 응답이 반환된다")
    void exitRoom_Fail_ServerError() throws Exception {
        //given
        Long fakerId = 1L;
        Long hostFakerId = 2L;
        User guest = userSetup.setUpUser(fakerId);
        User host = userSetup.setUpUser(hostFakerId);
        Room room = roomSetup.setUpRoom(host);
        userRoomSetUp.setUpUserRoom(guest, room);
        userRoomSetUp.setUpUserRoom(host, room);

        ExitRoomRequest exitRoomRequest = UserDtoFixture.exitRoomRequest(guest.getId());

        doThrow(new BussinessException(SEVER_ERROR))
                .when(roomService)
                .exitRoomById(any(), any());

        //when
        ResultActions resultActions = mockMvc.perform(
                post(BASE_URL + "/out/{roomId}", room.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(exitRoomRequest))
        );

        //then
        resultActions
                .andExpect(jsonPath("$.code").value(SEVER_ERROR.getCode()))
                .andExpect(jsonPath("$.message").value(SEVER_ERROR.getMessage()))
                .andDo(print())
                .andDo(document("RoomControllerTest/exitRoom_Fail_ServerError",
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
