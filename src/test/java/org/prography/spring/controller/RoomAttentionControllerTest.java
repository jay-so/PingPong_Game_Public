package org.prography.spring.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.prography.spring.common.BussinessException;
import org.prography.spring.domain.Room;
import org.prography.spring.domain.User;
import org.prography.spring.dto.request.AttentionUserRequest;
import org.prography.spring.fixture.dto.UserDtoFixture;
import org.prography.spring.fixture.setup.RoomSetup;
import org.prography.spring.fixture.setup.UserRoomSetUp;
import org.prography.spring.fixture.setup.UserSetup;
import org.prography.spring.repository.RoomRepository;
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
import static org.mockito.Mockito.doThrow;
import static org.prography.spring.common.ApiResponseCode.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureRestDocs
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class RoomAttentionControllerTest {

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

    @Autowired
    private RoomRepository roomRepository;

    @Test
    @DisplayName("정상적으로 유저가 생성된 방에 참여하면, 성공 응답이 반환된다")
    void attentionUser_Success() throws Exception {
        //given
        Long fakerId = 1L;
        roomSetup.setUpRooms(userSetup.setUpUsers(11));
        User user = userSetup.setUpUser(fakerId);
        Room room = roomRepository.findAll().get(2);

        AttentionUserRequest attentionUserRequest = UserDtoFixture.attentionUserRequest(user.getFakerId());

        //when
        ResultActions resultActions = mockMvc.perform(
                post(BASE_URL + "/attention/{roomId}", room.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(attentionUserRequest))
        );

        //then
        resultActions
                .andExpect(jsonPath("$.code").value(SUCCESS.getCode()))
                .andExpect(jsonPath("$.message").value(SUCCESS.getMessage()))
                .andDo(print())
                .andDo(document("RoomControllerTest/attentionUser_Success",
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
    @DisplayName("존재하지 않는 방에 유저가 참여하려고 하면, 실패 응답이 반환된다")
    void attentionUser_Fail_RoomNotExist() throws Exception {
        //given
        Long fakerId = 1L;
        roomSetup.setUpRooms(userSetup.setUpUsers(11));
        User user = userSetup.setUpUser(fakerId);
        Long notExistRoomId = 100L;

        AttentionUserRequest attentionUserRequest = UserDtoFixture.attentionUserRequest(user.getFakerId());

        doThrow(new BussinessException(BAD_REQUEST))
                .when(roomService)
                .attentionRoomById(notExistRoomId, attentionUserRequest);

        //when
        ResultActions resultActions = mockMvc.perform(
                post(BASE_URL + "/attention/{roomId}", notExistRoomId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(attentionUserRequest))
        );

        //then
        resultActions
                .andExpect(jsonPath("$.code").value(BAD_REQUEST.getCode()))
                .andExpect(jsonPath("$.message").value(BAD_REQUEST.getMessage()))
                .andDo(print())
                .andDo(document("RoomControllerTest/attentionUser_Fail_RoomNotExist",
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
    @DisplayName("대기 상태가 아닌 방에 유저가 참여하려고 하면, 실패 응답이 반환된다")
    void attentionUser_Fail_RoomStatusIsNotWait() throws Exception {
        //given
        Long fakerId = 1L;
        Long hostFakerId = 2L;
        User roomHost = userSetup.setUpUser(hostFakerId);
        Room room = roomSetup.notWaitStatusRoom(roomHost);
        User user = userSetup.setUpUser(fakerId);

        AttentionUserRequest attentionUserRequest = UserDtoFixture.attentionUserRequest(user.getFakerId());

        doThrow(new BussinessException(BAD_REQUEST))
                .when(roomService)
                .attentionRoomById(room.getId(), attentionUserRequest);

        //when
        ResultActions resultActions = mockMvc.perform(
                post(BASE_URL + "/attention/{roomId}", room.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(attentionUserRequest))
        );

        //then
        resultActions
                .andExpect(jsonPath("$.code").value(BAD_REQUEST.getCode()))
                .andExpect(jsonPath("$.message").value(BAD_REQUEST.getMessage()))
                .andDo(print())
                .andDo(document("RoomControllerTest/attentionUser_Fail_RoomStatusIsNotWait",
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
    @DisplayName("방에 참여하려는 유저 수가 방의 최대 인원 수를 초과하면, 실패 응답이 반환된다")
    void attentionUser_Fail_RoomOverCapacity() throws Exception {
        //given
        Long hostfakerId = 1L;
        Long guestFakerId = 2L;
        Long overCapacityFakerId = 3L;

        User host = userSetup.setUpUser(hostfakerId);
        User guest = userSetup.setUpUser(guestFakerId);
        User overCapacityUser = userSetup.setUpUser(overCapacityFakerId);

        Room room = roomSetup.setUpRoom(host);
        userRoomSetUp.setUpUserRoom(host, room);
        userRoomSetUp.setUpUserRoom(guest, room);

        AttentionUserRequest overCapacityAttentionUserRequest = UserDtoFixture.attentionUserRequest(overCapacityUser.getFakerId());

        doThrow(new BussinessException(BAD_REQUEST))
                .when(roomService)
                .attentionRoomById(room.getId(), overCapacityAttentionUserRequest);

        //when
        ResultActions resultActions = mockMvc.perform(
                post(BASE_URL + "/attention/{roomId}", room.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(overCapacityAttentionUserRequest))
        );

        //then
        resultActions
                .andExpect(jsonPath("$.code").value(BAD_REQUEST.getCode()))
                .andExpect(jsonPath("$.message").value(BAD_REQUEST.getMessage()))
                .andDo(print())
                .andDo(document("RoomControllerTest/attentionUser_Fail_RoomOverCapacity",
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
    @Transactional
    @DisplayName("활성 상태가 아닌 유저가 방에 참여하려고 하면, 실패 응답이 반환된다.")
    void attentionUser_Fail_UserNotActive() throws Exception {
        //given
        Long fakerId = 1L;
        Long hostFakerId = 2L;
        User user = userSetup.notActiveUser(fakerId);
        User host = userSetup.setUpUser(hostFakerId);
        Room room = roomSetup.setUpRoom(host);

        AttentionUserRequest attentionUserRequest = UserDtoFixture.attentionUserRequest(user.getId());

        doThrow(new BussinessException(BAD_REQUEST))
                .when(roomService)
                .attentionRoomById(room.getId(), attentionUserRequest);

        //when
        ResultActions resultActions = mockMvc.perform(
                post(BASE_URL + "/attention/{roomId}", room.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(attentionUserRequest))
        );

        //then
        resultActions
                .andExpect(jsonPath("$.code").value(BAD_REQUEST.getCode()))
                .andExpect(jsonPath("$.message").value(BAD_REQUEST.getMessage()))
                .andDo(print())
                .andDo(document("RoomControllerTest/attentionUser_Fail_UserNotActive",
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
    @DisplayName("이미 방에 참여하고 있는 유저가 방에 참여하려고 하면, 실패 응답이 반환된다")
    void attentionUser_Fail_UserAlreadyJoinedRoom() throws Exception {
        //given
        Long fakerId = 1L;
        Long hostFakerId = 2L;
        User guest = userSetup.setUpUser(fakerId);
        User host = userSetup.setUpUser(hostFakerId);
        Room room = roomSetup.setUpRoom(host);
        Room joinedRoom = roomSetup.setUpRoom(guest);
        userRoomSetUp.setUpUserRoom(guest, joinedRoom);

        AttentionUserRequest attentionUserRequest = UserDtoFixture.attentionUserRequest(guest.getId());

        doThrow(new BussinessException(BAD_REQUEST))
                .when(roomService)
                .attentionRoomById(room.getId(), attentionUserRequest);

        //when
        ResultActions resultActions = mockMvc.perform(
                post(BASE_URL + "/attention/{roomId}", room.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(attentionUserRequest))
        );

        //then
        resultActions
                .andExpect(jsonPath("$.code").value(BAD_REQUEST.getCode()))
                .andExpect(jsonPath("$.message").value(BAD_REQUEST.getMessage()))
                .andDo(print())
                .andDo(document("RoomControllerTest/attentionUser_Fail_UserAlreadyJoinedRoom",
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
    @DisplayName("유저가 방에 참가하려고 할때, 서버 에러가 발생하면 서버 에러 응답이 반환된다")
    void attentionUser_Fail_ServerError() throws Exception {
        //given
        Long fakerId = 1L;
        Long hostFakerId = 2L;
        User user = userSetup.setUpUser(fakerId);
        User host = userSetup.setUpUser(hostFakerId);
        Room room = roomSetup.setUpRoom(host);

        AttentionUserRequest attentionUserRequest = UserDtoFixture.attentionUserRequest(user.getId());

        doThrow(new BussinessException(SEVER_ERROR))
                .when(roomService)
                .attentionRoomById(any(), any());

        //when
        ResultActions resultActions = mockMvc.perform(
                post(BASE_URL + "/attention/{roomId}", room.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(attentionUserRequest))
        );

        //then
        resultActions
                .andExpect(jsonPath("$.code").value(SEVER_ERROR.getCode()))
                .andExpect(jsonPath("$.message").value(SEVER_ERROR.getMessage()))
                .andDo(print())
                .andDo(document("RoomControllerTest/attentionUser_Fail_ServerError",
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
