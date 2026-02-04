package com.abbasza.contactapi.controller;

import com.abbasza.contactapi.dto.GetSelfResponseDto;
import com.abbasza.contactapi.dto.UpdateUserResponseDto;
import com.abbasza.contactapi.security.JwtAuthFilter;
import com.abbasza.contactapi.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
@AutoConfigureMockMvc(addFilters = false)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;
    @MockBean
    private JwtAuthFilter jwtAuthFilter;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @WithMockUser(username = "user@mail.com")
    void getSelf_shouldReturn200() throws Exception {
        when(userService.getUser("user@mail.com"))
                .thenReturn(new GetSelfResponseDto());

        mockMvc.perform(get("/user"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "user@mail.com")
    void updateSelf_shouldReturn200() throws Exception {
        when(userService.updateUser(eq("user@mail.com"), any()))
                .thenReturn(new UpdateUserResponseDto());

        mockMvc.perform(put("/user/edit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "user@mail.com")
    void changePassword_shouldReturn200() throws Exception {
        when(userService.changePassword(any(), any()))
                .thenReturn(true);

        mockMvc.perform(put("/user/change-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "oldPassword": "old",
                                  "newPassword": "new"
                                }
                                """))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "user@mail.com")
    void deleteSelf_shouldReturn204() throws Exception {
        mockMvc.perform(delete("/user"))
                .andExpect(status().isNoContent());
    }
}
