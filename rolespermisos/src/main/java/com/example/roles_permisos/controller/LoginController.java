/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.roles_permisos.controller;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 *
 * @author PedroCoronado
 */
@Controller // Indica que esta clase es un controlador de Spring MVC
public class LoginController {  // Controlador para manejar las peticiones relacionadas con el login
    @GetMapping("/login") // obtiene la vista de login
    public String login() { //metodo que devuelve la vista de login
        return "login";
    }

    @GetMapping("/home") // obtiene la vista de home
    public String home(Authentication authentication) { //metodo que devuelve la vista de home con la autenticacion del usuario
        String rol = authentication.getAuthorities().stream() // segun el rol del usuario autenticado obtiene las autorizaciones
                .findFirst() // obtiene la primera autorizacion
                .orElseThrow() // lanza una excepcion si no hay autorizaciones
                .getAuthority(); // obtiene la autorizacion

        return switch (rol) {   // segun el rol del usuario redirige a la vista correspondiente
            case "ROLE_ADMIN" -> "redirect:/admin/dashboard";
            case "ROLE_COORDINADOR" -> "redirect:/coordinador/dashboard";
            case "ROLE_SECRETARIO" -> "redirect:/secretario/dashboard";
            default -> "redirect:/login"; // si no hay rol redirige a la vista de login
        };
    }
    @GetMapping("/") // obtiene la vista de inicio
    public String redirigirPorRol(Authentication authentication) { //metodo que redirige a la vista correspondiente segun el rol del usuario autenticado
        if (authentication == null || !authentication.isAuthenticated()) { // si la autenticacion es nula marca que no hay usuario autenticado
            return "redirect:/login"; // redirige a la vista de login
        }
        String rol = authentication.getAuthorities().iterator().next().getAuthority();  // obtiene el rol del usuario autenticado
        return switch (rol) {  // segun el rol del usuario redirige a la vista correspondiente
            case "ROLE_ADMIN" -> "redirect:/empleados"; // redirige a la vista de empleados
            case "ROLE_COORDINADOR", "ROLE_SECRETARIO" -> "redirect:/tareas"; // redirige a la vista de tareas
            default -> "redirect:/login"; // si no hay rol redirige a la vista de login
        };
    }
}
