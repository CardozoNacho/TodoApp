package com.example.todoapp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.todoapp.model.Todo;

public interface TodoRepo extends JpaRepository<Todo, Integer> {
}