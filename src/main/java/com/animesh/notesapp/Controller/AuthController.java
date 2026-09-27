package com.animesh.notesapp.Controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.animesh.notesapp.DTO.AuthResponse;
import com.animesh.notesapp.DTO.LoginRequest;
import com.animesh.notesapp.DTO.RefreshRequest;
import com.animesh.notesapp.DTO.SignupRequest;
import com.animesh.notesapp.Model.RefreshToken;
import com.animesh.notesapp.Model.User;
import com.animesh.notesapp.Repository.UserRepository;
import com.animesh.notesapp.Security.JwtUtil;
import com.animesh.notesapp.Service.RefreshTokenService;

import java.util.Optional;


@RestController
@RequestMapping("/auth")
public class AuthController {
    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final JwtUtil jwtUtil;

    private final RefreshTokenService refreshTokenService;

    AuthController(JwtUtil jwtUtil, UserRepository userRepository, PasswordEncoder passwordEncoder, RefreshTokenService refreshTokenService) {
        this.jwtUtil = jwtUtil;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.refreshTokenService = refreshTokenService;
    }

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody SignupRequest request) {
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            return ResponseEntity.badRequest().body("Email already registered");
        }

        User user = new User();
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword())); // hash before saving
        user.setName(request.getName());
        userRepository.save(user);

        String accessToken = jwtUtil.generateToken(user);
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(user.getId());
        return ResponseEntity.ok(new AuthResponse(accessToken, refreshToken.getToken()));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        // return userRepository.findByEmail(request.getEmail())
        //         .filter(user -> passwordEncoder.matches(request.getPassword(), user.getPassword()))
        //         .map(user -> {
        //             String accessToken = jwtUtil.generateToken(user);
        //             RefreshToken refreshToken = refreshTokenService.createRefreshToken(user.getId());
        //             return ResponseEntity.ok(new AuthResponse(accessToken, refreshToken.getToken()));
        //         })
        //         .orElse(ResponseEntity.status(401).body("Invalid email or password"));

       Optional<User> userOpt = userRepository.findByEmail(request.getEmail());

        if (userOpt.isPresent()) {
            User user = userOpt.get();
            if (passwordEncoder.matches(request.getPassword(), user.getPassword())) {
                String accessToken = jwtUtil.generateToken(user);
                RefreshToken refreshToken = refreshTokenService.createRefreshToken(user.getId());
                return ResponseEntity.ok(new AuthResponse(accessToken, refreshToken.getToken()));
            }
        }

        return ResponseEntity.status(401).body("Invalid email or password");
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(@RequestBody RefreshRequest request) {
        return refreshTokenService.findByToken(request.getRefreshToken())
                .map(storedToken -> {
                    if (refreshTokenService.isExpired(storedToken)) {
                        return ResponseEntity.status(401).body("Refresh token expired, please log in again");
                    }
                    User user = userRepository.findById(storedToken.getUserId()).orElseThrow();
                    String newAccessToken = jwtUtil.generateToken(user);
                    return ResponseEntity.ok(new AuthResponse(newAccessToken, storedToken.getToken()));
                })
                .orElse(ResponseEntity.status(401).body("Invalid refresh token"));
    }
}
