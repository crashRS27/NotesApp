package com.rupayan.notesapp.NotesApp.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.rupayan.notesapp.NotesApp.entity.Tag;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface TagRepository extends JpaRepository<Tag, UUID> {
    
    Optional<Tag> findByUserIdAndNameIgnoreCase(UUID userId, String name);
}
