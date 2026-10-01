package com.animesh.notesapp.DTO;

public interface ChunkMatch {
    Long getNoteId();
    String getTitle();
    String getChunkText();
    Integer getChunkOrder();
    Double getDistance();
}
