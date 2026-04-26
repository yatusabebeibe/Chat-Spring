<template>
  <ChatCard v-for="room in listaChats"
    :key="room.id"
    :chat-id="room.id"
    :titulo="room.nombre"
    :fecha-ultimo-msg="room.ultimoMensaje?.fechaEnvio ?? room.fechaCreacion"
    :msg="formatearMensaje(room.ultimoMensaje)"
    :es-grupo="room.tipo === 'GRUPO'"
    :img="room.id.replace('-','')"
    />
</template>

<script setup>
import { cargarChats } from '@/utils/api.js'
import { listaChats } from '@/utils/chat.js'
import { conectarSocket } from '@/utils/ws.js'
import { onBeforeMount, onBeforeUnmount } from 'vue'
import ChatCard from './ChatCard.vue'

let listarIntervalo

const formatearMensaje = (msg) => {
  if (!msg) return ''
  return msg.mensaje ?? ''
}

onBeforeMount(() => {
  cargarChats()
  conectarSocket()

  listarIntervalo = setInterval(cargarChats, 15000)
})

onBeforeUnmount(() => {
  clearInterval(listarIntervalo)
})
</script>