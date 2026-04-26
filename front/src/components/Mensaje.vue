<template>
  <div class="mensaje" :data-id="mensaje.id" :data-respuesta="mensaje.mensajeRespuestaId">
    
    <div class="header">
      <span class="user">Usuario: {{ mensaje.usuarioId }}</span>
      <span class="fecha">{{ formatDate(mensaje.fechaEnvio) }}</span>
    </div>

    <div class="body">
      <p v-if="mensaje.mensaje">{{ mensaje.mensaje }}</p>

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
const props = defineProps({
  mensaje: Object
})

const emit = defineEmits(['responder'])

function responder() {
  emit('responder', props.mensaje.mensajeRespuestaId)
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