package com.techie.mynews.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.techie.mynews.dto.response.CheckUserResponse;
import com.techie.mynews.dto.response.UserResponse;
import com.techie.mynews.exception.MyNewsException;
import com.techie.mynews.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
public class UserControllerTest {
    private static final String BASE_URL = "/api/users";

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

    @Nested
    @DisplayName("GET /api/users/check")
    class CheckEndpointTests {

        @Test
        @DisplayName("Should return 200 OK with CheckUserResponse when user exists")
        void check_whenUserExists_returns200() throws Exception {
            String email = "example@email.com";
            CheckUserResponse response = new CheckUserResponse();
            response.setExists(true);
            response.setUserResponse(new UserResponse());

            given(userService.checkIfUserExists(email)).willReturn(response);

            mockMvc.perform(get(BASE_URL + "/check")
                            .with(jwt().jwt(jwt -> jwt.claim("email", email))))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.exists").value(true));

            verify(userService).checkIfUserExists(email);
        }

        @Test
        @DisplayName("Should return 401 Unauthorized when no authentication token is provided")
        void check_unauthenticated_returns401() throws Exception {
            mockMvc.perform(get(BASE_URL + "/check"))
                    .andExpect(status().isUnauthorized());
            verifyNoInteractions(userService);
        }

        @Test
        @DisplayName("Should return 200 OK with exists=false when user does not exist")
        void check_whenUserDoesNotExist_returns200() throws Exception {
            String email = "example@email.com";
            CheckUserResponse response = new CheckUserResponse();
            response.setExists(false);
            response.setUserResponse(null);

            given(userService.checkIfUserExists(email)).willReturn(response);

            mockMvc.perform(get(BASE_URL + "/check")
                            .with(jwt().jwt(jwt -> jwt.claim("email", email)))
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.exists").value(false))
                    .andExpect(jsonPath("$.userResponse").doesNotExist());

            verify(userService).checkIfUserExists(email);
        }

        @Test
        @DisplayName("Should return 404 Νot found when authentication token doesn't have email claim")
        void check_whenEmailClaimMissing_returns404() throws Exception {
            given(userService.checkIfUserExists(null)).willThrow((new MyNewsException(HttpStatus.NOT_FOUND,"No email found")));

            mockMvc.perform(get(BASE_URL + "/check")
                            .with(jwt().jwt(jwt -> jwt.subject("auth0|123456"))))
                    .andExpect(status().isNotFound());

            verify(userService).checkIfUserExists(null);
        }
    }
}
