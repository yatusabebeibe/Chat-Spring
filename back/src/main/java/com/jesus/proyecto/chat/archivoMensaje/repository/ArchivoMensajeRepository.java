package com.jesus.proyecto.chat.archivoMensaje.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.jesus.proyecto.chat.archivoMensaje.entity.ArchivoMensaje;
import com.jesus.proyecto.chat.archivoMensaje.entity.I_ArchivoMensajeId;

public interface ArchivoMensajeRepository extends JpaRepository<ArchivoMensaje, I_ArchivoMensajeId> {
}