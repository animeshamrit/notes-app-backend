package com.animesh.notesapp.Repository;

import com.animesh.notesapp.DTO.ChunkMatch;
import com.animesh.notesapp.Model.NoteChunk;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface NoteChunkRepository extends JpaRepository<NoteChunk, Long> {
    @Modifying
    @Query(value = "DELETE FROM note_chunks WHERE note_id = :noteId", nativeQuery = true)
    void deleteChunksByNoteId(@Param("noteId") Long noteId);

    @Modifying
    @Query(value = """
            INSERT INTO note_chunks (note_id, chunk_order, chunk_text, embedding)
            VALUES (:noteId, :chunkOrder, :chunkText, CAST(:embedding AS vector))
            """, nativeQuery = true)
    void insertChunk(@Param("noteId") Long noteId,
                     @Param("chunkOrder") int chunkOrder,
                     @Param("chunkText") String chunkText,
                     @Param("embedding") String embedding);

    @Query(value = """
                    SELECT c.note_id AS \"noteId\", n.title AS \"title\", c.chunk_text AS \"chunkText\", c.chunk_order AS \"chunkOrder\", 
                    (c.embedding <=> CAST(:embedding AS vector)) AS \"distance\" FROM note_chunks c JOIN notes n ON n.id = c.note_id WHERE n.user_id = :userId 
                    ORDER BY c.embedding <=> CAST(:embedding AS vector) LIMIT :limit
                    """, nativeQuery = true)
    List<ChunkMatch> searchSimilar(@Param("userId") Long userId,
                               @Param("embedding") String embedding,
                               @Param("limit") int limit);
}