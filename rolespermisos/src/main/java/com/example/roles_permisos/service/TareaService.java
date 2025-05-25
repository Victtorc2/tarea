package com.example.roles_permisos.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.roles_permisos.model.Empleado;
import com.example.roles_permisos.model.Tarea;
import com.example.roles_permisos.repository.EmpleadoRepository;
import com.example.roles_permisos.repository.TareaRepository;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class TareaService {

    private final TareaRepository tareaRepository;
    private final EmpleadoRepository empleadoRepository;

    public TareaService(TareaRepository tareaRepository, EmpleadoRepository empleadoRepository) {
        this.tareaRepository = tareaRepository;
        this.empleadoRepository = empleadoRepository;
    }

    public List<Tarea> findAll() {
        return tareaRepository.findAll();
    }

    public List<Tarea> findByEmpleadoId(Long empleadoId) {
        return tareaRepository.findTareasByEmpleadoId(empleadoId);
    }

    public List<Tarea> findByEstado(String estado) {
        return tareaRepository.findTareasByEstado(estado);
    }

    public void asignarTarea(Tarea tarea) {
        Long empleadoId = tarea.getEmpleado() != null ? tarea.getEmpleado().getId() : null;

        if (empleadoId == null) {
            throw new IllegalArgumentException("Debe seleccionar un empleado.");
        }

        Empleado empleado = empleadoRepository.findById(empleadoId)
                .orElseThrow(() -> new IllegalArgumentException("Empleado no encontrado."));

        if (empleado.getTareas().size() >= 5) {
            throw new IllegalStateException("El empleado ya tiene 5 tareas asignadas.");
        }

        tarea.setEmpleado(empleado);
        tareaRepository.save(tarea);
    }
}
