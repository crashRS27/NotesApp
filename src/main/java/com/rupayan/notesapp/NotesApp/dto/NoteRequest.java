package com.rupayan.notesapp.NotesApp.dto;


import java.util.Set;

public record NoteRequest(
    String title,
    String content,
    Set<String> tags
) {}