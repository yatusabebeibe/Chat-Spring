<template>
  <div class="mensaje" :data-id="mensaje.id" :data-respuesta="mensaje.mensajeRespuestaId">
    
    <div class="header">
      <span class="user">Usuario: {{ mensaje.usuarioId }}</span>
      <span class="fecha">{{ formatDate(mensaje.fechaEnvio) }}</span>
    </div>

    <div class="body">
      <div style="position: relative;">
        <div class="mensaje-texto" v-if="mensaje.mensaje">
          {{ mensaje.mensaje }}
        </div>

        <!-- RESUMEN -->
        <div v-if="mensaje.resumen" class="resumen">
          <strong>Resumen:</strong>
          <p>{{ mensaje.resumen }}</p>
        </div>

        <button @click="resumirMensaje">
          Resumir
        </button>
      </div>

      <!-- IMAGEN -->
      <div v-if="mensaje.tipo === 'IMAGEN' && mensaje.urls">
        <img v-for="(url, i) in mensaje.urls" :key="i" :src="getUrl(url)" class="media" />
      </div>

      <!-- VIDEO -->
      <div v-if="mensaje.tipo === 'VIDEO' && mensaje.urls">
        <video v-for="(url, i) in mensaje.urls" :key="i" controls class="media">
          <source :src="getUrl(url)" />
        </video>
      </div>

      <!-- AUDIO -->
      <div v-if="mensaje.tipo === 'AUDIO' && mensaje.urls">
        <audio v-for="(url, i) in mensaje.urls" :key="i" controls>
          <source :src="getUrl(url)" />
        </audio>
      </div>

      <!-- OTROS -->
      <div v-if="mensaje.tipo === 'OTROS' && mensaje.urls">
        <div v-for="(url, i) in mensaje.urls" :key="i" class="file">
          <span>{{ url }}</span>
          <a :href="url" download>Descargar</a>
        </div>
      </div>
    </div>

    <button @click="responder">
      Responder
    </button>

  </div>
</template>

<script setup>
import { apiFetch } from '@/utils/api.js'

const props = defineProps({
  mensaje: Object
})

const emit = defineEmits(['responder'])

function responder() {
  emit('responder', props.mensaje.mensajeRespuestaId)
}

async function resumirMensaje() {
  try {
    const url = `/msg/resumir?id=${props.mensaje.id}`

    const res = await apiFetch(url)

    // if (!res.ok) return

    // dependiendo de tu backend puede venir como string o {data: "..."}
    const resumen = res.data.mensaje

    // importante: asegurar reactividad
    props.mensaje.resumen = resumen
  } catch (e) {
    console.error("Error al resumir mensaje:", e)
  }
}

function getUrl(url) {
  console.log(url);
  console.log(url.split('/').pop());
  return `${import.meta.env.VITE_ARCHIVOS_URL}/${props.mensaje.chatId}/${url}`
}

function formatDate(date) {
  return new Date(date).toLocaleString()
}
</script>

<style scoped>
.mensaje {
  border: 1px solid #ddd;
  padding: 10px;
  margin-bottom: 10px;
  border-radius: 6px;
}
.mensaje-texto {
  white-space: pre-wrap;
  word-break: break-word;
  overflow-wrap: anywhere;
}

.header {
  display: flex;
  justify-content: space-between;
  font-size: 12px;
  color: gray;
}

.body {
  margin-top: 5px;
}

.media {
  max-width: 200px;
  display: block;
  margin-top: 5px;
}

.file {
  display: flex;
  justify-content: space-between;
  margin-top: 5px;
}

button {
  margin-top: 5px;
  font-size: 12px;
}
</style>