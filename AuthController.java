package com.controller;

import com.home4paws.dto.*;
import com.home4paws.entity.User;
import com.home4paws.repository.RoleRepository;
import com.home4paws.security.JwtUtils;
import com.home4paws.service.UserService;

import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    @Autowired private AuthenticationManager authenticationManager;
    @Autowired private UserService userService;
    @Autowired private JwtUtils jwtUtils;
    @Autowired private RoleRepository roleRepository;

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest req) {
        User saved = userService.registerNewUser(req);
        return ResponseEntity.status(HttpStatus.CREATED).body("User registered: " + saved.getUsername());
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody com.home4paws.dto.LoginRequest req) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(req.getUsername(), req.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);

        var principal = (org.springframework.security.core.userdetails.User)authentication.getPrincipal();
        var roles = principal.getAuthorities().stream().map(a -> a.getAuthority()).collect(Collectors.toList());
        String jwt = jwtUtils.generateToken(principal.getUsername(), roles);
        return ResponseEntity.ok().body(Map.of("token", jwt));
    }
}
