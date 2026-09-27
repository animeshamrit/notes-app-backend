package com.animesh.notesapp.Repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.animesh.notesapp.Model.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
}