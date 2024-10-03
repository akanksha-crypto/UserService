package com.example.userservice.controllers;

import com.example.userservice.dtos.*;
import com.example.userservice.dtos.ResponseStatus;
import com.example.userservice.models.Token;
import com.example.userservice.models.User;
import com.example.userservice.repositories.TokenRepository;
import com.example.userservice.services.UserService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
public class UserController {
    private UserService userService;
    private final TokenRepository tokenRepository;
    public UserController(UserService userService,
                          TokenRepository tokenRepository){
        this.userService = userService;
        this.tokenRepository = tokenRepository;
    }

    @PostMapping("/signup")
    public UserDto signup(@RequestBody SignupRequestDto signupRequestDto){
        User user = userService.signup(signupRequestDto.getName(),
                signupRequestDto.getEmail(),
                signupRequestDto.getPassword());
        return UserDto.convertToUserDtoFromUser(user);
    }

    @PostMapping("/login")
    public LoginResponseDto login(@RequestBody LoginRequestDto loginRequestDto){
        LoginResponseDto loginResponseDto = new LoginResponseDto();
        try {
            Token token = userService.loginUser(loginRequestDto.getEmail(),
                    loginRequestDto.getPassword());

            loginResponseDto.setToken(token.getValue());
            loginResponseDto.setResponseStatus(ResponseStatus.SUCCESS);
        } catch (Exception e){
            loginResponseDto.setResponseStatus(ResponseStatus.FAILURE);
        }
        return loginResponseDto;
    }

    @GetMapping("/validate/{token}")
    public UserDto validateToken(@PathVariable String token){
        User user = userService.validate(token);
        return UserDto.convertToUserDtoFromUser(user);
    }

    @PatchMapping("/logout")
    public void logout(@RequestBody LogoutRequestDto logoutRequestDto){
        userService.logout(logoutRequestDto.getToken());
    }
}
