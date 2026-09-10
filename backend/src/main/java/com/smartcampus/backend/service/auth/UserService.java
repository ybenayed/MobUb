package com.smartcampus.backend.service.auth;

import com.smartcampus.backend.dto.auth.ChangePasswordRequest;
import com.smartcampus.backend.dto.auth.UpdateUserRequest;
import com.smartcampus.backend.dto.auth.UserResponse;
import com.smartcampus.backend.entity.User;
import com.smartcampus.backend.exception.InvalidCredentialsException;
import com.smartcampus.backend.exception.ResourceNotFoundException;
import com.smartcampus.backend.exception.UserAlreadyExistsException;
import com.smartcampus.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public List<UserResponse> getAllUsers() {
        return userRepository.findAll().stream()
                .map(UserResponse::fromEntity)
                .toList();
    }

    public UserResponse getUserById(Long id) {
        User user = findUserOrThrow(id);
        return UserResponse.fromEntity(user);
    }

    public UserResponse getUserByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Utilisateur introuvable : " + username));
        return UserResponse.fromEntity(user);
    }

    @Transactional
    public UserResponse updateUser(Long id, UpdateUserRequest request) {
        User user = findUserOrThrow(id);
        return applyProfileUpdate(user, request);
    }

    /**
     * Meme logique que updateUser(id, ...), mais l'utilisateur est retrouve via
     * son username (issu du JWT), jamais via un id fourni par le client.
     * Utilisee par PUT /api/users/me (screen "Mon compte").
     */
    @Transactional
    public UserResponse updateCurrentUser(String username, UpdateUserRequest request) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Utilisateur introuvable : " + username));
        return applyProfileUpdate(user, request);
    }

    private UserResponse applyProfileUpdate(User user, UpdateUserRequest request) {
        if (request.getEmail() != null && !request.getEmail().equalsIgnoreCase(user.getEmail())) {
            if (userRepository.existsByEmail(request.getEmail())) {
                throw new UserAlreadyExistsException("Un compte existe deja avec cet email : "
                        + request.getEmail());
            }
            user.setEmail(request.getEmail());
        }
        if (request.getPhoneNumber() != null) {
            user.setPhoneNumber(request.getPhoneNumber());
        }
        if (request.getNationality() != null) {
            user.setNationality(request.getNationality());
        }
        if (request.getResidence() != null) {
            user.setResidence(request.getResidence());
        }

        User saved = userRepository.save(user);
        return UserResponse.fromEntity(saved);
    }

    /**
     * Changement de mot de passe depuis le screen "Mon compte" : verifie
     * l'ancien mot de passe avant d'ecrire le nouveau (hashe) en base.
     */
    @Transactional
    public void changePassword(String username, ChangePasswordRequest request) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Utilisateur introuvable : " + username));

        if (!passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
            throw new InvalidCredentialsException("Ancien mot de passe incorrect");
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
    }

    @Transactional
    public void deleteUser(Long id) {
        User user = findUserOrThrow(id);
        userRepository.delete(user);
    }

    private User findUserOrThrow(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Utilisateur introuvable avec l'id : " + id));
    }
}