package com.animesh.notesapp.Repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.animesh.notesapp.Model.Note;

import java.util.List;
import java.util.Optional;

public interface NoteRepository extends JpaRepository<Note, Long> {
    List<Note> findByUserId(Long userId);
    Optional<Note> findByIdAndUserId(Long id, Long userId);
}
