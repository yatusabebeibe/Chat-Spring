package com.jesus.proyecto.chat._general.utils;

import org.springframework.data.domain.Sort.Direction;

public abstract class Paginacion {

    public static int validarLimite(Integer limite) {
        if (limite == null || limite <= 0) {
            return 6; // default
        }
        return Math.min(limite, 50); // máximo 50
    }

    public static int validarPagina(Integer pagina) {
        if (pagina == null || pagina < 0) {
            return 0; // primera página
        }
        return pagina;
    }

    public static Direction validarOrdenOAsc(Direction orden) {
        if (orden == null) {
            return Direction.ASC;
        }
        return orden;
    }

    public static Direction validarOrdenODesc(Direction orden) {
        if (orden == null) {
            return Direction.DESC;
        }
        return orden;
    }
}
