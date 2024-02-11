package org.prography.spring.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.prography.spring.common.ApiResponse;
import org.prography.spring.service.ServerStatusService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.mockito.Mockito.doReturn;
import static org.prography.spring.common.ApiResponseCode.SEVER_ERROR;
import static org.prography.spring.common.ApiResponseCode.SUCCESS;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureRestDocs
public class ServerStatusControllerTest {

    private static final String BASE_URL = "/health";

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ServerStatusService serverStatusService;

    private ApiResponse<Object> successResponse;
    private ApiResponse<Object> errorResponse;

    @BeforeEach
    void setUp() {
        successResponse = new ApiResponse<>(SUCCESS.getCode(), SUCCESS.getMessage(), null);
        errorResponse = new ApiResponse<>(SEVER_ERROR.getCode(), SEVER_ERROR.getMessage(), null);
    }

    @Test
    @DisplayName("정상적으로 서버 상태 체크 요청을 처리를 성공하면 성공 응답이 반환된다")
    void serverStatusCheck_Success() throws Exception {
        //given
        doReturn(successResponse)
                .when(serverStatusService)
                .serverStatusCheck();

        //when
        ResultActions resultActions = mockMvc.perform(get(BASE_URL)
                .accept(MediaType.APPLICATION_JSON));

        //then
        resultActions.andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("code").value(successResponse.getCode()))
                .andExpect(jsonPath("message").value(successResponse.getMessage()))
                .andExpect(jsonPath("result").doesNotExist())
                .andDo(print())
                .andDo(document("ServerStatusControllerTest/serverStatusCheck_Success",
                        responseFields(
                                fieldWithPath("code").description("응답 코드"),
                                fieldWithPath("message").description("응답 메시지")
                        )
                ));
    }

    @Test
    @DisplayName("서버 상태 체크 요청 시 서버가 정상적이지 않으면 실패 응답이 반환된다")
    void serverStatusCheck_Fail() throws Exception {
        //given
        doReturn(errorResponse)
                .when(serverStatusService)
                .serverStatusCheck();

        //when
        ResultActions resultActions = mockMvc.perform(get(BASE_URL)
                .accept(MediaType.APPLICATION_JSON));

        //then
        resultActions.andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.code").value(errorResponse.getCode()))
                .andExpect(jsonPath("$.message").value(errorResponse.getMessage()))
                .andExpect(jsonPath("$.result").doesNotExist())
                .andDo(print())
                .andDo(document("ServerStatusControllerTest/serverStatusCheck_Fail",
                        responseFields(
                                fieldWithPath("code").description("응답 코드"),
                                fieldWithPath("message").description("응답 메시지")
                        )
                ));
    }
}
