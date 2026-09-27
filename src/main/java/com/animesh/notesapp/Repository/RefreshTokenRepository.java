package com.animesh.notesapp.Repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.animesh.notesapp.Model.RefreshToken;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByToken(String token);
}
