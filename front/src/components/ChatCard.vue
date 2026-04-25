<template>
  <RouterLink
    :to="{ name: 'chatRoom', params: { chatId: props.chatId } }"
    class="chatCard" :class="esGrupo ? 'grupo' : 'conversacion'"
  >
    <!-- <img src="/favicon.ico" alt=""> -->
    <img 
      :src="imagenActual ?? imgPorDefecto" 
      @error="imagenActual = imgPorDefecto"
      alt=""
    />
    <div>
      <h3>{{ props.titulo }}</h3>
      <span>{{ formatearFecha(props.fechaUltimoMsg) }}</span>
      <p>{{ props.msg }}</p>
    </div>
  </RouterLink>
</template>

<!--  -->

<script setup>
import { computed, ref } from 'vue';
import { RouterLink } from 'vue-router';

const props = defineProps({
  fechaUltimoMsg: { type: String, required: true },
  chatId: { type: String, required: true },
  titulo: { type: String, required: true },
  msg: { type: String, required: true },
  img: { type: String, required: false },
  esGrupo: { type: Boolean, required: true },
});


const imagenActual = ref(props.img)
// const imgPorDefecto = props.esGrupo ? "/grupo.png" : "/conversacion.png"
const imgPorDefecto = props.esGrupo ? "/icons/chat/grupos.svg" : "/icons/chat/conversaciones.svg"

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