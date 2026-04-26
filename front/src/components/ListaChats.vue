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
import { cargarChats, listaChats } from '@/utils/api.js'
import { conectarSocket } from '@/utils/ws.js'
import { onMounted, onUnmounted } from 'vue'
import ChatCard from './ChatCard.vue'

let listarIntervalo

const formatearMensaje = (msg) => {
  if (!msg) return ''
  return msg.mensaje ?? ''
}




onMounted(() => {
  cargarChats()
  conectarSocket()

  listarIntervalo = setInterval(cargarChats, 15000)
})

onUnmounted(() => {
  clearInterval(listarIntervalo)
})
</script>