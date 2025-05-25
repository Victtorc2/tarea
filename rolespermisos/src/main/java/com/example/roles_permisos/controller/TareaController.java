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

@Controller // Controlador para manejar tareas de empleados
@RequestMapping("/tareas") // Ruta base para las operaciones de tareas
public class TareaController { // Controlador para manejar tareas de empleados

    private final TareaRepository tareaRepository;  // Repositorio para acceder a los datos de tareas
    private final EmpleadoRepository empleadoRepository; // Repositorio para acceder a los datos de empleados

    public TareaController(TareaRepository tareaRepository, EmpleadoRepository empleadoRepository) { // Constructor que inyecta los repositorios necesarios
        this.tareaRepository = tareaRepository; // Inicializa el repositorio de tareas
        this.empleadoRepository = empleadoRepository; // Inicializa el repositorio de empleados
    }

    // Listar tareas (Accesible por todos los roles)
    @GetMapping // tipo de peticion GET para obtener todas las tareas
    public String listarTareas(Model model) { // metodo listarTareas que recibe un objeto de tipo Model
        model.addAttribute("tareas", tareaRepository.findAll());
        return "tareas/listar";
    }      

    // Asignar tarea (Solo ADMIN, con validación transaccional)
    @Secured("ROLE_ADMIN") // Solo ADMIN puede acceder
    @Transactional // para que la base de datos se mantenga consistente
    @PostMapping("/asignar") // metodo para asignar una tarea a un empleado
    public String asignarTarea(@RequestParam Long empleadoId, @ModelAttribute Tarea tarea) { // metodo que recibe un objeto de tipo Tarea  con los parametros id del empleado y la tarea
        Empleado empleado = empleadoRepository.findById(empleadoId)  // Busca el empleado por ID en el repositorio y lo asigna a la variable empleado
                .orElseThrow(() -> new IllegalArgumentException("Empleado no encontrado")); // Si no se encuentra el empleado, lanza una excepción
        if (empleado.getTareas().size() >= 5) { // Verifica si el empleado ya tiene 5 tareas asignadas
            throw new IllegalStateException("El empleado ya tiene 5 tareas asignadas."); // Si es así, lanza una excepción
        }

        tarea.setEmpleado(empleado); // al tarea se le asigna el empleado
        tareaRepository.save(tarea); // llama al metodo save del repositorio de tareas para guardar la tarea en la base de datos
        return "redirect:/tareas"; // devuelve a la lista de tareas
    }

    // Consultas JPQL (Ejemplo: Tareas por empleado) 
    @Secured({"ROLE_ADMIN", "ROLE_COORDINADOR", "ROLE_SECRETARIO"}) // Solo ADMIN, COORDINADOR y SECRETARIO pueden acceder
    @GetMapping("/por-empleado/{id}") // tipo de peticion GET para obtener las tareas por empleado
    public String tareasPorEmpleado(@PathVariable Long id, Model model) { // metodo que recibe un objeto de tipo Model
        model.addAttribute("tareas", tareaRepository.findTareasByEmpleadoId(id)); // llama al metodo findTareasByEmpleadoId del repositorio de tareas para obtener las tareas por empleado
        return "tareas/consulta"; // devuelve a la vista de consulta
    }
}
