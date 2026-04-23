package com.jesus.proyecto.chat.usuarios.utils;

public final class UsuarioValidations {

    // Usuario
    public static final int MIN_USR_LENGTH = 3;
    public static final int MAX_USR_LENGTH = 22;
    public static final String MSG_USR_LENGTH = "El usuario necesita un largo entre "+UsuarioValidations.MIN_USR_LENGTH+" y "+UsuarioValidations.MAX_USR_LENGTH;

    public static final String PATRON_USUARIO = "^[a-z0-9_]+$";


    // Nombre
    public static final int MIN_NOMBRE_LENGTH = 3;
    public static final int MAX_NOMBRE_LENGTH = 35;
    public static final String MSG_NOMBRE_LENGTH = "El nombre necesita un largo entre "+UsuarioValidations.MIN_NOMBRE_LENGTH+" y "+UsuarioValidations.MAX_NOMBRE_LENGTH;

    public static final String PATRON_NOMBRE = "^[a-zA-Z0-9 ]+$";
}