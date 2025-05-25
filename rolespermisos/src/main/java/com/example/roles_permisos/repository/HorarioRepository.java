/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.roles_permisos.repository;

import com.example.roles_permisos.model.Horario;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 *
 * @author PedroCoronado
 */
public interface HorarioRepository extends JpaRepository<Horario, Long> {
    List<Horario> findByEmpleadoId(Long empleadoId);
}

