package com.controller;

import com.home4paws.entity.User;
import com.home4paws.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {
    @Autowired private UserService userService;

    @GetMapping("/users")
    public List<?> listUsers() {
        return userService.findAllUsers().stream()
                .map(u -> Map.of("id", u.getId(), "username", u.getUsername(), "email", u.getEmail(), "roles", u.getRoles().stream().map(r->r.getName()).collect(Collectors.toList())))
                .collect(Collectors.toList());
    }

    @PostMapping("/users/{id}/role")
    public String assignRole(@PathVariable Long id, @RequestParam String role) {
        userService.assignRole(id, role);
        return "Role assigned";
    }

    @GetMapping("/stats")
    public Map<String, Object> stats() {
        long totalUsers = userService.findAllUsers().size();
        return Map.of("totalUsers", totalUsers);
    }
}

