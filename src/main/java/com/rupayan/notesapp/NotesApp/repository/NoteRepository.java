package com.rupayan.notesapp.NotesApp.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.rupayan.notesapp.NotesApp.entity.Note;

import java.util.List;
import java.util.UUID;

@Repository
public interface NoteRepository extends JpaRepository<Note, UUID> {

    // Fetch all active notes for a specific user, newest first
    List<Note> findAllByUserIdOrderByUpdatedAtDesc(UUID userId);

    // Native PostgreSQL Full-Text Search
    @Query(value = """
            SELECT * FROM notes 
            WHERE user_id = :userId 
            AND search_vector @@ plainto_tsquery('english', :query) 
            AND deleted_at IS NULL
            """, 
            nativeQuery = true)
    List<Note> searchNotesByText(@Param("userId") UUID userId, @Param("query") String query);
}