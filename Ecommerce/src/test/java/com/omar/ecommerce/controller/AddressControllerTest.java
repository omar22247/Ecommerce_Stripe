package com.omar.ecommerce.controller;

import com.omar.ecommerce.dto.response.AddressResponse;
import com.omar.ecommerce.security.AuthenticatedUser;
import com.omar.ecommerce.service.AddressService;
import com.omar.ecommerce.service.JwtService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.verify;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AddressController.class)
@AutoConfigureMockMvc(addFilters = false)
class AddressControllerWebTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AddressService addressService;

    @MockitoBean
    private JwtService jwtService;

    @Test
    void getAddresses_returns200() throws Exception {
        UUID userId = UUID.randomUUID();

        when(addressService.getAllAddresses(any()))
                .thenReturn(List.of(
                        new AddressResponse(UUID.randomUUID(), "Street 1", "Cairo", "State 1", "12345", "Egypt", true),
                        new AddressResponse(UUID.randomUUID(), "Street 2", "Giza", "State 2", "54321", "Egypt", false)
                ));

        mockMvc.perform(get("/api/v1/addresses/me"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].city").value("Cairo"))
                .andExpect(jsonPath("$.data[1].city").value("Giza"));
        verify(addressService).getAllAddresses(any());
    }
}
