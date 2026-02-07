package com.abbasza.contactapi.controller;

import com.abbasza.contactapi.dto.ContactDetailResponseDto;
import com.abbasza.contactapi.dto.ContactRequestDto;
import com.abbasza.contactapi.dto.ContactResponseDto;
import com.abbasza.contactapi.security.JwtAuthFilter;
import com.abbasza.contactapi.service.ContactService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ContactController.class)
@AutoConfigureMockMvc(addFilters = false) // disable JWT filter
class ContactControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ContactService contactService;
    @MockBean
    private JwtAuthFilter jwtAuthFilter;

    @Autowired
    private ObjectMapper objectMapper;

    private static final String USERNAME = "test@email.com";

    @BeforeEach
    void setupSecurityContext() {
        Authentication auth = new UsernamePasswordAuthenticationToken(USERNAME, null, List.of());
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    @Test
    void getAllContacts_shouldReturnPage() throws Exception {
        ContactResponseDto dto = new ContactResponseDto();
        dto.setId(UUID.randomUUID());
        dto.setFirstname("John");

        Page<ContactResponseDto> page =
                new PageImpl<>(List.of(dto));

        when(contactService.getAllContacts(USERNAME, 0, 10)).thenReturn(page);

        mockMvc.perform(get("/contact"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].firstname").value("John"));
    }

    @Test
    void getSearchContacts_shouldReturnList() throws Exception {
        ContactResponseDto dto = new ContactResponseDto();
        dto.setFirstname("Jane");

        when(contactService.getSearchContacts(USERNAME, "ja"))
                .thenReturn(List.of(dto));

        mockMvc.perform(get("/contact/s").param("query", "ja"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].firstname").value("Jane"));
    }

    @Test
    void getContact_shouldReturnContact() throws Exception {
        UUID id = UUID.randomUUID();
        ContactDetailResponseDto dto = new ContactDetailResponseDto();
        dto.setId(id);

        when(contactService.getContact(USERNAME, id)).thenReturn(dto);

        mockMvc.perform(get("/contact/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id.toString()));
    }

    @Test
    void getContact_notFound() throws Exception {
        UUID id = UUID.randomUUID();
        when(contactService.getContact(USERNAME, id))
                .thenThrow(IllegalArgumentException.class);

        mockMvc.perform(get("/contact/{id}", id))
                .andExpect(status().isNotFound());
    }

    @Test
    void createContact_shouldReturn201() throws Exception {
        ContactRequestDto req = new ContactRequestDto();
        req.setFirstname("John");

        ContactDetailResponseDto res = new ContactDetailResponseDto();
        UUID id = UUID.randomUUID();
        res.setId(id);

        when(contactService.saveContact(eq(USERNAME), any()))
                .thenReturn(res);

        mockMvc.perform(post("/contact")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/contact/" + id));
    }

    @Test
    void deleteContact_success() throws Exception {
        UUID id = UUID.randomUUID();
        when(contactService.deleteContactById(USERNAME, id)).thenReturn(true);

        mockMvc.perform(delete("/contact/{id}", id))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteContact_notFound() throws Exception {
        UUID id = UUID.randomUUID();
        when(contactService.deleteContactById(USERNAME, id)).thenReturn(false);

        mockMvc.perform(delete("/contact/{id}", id))
                .andExpect(status().isNotFound());
    }
}
