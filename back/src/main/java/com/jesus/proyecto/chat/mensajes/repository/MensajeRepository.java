package com.jesus.proyecto.chat.mensajes.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.jesus.proyecto.chat.mensajes.entity.Mensaje;

@Repository
public interface MensajeRepository extends JpaRepository<Mensaje, UUID> {

    Optional<Mensaje> findById(UUID id);

    List<Mensaje> findByChatId(UUID chatId);

    List<Mensaje> findByUsuarioId(UUID usuarioId);

    List<Mensaje> findByChatIdOrderByFechaEnvioDesc(UUID chatId);

    List<Mensaje> findByChatIdAndEliminadoFalseOrderByFechaEnvioDesc(UUID chatId);

    Optional<Mensaje> findFirstByChatIdAndEliminadoFalseOrderByFechaEnvioDesc(UUID chatId);

    List<Mensaje> findTop25ByChatIdAndEliminadoFalseOrderByIdDesc(UUID chatId); // obtien lista de los 25 ultimos mensajes
    List<Mensaje> findTop25ByChatIdAndEliminadoFalseAndIdLessThanOrderByIdDesc(UUID chatId, UUID msgId); // ^ a partir de X msg

}
