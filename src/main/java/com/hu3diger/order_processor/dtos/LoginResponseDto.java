package com.hu3diger.order_processor.dtos;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginResponseDto {
    private String token;

    private long expiresIn;
}
