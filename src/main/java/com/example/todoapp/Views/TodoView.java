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
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Route("")
public class TodoView extends VerticalLayout {

    private final TodoRepo todoRepo;
    private final PersonaRepo personaRepo;

    private final VerticalLayout todosLayout = new VerticalLayout();
    private final ComboBox<Persona> personaCombo = new ComboBox<>();

    @Autowired
    public TodoView(TodoRepo todoRepo, PersonaRepo personaRepo) {
        this.todoRepo = todoRepo;
        this.personaRepo = personaRepo;

        TextField taskField = new TextField();
        Button addButton = new Button("Add");
        Button goToPersonas = new Button("Ir a Personas");

        // Navegación
        goToPersonas.addClickListener(e -> UI.getCurrent().navigate("personas"));

        personaCombo.setItems(personaRepo.findAll());
        personaCombo.setItemLabelGenerator(p -> p.getNombre() + " " + p.getApellido());
        personaCombo.addValueChangeListener(e -> actualizarTareas());

        addButton.addClickListener(click -> {
            Persona selectedPersona = personaCombo.getValue();
            if (selectedPersona != null && !taskField.isEmpty()) {
                Todo todo = new Todo(taskField.getValue());
                todo.setPersona(selectedPersona);
                todo = todoRepo.save(todo);
                taskField.clear();
                actualizarTareas(); // actualizamos la lista
            }
        });

        add(
                new H1("Todo"),
                new HorizontalLayout(personaCombo, goToPersonas),
                new HorizontalLayout(taskField, addButton),
                todosLayout
        );

        actualizarTareas();
    }

    private void actualizarTareas() {
        todosLayout.removeAll();
        Persona seleccionada = personaCombo.getValue();

        if (seleccionada == null) {
            // No hay selección, no mostramos nada
            return;
        }

        List<Todo> tareas = todoRepo.findAll().stream()
                .filter(t -> seleccionada.equals(t.getPersona()))
                .toList();

        tareas.forEach(todo -> todosLayout.add(createCheckbox(todo)));
    }

    private Component createCheckbox(Todo todo) {
        Checkbox checkbox = new Checkbox(todo.getTask(), todo.isDone(), e -> {
            todo.setDone(e.getValue());
            todoRepo.save(todo);
        });


        return new HorizontalLayout(checkbox);
    }
}
