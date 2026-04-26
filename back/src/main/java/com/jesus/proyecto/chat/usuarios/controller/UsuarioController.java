package com.jesus.proyecto.chat.usuarios.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.jesus.proyecto.chat._general.utils.Paginacion;
import com.jesus.proyecto.chat.chats.service.ChatService;
import com.jesus.proyecto.chat.usuarios.dto.UsuarioRequest;
import com.jesus.proyecto.chat.usuarios.dto.UsuarioResponse;
import com.jesus.proyecto.chat.usuarios.entity.Usuario;
import com.jesus.proyecto.chat.usuarios.mapper.UsuarioMapper;
import com.jesus.proyecto.chat.usuarios.service.UsuarioService;

import lombok.AllArgsConstructor;


@RestController
@RequestMapping("/api/usuario")
@AllArgsConstructor
public class UsuarioController {

    private final ChatService chatService;
    private final UsuarioMapper usuarioMapper;
    private final UsuarioService usuarioService;

    @GetMapping({"", "/"})
    public ResponseEntity<?> buscar(@ModelAttribute UsuarioRequest request, @RequestParam(required = false) UUID chatId) {

        if (chatId != null) {
            List<UsuarioResponse> response = chatService.obtenerMiembrosDeGrupo(chatId)
                    .stream()
                    .map(usr -> usuarioMapper.toResponse(usr))
                    .toList();
            return ResponseEntity.ok(response);
        }

        int pagina = Paginacion.validarPagina(request.getPagina());
        int limite = Paginacion.validarLimite(request.getLimite());
        Direction orden = Paginacion.validarOrdenOAsc(request.getOrden());

        Pageable pageable = PageRequest.of(pagina, limite, Sort.by(orden, "usuario"));

        if (request.getId() != null) {
            Usuario usuario = usuarioService.buscarPorId(request.getId());
            return ResponseEntity.ok(usuarioMapper.toResponse(usuario));
        }
        
        if (request.getBuscar() != null && !request.getBuscar().isBlank()) {
            return ResponseEntity.ok(usuarioService.listarPorUsuarioYNombreResponse(request.getBuscar(), pageable));
        }

        return ResponseEntity.ok(usuarioService.obtenerTodos(pageable));
    }

}
