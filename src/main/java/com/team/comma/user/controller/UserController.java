package com.team.comma.user.controller;

import com.team.comma.common.dto.MessageResponse;
import com.team.comma.user.dto.LoginRequest;
import com.team.comma.user.dto.RegisterRequest;
import com.team.comma.user.dto.UserDetailRequest;
import com.team.comma.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.security.auth.login.AccountException;


@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/login")
    public ResponseEntity<MessageResponse> loginUser(@RequestBody LoginRequest login) throws AccountException {
        return userService.login(login);
    }

    @PostMapping("/register")
    public ResponseEntity<MessageResponse> registerUser(@RequestBody RegisterRequest register) throws AccountException {
        return ResponseEntity.ok().body(userService.register(register));
    }

    @PostMapping("/private-information")
    public ResponseEntity<MessageResponse> createUserInformation(
        @CookieValue(value = "accessToken", required = false) String accessToken
        , @RequestBody UserDetailRequest userDetail) throws AccountException {
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.createUserInformation(userDetail, accessToken));
    }

    @GetMapping("/user/information")
    public ResponseEntity<MessageResponse> getUserInfoByEmail(
        @CookieValue("accessToken") String accessToken) throws AccountException {
        return ResponseEntity.ok().body(userService.getUserByCookie(accessToken));
    }

    @GetMapping("/search/user")
    public ResponseEntity<MessageResponse> searchUserByNameAndNickName(@RequestParam String name
            , @CookieValue("accessToken") String accessToken) throws AccountException {
        return ResponseEntity.ok().body(userService.searchUserByNameAndNickName(name , accessToken));
    }

}
