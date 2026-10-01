package com.animesh.notesapp.Model;

import com.pgvector.PGvector;
import jakarta.persistence.*;

@Entity
@Table(name = "note_chunks")
public class NoteChunk {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "note_id", nullable = false)
    private Long noteId;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String chunkText;

    @Column(name = "chunk_order", nullable = false)
    private Integer chunkOrder;

    @Column(columnDefinition = "vector(768)")
    private PGvector embedding;

    public NoteChunk() {}

    public NoteChunk(Long noteId, String chunkText, Integer chunkOrder, PGvector embedding) {
        this.noteId = noteId;
        this.chunkText = chunkText;
        this.chunkOrder = chunkOrder;
        this.embedding = embedding;
    }

    public Long getId() { return id; }
    public Long getNoteId() { return noteId; }
    public String getChunkText() { return chunkText; }
    public Integer getChunkOrder() { return chunkOrder; }
    public PGvector getEmbedding() { return embedding; }
    public void setEmbedding(PGvector embedding) { this.embedding = embedding; }
}