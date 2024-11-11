package com.hu3diger.order_processor.services;

import com.hu3diger.order_processor.dtos.LoginResponseDto;
import com.hu3diger.order_processor.dtos.LoginUserDto;
import com.hu3diger.order_processor.dtos.RegisterUserDto;
import com.hu3diger.order_processor.entities.UserEntity;
import com.hu3diger.order_processor.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class AuthenticationServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private AuthenticationService authenticationService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void shouldSignupNewUserWithEncodedPassword() {
        RegisterUserDto input = new RegisterUserDto("test@gmail.com", "password123", "Alfred Valdevin");
        UserEntity userEntity = new UserEntity();
        userEntity.setFullName(input.getFullName());
        userEntity.setEmail(input.getEmail());
        userEntity.setPassword("encodedPassword");

        when(passwordEncoder.encode(input.getPassword())).thenReturn("encodedPassword");
        when(userRepository.save(any(UserEntity.class))).thenReturn(userEntity);

        UserEntity savedUser = authenticationService.signup(input);

        assertEquals("Alfred Valdevin", savedUser.getFullName());
        assertEquals("test@gmail.com", savedUser.getEmail());
        assertEquals("encodedPassword", savedUser.getPassword());
        verify(passwordEncoder).encode("password123");
        verify(userRepository).save(any(UserEntity.class));
    }

    @Test
    void shouldAuthenticateAndReturnUser() {
        LoginUserDto input = new LoginUserDto("john@example.com", "password123");
        UserEntity userEntity = new UserEntity();
        userEntity.setEmail("john@example.com");
        userEntity.setPassword("encodedPassword");

        when(userRepository.findByEmail(input.getEmail())).thenReturn(Optional.of(userEntity));

        authenticationService.authenticate(input);

        verify(authenticationManager).authenticate(new UsernamePasswordAuthenticationToken(input.getEmail(), input.getPassword()));
        UserEntity authenticatedUser = authenticationService.authenticate(input);
        assertNotNull(authenticatedUser);
        assertEquals("john@example.com", authenticatedUser.getEmail());
    }

    @Test
    void shouldThrowExceptionIfUserNotFoundDuringAuthentication() {
        LoginUserDto input = new LoginUserDto("nonexistent@example.com", "password123");

        when(userRepository.findByEmail(input.getEmail())).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> authenticationService.authenticate(input));
        verify(authenticationManager).authenticate(new UsernamePasswordAuthenticationToken(input.getEmail(), input.getPassword()));
    }

    @Test
    void shouldAuthenticateAndGenerateToken() {
        LoginUserDto input = new LoginUserDto("john@example.com", "password123");
        UserEntity userEntity = new UserEntity();
        userEntity.setEmail("john@example.com");
        userEntity.setPassword("encodedPassword");

        when(userRepository.findByEmail(input.getEmail())).thenReturn(Optional.of(userEntity));

        UserEntity authenticatedUser = authenticationService.authenticate(input);

        when(jwtService.generateToken(authenticatedUser)).thenReturn("sampleJwtToken");
        when(jwtService.getExpirationTime()).thenReturn(3600L);

        LoginResponseDto response = authenticationService.authenticateAndGetResponse(input);

        assertEquals("sampleJwtToken", response.getToken());
        assertEquals(3600L, response.getExpiresIn());
    }
}
