package com.example.todoapp.repository;

import com.example.todoapp.model.Persona;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PersonaRepo extends JpaRepository<Persona, String> {
}