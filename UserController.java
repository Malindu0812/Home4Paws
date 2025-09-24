package com.controller;

import com.home4paws.dto.UserDto;
import com.home4paws.entity.User;
import com.home4paws.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/users")
public class UserController {
    @Autowired private UserService userService;

    @GetMapping("/me")
    public ResponseEntity<?> getProfile(@AuthenticationPrincipal UserDetails userDetails) {
        Optional<User> user = userService.findByUsername(userDetails.getUsername());
        if (user.isEmpty()) return ResponseEntity.notFound().build();
        User u = user.get();
        UserDto dto = new UserDto(u.getId(), u.getUsername(), u.getEmail(), u.getFirstName(), u.getLastName());
        return ResponseEntity.ok(dto);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody com.home4paws.dto.RegisterRequest update, @AuthenticationPrincipal UserDetails userDetails) {
        // allow only owner or admin (admin check via annotation in other endpoints). Simple owner check:
        Optional<User> caller = userService.findByUsername(userDetails.getUsername());
        if (caller.isEmpty()) return ResponseEntity.status(401).build();
        if (!caller.get().getId().equals(id) && userDetails.getAuthorities().stream().noneMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
            return ResponseEntity.status(403).build();
        }
        var updated = userService.updateUser(id, update);
        return ResponseEntity.ok(new UserDto(updated.getId(), updated.getUsername(), updated.getEmail(), updated.getFirstName(), updated.getLastName()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id, @AuthenticationPrincipal UserDetails userDetails) {
        Optional<User> caller = userService.findByUsername(userDetails.getUsername());
        if (caller.isEmpty()) return ResponseEntity.status(401).build();
        if (!caller.get().getId().equals(id) && userDetails.getAuthorities().stream().noneMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
            return ResponseEntity.status(403).build();
        }
        userService.deleteUser(id);
        return ResponseEntity.ok(Map.of("message", "User deleted"));
    }
}
