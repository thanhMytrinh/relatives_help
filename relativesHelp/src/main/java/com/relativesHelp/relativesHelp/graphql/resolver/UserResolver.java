package com.relativesHelp.relativesHelp.graphql.resolver;

import com.relativesHelp.relativesHelp.security.dto.ClientMetadata;
import com.relativesHelp.relativesHelp.user.dto.AuthResponse;
import com.relativesHelp.relativesHelp.user.dto.LoginRequest;
import com.relativesHelp.relativesHelp.user.dto.RegisterRequest;
import com.relativesHelp.relativesHelp.user.dto.UserDto;
import com.relativesHelp.relativesHelp.user.service.AuthService;
import com.relativesHelp.relativesHelp.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class UserResolver {
    private final UserService userService;
    private final AuthService authService;

    @QueryMapping
    public UserDto me(Authentication authentication) {
        Long userId = (Long) authentication.getPrincipal();
        return userService.getUserById(userId);
    }

    @QueryMapping
    public UserDto user(@Argument Long id) {
        return userService.getUserById(id);
    }

    @MutationMapping
    public AuthResponse register(@Argument("input") RegisterInput input) {
        RegisterRequest request = new RegisterRequest();
        request.setUsername(input.username());
        request.setEmail(input.email());
        request.setPassword(input.password());
        request.setFullName(input.fullName());
        request.setPhone(input.phone());
        return authService.register(request, ClientMetadata.empty());
    }

    @MutationMapping
    public AuthResponse login(@Argument("input") LoginInput input) {
        LoginRequest request = new LoginRequest();
        request.setEmailOrUsername(input.emailOrUsername());
        request.setPassword(input.password());
        return authService.login(request, ClientMetadata.empty());
    }

    // GraphQL Input Records
    public record RegisterInput(
            String username,
            String email,
            String password,
            String fullName,
            String phone
    ) {}

    public record LoginInput(
            String emailOrUsername,
            String password
    ) {}
}

