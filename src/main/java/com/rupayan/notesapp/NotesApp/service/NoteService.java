package com.rupayan.notesapp.NotesApp.service;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.rupayan.notesapp.NotesApp.dto.NoteRequest;
import com.rupayan.notesapp.NotesApp.dto.NoteResponse;
import com.rupayan.notesapp.NotesApp.entity.Note;
import com.rupayan.notesapp.NotesApp.entity.Tag;
import com.rupayan.notesapp.NotesApp.entity.User;
import com.rupayan.notesapp.NotesApp.repository.NoteRepository;
import com.rupayan.notesapp.NotesApp.repository.TagRepository;
import com.rupayan.notesapp.NotesApp.repository.UserRepository;

import java.time.Instant;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Getter
@Setter
@RequiredArgsConstructor
public class NoteService {

    private final NoteRepository noteRepository;
    private final TagRepository tagRepository;
    private final UserRepository userRepository;

    @Transactional
    public NoteResponse createNote(UUID userId, NoteRequest request) {
        
    	
    	
    	// 1. Verify the user exists
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
    	
    	/*
    	User user = userRepository.findById(userId).orElseGet(() -> {
            User dummyUser = new User();
            dummyUser.setEmail("local-" + UUID.randomUUID() + "@test.com");
            dummyUser.setPasswordHash("dummy_hash");
            return userRepository.save(dummyUser);
        });
        */
    	
    	
//		User user = userRepository.findById(userId)
        // 2. Build the Note entity
        Note note = new Note();
        note.setUser(user);
        note.setTitle(request.title());
        note.setContent(request.content());

        // 3. Process Tags (Create if they don't exist)
        if (request.tags() != null) {
            for (String tagName : request.tags()) {
                String cleanName = tagName.trim().toLowerCase();
                Tag tag = tagRepository.findByUserIdAndNameIgnoreCase(userId, cleanName)
                        .orElseGet(() -> {
                            Tag newTag = new Tag();
                            newTag.setUser(user);
                            newTag.setName(cleanName);
                            return tagRepository.save(newTag);
                        });
                note.addTag(tag);
            }
        }

        // 4. Save and return mapped response
        Note savedNote = noteRepository.saveAndFlush(note);
        return mapToResponse(savedNote);
    }

    public List<NoteResponse> getAllNotes(UUID userId) {
        return noteRepository.findAllByUserIdOrderByUpdatedAtDesc(userId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public void softDeleteNote(UUID noteId) {
        Note note = noteRepository.findById(noteId)
                .orElseThrow(() -> new RuntimeException("Note not found"));
        note.setDeletedAt(Instant.now());
        noteRepository.save(note);
    }

    // Manual mapper (We will upgrade to MapStruct later)
    private NoteResponse mapToResponse(Note note) {
        Set<String> tagNames = note.getTags().stream()
                .map(Tag::getName)
                .collect(Collectors.toSet());

        return new NoteResponse(
                note.getId(),
                note.getTitle(),
                note.getContent(),
                tagNames,
                note.getCreatedAt()
        );
    }
    
    public NoteResponse updateNote(UUID noteId, String userId, NoteRequest request) {
        // 1. Find the existing note
        Note note = noteRepository.findById(noteId)
            .orElseThrow(() -> new RuntimeException("Note not found"));
            
        // 2. Security Check: Make sure the user actually owns this note
        if (!note.getUser().getId().toString().equalsIgnoreCase(userId)) {
            throw new RuntimeException("Unauthorized to edit this note");
        }
        
        // 3. Update the fields
        note.setTitle(request.title());
        note.setContent(request.content());
        
        // Note: If you are mapping tags here, update them according to your JPA setup!
        
        // 4. Save and return mapped response
        Note savedNote = noteRepository.save(note);
        return mapToResponse(savedNote);
    }
}