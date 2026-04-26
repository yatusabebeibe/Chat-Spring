package com.jesus.proyecto.chat.mensajes.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.jesus.proyecto.chat.mensajes.entity.Mensaje;

@Repository
public interface MensajeRepository extends JpaRepository<Mensaje, UUID> {

    List<Mensaje> findByChatId(UUID chatId);

    List<Mensaje> findByUsuarioId(UUID usuarioId);

    Optional<Mensaje> findFirstByChatIdAndEliminadoFalseOrderByFechaEnvioDesc(UUID chatId);

    List<Mensaje> findByChatIdAndEliminadoFalse(UUID chatId, Pageable pageable); // obtien lista de los X ultimos mensajes
    List<Mensaje> findByChatIdAndEliminadoFalseAndIdLessThanOrderByIdDesc(UUID chatId, UUID msgId, Pageable pageable); // ^ posteriores a X msg
    List<Mensaje> findByChatIdAndEliminadoFalseAndIdGreaterThanOrderByIdAsc(UUID chatId, UUID msgId, Pageable pageable); // ^ anteriores a X msg

}
