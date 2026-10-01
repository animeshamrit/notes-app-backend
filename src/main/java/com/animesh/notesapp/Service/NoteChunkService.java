package com.animesh.notesapp.Service;

import com.animesh.notesapp.DTO.ChunkMatch;
import com.animesh.notesapp.Repository.NoteChunkRepository;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;

@Service
public class NoteChunkService {

    private final EmbeddingService embeddingService;
    private final NoteChunkRepository noteChunkRepository;
    private final TransactionTemplate transactionTemplate;

    public NoteChunkService(EmbeddingService embeddingService, NoteChunkRepository noteChunkRepository, TransactionTemplate transactionTemplate) {
        this.embeddingService = embeddingService;
        this.noteChunkRepository = noteChunkRepository;
        this.transactionTemplate = transactionTemplate;
    }

    List<String> splitIntoParagraphs(String content) {
        List<String> chunks = new ArrayList<>();
        if (content == null) return chunks;
        for (String p : content.split("\\R\\s*\\R")) {  
            String trimmed = p.trim();
            if (!trimmed.isEmpty()) chunks.add(trimmed);
        }
        return chunks;
    }

    private static final Logger log = LoggerFactory.getLogger(NoteChunkService.class);

    public void indexNoteSafely(Long noteId, String content) {
        try {
            List<String> paragraphs = splitIntoParagraphs(content);
            List<String> embeddings = new ArrayList<>();
            for (String p : paragraphs) {
                embeddings.add(embeddingService.generateEmbedding(p).toString());
            }
            transactionTemplate.executeWithoutResult(status -> {
            noteChunkRepository.deleteChunksByNoteId(noteId);
            for (int i = 0; i < paragraphs.size(); i++) {
                noteChunkRepository.insertChunk(noteId, i, paragraphs.get(i), embeddings.get(i));
                }
            });

        } catch (Exception e) {
            log.error("Failed to index note {}. It is saved but not searchable yet.", noteId, e);
        }
    }

    @Transactional
    public void deleteChunks(Long noteId) {
        noteChunkRepository.deleteChunksByNoteId(noteId);
    }

    public List<ChunkMatch> search(Long userId, String query, int limit) {
    String embedding = embeddingService.generateEmbedding(query).toString();
    return noteChunkRepository.searchSimilar(userId, embedding, limit);
}
}