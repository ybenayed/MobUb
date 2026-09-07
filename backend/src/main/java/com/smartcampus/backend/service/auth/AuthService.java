package com.smartcampus.backend.service.auth;

import com.smartcampus.backend.dto.auth.AuthResponse;
import com.smartcampus.backend.dto.auth.LoginRequest;
import com.smartcampus.backend.dto.auth.RegisterRequest;
import com.smartcampus.backend.dto.auth.UserResponse;
import com.smartcampus.backend.entity.User;
import com.smartcampus.backend.exception.InvalidCredentialsException;
import com.smartcampus.backend.exception.UserAlreadyExistsException;
import com.smartcampus.backend.repository.UserRepository;
import com.smartcampus.backend.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

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
}