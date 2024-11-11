package com.hu3diger.order_processor.controllers;

import com.hu3diger.order_processor.dtos.LoginResponseDto;
import com.hu3diger.order_processor.dtos.LoginUserDto;
import com.hu3diger.order_processor.dtos.RegisterUserDto;
import com.hu3diger.order_processor.entities.UserEntity;
import com.hu3diger.order_processor.services.AuthenticationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/auth")
@RestController
public class AuthenticationController {

    private final AuthenticationService authenticationService;
    public AuthenticationController(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @PostMapping("/signup")
    public ResponseEntity<UserEntity> register(@RequestBody RegisterUserDto registerUserDto) {
        var registeredUser = authenticationService.signup(registerUserDto);

        return ResponseEntity.ok(registeredUser);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> authenticate(@RequestBody LoginUserDto loginUserDto) {
        var authenticatedUser = authenticationService.authenticateAndGetResponse(loginUserDto);

        return ResponseEntity.ok(authenticatedUser);
    }
}
