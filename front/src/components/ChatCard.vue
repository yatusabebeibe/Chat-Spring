<template>
  <RouterLink
    :to="{ name: 'chatRoom', params: { chatId: chat.id } }"
    class="chatCard"
    :class="chat.tipo === 'GRUPO' ? 'grupo' : 'conversacion'"
  >
    <img
      :src="imagenChat"
      @error="imgError = true"
      alt=""
    />

    <div>
      <h3>{{ chat.nombre }}</h3>
      <span>{{ formatearFecha(fechaUltimoMsg) }}</span>
      <p>{{ mensajeFormateado }}</p>
    </div>
  </RouterLink>
</template>

<script setup>
import { obtenerImgPorDefecto } from '@/utils/api.js';
import { computed, ref } from 'vue';
import { RouterLink } from 'vue-router';

const imgError = ref(false)

const props = defineProps({
  chat: { type: Object, required: true }
})

const fechaUltimoMsg = computed(() => {
  return props.chat.ultimoMensaje?.fechaEnvio ?? props.chat.fechaCreacion
})

const mensajeFormateado = computed(() => {
  const msg = props.chat.ultimoMensaje?.mensaje

  if (msg === null || msg === undefined) return "/* no hay mensaje */"
  if (msg.trim() === "") return "/* mensaje vacio */"

  return msg
})

const imagenChat = computed(() => {
  if (imgError.value) return imgPorDefecto

  if (props.chat.tipo === "CONVERSACION") {
    return `${import.meta.env.VITE_ARCHIVOS_URL}/usuario/${props.chat.avatarConversacion}`
  }

  const ext = props.chat.extensionImagen || "jpg"
  return `${import.meta.env.VITE_ARCHIVOS_URL}/${props.chat.id}/0.${ext}`
})
const imgPorDefecto = obtenerImgPorDefecto(props.chat.tipo === 'GRUPO')

function formatearFecha(fechaMensaje) {
  const fechaHoy = new Date()
  const fechaDelMensaje = new Date(fechaMensaje)

  // Si es hoy, mostramos hora y minuto
  if (fechaDelMensaje.toDateString() === fechaHoy.toDateString()) {
    return fechaDelMensaje.toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' })
  }

  // Si fue ayer, mostramos "Ayer"
  const fechaAyer = new Date()
  fechaAyer.setDate(fechaHoy.getDate() - 1)
  if (fechaDelMensaje.toDateString() === fechaAyer.toDateString()) {
    return "Ayer"
  }

  // Si es más antiguo, mostramos la fecha completa en formato local
  return fechaDelMensaje.toLocaleDateString([], { day: '2-digit', month: '2-digit', year: 'numeric' })
}
</script>

<!--  -->

<style scoped>
.chatCard {
  height: 80px;
  display: flex;
  -webkit-user-select: none;
  -ms-user-select: none;
  user-select: none;
  &:hover,&:active {
    background-color: #55555549;
    background-color: rgba(0, 0, 0, 0.2);
  }
  &>img {
    padding: 10px;
    height: 100%; aspect-ratio: 1;
    border-radius: 100%;
  }
  &> div {
    padding: 8px 0;
    width: 100%;
    display: grid;
    grid-template-columns: 1fr max-content; /* titulo toma todo lo que puede, span al final */
    grid-template-rows: auto 1fr;
    grid-template-areas:
      "titulo fecha"
      "mensaje mensaje";
      gap: 0 5px;
    padding-right: 10px;
  }
  &> div > h3 {
    font-weight: 500;
    grid-area: titulo;
    font-size: 1rem; line-height: 1.2rem;
    white-space: nowrap;      /* evita saltos de línea */
  overflow: hidden;         /* corta lo que no cabe */
  text-overflow: ellipsis;
  }
  &> div > span {
    grid-area: fecha;
    font-size: 0.6rem;
    text-align: right;
    padding-top: 1px;

  }
  & > div > p {
    font-size: 0.72rem;
    grid-area: mensaje;
    color: rgb(222, 222, 222);
    font-weight: 300;

    line-height: 1rem; /* altura de línea */
    max-height: 2rem; /* líneas × line-height */
    overflow: hidden;

    display: -webkit-box;     
  -webkit-box-orient: vertical;
  line-clamp: 2;
  -webkit-line-clamp: 2;

  word-break: break-word;      /* corta palabras largas */
  overflow-wrap: anywhere;
  }
}
</style>