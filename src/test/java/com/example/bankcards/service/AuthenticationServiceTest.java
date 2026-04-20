package com.example.bankcards.service;

import com.example.bankcards.dto.SignInRequest;
import com.example.bankcards.security.JwtService;
import com.example.bankcards.service.impl.AuthenticationServiceImpl;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class AuthenticationServiceTest {

    @Test
    void sign_in_returns_token_from_jwt_service() {
        JwtService jwtService = Mockito.mock(JwtService.class);
        AuthenticationManager authenticationManager = Mockito.mock(AuthenticationManager.class);
        UserDetails principal = Mockito.mock(UserDetails.class);
        Authentication authentication = Mockito.mock(Authentication.class);

        when(authentication.getPrincipal()).thenReturn(principal);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(jwtService.generateToken(principal)).thenReturn("jwt-token");

        AuthenticationServiceImpl service = new AuthenticationServiceImpl(jwtService, authenticationManager);

        SignInRequest request = new SignInRequest();
        request.setUsername("admin");
        request.setPassword("admin");

        assertEquals("jwt-token", service.signIn(request).getToken());
    }
}
