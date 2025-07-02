package com.example.todoapp.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.JoinColumn;

import java.time.LocalDate;

@Data
@Entity
@NoArgsConstructor
public class Todo {

    @ManyToOne
    @JoinColumn(name = "persona_dni") // Cambia porque la PK ahora es dni
    private Persona persona;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String task;
    private boolean done;

    private LocalDate fechaCreacion;
    private LocalDate fechaDeseada;

    public Todo(String task) {
        this.task = task;
        this.fechaCreacion = LocalDate.now();
    }
}