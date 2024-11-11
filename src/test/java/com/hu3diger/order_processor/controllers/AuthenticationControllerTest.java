package com.hu3diger.order_processor.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.hu3diger.order_processor.dtos.LoginResponseDto;
import com.hu3diger.order_processor.dtos.LoginUserDto;
import com.hu3diger.order_processor.dtos.RegisterUserDto;
import com.hu3diger.order_processor.entities.UserEntity;
import com.hu3diger.order_processor.services.AuthenticationService;
import com.hu3diger.order_processor.services.JwtService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthenticationController.class)
public class AuthenticationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private AuthenticationService authenticationService;

    public static String asJsonString(Object obj) {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        try {
            return mapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Test
    @WithMockUser(username = "user")
    public void testRegister_Success() throws Exception {
        RegisterUserDto registerUserDto = new RegisterUserDto();
        UserEntity userEntity = new UserEntity();

        when(authenticationService.signup(any(RegisterUserDto.class))).thenReturn(userEntity);

        mockMvc.perform(post("/auth/signup")
                        .contentType("application/json")
                        .content(asJsonString(registerUserDto))
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().json(asJsonString(userEntity)));

        verify(authenticationService, times(1)).signup(any(RegisterUserDto.class));
    }

    @Test
    @WithMockUser(username = "user")
    public void testAuthenticate_Success() throws Exception {
        LoginUserDto loginUserDto = new LoginUserDto();
        LoginResponseDto loginResponseDto = new LoginResponseDto();

        when(authenticationService.authenticateAndGetResponse(any(LoginUserDto.class))).thenReturn(loginResponseDto);

        mockMvc.perform(post("/auth/login")
                        .contentType("application/json")
                        .content(asJsonString(loginUserDto))
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().json(asJsonString(loginResponseDto)));

        verify(authenticationService, times(1)).authenticateAndGetResponse(any(LoginUserDto.class));
    }
}
