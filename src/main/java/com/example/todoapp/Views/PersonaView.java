package com.example.todoapp.Views;

import com.example.todoapp.model.Persona;
import com.example.todoapp.repository.PersonaRepo;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;
import org.springframework.beans.factory.annotation.Autowired;

@Route("personas")
public class PersonaView extends VerticalLayout {

    private final PersonaRepo repo;
    private final Grid<Persona> grid = new Grid<>(Persona.class);
    private final TextField dni = new TextField("DNI");
    private final TextField nombre = new TextField("Nombre");
    private final TextField apellido = new TextField("Apellido");
    private final NumberField edad = new NumberField("Edad");
    private final Button guardar = new Button("Guardar");
    private final Button volver = new Button("Volver a TODOs");

    @Autowired
    public PersonaView(PersonaRepo repo) {
        this.repo = repo;


        grid.setColumns("id", "dni", "nombre", "apellido", "edad");

        grid.addColumn(persona -> {
            if (persona.getTareas() != null) {
                return persona.getTareas().size();
            } else {
                return 0;
            }
        }).setHeader("Tareas");
        grid.setItems(repo.findAll());

        guardar.addClickListener(click -> {
            Persona p = new Persona();
            p.setDni(dni.getValue());
            p.setNombre(nombre.getValue());
            p.setApellido(apellido.getValue());
            p.setEdad(edad.getValue().intValue());
            repo.save(p);
            grid.setItems(repo.findAll());
            limpiarFormulario();
        });

        volver.addClickListener(e -> UI.getCurrent().navigate(""));

        var form = new HorizontalLayout(dni, nombre, apellido, edad, guardar);
        add(new H2("Personas"), form, volver, grid);
    }

    private void limpiarFormulario() {
        dni.clear();
        nombre.clear();
        apellido.clear();
        edad.clear();
    }
}
