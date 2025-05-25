/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.roles_permisos.controller;

/**
 *
 * @author PedroCoronado
 */
import com.example.roles_permisos.model.Empleado;
import com.example.roles_permisos.model.Tarea;
import com.example.roles_permisos.repository.EmpleadoRepository;
import com.example.roles_permisos.repository.TareaRepository;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller //anotacion que define esta clase como controlador
@RequestMapping("/tareas") 
public class TareaController {

    private final TareaRepository tareaRepository;
    private final EmpleadoRepository empleadoRepository;

    public TareaController(TareaRepository tareaRepository, EmpleadoRepository empleadoRepository) {
        this.tareaRepository = tareaRepository;
        this.empleadoRepository = empleadoRepository;
    }

    // Listar tareas (Accesible por todos los roles)
    @GetMapping
    public String listarTareas(Model model) {
        model.addAttribute("tareas", tareaRepository.findAll());
        return "tareas/listar";
    }

    // Asignar tarea (Solo ADMIN, con validación transaccional)
    @Secured("ROLE_ADMIN")
    @Transactional
    @PostMapping("/asignar")
    public String asignarTarea(@RequestParam Long empleadoId, @ModelAttribute Tarea tarea) {
        Empleado empleado = empleadoRepository.findById(empleadoId)
                .orElseThrow(() -> new IllegalArgumentException("Empleado no encontrado"));
        if (empleado.getTareas().size() >= 5) {
            throw new IllegalStateException("El empleado ya tiene 5 tareas asignadas.");
        }

        tarea.setEmpleado(empleado);
        tareaRepository.save(tarea);
        return "redirect:/tareas";
    }

    // Consultas JPQL (Ejemplo: Tareas por empleado)
    @Secured({"ROLE_ADMIN", "ROLE_COORDINADOR", "ROLE_SECRETARIO"})
    @GetMapping("/por-empleado/{id}")
    public String tareasPorEmpleado(@PathVariable Long id, Model model) {
        model.addAttribute("tareas", tareaRepository.findTareasByEmpleadoId(id));
        return "tareas/consulta";
    }
}
