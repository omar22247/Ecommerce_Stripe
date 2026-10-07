package com.omar.ecommerce.controller;

import com.omar.ecommerce.dto.response.UserResponseDto;
import com.omar.ecommerce.entity.Role;
import com.omar.ecommerce.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.mockito.Mockito.when;



@WebMvcTest(UserController.class)
@AutoConfigureMockMvc(addFilters = false)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private com.omar.ecommerce.service.JwtService jwtService;

    @Test
    void getUserByIdPublic_returns200WithUser() throws Exception {
        UUID id = UUID.randomUUID();
        UserResponseDto dto = new UserResponseDto(
                id, "Omar", "Abohashim", "omar@example.com", Role.ADMIN, true);
        when(userService.getUserById(id)).thenReturn(dto);

        mockMvc.perform(get("/api/v1/users/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.email").value("omar@example.com"));
    }
}