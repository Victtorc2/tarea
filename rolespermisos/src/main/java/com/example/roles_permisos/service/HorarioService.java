package com.example.roles_permisos.service;

import com.example.roles_permisos.model.Empleado;
import com.example.roles_permisos.model.Horario;
import com.example.roles_permisos.repository.EmpleadoRepository;
import com.example.roles_permisos.repository.HorarioRepository;
import jakarta.transaction.Transactional;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class HorarioService {

    private final HorarioRepository horarioRepository;
    private final EmpleadoRepository empleadoRepository;

    public HorarioService(HorarioRepository horarioRepository, EmpleadoRepository empleadoRepository) {
        this.horarioRepository = horarioRepository;
        this.empleadoRepository = empleadoRepository;
    }

    @Secured("ROLE_ADMIN")
    @Transactional
    public Horario asignarHorario(Horario horario, Long empleadoId) {
        Empleado empleado = empleadoRepository.findById(empleadoId)
                .orElseThrow(() -> new IllegalArgumentException("Empleado no encontrado"));

        if (empleado.getTareas().size() >= 5) {
            throw new IllegalStateException("Empleado ya tiene 5 tareas asignadas.");
        }

        horario.setEmpleado(empleado);
        return horarioRepository.save(horario);
    }

    @Secured({"ROLE_COORDINADOR", "ROLE_SECRETARIO"})
    public List<Horario> listarTodosHorarios() {
        return horarioRepository.findAll();
    }

    @Secured("ROLE_COORDINADOR")
    public Horario obtenerHorarioPorId(Long id) {
        return horarioRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("ID inválido: " + id));
    }

    @Secured("ROLE_COORDINADOR")
    @Transactional
    public Horario actualizarHorario(Long id, Horario horarioActualizado) {
        Horario horario = horarioRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("ID inválido"));

        horario.setDia(horarioActualizado.getDia());
        horario.setHoraInicio(horarioActualizado.getHoraInicio());
        horario.setHoraFin(horarioActualizado.getHoraFin());

        return horarioRepository.save(horario);
    }

    public List<Horario> obtenerHorariosPorEmpleado(Long empleadoId) {
        return horarioRepository.findByEmpleadoId(empleadoId);
    }
}