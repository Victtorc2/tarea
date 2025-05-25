/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.roles_permisos.controller;

import com.example.roles_permisos.model.Empleado;
import com.example.roles_permisos.model.Horario;
import com.example.roles_permisos.repository.EmpleadoRepository;
import com.example.roles_permisos.service.HorarioService;

import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 *
 * @author PedroCoronado
 */
@Controller
@RequestMapping("/horarios")
public class HorarioController {

    private final HorarioService horarioService;
    private final EmpleadoRepository empleadoRepository;

    public HorarioController(HorarioService horarioService, EmpleadoRepository empleadoRepository) {
        this.horarioService = horarioService;
        this.empleadoRepository = empleadoRepository;
    }

    @Secured("ROLE_ADMIN")
    @GetMapping 
    public String listarHorarios(Model model) {
        model.addAttribute("horarios", horarioService.listarTodosHorarios());
        return "horarios/listar";
    }

    @Secured("ROLE_ADMIN")
    @GetMapping("/asignar")
    public String mostrarFormularioAsignar(Model model) {
        model.addAttribute("horario", new Horario());
        model.addAttribute("empleados", empleadoRepository.findAll());
        return "horarios/asignar";
    }

    // Recibimos el Horario sin empleado completo y el empleadoId por separado
    @Secured("ROLE_ADMIN")
    @Transactional
    @PostMapping("/asignar")
    public String asignarHorario(@ModelAttribute Horario horario, @RequestParam Long empleadoId) {
        Empleado empleado = empleadoRepository.findById(empleadoId)
                .orElseThrow(() -> new IllegalArgumentException("Empleado inválido"));
        horario.setEmpleado(empleado);
        horarioService.asignarHorario(horario, empleadoId);
        return "redirect:/horarios";
    }

    @Secured({ "ROLE_COORDINADOR", "ROLE_SECRETARIO" })
    @GetMapping("/ver")
    public String listarHorariosCoordinador(Model model) {
        model.addAttribute("horarios", horarioService.listarTodosHorarios());
        return "horarios/listar";
    }

    @Secured("ROLE_COORDINADOR")
    @GetMapping("/editar/{id}")
    public String editarHorario(@PathVariable Long id, Model model) {
        Horario horario = horarioService.obtenerHorarioPorId(id);
        model.addAttribute("horario", horario);
        model.addAttribute("empleados", empleadoRepository.findAll()); // Para editar si cambias empleado
        return "horarios/editar";
    }

    @Secured("ROLE_COORDINADOR")
    @PostMapping("/editar/{id}")
    public String guardarEdicion(@PathVariable Long id, @ModelAttribute Horario horarioActualizado,
            @RequestParam Long empleadoId) {
        Empleado empleado = empleadoRepository.findById(empleadoId)
                .orElseThrow(() -> new IllegalArgumentException("Empleado inválido"));
        horarioActualizado.setEmpleado(empleado);
        horarioService.actualizarHorario(id, horarioActualizado);
        return "redirect:/horarios";
    }
}
