package com.example.auth.dto;

import lombok.Data;
import java.util.List;

@Data
public class UserResponseDto {

    private Long id;
    private String login;
    private String email;
    private List<String> roles;
}