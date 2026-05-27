package com.rupayan.notesapp.NotesApp.dto;

import java.time.Instant;
import java.util.Set;
import java.util.UUID;

public record NoteResponse(
    UUID id,
    String title,
    String content,
    Set<String> tags,
    Instant createdAt
) {}