package com.example.roles_permisos.service;

import com.example.roles_permisos.model.Empleado;
import com.example.roles_permisos.repository.EmpleadoRepository;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class EmpleadoDetailsService implements UserDetailsService {

    private final EmpleadoRepository empleadoRepository;

    public EmpleadoDetailsService(EmpleadoRepository empleadoRepository) {
        this.empleadoRepository = empleadoRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Empleado empleado = empleadoRepository.findByEmail(email);
        if (empleado == null) {
            throw new UsernameNotFoundException("Empleado no encontrado con email: " + email);
        }

        return User.builder()
                .username(empleado.getEmail())
                .password(empleado.getPassword())
                .roles(empleado.getRol().name())
                .build();
    }
}