package com.animesh.notesapp.DTO;

import com.animesh.notesapp.Model.Note;

public class NoteResponse {
    private Long id;
    private String title;
    private String content;

    public NoteResponse(Note note) {
        this.id = note.getId();
        this.title = note.getTitle();
        this.content = note.getContent();
    }

    public Long getId() { return id; }
    public String getTitle() { return title; }
    public String getContent() { return content; }
}
