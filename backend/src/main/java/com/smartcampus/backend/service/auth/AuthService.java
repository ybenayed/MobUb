package com.smartcampus.backend.service.auth;

import com.smartcampus.backend.dto.auth.AuthResponse;
import com.smartcampus.backend.dto.auth.ForgotPasswordRequest;
import com.smartcampus.backend.dto.auth.LoginRequest;
import com.smartcampus.backend.dto.auth.RegisterRequest;
import com.smartcampus.backend.dto.auth.ResetPasswordRequest;
import com.smartcampus.backend.dto.auth.UserResponse;
import com.smartcampus.backend.entity.User;
import com.smartcampus.backend.exception.InvalidCredentialsException;
import com.smartcampus.backend.exception.ResourceNotFoundException;
import com.smartcampus.backend.exception.UserAlreadyExistsException;
import com.smartcampus.backend.repository.UserRepository;
import com.smartcampus.backend.security.JwtUtil;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final EmailService emailService;

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new UserAlreadyExistsException(
                    "Ce nom d'utilisateur est deja pris : " + request.getUsername());
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new UserAlreadyExistsException(
                    "Un compte existe deja avec cet email : " + request.getEmail());
        }

        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .phoneNumber(request.getPhoneNumber())
                .nationality(request.getNationality())
                .residence(request.getResidence())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(User.Role.USER)
                .build();

        User saved = userRepository.save(user);

        String token = jwtUtil.generateToken(saved.getUsername());

        return AuthResponse.builder()
                .token(token)
                .user(UserResponse.fromEntity(saved))
                .build();
    }

    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new InvalidCredentialsException("Identifiants incorrects"));

        // Verifie que l'email correspond bien au compte (coherent avec le screen de login)
        if (!user.getEmail().equalsIgnoreCase(request.getEmail())) {
            throw new InvalidCredentialsException("Identifiants incorrects");
        }

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new InvalidCredentialsException("Identifiants incorrects");
        }

        String token = jwtUtil.generateToken(user.getUsername());

        return AuthResponse.builder()
                .token(token)
                .user(UserResponse.fromEntity(user))
                .build();
    }
    @Transactional
    public void requestPasswordReset(ForgotPasswordRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("Aucun utilisateur trouvé avec cet email"));

        // Génère un code aléatoire à 6 chiffres
        String code = String.format("%06d", new java.util.Random().nextInt(900000) + 100000);
        
        user.setResetCode(code);
        user.setResetCodeExpiration(LocalDateTime.now().plusMinutes(15));
        userRepository.save(user);

        emailService.sendResetCodeEmail(user.getEmail(), code);
    }

    @Transactional
    public void resetPassword(ResetPasswordRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("Aucun utilisateur trouvé avec cet email"));

        if (user.getResetCode() == null || !user.getResetCode().equals(request.getCode())) {
            throw new InvalidCredentialsException("Code invalide");
        }

        if (user.getResetCodeExpiration() == null || user.getResetCodeExpiration().isBefore(LocalDateTime.now())) {
            throw new InvalidCredentialsException("Code expiré");
        }

        // Mise à jour du mot de passe + suppression du code utilisé
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        user.setResetCode(null);
        user.setResetCodeExpiration(null);
        userRepository.save(user);
    }
}