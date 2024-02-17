package org.prography.spring.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.prography.spring.common.BussinessException;
import org.prography.spring.domain.Room;
import org.prography.spring.fixture.setup.RoomSetup;
import org.prography.spring.fixture.setup.UserSetup;
import org.prography.spring.repository.RoomRepository;
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
import static org.mockito.Mockito.doThrow;
import static org.prography.spring.common.ApiResponseCode.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureRestDocs
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class RoomFindDetailControllerTest {

    private static final String BASE_URL = "/room";

    @Autowired
    private MockMvc mockMvc;

    @SpyBean
    private RoomService roomService;

    @Autowired
    private UserSetup userSetup;

    @Autowired
    private RoomSetup roomSetup;

    @Autowired
    private RoomRepository roomRepository;

    @Test
    @DisplayName("정상적으로 유저가 방 상세 정보를 조회하면, 성공 응답이 반환된다")
    void findRoomDetail_Success() throws Exception {
        //given
        roomSetup.setUpRooms(userSetup.setUpUsers(10));
        Long roomId = 1L;
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new BussinessException(BAD_REQUEST));

        //when
        ResultActions resultActions = mockMvc.perform(RestDocumentationRequestBuilders.get(BASE_URL + "/{roomId}", roomId)
                .contentType(MediaType.APPLICATION_JSON));

        //then
        resultActions
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.code").value(SUCCESS.getCode()))
                .andExpect(jsonPath("$.message").value(SUCCESS.getMessage()))
                .andExpect(jsonPath("$.result.id").value(roomId))
                .andExpect(jsonPath("$.result.title").value(room.getTitle()))
                .andExpect(jsonPath("$.result.hostId").value(room.getHost().getId()))
                .andExpect(jsonPath("$.result.roomType").value(room.getRoomType().toString()))
                .andExpect(jsonPath("$.result.roomStatus").value(room.getStatus().toString()))
                .andExpect(jsonPath("$.result.createdAt").exists())
                .andExpect(jsonPath("$.result.updatedAt").exists())
                .andDo(print())
                .andDo(document("RoomControllerTest/findRoomDetail_Success",
                        pathParameters(
                                parameterWithName("roomId").description("방 ID")
                        ),
                        responseFields(
                                fieldWithPath("code").description("응답 코드"),
                                fieldWithPath("message").description("응답 메시지"),
                                fieldWithPath("result.id").description("방 ID"),
                                fieldWithPath("result.title").description("방 제목"),
                                fieldWithPath("result.hostId").description("호스트 ID"),
                                fieldWithPath("result.roomType").description("방 유형"),
                                fieldWithPath("result.roomStatus").description("방 상태"),
                                fieldWithPath("result.createdAt").description("방 생성일자"),
                                fieldWithPath("result.updatedAt").description("방 수정일자")
                        )
                ));
    }

    @Test
    @DisplayName("유저가 방 상세 정보 조회 시 존재하지 않은 방 id로 요청으로 실패하면, 잘못된 요청 응답이 반환된다")
    void findRoomDetail_Fail_BadRequest() throws Exception {
        //given
        roomSetup.setUpRooms(userSetup.setUpUsers(10));
        Long notExistRoomId = 100L;

        doThrow(new BussinessException(BAD_REQUEST))
                .when(roomService)
                .findRoomById(any());

        //when
        ResultActions resultActions = mockMvc.perform(RestDocumentationRequestBuilders.get(BASE_URL + "/{notExistRoomId}", notExistRoomId)
                .contentType(MediaType.APPLICATION_JSON));

        //then
        resultActions
                .andExpect(jsonPath("$.code").value(BAD_REQUEST.getCode()))
                .andExpect(jsonPath("$.message").value(BAD_REQUEST.getMessage()))
                .andDo(print())
                .andDo(document("RoomControllerTest/findRoomDetail_Fail_BadRequest",
                        pathParameters(
                                parameterWithName("notExistRoomId").description("방 ID")
                        ),
                        responseFields(
                                fieldWithPath("code").description("응답 코드"),
                                fieldWithPath("message").description("응답 메시지")
                        )));
    }

    @Test
    @DisplayName("유저가 방 상세 정보 조회 시 서버 내부 오류로 실패하면, 서버 응답 에러가 반환된다")
    void findRoomDetail_Fail_ServerError() throws Exception {
        //given
        roomSetup.setUpRooms(userSetup.setUpUsers(10));
        Long roomId = 1L;

        doThrow(new BussinessException(SEVER_ERROR))
                .when(roomService)
                .findRoomById(any());

        //when
        ResultActions resultActions = mockMvc.perform(RestDocumentationRequestBuilders.get(BASE_URL + "/{roomId}", roomId)
                .contentType(MediaType.APPLICATION_JSON));

        //then
        resultActions
                .andExpect(jsonPath("$.code").value(SEVER_ERROR.getCode()))
                .andExpect(jsonPath("$.message").value(SEVER_ERROR.getMessage()))
                .andDo(print())
                .andDo(document("RoomControllerTest/findRoomDetail_Fail_ServerError",
                        pathParameters(
                                parameterWithName("roomId").description("방 ID")
                        ),
                        responseFields(
                                fieldWithPath("code").description("응답 코드"),
                                fieldWithPath("message").description("응답 메시지")
                        )));
    }
}
