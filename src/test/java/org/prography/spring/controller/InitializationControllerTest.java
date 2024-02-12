package org.prography.spring.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.prography.spring.common.BussinessException;
import org.prography.spring.dto.request.InitializationRequest;
import org.prography.spring.service.InitializationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.prography.spring.common.ApiResponseCode.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureRestDocs
public class InitializationControllerTest {

    private static final String BASE_URL = "/init";

    @Autowired
    private MockMvc mockMvc;

    @SpyBean
    private InitializationService initializationService;

    private InitializationRequest initializationRequest;

    @Test
    @DisplayName("정상적으로 초기화 요청을 처리를 성공하면, 성공 응답이 반환된다")
    void initialization_Success() throws Exception {
        // given
        initializationRequest = InitializationRequest.builder()
                .seed(1L)
                .quantity(10L)
                .build();

        // when
        ResultActions resultActions = mockMvc.perform(post(BASE_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(initializationRequest)));

        // then
        resultActions
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.code").value(SUCCESS.getCode()))
                .andExpect(jsonPath("$.message").value(SUCCESS.getMessage()))
                .andDo(print())
                .andDo(document("InitializationControllerTest/initialization_Success",
                        requestFields(
                                fieldWithPath("seed").description("난수(seed)"),
                                fieldWithPath("quantity").description("생성할 데이터 수(quantity)")
                        ),
                        responseFields(
                                fieldWithPath("code").description("응답 코드"),
                                fieldWithPath("message").description("응답 메시지")
                        )
                ));
    }

    @Test
    @DisplayName("초기화 요청에 잘못된 값이 들어오면, 실패 응답이 반환된다")
    void initialization_Fail_BadRequest() throws Exception {
        //given
        InitializationRequest invalidRequest = InitializationRequest.builder()
                .seed(-1L)
                .quantity(-10L)
                .build();

        doThrow(new BussinessException(BAD_REQUEST))
                .when(initializationService)
                .init(invalidRequest);

        //when
        ResultActions resultActions = mockMvc.perform(post(BASE_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(invalidRequest)));

        //then
        resultActions
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.code").value(BAD_REQUEST.getCode()))
                .andExpect(jsonPath("$.message").value(BAD_REQUEST.getMessage()))
                .andDo(print())
                .andDo(document("InitializationControllerTest/initialization_Fail_BadRequest",
                        requestFields(
                                fieldWithPath("seed").description("난수(seed)"),
                                fieldWithPath("quantity").description("생성할 데이터 수(quantity)")
                        ),
                        responseFields(
                                fieldWithPath("code").description("응답 코드"),
                                fieldWithPath("message").description("응답 메시지")
                        )
                ));
    }

    @Test
    @DisplayName("초기화 요청이 서버에서 정상적으로 이루어지지 않으면, 실패 응답이 반환된다")
    void initialization_Fail_ServerError() throws Exception {
        initializationRequest = InitializationRequest.builder()
                .seed(1L)
                .quantity(10L)
                .build();

        //given
        doThrow(new BussinessException(SEVER_ERROR))
                .when(initializationService)
                .init(any(InitializationRequest.class));

        //when
        ResultActions resultActions = mockMvc.perform(post(BASE_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(initializationRequest)));

        //then
        resultActions
                .andExpect(jsonPath("$.code").value(SEVER_ERROR.getCode()))
                .andExpect(jsonPath("$.message").value(SEVER_ERROR.getMessage()))
                .andDo(print())
                .andDo(document("InitializationControllerTest/initialization_Fail_ServerError",
                        requestFields(
                                fieldWithPath("seed").description("난수(seed)"),
                                fieldWithPath("quantity").description("생성할 데이터 수(quantity)")
                        ),
                        responseFields(
                                fieldWithPath("code").description("응답 코드"),
                                fieldWithPath("message").description("응답 메시지")
                        )
                ));
    }
}
