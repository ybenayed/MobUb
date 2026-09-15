package com.smartcampus.backend.controller.auth;

import com.smartcampus.backend.dto.auth.UserResponse;
import com.smartcampus.backend.dto.auth.UserStatsDTO;
import com.smartcampus.backend.service.auth.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Endpoints reserves a l'admin. L'admin ne modifie JAMAIS les infos
 * d'un utilisateur : consultation (liste, stats) et suppression uniquement.
 */
@RestController
@RequestMapping("/api/admin/users")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminUserController {

    private final UserService userService;

    @GetMapping
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    @GetMapping("/stats")
    public ResponseEntity<UserStatsDTO> getStats() {
        return ResponseEntity.ok(userService.getUserStats());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}