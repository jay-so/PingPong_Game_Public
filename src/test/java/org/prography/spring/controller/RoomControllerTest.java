package org.prography.spring.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.prography.spring.common.ApiResponse;
import org.prography.spring.common.BussinessException;
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

import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.prography.spring.common.ApiResponseCode.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureRestDocs
public class RoomControllerTest {

    private static final String BASE_URL = "/room";

    @Autowired
    private MockMvc mockMvc;

    @SpyBean
    private RoomService roomService;

    @Autowired
    private UserSetup userSetup;

    @Autowired
    private RoomSetup roomSetup;

    private ApiResponse<Object> successResponse;

    private ApiResponse<Object> errorResponse;

    private ApiResponse<Object> ServerErrorResponse;

    @BeforeEach
    void setUp() {
        successResponse = new ApiResponse<>(
                SUCCESS.getCode(),
                SUCCESS.getMessage(),
                null
        );

        errorResponse = new ApiResponse<>(
                BAD_REQUEST.getCode(),
                BAD_REQUEST.getMessage(),
                null
        );

        ServerErrorResponse = new ApiResponse<>(
                SEVER_ERROR.getCode(),
                SEVER_ERROR.getMessage(),
                null
        );
    }

    @Test
    @DisplayName("정상적으로 유저가 방을 생성하면, 성공 응답이 반환된다")
    void createRoom_Success() throws Exception {
        //given
        Long fakerId = 1L;
        userSetup.setUpUser(fakerId);
        CreateRoomRequest createRoomRequest = RoomDtoFixture.createRoomRequest();

        doNothing()
                .when(roomService)
                .createRoom(any(CreateRoomRequest.class));


        //when
        ResultActions resultActions = mockMvc.perform(post(BASE_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(createRoomRequest)));

        //then
        resultActions.andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.code").value(SUCCESS.getCode()))
                .andExpect(jsonPath("$.message").value(SUCCESS.getMessage()))
                .andDo(document("RoomControllerTest/createRoom_Success",
                        responseFields(
                                fieldWithPath("code").description("응답 코드"),
                                fieldWithPath("message").description("응답 메시지")
                        )
                ));
    }

    @Test
    @DisplayName("유저가 방 생성을 실패하면, 실패 응답이 반환된다")
    void createRoom_Fail() throws Exception {
        //given
        Long fakerId = 1L;
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
        resultActions.andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.code").value(SEVER_ERROR.getCode()))
                .andExpect(jsonPath("$.message").value(SEVER_ERROR.getMessage()))
                .andDo(document("RoomControllerTest/createRoom_Fail",
                        responseFields(
                                fieldWithPath("code").description("응답 코드"),
                                fieldWithPath("message").description("응답 메시지")
                        )
                ));
    }

    @Test
    @DisplayName("정상적으로 유저가 방 전체 목록을 조회하면, 성공 응답이 반환된다")
    void findAllRoom_Success() throws Exception {
        //given
        roomSetup.setUpRooms(userSetup.setUpUsers(11));

        //when
        ResultActions resultActions = mockMvc.perform(get(BASE_URL)
                .param("page", "0")
                .param("size", "10")
                .contentType(MediaType.APPLICATION_JSON));

        //then
        resultActions.andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.code").value(SUCCESS.getCode()))
                .andExpect(jsonPath("$.message").value(SUCCESS.getMessage()))
                .andExpect(jsonPath("$.result.totalElements").value(10))
                .andExpect(jsonPath("$.result.totalPages").value(1))
                .andExpect(jsonPath("$.result.roomList", hasSize(10)))
                .andExpect(jsonPath("$.result.roomList[0].id").exists())
                .andExpect(jsonPath("$.result.roomList[0].title").exists())
                .andExpect(jsonPath("$.result.roomList[0].hostId").exists())
                .andExpect(jsonPath("$.result.roomList[0].roomType").exists())
                .andExpect(jsonPath("$.result.roomList[0].status").exists())
                .andDo(document("RoomControllerTest/findAllRoom_Success",
                        responseFields(
                                fieldWithPath("code").description("응답 코드"),
                                fieldWithPath("message").description("응답 메시지"),
                                fieldWithPath("result.totalElements").description("전체 항목 수"),
                                fieldWithPath("result.totalPages").description("전체 페이지 수"),
                                fieldWithPath("result.roomList[].id").description("방 아이디"),
                                fieldWithPath("result.roomList[].title").description("방 제목"),
                                fieldWithPath("result.roomList[].hostId").description("호스트 아이디"),
                                fieldWithPath("result.roomList[].roomType").description("방 타입"),
                                fieldWithPath("result.roomList[].status").description("방 상태")
                        )
                ));
    }

    @Test
    @DisplayName("유저가 방 전체 목록 조회를 잘못된 요청으로 조회 시, 잘못된 요청 응답이 반환된다")
    void findAllRoom_BadRequest() throws Exception {
        //given
        roomSetup.setUpRooms(userSetup.setUpUsers(11));

        doThrow(new BussinessException(BAD_REQUEST))
                .when(roomService)
                .findAllRooms(any());

        //when
        ResultActions resultActions = mockMvc.perform(get(BASE_URL)
                .param("page", "-1")
                .param("size", "10")
                .contentType(MediaType.APPLICATION_JSON));

        //then
        resultActions.andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(BAD_REQUEST.getCode()))
                .andDo(print())
                .andDo(document("RoomControllerTest/findAllRoom_BadRequest",
                        responseFields(
                                fieldWithPath("code").description("응답 코드"),
                                fieldWithPath("message").description("응답 메시지")
                        )
                ));
    }

    @Test
    @DisplayName("유저가 방 전체 목록 조회가 서버 내부 오류로 실패하면, 서버 응답 에러가 반환된다")
    void findAllRoom_ServerError() throws Exception {
        //given
        doThrow(new BussinessException(SEVER_ERROR))
                .when(roomService)
                .findAllRooms(any());

        //when
        ResultActions resultActions = mockMvc.perform(get(BASE_URL)
                .param("page", "0")
                .param("size", "10")
                .contentType(MediaType.APPLICATION_JSON));

        //then
        resultActions.andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(SEVER_ERROR.getCode()))
                .andDo(print())
                .andDo(document("RoomControllerTest/findAllRoom_ServerError",
                        responseFields(
                                fieldWithPath("code").description("응답 코드"),
                                fieldWithPath("message").description("응답 메시지")
                        )
                ));
    }

    @Test
    @DisplayName("정상적으로 유저가 방 상세 정보를 조회하면, 성공 응답이 반환된다")
    void findRoomDetail_Success() throws Exception {
        //given

        //when

        //then
    }

    @Test
    @DisplayName("유저가 방 상세 정보 조회를 실패하면, 실패 응답이 반환된다")
    void findRoomDetail_Fail() throws Exception {
        //given

        //when

        //then
    }

    @Test
    @DisplayName("정상적으로 유저가 생성된 방에 참여하면, 성공 응답이 반환된다")
    void attentionUser_Success() throws Exception {
        //given

        //when

        //then
    }

    @Test
    @DisplayName("유저가 생성된 방에 참여를 실패하면, 실패 응답이 반환된다")
    void attentionUser_Fail() throws Exception {
        //given

        //when

        //then
    }

    @Test
    @DisplayName("정상적으로 유저가 생성된 방에서 나가면, 성공 응답이 반환된다")
    void exitRoom_Success() throws Exception {
        //given

        //when

        //then
    }

    @Test
    @DisplayName("유저가 생성된 방에서 나가기를 실패하면, 실패 응답이 반환된다")
    void exitRoom_Fail() throws Exception {
        //given

        //when

        //then
    }

    @Test
    @DisplayName("정상적으로 호스트가 방에서 게임을 시작하면, 성공 응답이 반환된다")
    void startGame_Success() throws Exception {
        //given

        //when

        //then
    }

    @Test
    @DisplayName("호스트가 아닌 유저가 방에서 게임을 시작하는 것을 실패하면, 실패 응답이 반환된다")
    void startGame_Fail() throws Exception {
        //given

        //when

        //then
    }


}
