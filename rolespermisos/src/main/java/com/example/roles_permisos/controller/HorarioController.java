/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.roles_permisos.controller;

import com.example.roles_permisos.model.Empleado;
import com.example.roles_permisos.model.Horario;
import com.example.roles_permisos.repository.EmpleadoRepository;
import com.example.roles_permisos.repository.HorarioRepository;
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
@Controller // Controlador para manejar horarios de empleados
@RequestMapping("/horarios") // Ruta base para las operaciones de horarios
public class HorarioController { // Controlador para manejar horarios de empleados

    private final HorarioRepository horarioRepository; // Repositorio para acceder a los datos de horarios
    private final EmpleadoRepository empleadoRepository; // Repositorio para acceder a los datos de empleados

    public HorarioController(HorarioRepository horarioRepository, EmpleadoRepository empleadoRepository) { // Constructor que inyecta los repositorios necesarios
        this.horarioRepository = horarioRepository; // Inicializa el repositorio de horarios
        this.empleadoRepository = empleadoRepository; // Inicializa el repositorio de empleados
    }

    // ADMIN puede asignar si el empleado tiene < 5 tareas
    @Secured("ROLE_ADMIN") // Solo ADMIN puede acceder
    @Transactional // pra que la base de datos se mantenga consistente  
    @PostMapping("/asignar") // Asignar horario a un empleado
    public String asignarHorario(@ModelAttribute Horario horario, @RequestParam Long empleadoId) { // Método para asignar un horario a un empleado
        Empleado empleado = empleadoRepository.findById(empleadoId) // Busca el empleado por ID en el repositorio y lo asigna a la variable empleado
                .orElseThrow(() -> new IllegalArgumentException("Empleado no encontrado")); // Si no se encuentra el empleado, lanza una excepción

        if (empleado.getTareas().size() >= 5) { // Verifica si el empleado ya tiene 5 tareas asignadas
            throw new IllegalStateException("Empleado ya tiene 5 tareas asignadas."); // Si es así, lanza una excepción
        }

        horario.setEmpleado(empleado); // al horario se le asigna el empleado
        horarioRepository.save(horario); // llama al metodo save del repositorio de horarios para guardar el horario en la base de datos
        return "redirect:/horarios"; //devuelve a la lista de horarios
    }

    // COORDINADOR y SECRETARIO: ver todos los horarios
    @Secured({"ROLE_COORDINADOR", "ROLE_SECRETARIO"}) // Solo COORDINADOR y SECRETARIO pueden acceder
    @GetMapping // tipo de peticion GET para obtener todos los horarios
    public String listarHorarios(Model model) { // metodo listarHorarios que recibe un objeto de tipo Model
        model.addAttribute("horarios", horarioRepository.findAll()); // llama al metodo findAll del repositorio de horarios para obtener todos los horarios
        return "horarios/listar"; // devuelve a la vista listar
    }

    // COORDINADOR puede editar
    @Secured("ROLE_COORDINADOR") // Solo COORDINADOR puede acceder
    @GetMapping("/editar/{id}") // tipo de peticion GET para editar un horario por ID de horario
    public String editarHorario(@PathVariable Long id, Model model) { // metodo editarHorario que recibe un objeto de tipo Model
        Horario horario = horarioRepository.findById(id) // a la variable horario se le asigna el horario que se busca por ID en el repositorio
                .orElseThrow(() -> new IllegalArgumentException("ID inválido: " + id)); // Si no se encuentra el horario, lanza una excepción
        model.addAttribute("horario", horario); //al modelo se le agrega atributo horario
        return "horarios/editar"; // devuelve a la vista editar
    }

    @Secured("ROLE_COORDINADOR") // Solo COORDINADOR puede acceder
    @PostMapping("/editar/{id}") // tipo de peticion POST para guardar la edicion de un horario por ID de horario
    public String guardarEdicion(@PathVariable Long id, @ModelAttribute Horario horarioActualizado) { // metodo guardarEdicion que recibe un objeto de tipo model que tiene atributo horarioActualizado
        Horario horario = horarioRepository.findById(id) // a la variable horario se le asigna el horario que se busca por ID en el repositorio
                .orElseThrow(() -> new IllegalArgumentException("ID inválido")); // Si no se encuentra el horario, lanza una excepción

        horario.setDia(horarioActualizado.getDia());    // actualiza el dia del horario y se guarda como objeto horario actualizado
        horario.setHoraInicio(horarioActualizado.getHoraInicio()); // actualiza la hora de inicio del horario y se guarda como objeto horario actualizado
        horario.setHoraFin(horarioActualizado.getHoraFin()); // actualiza la hora de fin del horario y se guarda como objeto horario actualizado

        horarioRepository.save(horario); // llama al metodo save del repositorio de horarios para guardar el horario actualizado en la base de datos
        return "redirect:/horarios"; // devuelve a la lista de horarios
    }
}
