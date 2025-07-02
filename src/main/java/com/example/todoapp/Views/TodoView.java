package com.example.todoapp.Views;

import com.example.todoapp.model.Persona;
import com.example.todoapp.model.Todo;
import com.example.todoapp.repository.PersonaRepo;
import com.example.todoapp.repository.TodoRepo;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.util.List;

@Route("")
public class TodoView extends VerticalLayout {

    private final TodoRepo todoRepo;
    private final PersonaRepo personaRepo;

    private final VerticalLayout todosLayout = new VerticalLayout();
    private final ComboBox<Persona> personaCombo = new ComboBox<>();

    public TodoView(TodoRepo todoRepo, PersonaRepo personaRepo) {
        this.todoRepo = todoRepo;
        this.personaRepo = personaRepo;

        TextField taskField = new TextField();
        DatePicker fechaDeseadaPicker = new DatePicker("Fecha deseada");
        fechaDeseadaPicker.setEnabled(false);

        Button addButton = new Button("Add");
        Button goToPersonas = new Button("Ir a Personas");

        personaCombo.setItems(personaRepo.findAll());
        personaCombo.setItemLabelGenerator(p -> p.getNombre() + " " + p.getApellido());
        personaCombo.addValueChangeListener(e -> actualizarTareas());

        taskField.addValueChangeListener(e -> {
            fechaDeseadaPicker.setEnabled(!taskField.isEmpty());
        });

        goToPersonas.addClickListener(e -> UI.getCurrent().navigate("personas"));

        addButton.addClickListener(click -> {
            Persona selectedPersona = personaCombo.getValue();
            String taskText = taskField.getValue();
            LocalDate fechaDeseada = fechaDeseadaPicker.getValue();

            if (selectedPersona != null && !taskText.isEmpty() && fechaDeseada != null) {
                Todo todo = new Todo(taskText);
                todo.setFechaDeseada(fechaDeseada);
                todo.setPersona(selectedPersona);
                todoRepo.save(todo);
                taskField.clear();
                fechaDeseadaPicker.clear();
                fechaDeseadaPicker.setEnabled(false);
                actualizarTareas();
            }
        });

        HorizontalLayout formLayout = new HorizontalLayout(taskField, fechaDeseadaPicker, addButton);
        formLayout.setAlignItems(Alignment.END);

        add(
                new H1("Todo"),
                new HorizontalLayout(personaCombo, goToPersonas),
                formLayout,
                todosLayout
        );

        actualizarTareas();
    }

    private void actualizarTareas() {
        todosLayout.removeAll();
        Persona seleccionada = personaCombo.getValue();

        if (seleccionada == null) return;

        todoRepo.findAll().stream()
                .filter(t -> seleccionada.equals(t.getPersona()))
                .forEach(todo -> todosLayout.add(createCheckbox(todo)));
    }

    private Component createCheckbox(Todo todo) {
        Checkbox checkbox = new Checkbox(todo.getTask(), todo.isDone());
        checkbox.addValueChangeListener(e -> {
            todo.setDone(e.getValue());
            todoRepo.save(todo);
        });

        // Info de fechas
        Span fechas = new Span("(Creado: " + todo.getFechaCreacion() +
                ", Deseado: " + todo.getFechaDeseada() + ")");

        // Botón eliminar
        Button deleteBtn = new Button("❌", e -> {
            Persona persona = todo.getPersona();
            if (persona != null) {
                persona.getTareas().remove(todo); // Borra la relación
                personaRepo.save(persona);        // Guarda el cambio (cascada)
            }
            todoRepo.delete(todo); // Elimina la tarea
            actualizarTareas();    // Refresca la vista
        });
        deleteBtn.getStyle().set("color", "darkred");

        // Contenedor de todo
        HorizontalLayout layout = new HorizontalLayout(checkbox, fechas, deleteBtn);
        layout.setAlignItems(Alignment.CENTER);

        // Resaltar si está vencido
        if (todo.getFechaDeseada() != null && todo.getFechaDeseada().isBefore(LocalDate.now())) {
            layout.getStyle().set("background-color", "#ffd6d6"); // Rojo clarito
            layout.getStyle().set("border-radius", "10px");
        }

        return layout;
    }
}
