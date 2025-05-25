/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.roles_permisos.controller;

/**
 *
 * @author PedroCoronado
 */
import com.example.roles_permisos.model.Tarea;
import com.example.roles_permisos.repository.EmpleadoRepository;
import com.example.roles_permisos.service.TareaService;

import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/tareas")
public class TareaController {

    private final TareaService tareaService;
    private final EmpleadoRepository empleadoRepository;

    public TareaController(TareaService tareaService, EmpleadoRepository empleadoRepository) {
        this.tareaService = tareaService;
        this.empleadoRepository = empleadoRepository;
    }

    @GetMapping
    public String listarTareas(Model model) {
        model.addAttribute("tareas", tareaService.findAll());
        return "tareas/listar";
    }

    @Secured("ROLE_ADMIN")
    @GetMapping("/asignar")
    public String mostrarFormularioAsignarTarea(Model model) {
        model.addAttribute("tarea", new Tarea());
        model.addAttribute("empleados", empleadoRepository.findAll());
        return "tareas/asignar";
    }

    @Secured("ROLE_ADMIN")
    @PostMapping("/asignar")
    public String asignarTarea(@ModelAttribute Tarea tarea) {
        tareaService.asignarTarea(tarea);
        return "redirect:/tareas";
    }

    @Secured({ "ROLE_ADMIN", "ROLE_COORDINADOR", "ROLE_SECRETARIO" })
    @GetMapping("/por-empleado/{id}")
    public String tareasPorEmpleado(@PathVariable Long id, Model model) {
        model.addAttribute("tareas", tareaService.findByEmpleadoId(id));
        return "tareas/consulta";
    }
}
