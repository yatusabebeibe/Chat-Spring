package com.jesus.proyecto.chat.chats.utils;

public final class ChatValidations {
    public static final int MIN_NOMBRE_LENGTH = 3;
    public static final int MAX_NOMBRE_LENGTH = 30;
    public static final String MSG_NOMBRE_LENGTH = "El nombre necesita un largo entre "+ChatValidations.MIN_NOMBRE_LENGTH+" y "+ChatValidations.MAX_NOMBRE_LENGTH;

    public static final String PATRON_NOMBRE = "^[a-zA-Z0-9 ]+$";
}
