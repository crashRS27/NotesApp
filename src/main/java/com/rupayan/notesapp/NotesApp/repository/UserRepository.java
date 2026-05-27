package com.rupayan.notesapp.NotesApp.repository;


import com.rupayan.notesapp.NotesApp.entity.User;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
    
    // Spring automatically translates this to: 
    // SELECT * FROM users WHERE email = ?
    Optional<User> findByEmail(String email);
}