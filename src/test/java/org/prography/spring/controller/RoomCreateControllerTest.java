package org.prography.spring.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.prography.spring.common.BussinessException;
import org.prography.spring.domain.User;
import org.prography.spring.dto.request.CreateRoomRequest;
import org.prography.spring.fixture.dto.RoomDtoFixture;
import org.prography.spring.fixture.setup.RoomSetup;
import org.prography.spring.fixture.setup.UserSetup;
import org.prography.spring.service.RoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.prography.spring.common.ApiResponseCode.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureRestDocs
class RoomCreateControllerTest {

    private static final String BASE_URL = "/room";

    @Autowired
    private MockMvc mockMvc;

    @SpyBean
    private RoomService roomService;

    @Autowired
    private UserSetup userSetup;

    @Autowired
    private RoomSetup roomSetup;

    @Test
    @DisplayName("정상적으로 유저가 방을 생성하면, 성공 응답이 반환된다")
    void createRoom_Success() throws Exception {
        //given
        Long fakerId = 1L;
        userSetup.setUpUser(fakerId);

        CreateRoomRequest createRoomRequest = RoomDtoFixture.createRoomRequest();

        doNothing()
                .when(roomService)
                .createRoom(createRoomRequest);

        //when
        ResultActions resultActions = mockMvc.perform(post(BASE_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(createRoomRequest)));

        //then
        resultActions
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.code").value(SUCCESS.getCode()))
                .andExpect(jsonPath("$.message").value(SUCCESS.getMessage()))
                .andDo(document("RoomControllerTest/createRoom_Success",
                        requestFields(
                                fieldWithPath("userId").description("유저 ID"),
                                fieldWithPath("roomType").description("방 타입"),
                                fieldWithPath("title").description("방 제목")
                        ),
                        responseFields(
                                fieldWithPath("code").description("응답 코드"),
                                fieldWithPath("message").description("응답 메시지")
                        )
                ));
    }

    @Test
    @DisplayName("유저의 상태가 활성화 상태가 아닌 경우, 방 생성을 실패하면, 실패 응답이 반환된다")
    void createRoom_Fail_UserNotActivated() throws Exception {
        //given
        Long fakerId = 2L;
        userSetup.notActiveUser(fakerId);

        CreateRoomRequest createRoomRequest = RoomDtoFixture.createRoomRequest();

        doThrow(new BussinessException(BAD_REQUEST))
                .when(roomService)
                .createRoom(createRoomRequest);

        //when
        ResultActions resultActions = mockMvc.perform(post(BASE_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(createRoomRequest)));

        //then
        resultActions
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.code").value(BAD_REQUEST.getCode()))
                .andExpect(jsonPath("$.message").value(BAD_REQUEST.getMessage()))
                .andDo(document("createRoom_Fail_UserNotActivated",
                        requestFields(
                                fieldWithPath("userId").description("유저 ID"),
                                fieldWithPath("roomType").description("방 타입"),
                                fieldWithPath("title").description("방 제목")),
                        responseFields(
                                fieldWithPath("code").description("응답 코드"),
                                fieldWithPath("message").description("응답 메시지")
                        )
                ));
    }

    @Test
    @DisplayName("유저가 이미 다른 방에 참여하고 있는 경우, 방 생성을 실패하면, 실패 응답이 반환된다")
    void createRoom_Fail_UserAlreadyJoinedRoom() throws Exception {
        //given
        Long fakerId = 3L;
        User participateUser = userSetup.setUpUser(fakerId);
        roomSetup.setUpRoom(participateUser);

        CreateRoomRequest createRoomRequest = RoomDtoFixture.createRoomRequest();

        doThrow(new BussinessException(BAD_REQUEST))
                .when(roomService)
                .createRoom(createRoomRequest);

        //when
        ResultActions resultActions = mockMvc.perform(post(BASE_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(createRoomRequest)));

        //then
        resultActions
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.code").value(BAD_REQUEST.getCode()))
                .andExpect(jsonPath("$.message").value(BAD_REQUEST.getMessage()))
                .andDo(document("createRoom_Fail_UserAlreadyJoinedRoom",
                        requestFields(
                                fieldWithPath("userId").description("유저 ID"),
                                fieldWithPath("roomType").description("방 타입"),
                                fieldWithPath("title").description("방 제목")),
                        responseFields(
                                fieldWithPath("code").description("응답 코드"),
                                fieldWithPath("message").description("응답 메시지")
                        )
                ));
    }

    @Test
    @DisplayName("유저가 방을 생성할 때, 서버 오류가 발생하면 서버 오류 응답이 반환된다")
    void createRoom_Fail_ServerError() throws Exception {
        //given
        Long fakerId = 4L;
        userSetup.setUpUser(fakerId);

        CreateRoomRequest createRoomRequest = RoomDtoFixture.createRoomRequest();

        doThrow(new BussinessException(SEVER_ERROR))
                .when(roomService)
                .createRoom(any(CreateRoomRequest.class));

        //when
        ResultActions resultActions = mockMvc.perform(post(BASE_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(createRoomRequest)));

        //then
        resultActions
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.code").value(SEVER_ERROR.getCode()))
                .andExpect(jsonPath("$.message").value(SEVER_ERROR.getMessage()))
                .andDo(document("createRoom_Fail_ServerError",
                        requestFields(
                                fieldWithPath("userId").description("유저 ID"),
                                fieldWithPath("roomType").description("방 타입"),
                                fieldWithPath("title").description("방 제목")),
                        responseFields(
                                fieldWithPath("code").description("응답 코드"),
                                fieldWithPath("message").description("응답 메시지")
                        )
                ));
    }
}
