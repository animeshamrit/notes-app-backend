package com.animesh.notesapp.Controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import com.animesh.notesapp.DTO.NoteResponse;
import com.animesh.notesapp.Model.Note;
import com.animesh.notesapp.Repository.NoteRepository;
import com.animesh.notesapp.Repository.UserRepository;

import java.util.List;

@RestController
@RequestMapping("/notes")
public class NoteController {

    private final NoteRepository noteRepository;

    private final UserRepository userRepository;

    NoteController(NoteRepository noteRepository, UserRepository userRepository) {
        this.noteRepository = noteRepository;
        this.userRepository = userRepository;
    }

    private Long getCurrentUserId() {
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    return (Long) auth.getPrincipal();
}

    @PostMapping
    public NoteResponse createNote(@RequestBody Note note) {
        note.setUser(userRepository.getReferenceById(getCurrentUserId()));
        Note saved = noteRepository.save(note);
        return new NoteResponse(saved);
    }

    @GetMapping
    public List<NoteResponse> getAllNotes() {
        return noteRepository.findByUserId(getCurrentUserId())
                .stream()
                .map(NoteResponse::new)
                .toList();
    }

    @GetMapping("/{id}")
    public ResponseEntity<NoteResponse> getNoteById(@PathVariable Long id) {
        return noteRepository.findByIdAndUserId(id, getCurrentUserId())
                .map(NoteResponse::new)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<NoteResponse> updateNote(@PathVariable Long id, @RequestBody Note updatedNote) {
        return noteRepository.findByIdAndUserId(id, getCurrentUserId())
                .map(existing -> {
                    existing.setTitle(updatedNote.getTitle());
                    existing.setContent(updatedNote.getContent());
                    return ResponseEntity.ok(new NoteResponse(noteRepository.save(existing)));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteNote(@PathVariable Long id) {
        if (!noteRepository.findByIdAndUserId(id, getCurrentUserId()).isPresent()) {
            return ResponseEntity.notFound().build();
        }
        noteRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}

/*import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

@RestController
@RequestMapping("/notes")
public class NoteController {
    // Temporary in-memory "database" — just a list, resets every restart
    private final List<Note> notes = new ArrayList<>();
    private final AtomicLong idCounter = new AtomicLong();

    @PostMapping
    public Note createNote(@RequestBody Note note) {
        note.setId(idCounter.incrementAndGet());
        notes.add(note);
        return note;
    }

    @GetMapping
    public List<Note> getAllNotes() {
        return notes;
    }

    @GetMapping("/{id}")
    public ResponseEntity<Note> getNoteById(@PathVariable Long id) {
        return notes.stream()
                .filter(n -> n.getId().equals(id))
                .findFirst()
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build()); // we'll handle "not found" properly later
    }

    @PutMapping("/{id}")
    public ResponseEntity<Note> updateNote(@PathVariable Long id, @RequestBody Note updatedNote) {
        Note existing = notes.stream()
                .filter(n -> n.getId().equals(id))
                .findFirst()
                .orElse(null);

        if (existing == null) {
            return ResponseEntity.notFound().build(); // we'll fix this properly below with real error handling
        }

        existing.setTitle(updatedNote.getTitle());
        existing.setContent(updatedNote.getContent());
        return ResponseEntity.ok(existing);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteNote(@PathVariable Long id) {
        boolean removed = notes.removeIf(n -> n.getId().equals(id));

        if (!removed) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.noContent().build();
    }
    
}*/


