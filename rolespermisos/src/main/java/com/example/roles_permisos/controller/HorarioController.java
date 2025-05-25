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
@Controller
@RequestMapping("/horarios")
public class HorarioController {
    private final HorarioRepository horarioRepository;
    private final EmpleadoRepository empleadoRepository;

    public HorarioController(HorarioRepository horarioRepository, EmpleadoRepository empleadoRepository) {
        this.horarioRepository = horarioRepository;
        this.empleadoRepository = empleadoRepository;
    }

    // Método GET para mostrar formulario
    @Secured("ROLE_ADMIN")
@GetMapping("/asignar")
public String mostrarFormularioAsignar(Model model) {
    System.out.println("DEBUG: Entrando a GET /horarios/asignar"); // Log de diagnóstico
    
    List<Empleado> empleados = empleadoRepository.findAll();
    System.out.println("DEBUG: Número de empleados encontrados: " + empleados.size());
    
    model.addAttribute("horario", new Horario());
    model.addAttribute("empleados", empleados);
    return "horarios/asignar";
}

    // Método POST para procesar formulario
    @Secured("ROLE_ADMIN")
    @Transactional
    @PostMapping("/asignar")
    public String asignarHorario(@Valid @ModelAttribute("horario") Horario horario, 
                               BindingResult result,
                               @RequestParam Long empleadoId,
                               Model model) {
        
        if(result.hasErrors()) {
            model.addAttribute("empleados", empleadoRepository.findAll());
            return "horarios/asignar";
        }

        Empleado empleado = empleadoRepository.findById(empleadoId)
                .orElseThrow(() -> new IllegalArgumentException("Empleado no encontrado"));

        if (empleado.getTareas().size() >= 5) {
            model.addAttribute("error", "El empleado ya tiene 5 tareas asignadas");
            model.addAttribute("empleados", empleadoRepository.findAll());
            return "horarios/asignar";
        }

        horario.setEmpleado(empleado);
        horarioRepository.save(horario);
        return "redirect:/horarios";
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
