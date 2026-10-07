package com.omar.ecommerce.service.impl;

import com.omar.ecommerce.dto.request.AddressRequest;
import com.omar.ecommerce.dto.response.AddressResponse;
import com.omar.ecommerce.entity.Address;
import com.omar.ecommerce.entity.Role;
import com.omar.ecommerce.entity.User;
import com.omar.ecommerce.mapper.AddressMapper;
import com.omar.ecommerce.repository.AddressRepository;
import com.omar.ecommerce.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AddressServiceImplTest {

    @Mock
    private AddressRepository addressRepository;
    @Mock
    private AddressMapper addressMapper;
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private AddressServiceImpl addressService;

    private UUID userId;
    private User user;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();

        user = new User();
        user.setId(userId);
        user.setEmail("omar@example.com");
        user.setRole(Role.ADMIN);
        user.setFirstName("Omar");
        user.setLastName("Abohashim");
        user.setPassword("Test@@1235445");
        user.setEnabled(true);

    }

    @Test
    void getAllAddresses_returnsMappedAddress() {
        Address address = new Address();
        address.setId(UUID.randomUUID());
        address.setCity("Cairo");
        address.setUser(user);

        AddressResponse response = new AddressResponse(
                UUID.randomUUID(), "Street", "Cairo", "State", "12345", "Egypt", true);

        when(addressRepository.findByUserId(userId)).thenReturn(List.of(address));
        when(addressMapper.toAddressResponse(address)).thenReturn(response);

        List<AddressResponse> result = addressService.getAllAddresses(userId);

        assertEquals(1, result.size());
        assertEquals(response, result.get(0));
        verify(addressRepository).findByUserId(userId);
    }
    @Test
    void getAllAddresses_returnsEmptyList_whenNoAddresses() {
        when(addressRepository.findByUserId(userId)).thenReturn(List.of());

        List<AddressResponse> result = addressService.getAllAddresses(userId);

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(addressRepository).findByUserId(userId);
    }
    @Test
    void updateAddresses_updatesAndReturnsAddress_whenUserIsOwner() {
        UUID addressId = UUID.randomUUID();

        Address address = new Address();
        address.setId(addressId);
        address.setCity("Cairo");
        address.setUser(user);

        AddressRequest request = new AddressRequest(
                "Street", "egypt", "State", "12345", "Egypt", true);

        AddressResponse response = new AddressResponse(
                UUID.randomUUID(), "Street", "egypt", "State", "12345", "Egypt", true);

        when(addressRepository.findById(addressId)).thenReturn(Optional.of(address));
        when(addressMapper.toAddressResponse(address)).thenReturn(response);

        AddressResponse result = addressService.updateAddresses(userId, addressId, request);

        assertEquals(response, result);
        verify(addressRepository).findById(addressId);
        verify(addressMapper).updateAddressFromRequest(request, address);
        verify(addressRepository).save(address);
        verify(addressMapper).toAddressResponse(address);
    }

}