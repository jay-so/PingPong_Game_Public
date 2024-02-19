package org.prography.spring.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.prography.spring.common.BussinessException;
import org.prography.spring.domain.Room;
import org.prography.spring.dto.response.RoomListResponse;
import org.prography.spring.dto.response.RoomResponse;
import org.prography.spring.fixture.setup.RoomSetup;
import org.prography.spring.fixture.setup.UserSetup;
import org.prography.spring.service.RoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.List;

import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.prography.spring.common.ApiResponseCode.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureRestDocs
class RoomFindAllControllerTest {

    private static final String BASE_URL = "/room";

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RoomService roomService;

    @Autowired
    private UserSetup userSetup;

    @Autowired
    private RoomSetup roomSetup;

    @Test
    @DisplayName("정상적으로 유저가 방 전체 목록을 조회하면, 성공 응답이 반환된다")
    void findAllRoom_Success() throws Exception {
        //given
        long totalElements = 10;
        long pageSize = 10;

        List<Room> rooms = roomSetup.setUpRooms(userSetup.setUpUsers(10));
        List<RoomResponse> roomResponses = rooms.stream()
                .map(RoomResponse::from)
                .toList();

        RoomListResponse roomListResponse = RoomListResponse.of(totalElements, totalElements / pageSize + 1, roomResponses);

        when(roomService.findAllRooms(any())).thenReturn(roomListResponse);

        //when
        ResultActions resultActions = mockMvc.perform(get(BASE_URL)
                .param("page", "0")
                .param("size", "10")
                .contentType(MediaType.APPLICATION_JSON));

        //then
        resultActions
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.code").value(SUCCESS.getCode()))
                .andExpect(jsonPath("$.message").value(SUCCESS.getMessage()))
                .andExpect(jsonPath("$.result.totalElements").value(10))
                .andExpect(jsonPath("$.result.totalPages").value(2))
                .andExpect(jsonPath("$.result.roomList", hasSize(10)))
                .andExpect(jsonPath("$.result.roomList[0].id").exists())
                .andExpect(jsonPath("$.result.roomList[0].title").exists())
                .andExpect(jsonPath("$.result.roomList[0].hostId").exists())
                .andExpect(jsonPath("$.result.roomList[0].roomType").exists())
                .andExpect(jsonPath("$.result.roomList[0].status").exists())
                .andDo(print())
                .andDo(document("RoomControllerTest/findAllRoom_Success",
                        responseFields(
                                fieldWithPath("code").description("응답 코드"),
                                fieldWithPath("message").description("응답 메시지"),
                                fieldWithPath("result.totalElements").description("전체 항목 수"),
                                fieldWithPath("result.totalPages").description("전체 페이지 수"),
                                fieldWithPath("result.roomList[].id").description("방 ID"),
                                fieldWithPath("result.roomList[].title").description("방 제목"),
                                fieldWithPath("result.roomList[].hostId").description("호스트 ID"),
                                fieldWithPath("result.roomList[].roomType").description("방 타입"),
                                fieldWithPath("result.roomList[].status").description("방 상태")
                        )
                ));
    }

    @Test
    @DisplayName("유저가 방 전체 목록 조회를 잘못된 값으로 요청으로 조회 시, 실패 처리가 반환된다")
    void findAllRoom_Fail_BadRequest() throws Exception {
        //given
        roomSetup.setUpRooms(userSetup.setUpUsers(10));

        doThrow(new BussinessException(BAD_REQUEST))
                .when(roomService)
                .findAllRooms(any());

        //when
        ResultActions resultActions = mockMvc.perform(get(BASE_URL)
                .param("page", "-1")
                .param("size", "10")
                .contentType(MediaType.APPLICATION_JSON));

        //then
        resultActions
                .andExpect(jsonPath("$.code").value(BAD_REQUEST.getCode()))
                .andExpect(jsonPath("$.message").value(BAD_REQUEST.getMessage()))
                .andDo(print())
                .andDo(document("RoomControllerTest/findAllRoom_Fail_BadRequest",
                        responseFields(
                                fieldWithPath("code").description("응답 코드"),
                                fieldWithPath("message").description("응답 메시지")
                        )
                ));
    }

    @Test
    @DisplayName("유저가 방 전체 목록 조회가 서버 내부 오류로 실패하면, 서버 응답 에러가 반환된다")
    void findAllRoom_Fail_ServerError() throws Exception {
        //given
        roomSetup.setUpRooms(userSetup.setUpUsers(10));

        doThrow(new BussinessException(SEVER_ERROR))
                .when(roomService)
                .findAllRooms(any());

        //when
        ResultActions resultActions = mockMvc.perform(get(BASE_URL)
                .param("page", "0")
                .param("size", "10")
                .contentType(MediaType.APPLICATION_JSON));

        //then
        resultActions
                .andExpect(jsonPath("$.code").value(SEVER_ERROR.getCode()))
                .andExpect(jsonPath("$.message").value(SEVER_ERROR.getMessage()))
                .andDo(print())
                .andDo(document("RoomControllerTest/findAllRoom_Fail_ServerError",
                        responseFields(
                                fieldWithPath("code").description("응답 코드"),
                                fieldWithPath("message").description("응답 메시지")
                        )
                ));
    }
}
