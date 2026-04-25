package com.jesus.proyecto.chat.auth.utils;

public class AuthValidations {

    // Contraseña
    public static final int MIN_PASS_LENGTH = 3;
    public static final int MAX_PASS_LENGTH = 25;
    public static final String MSG_PASS_LENGTH = "La contraseña necesita un largo entre "+AuthValidations.MIN_PASS_LENGTH+" y "+AuthValidations.MAX_PASS_LENGTH;
}
