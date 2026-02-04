package com.abbasza.contactapi.controller;

import com.abbasza.contactapi.dto.GetContactResponseDto;
import com.abbasza.contactapi.security.JwtAuthFilter;
import com.abbasza.contactapi.service.ContactService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ContactController.class)
@AutoConfigureMockMvc(addFilters = false)
class ContactControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ContactService contactService;
    @MockBean
    private JwtAuthFilter jwtAuthFilter;

    @Test
    @WithMockUser(username = "user@mail.com")
    void getContact_shouldReturn200() throws Exception {
        UUID contactId = UUID.randomUUID();

        when(contactService.getContact(eq("user@mail.com"), eq(contactId)))
                .thenReturn(new GetContactResponseDto());

        mockMvc.perform(get("/contact/{id}", contactId))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "user@mail.com")
    void getContact_shouldReturn404WhenNotFound() throws Exception {
        UUID contactId = UUID.randomUUID();

        when(contactService.getContact(any(), any()))
                .thenThrow(new RuntimeException("Not found"));

        mockMvc.perform(get("/contact/{id}", contactId))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "user@mail.com")
    void createContact_shouldReturn201() throws Exception {
        UUID contactId = UUID.randomUUID();
        GetContactResponseDto dto = new GetContactResponseDto();
        dto.setId(contactId);

        when(contactService.saveContact(any(), any()))
                .thenReturn(dto);

        mockMvc.perform(post("/contact")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/contact/" + contactId));
    }

    @Test
    @WithMockUser(username = "user@mail.com")
    void deleteContact_shouldReturn204() throws Exception {
        UUID contactId = UUID.randomUUID();

        when(contactService.deleteContactById(any(), eq(contactId)))
                .thenReturn(true);

        mockMvc.perform(delete("/contact/{id}", contactId))
                .andExpect(status().isNoContent());
    }
}
