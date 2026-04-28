<template>
  <ChatCard
    v-for="room in listaChats"
    :key="room.id"
    :chat="room"
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