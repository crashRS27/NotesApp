package com.rupayan.notesapp.NotesApp.controller;


import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.rupayan.notesapp.NotesApp.dto.NoteRequest;
import com.rupayan.notesapp.NotesApp.dto.NoteResponse;
import com.rupayan.notesapp.NotesApp.service.NoteService;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/notes")
@RequiredArgsConstructor
public class NoteController {

    private final NoteService noteService;

    // POST /api/v1/notes?userId={uuid}
    @PostMapping
    public ResponseEntity<NoteResponse> createNote(
            @RequestParam UUID userId,
            @RequestBody NoteRequest request) {
        
        NoteResponse response = noteService.createNote(userId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // GET /api/v1/notes?userId={uuid}
    @GetMapping
    public ResponseEntity<List<NoteResponse>> getNotes(@RequestParam UUID userId) {
        return ResponseEntity.ok(noteService.getAllNotes(userId));
    }

    // DELETE /api/v1/notes/{noteId}
    @DeleteMapping("/{noteId}")
    public ResponseEntity<Void> deleteNote(@PathVariable UUID noteId) {
        noteService.softDeleteNote(noteId);
        return ResponseEntity.noContent().build();
    }
    
    //UPDATE /api/v1/notes/{id}?userId={uuid}
    @PutMapping("/{id}")
    public ResponseEntity<NoteResponse> updateNote(
            @PathVariable UUID id,
            @RequestParam String userId,
            @RequestBody NoteRequest request) {
        
        // The service handles the mapping and hands us back the finished DTO
        NoteResponse updatedNote = noteService.updateNote(id, userId, request);
        return ResponseEntity.ok(updatedNote);
    }
}