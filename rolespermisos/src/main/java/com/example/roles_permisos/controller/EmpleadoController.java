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
import com.example.roles_permisos.model.Rol;
import com.example.roles_permisos.service.EmpleadoService;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/empleados")
public class EmpleadoController {

    private final EmpleadoService empleadoService;

    public EmpleadoController(EmpleadoService empleadoService) {
        this.empleadoService = empleadoService; //llama al constructor de la clase EmpleadoService
    }

    // Listar empleados (Accesible por ADMIN y COORDINADOR)
    @Secured({"ROLE_ADMIN", "ROLE_COORDINADOR"}) // Solo ADMIN y COORDINADOR pueden acceder
    @GetMapping //tipo de peticion GET
    public String listarEmpleados(Model model) { //metodo que recibe un objeto de tipo Model
        model.addAttribute("empleados", empleadoService.findAll()); //llama al metodo findAll de la clase EmpleadoService
        return "empleados/listar"; //devuelve a esa ruta 
    }

    // Mostrar formulario de creación (Solo ADMIN)
    @Secured("ROLE_ADMIN") // Solo ADMIN puede acceder
    @GetMapping("/crear") //getMapping para devolver el formulario de creacion
    public String mostrarFormularioCreacion(Model model) { //metodo que recibe un objeto de tipo Model
        model.addAttribute("empleado", new Empleado()); //crea un nuevo objeto de tipo Empleado
        model.addAttribute("roles", Rol.values()); //agrega los roles al modelo
        return "empleados/crear"; //devuelve a la ruta empleados/crear
    }

    // Procesar creación de empleado (Solo ADMIN)
    @Secured("ROLE_ADMIN") // Solo ADMIN puede acceder
    @PostMapping("/crear") //postMapping es para agregar un nuevo empleado
    public String crearEmpleado(@ModelAttribute Empleado empleado) { //metodo que recibe un objeto de tipo Empleado
        empleadoService.registrarEmpleado(empleado);    //llama al metodo registrarEmpleado de la clase EmpleadoService
        return "redirect:/empleados"; //redirecciona a la lista de empleados
    }

    // Mostrar formulario de edición (Solo ADMIN)
    @Secured("ROLE_ADMIN")
    @GetMapping("/editar/{id}")
    public String mostrarFormularioEdicion(@PathVariable Long id, Model model) {
        Empleado empleado = empleadoService.findById(id);
        model.addAttribute("empleado", empleado);
        model.addAttribute("roles", Rol.values());
        return "empleados/editar";
    }

    // Procesar actualización de empleado (Solo ADMIN)
    @Secured("ROLE_ADMIN")
    @PostMapping("/editar/{id}")
    public String actualizarEmpleado(@PathVariable Long id, @ModelAttribute Empleado empleado) {
        empleado.setId(id);
        empleadoService.save(empleado);
        return "redirect:/empleados";
    }

    // Eliminar empleado (Solo ADMIN)
    @Secured("ROLE_ADMIN")
    @GetMapping("/eliminar/{id}")
    public String eliminarEmpleado(@PathVariable Long id) {
        empleadoService.deleteById(id);
        return "redirect:/empleados";
    }
}