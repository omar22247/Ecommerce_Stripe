package com.omar.ecommerce.controller;

import com.omar.ecommerce.dto.response.ApiResponse;
import com.omar.ecommerce.dto.response.UserResponseDto;
import com.omar.ecommerce.security.AuthenticatedUser;
import com.omar.ecommerce.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
@Tag(name = "Users", description = "Read user profiles")
public class UserController {

    private final UserService userService;

    @Operation(summary = "Get my profile", description = "Returns the profile of the authenticated user.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "User retrieved"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Missing or invalid token")
    })
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserResponseDto>> getUserById(@AuthenticationPrincipal AuthenticatedUser user) {
        UserResponseDto userDto = userService.getUserById(user.userId());
        return ResponseEntity.ok(ApiResponse.success("User retrieved successfully", userDto));
    }

    @Operation(summary = "Get a user by ID", description = "Returns the profile of any user by their ID.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "User retrieved"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Missing or invalid token"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "User not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<UserResponseDto>> getUserByIdPublic(
            @Parameter(description = "User ID") @PathVariable UUID id) {
        UserResponseDto userDto = userService.getUserById(id);
        return ResponseEntity.ok(ApiResponse.success("User retrieved successfully", userDto));
    }

}
