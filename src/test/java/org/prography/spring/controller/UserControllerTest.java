package org.prography.spring.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.prography.spring.domain.User;
import org.prography.spring.dto.response.UserListResponse;
import org.prography.spring.dto.response.UserResponse;
import org.prography.spring.fixture.setup.UserSetup;
import org.prography.spring.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.Commit;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.Collections;
import java.util.List;

import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.prography.spring.common.ApiResponseCode.SUCCESS;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureRestDocs
public class UserControllerTest {

    private static final String BASE_URL = "/user";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserSetup userSetup;

    @SpyBean
    private UserService userService;


    @Test
    @DisplayName("초기화 API 호출 전에는 유저 정보를 전체 조회하면 비어있다.")
    void findAllUsers_BeforeInitialization() throws Exception {
        //given
        List<User> userList = Collections.emptyList();
        Page<User> userPage = new PageImpl<>(userList);

        List<UserResponse> userResponses = userList.stream()
                .map(UserResponse::from)
                .toList();

        UserListResponse userListResponse = UserListResponse.of(
                userPage.getTotalElements(),
                (long) userPage.getTotalPages(),
                userResponses);

        doReturn(userListResponse).when(userService).findAllUsers(any(Pageable.class));

        //when
        ResultActions resultActions = mockMvc.perform(get(BASE_URL)
                .contentType(MediaType.APPLICATION_JSON));

        //then
        resultActions.andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.code").value(SUCCESS.getCode()))
                .andExpect(jsonPath("$.message").value(SUCCESS.getMessage()))
                .andExpect(jsonPath("$.result.totalElements").value(0))
                .andExpect(jsonPath("$.result.totalPages").value(1))
                .andExpect(jsonPath("$.result.userList", hasSize(0)))
                .andDo(document("UserControllerTest/findAllUsers_BeforeInitialization",
                        responseFields(
                                fieldWithPath("code").description("응답 코드"),
                                fieldWithPath("message").description("응답 메시지"),
                                fieldWithPath("result.totalElements").description("전체 항목 수"),
                                fieldWithPath("result.totalPages").description("전체 페이지 수"),
                                fieldWithPath("result.userList").description("유저 정보 리스트")
                        )
                ));
    }

    @Test
    @Commit
    @DisplayName("초기화 API 호출 후에는 유저 정보를 전체 조회할 수 있다.")
    void findAllUsers_AfterInitialization_Success() throws Exception {
        //given
        userSetup.setUpUsers(10);

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
                .andExpect(jsonPath("$.result.totalElements").value(20))
                .andExpect(jsonPath("$.result.totalPages").value(2))
                .andExpect(jsonPath("$.result.userList", hasSize(10)))
                .andExpect(jsonPath("$.result.userList[0].id").exists())
                .andExpect(jsonPath("$.result.userList[0].fakerId").exists())
                .andExpect(jsonPath("$.result.userList[0].name").exists())
                .andExpect(jsonPath("$.result.userList[0].email").exists())
                .andExpect(jsonPath("$.result.userList[0].status").exists())
                .andExpect(jsonPath("$.result.userList[0].createdAt").exists())
                .andExpect(jsonPath("$.result.userList[0].updatedAt").exists())
                .andDo(document("UserControllerTest/findAllUsers_AfterInitialization_Success",
                        responseFields(
                                fieldWithPath("code").description("응답 코드"),
                                fieldWithPath("message").description("응답 메시지"),
                                fieldWithPath("result.totalElements").description("전체 항목 수"),
                                fieldWithPath("result.totalPages").description("전체 페이지 수"),
                                fieldWithPath("result.userList[].id").description("유저 아이디"),
                                fieldWithPath("result.userList[].fakerId").description("faker 아이디"),
                                fieldWithPath("result.userList[].name").description("유저 이름"),
                                fieldWithPath("result.userList[].email").description("유저 이메일"),
                                fieldWithPath("result.userList[].status").description("유저 상태"),
                                fieldWithPath("result.userList[].createdAt").description("유저 생성 날짜"),
                                fieldWithPath("result.userList[].updatedAt").description("유저 수정 날짜")
                        )
                ));
    }
}