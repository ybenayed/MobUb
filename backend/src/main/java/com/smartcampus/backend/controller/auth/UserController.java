package com.smartcampus.backend.controller.auth;

import com.smartcampus.backend.dto.auth.ChangePasswordRequest;
import com.smartcampus.backend.dto.auth.UpdateUserRequest;
import com.smartcampus.backend.dto.auth.UserResponse;
import com.smartcampus.backend.service.auth.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * CRUD utilisateurs. Toutes les routes sont protegees (JWT requis),
 * conformement a la config dans SecurityConfig (anyRequest().authenticated()).
 */
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @GetMapping("/me")
    public ResponseEntity<UserResponse> getCurrentUser(Authentication authentication) {
        String username = authentication.getName();
        return ResponseEntity.ok(userService.getUserByUsername(username));
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserResponse> updateUser(@PathVariable Long id,
                                                     @Valid @RequestBody UpdateUserRequest request) {
        return ResponseEntity.ok(userService.updateUser(id, request));
    }

    /**
     * Self-service : modification du profil de l'utilisateur CONNECTE.
     * On identifie l'utilisateur via le JWT (Authentication), jamais via un id
     * fourni par le client -> evite qu'un utilisateur modifie le compte d'un autre
     * en changeant simplement l'id dans l'URL de PUT /api/users/{id}.
     */
    @PutMapping("/me")
    public ResponseEntity<UserResponse> updateCurrentUser(Authentication authentication,
                                                            @Valid @RequestBody UpdateUserRequest request) {
        String username = authentication.getName();
        return ResponseEntity.ok(userService.updateCurrentUser(username, request));
    }

    /**
     * Changement de mot de passe pour l'utilisateur CONNECTE (screen "Mon compte").
     * Necessite l'ancien mot de passe, contrairement au flux "mot de passe oublie"
     * (AuthController /forgot-password + /reset-password) qui utilise un code email.
     */
    @PutMapping("/me/password")
    public ResponseEntity<Void> changePassword(Authentication authentication,
                                                 @Valid @RequestBody ChangePasswordRequest request) {
        String username = authentication.getName();
        userService.changePassword(username, request);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}