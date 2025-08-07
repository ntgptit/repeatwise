package com.repeatwise.service;

import com.repeatwise.dto.AuthResponseDto;
import com.repeatwise.dto.UserDto;

public interface AuthService {
    AuthResponseDto login(String emailOrUsername, String password);
    AuthResponseDto register(String name, String email, String password);
    void logout(String token);
    UserDto getCurrentUser(String token);
}
