package org.prography.spring.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.prography.spring.common.BussinessException;
import org.prography.spring.domain.Room;
import org.prography.spring.domain.User;
import org.prography.spring.dto.request.ChangeTeamRequest;
import org.prography.spring.fixture.dto.UserDtoFixture;
import org.prography.spring.fixture.setup.RoomSetup;
import org.prography.spring.fixture.setup.UserRoomSetUp;
import org.prography.spring.fixture.setup.UserSetup;
import org.prography.spring.service.TeamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.prography.spring.common.ApiResponseCode.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.put;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureRestDocs
class TeamControllerTest {

    private static final String BASE_URL = "/team";

    @Autowired
    private MockMvc mockMvc;

    @SpyBean
    private TeamService teamService;

    @Autowired
    private UserSetup userSetup;

    @Autowired
    private UserRoomSetUp userRoomSetUp;

    @Autowired
    private RoomSetup roomSetup;

    @Test
    @DisplayName("정상적으로 유저가 팀을 변경하면, 성공 응답이 반환된다")
    void changeTeam_Success() throws Exception {
        //given
        Long fakerId = 1L;
        User user = userSetup.setUpUser(fakerId);
        Room room = roomSetup.setUpRoom(user);
        userRoomSetUp.setUpUserRoom(user, room);

        ChangeTeamRequest changeTeamRequest = UserDtoFixture.changeTeamRequest(user.getId());

        doNothing()
                .when(teamService)
                .changeTeamById(room.getId(), changeTeamRequest);

        //when
        ResultActions resultActions = mockMvc.perform(
                put(BASE_URL + "/{roomId}", room.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(changeTeamRequest))
        );

        //then
        resultActions
                .andExpect(jsonPath("$.code").value(SUCCESS.getCode()))
                .andExpect(jsonPath("$.message").value(SUCCESS.getMessage()))
                .andDo(print())
                .andDo(document("TeamControllerTest/changeTeam_Success",
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
    @DisplayName("팀 변경 요청에 잘못된 값이 들어오면, 실패 응답이 반환된다")
    void changeTeam_Fail_BadRequest() throws Exception {
        //given
        Long fakerId = 2L;
        User user = userSetup.setUpUser(fakerId);
        Room room = roomSetup.setUpRoom(user);
        userRoomSetUp.setUpUserRoom(user, room);

        ChangeTeamRequest changeTeamRequest = ChangeTeamRequest.builder()
                .userId(-1L)
                .build();

        doThrow(new BussinessException(BAD_REQUEST))
                .when(teamService)
                .changeTeamById(room.getId(), changeTeamRequest);

        //when
        ResultActions resultActions = mockMvc.perform(
                put(BASE_URL + "/{roomId}", room.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(changeTeamRequest))
        );

        //then

        resultActions
                .andExpect(jsonPath("$.code").value(BAD_REQUEST.getCode()))
                .andExpect(jsonPath("$.message").value(BAD_REQUEST.getMessage()))
                .andDo(print())
                .andDo(document("TeamControllerTest/changeTeam_Fail_BadRequest",
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
    @DisplayName("존재하지 않는 방에 팀 변경 요청이 오면, 실패 응답이 반환된다.")
    void changeTeam_Fail_NotExistRoom() throws Exception {
        //given
        Long fakerId = 11L;
        User user = userSetup.setUpUser(fakerId);
        roomSetup.setUpRooms(userSetup.setUpUsers(5));
        Long notExistRoomId = 100L;

        ChangeTeamRequest changeTeamRequest = UserDtoFixture.changeTeamRequest(user.getId());

        doThrow(new BussinessException(BAD_REQUEST))
                .when(teamService)
                .changeTeamById(notExistRoomId, changeTeamRequest);


        //when
        ResultActions resultActions = mockMvc.perform(
                put(BASE_URL + "/{roomId}", notExistRoomId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(changeTeamRequest))
        );

        //then
        resultActions
                .andExpect(jsonPath("$.code").value(BAD_REQUEST.getCode()))
                .andExpect(jsonPath("$.message").value(BAD_REQUEST.getMessage()))
                .andDo(print())
                .andDo(document("TeamControllerTest/changeTeam_Fail_NotExistRoom",
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
    @DisplayName("유저가 해당 방에 참가하지 않은 상태에서 팀 변경 요청이 오면, 실패 응답이 반환된다.")
    void changeTeam_Fail_NotParticipatedUser() throws Exception {
        //given
        Long fakerId = 12L;
        Long notParticipatedFakeId = 13L;
        User participatedUser = userSetup.setUpUser(fakerId);
        User notParticipatedUser = userSetup.setUpUser(notParticipatedFakeId);
        Room room = roomSetup.setUpRoom(participatedUser);

        ChangeTeamRequest changeTeamRequest = UserDtoFixture.changeTeamRequest(notParticipatedUser.getId());

        doThrow(new BussinessException(BAD_REQUEST))
                .when(teamService)
                .changeTeamById(room.getId(), changeTeamRequest);

        //when
        ResultActions resultActions = mockMvc.perform(
                put(BASE_URL + "/{roomId}", room.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(changeTeamRequest))
        );

        //then
        resultActions
                .andExpect(jsonPath("$.code").value(BAD_REQUEST.getCode()))
                .andExpect(jsonPath("$.message").value(BAD_REQUEST.getMessage()))
                .andDo(print())
                .andDo(document("TeamControllerTest/changeTeam_Fail_NotParticipatedUser",
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
    @DisplayName("방의 상태가 대기 상태가 아닐 때, 팀 변경 요청이 오면 실패 응답이 반환된다.")
    void changeTeam_Fail_NotWaitRoomStatus() throws Exception {
        Long fakerId = 14L;
        Long attendedFakerId = 15L;
        User host = userSetup.setUpUser(fakerId);
        User attendedUser = userSetup.setUpUser(attendedFakerId);
        Room room = roomSetup.notWaitStatusRoom(host);
        userRoomSetUp.setUpUserRoom(host, room);

        ChangeTeamRequest changeTeamRequest = UserDtoFixture.changeTeamRequest(attendedUser.getId());

        doThrow(new BussinessException(BAD_REQUEST))
                .when(teamService)
                .changeTeamById(room.getId(), changeTeamRequest);

        //when
        ResultActions resultActions = mockMvc.perform(
                put(BASE_URL + "/{roomId}", room.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(changeTeamRequest))
        );

        //then
        resultActions
                .andExpect(jsonPath("$.code").value(BAD_REQUEST.getCode()))
                .andExpect(jsonPath("$.message").value(BAD_REQUEST.getMessage()))
                .andDo(print())
                .andDo(document("TeamControllerTest/changeTeam_Fail_NotWaitRoomStatus",
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
    @DisplayName("변경하려는 팀의 인원이 방 정원의 절반에 도달했을 때 팀 변경 요청이 오면, 실패 응답이 반환된다.")
    void changeTeam_Fail_FullTeamStatus() throws Exception {
        Long hostFakerId = 16L;
        Long guestFakerId = 17L;
        Long attendedFakerId = 18L;

        User host = userSetup.setUpUser(hostFakerId);
        User guest = userSetup.setUpUser(guestFakerId);
        User attendedUser = userSetup.setUpUser(attendedFakerId);
        Room room = roomSetup.setUpRoom(host);

        userRoomSetUp.setUpUserRoom(host, room);
        userRoomSetUp.setUpUserRoom(guest, room);

        ChangeTeamRequest changeTeamRequest = UserDtoFixture.changeTeamRequest(attendedUser.getId());

        doThrow(new BussinessException(BAD_REQUEST))
                .when(teamService)
                .changeTeamById(room.getId(), changeTeamRequest);

        //when
        ResultActions resultActions = mockMvc.perform(
                put(BASE_URL + "/{roomId}", room.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(changeTeamRequest))
        );

        //then
        resultActions
                .andExpect(jsonPath("$.code").value(BAD_REQUEST.getCode()))
                .andExpect(jsonPath("$.message").value(BAD_REQUEST.getMessage()))
                .andDo(print())
                .andDo(document("TeamControllerTest/changeTeam_Fail_FullTeamStatus",
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
    @DisplayName("팀 변경 요청이 있을 때, 서버 오류가 발생하면 서버 오류 응답이 반환된다.")
    void changeTeam_Fail_ServerError() throws Exception {
        //given
        Long hostFakerId = 19L;
        Long attendedFakerId = 20L;

        User host = userSetup.setUpUser(hostFakerId);
        User attendedUser = userSetup.setUpUser(attendedFakerId);
        Room room = roomSetup.setUpRoom(host);

        ChangeTeamRequest changeTeamRequest = UserDtoFixture.changeTeamRequest(attendedUser.getId());

        doThrow(new RuntimeException())
                .when(teamService)
                .changeTeamById(anyLong(), any());

        //when
        ResultActions resultActions = mockMvc.perform(
                put(BASE_URL + "/{roomId}", room.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(changeTeamRequest))
        );

        //then
        resultActions
                .andExpect(jsonPath("$.code").value(SEVER_ERROR.getCode()))
                .andExpect(jsonPath("$.message").value(SEVER_ERROR.getMessage()))
                .andDo(print())
                .andDo(document("TeamControllerTest/changeTeam_Fail_ServerError",
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
