package com.techie.mynews.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.techie.mynews.dto.request.UserSourceRequest;
import com.techie.mynews.dto.response.CheckUserResponse;
import com.techie.mynews.dto.response.UserResponse;
import com.techie.mynews.dto.response.UserSourceResponse;
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
import java.util.List;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
public class UserControllerTest {
    private static final String BASE_URL = "/api/users";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

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

    @Nested
    @DisplayName("POST api/users")
    class CreateUserEndpointTests {

        //Test for the happy path -> 201 created
        @Test
        @DisplayName("Should return 201 created when the email is new")
        void create_whenUserIsNew_returns201() throws Exception {
            String email = "example@email.com";
            UserResponse userResponse = new UserResponse();
            userResponse.setId("1");
            userResponse.setEmail("example@email.com");

            given(userService.create(email)).willReturn(userResponse);

            mockMvc.perform(post(BASE_URL)
                    .with(jwt().jwt(jwt -> jwt.claim("email",email))))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.id").value("1"))
                    .andExpect(jsonPath("$.email").value(email));

            verify(userService).create(email);
        }

        //Test for missing email -> 404 NOT FOUND
        @Test
        @DisplayName("Should return 404 when the email claim is missing")
        void check_whenEmailClaimIsMissing_returns404() throws Exception {
            given(userService.create(null)).willThrow((new MyNewsException(HttpStatus.NOT_FOUND, "No email found")));

            mockMvc.perform(post(BASE_URL)
                            .with(jwt().jwt(jwt -> jwt.subject("auth0|123456"))))
                    .andExpect(status().isNotFound());

            verify(userService).create(null);
        }

        //Test that email already exists in db-> 409 CONFLICT
        @Test
        @DisplayName("Should return 409 when the email already exists in db")
        void check_whenEmailAlreadyExists_returns409() throws Exception {
            String email = "example@email.com";
            given(userService.create(email)).willThrow((new MyNewsException(HttpStatus.CONFLICT, String.format("User with email %s already exists",email))));

            mockMvc.perform(post(BASE_URL)
                            .with(jwt().jwt(jwt -> jwt.claim("email",email))))
                    .andExpect(status().isConflict());

            verify(userService).create(email);
        }
    }

    @Nested
    @DisplayName("POST /api/users/sources")
    class SaveSourcesEndpointTests {

        @Test
        @DisplayName("Should return 200 OK with list of UserSourceResponse on valid request")
        void saveSources_whenValidRequest_returns200() throws Exception {
            String email = "example@email.com";
            UserSourceRequest userSourceRequest = new UserSourceRequest();
            UserSourceResponse item = new UserSourceResponse();
            item.setId("source-1");
            item.setAction("ADD");
            List<UserSourceResponse> responseList = List.of(item);

            given(userService.saveSources(any(UserSourceRequest.class), eq(email)))
                    .willReturn(responseList);

            mockMvc.perform(post(BASE_URL + "/sources")
                    .with(jwt().jwt(jwt -> jwt.claim("email",email)))
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(userSourceRequest))
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(1)))
                    .andExpect(jsonPath("$[0].id").value("source-1"))
                    .andExpect(jsonPath("$[0].action").value("ADD"));

            verify(userService).saveSources(any(UserSourceRequest.class), eq(email));
        }

        @Test
        @DisplayName("Should return 401 Unauthorized when no authentication token is provided")
        void saveSources_unauthenticated_returns401() throws Exception {
            UserSourceRequest request = new UserSourceRequest();

            mockMvc.perform(post(BASE_URL + "/sources")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isUnauthorized());

            verifyNoInteractions(userService);
        }

        @Test
        @DisplayName("Should return 400 Bad Request when request body is missing")
        void saveSources_whenMissingBody_returns400() throws Exception {
            String email = "example@email.com";

            mockMvc.perform(post(BASE_URL + "/sources")
                            .with(jwt().jwt(jwt -> jwt.claim("email", email)))
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest());

            verifyNoInteractions(userService);
        }

        @Test
        @DisplayName("Should return 400 Bad Request when user does not exist in service")
        void saveSources_whenUserNotFoundInService_returns400() throws Exception {
            String email = "notfound@email.com";
            UserSourceRequest request = new UserSourceRequest();

            given(userService.saveSources(any(UserSourceRequest.class), eq(email)))
                    .willThrow(new MyNewsException(HttpStatus.BAD_REQUEST, "User with email " + email + " not found."));

            mockMvc.perform(post(BASE_URL + "/sources")
                            .with(jwt().jwt(jwt -> jwt.claim("email", email)))
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest());

            verify(userService).saveSources(any(UserSourceRequest.class), eq(email));
        }


    }

}
