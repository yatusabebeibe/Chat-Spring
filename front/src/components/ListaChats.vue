<template>
  <ChatCard v-for="room in listaChats"
    :key="room.id"
    :chat-id="room.id"
    :titulo="room.nombre"
    :fecha-ultimo-msg="room.ultimoMensaje?.fechaEnvio ?? room.fechaCreacion"
    :msg="formatearMensaje(room.ultimoMensaje)"
    :es-grupo="room.tipo === 'GRUPO'"
    :extension-img="room.extensionImagen"
    />
</template>

<script setup>
import { listaChats } from '@/utils/chat.js'
import ChatCard from './ChatCard.vue'

const formatearMensaje = (msg) => {
  if (!msg) return null
  return msg.mensaje ?? ''
}
</script>