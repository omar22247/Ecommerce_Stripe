package com.omar.ecommerce.service.impl;

import com.omar.ecommerce.dto.response.UserResponseDto;
import com.omar.ecommerce.entity.Role;
import com.omar.ecommerce.entity.User;
import com.omar.ecommerce.exception.ResourceNotFoundException;
import com.omar.ecommerce.mapper.UserMapper;
import com.omar.ecommerce.repository.UserRepository;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    void getUserById_returnsDto_whenUserExists() {
        UUID id = UUID.randomUUID();

        User user = new User();
        user.setId(id);
        user.setEmail("omar@example.com");
        user.setRole(Role.ADMIN);
        user.setFirstName("Omar");
        user.setLastName("Abohashim");
        user.setPassword("Test@@1235445");
        user.setEnabled(true);

        UserResponseDto dto = new UserResponseDto(id,"Omar","Abohashim",
        "omar2@example.com",Role.ADMIN,true);


        when(userRepository.findById(id)).thenReturn(Optional.of(user));
        when(userMapper.toDto(user)).thenReturn(dto);

        UserResponseDto result = userService.getUserById(id);

        System.out.println("result: " + result);
        System.out.println("dto: " + dto);
        assertSame(dto, result);
        verify(userRepository).findById(id);
        verify(userMapper).toDto(user);
    }
@Disabled
    @Test
    void getUserById_throwsNotFound_whenUserMissing() {
        UUID id = UUID.randomUUID();
        when(userRepository.findById(id)).thenReturn(Optional.empty());

        ResourceNotFoundException ex = assertThrows(
                ResourceNotFoundException.class,
                () -> userService.getUserById(id)
        );
        assertTrue(ex.getMessage().contains(id.toString()));

        verify(userMapper, never()).toDto(any());
    }
}