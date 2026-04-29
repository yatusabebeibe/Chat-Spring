<template>
  <div class="mensaje" :data-id="mensaje.id" :data-respuesta="mensaje.mensajeRespuestaId">
    
    <div class="header">
      <span class="user">{{ usuaioMsg }}</span>
      <span class="fecha">{{ formatDate(mensaje.fechaEnvio) }}</span>
    </div>

    <div class="body">
      <!-- PREVIEW RESPUESTA -->
      <div v-if="mensajeRespuesta" class="respuesta-preview" @click="irAlMensaje">
        <div class="respuesta-user">
          {{ getUsuarioRespuesta }}
        </div>
        <div class="respuesta-texto">
          {{ mensajeRespuesta.mensaje || '[archivo]' }}
        </div>
      </div>
      <div v-if="mensaje.mensaje" style="position: relative;">
        <div class="mensaje-texto" v-if="mensaje.mensaje">
          {{ mensaje.mensaje }}
        </div>

        <!-- RESUMEN -->
        <fieldset v-if="mensaje.resumen" class="resumen">
          <legend>Resumen:</legend>
          <p>{{ mensaje.resumen }}</p>
        </fieldset>

        <button @click="resumirMensaje">
          Resumir
        </button>
        <button @click="responderIA">
          Responder IA
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

        <button
          v-for="(url, i) in mensaje.urls"
          :key="'t-' + i"
          @click="transcribirAudio(i)"
        >
          Transcribir
        </button>

        <!-- TRANSCRIPCIÓN -->
        <fieldset v-if="mensaje.transcripcion" class="resumen">
          <legend>Transcripción:</legend>
          <p>{{ mensaje.transcripcion }}</p>
        </fieldset>
      </div>

      <!-- OTROS -->
      <div v-if="mensaje.tipo === 'OTROS' && mensaje.urls">
        <div v-for="(url, i) in mensaje.urls" :key="i" class="file">
          <span>{{ url }}</span>
          <button @click="descargar(url)">Descargar</button>
        </div>
      </div>
    </div>

    <button @click="responder">
      Responder
    </button>

  </div>
</template>

<script setup>
import { apiFetch, obtenerMsgEspecifico } from '@/utils/api.js'
import { listaMensajesChatActual, mapaUsuariosChatActual } from '@/utils/chat.js'
import { computed, ref, watch } from 'vue'

const props = defineProps({
  mensaje: Object,
})
const emit = defineEmits(['responder', 'ir-mensaje'])


const mensajeRespuesta = ref(null)


const usuaioMsg = computed(() =>{
  const usuario = mapaUsuariosChatActual.value.get(props.mensaje.usuarioId)
  if (usuario?.nombre && usuario?.usuario) {
    return`${usuario.nombre} (@${usuario.usuario})`
  }
  return ""
})
const getUsuarioRespuesta = computed(() => {
  const msg = mensajeRespuesta.value
  if (!msg) return ""

  const usuario = mapaUsuariosChatActual.value.get(msg.usuarioId)

  if (usuario?.nombre && usuario?.usuario) {
    return `${usuario.nombre} (@${usuario.usuario})`
  }

  return ""
})

function responder() {
  emit('responder', {mensajeId: props.mensaje.id})
}
function irAlMensaje() {
  if (!mensajeRespuesta.value) return

  emit('ir-mensaje', mensajeRespuesta.value.id)
}

async function resumirMensaje() {
  try {
    const url = `/msg/ai/resumir?id=${props.mensaje.id}`

    const res = await apiFetch(url)

    if (!res.ok) return

    const resumen = res.data.mensaje

    props.mensaje.resumen = resumen
  } catch (e) {
    console.error("Error al resumir mensaje:", e)
  }
}
async function responderIA() {
  try {
    const url = `/msg/ai/responder?id=${props.mensaje.id}`

    const res = await apiFetch(url)

    if (!res.ok) return

    const respuesta = res.data.mensaje

    // se lo mandas al padre
    emit('responder', {
      respuesta,
      mensajeId: props.mensaje.id
    })

  } catch (e) {
    console.error("Error al responder mensaje:", e)
  }
}

function getUrl(url) {
  return `${import.meta.env.VITE_ARCHIVOS_URL}/${props.mensaje.chatId}/${url}`
}

function formatDate(date) {
  return new Date(date).toLocaleString()
}

async function descargar(url) {
  try {
    const finalUrl = `${import.meta.env.VITE_ARCHIVOS_URL}/${props.mensaje.chatId}/${url}`

    const response = await fetch(finalUrl, {
      method: 'GET',
      credentials: 'include'
    })

    if (!response.ok) throw new Error('Error en descarga')

    const blob = await response.blob()

    const blobUrl = window.URL.createObjectURL(blob)

    const a = document.createElement('a')
    a.href = blobUrl
    a.download = url.split('/').pop()
    document.body.appendChild(a)
    a.click()
    a.remove()

    window.URL.revokeObjectURL(blobUrl)

  } catch (e) {
    console.error('Error descargando:', e)
  }
}

async function transcribirAudio(indice) {
  try {
    const url = `/msg/ai/transcribir/${props.mensaje.id}/${indice}`

    const res = await apiFetch(url)

    if (!res.ok) return

    const texto = res.data.mensaje

    props.mensaje.transcripcion = texto

  } catch (e) {
    console.error("Error al transcribir audio:", e)
  }
}

async function cargarMensajeRespuesta(id) {
  if (!id) return null

  // 1. buscar en lista local
  const local = listaMensajesChatActual.value.find(m => m.id === id)
  if (local) {
    mensajeRespuesta.value = local
    return
  }

  // 2. fallback API
  mensajeRespuesta.value = await obtenerMsgEspecifico(id, props.mensaje.chatId)
}

watch(
  () => props.mensaje.mensajeRespuestaId,
  (id) => cargarMensajeRespuesta(id),
  { immediate: true }
)
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

.resumen {
  border: 1px solid #ddd;
  padding: 8px;
  margin-top: 8px;
  border-radius: 6px;
}

.resumen legend {
  font-size: .8rem;
  font-weight: bold;
  padding: 0 6px;
}
.respuesta-preview {
  border-left: 3px solid #4caf50;
  padding-left: 6px;
  margin-bottom: 6px;
  opacity: 0.85;
}

.respuesta-user {
  font-size: 0.7rem;
  font-weight: bold;
  color: #aaa;
}

.respuesta-texto {
  font-size: 0.72rem;
  color: rgb(222, 222, 222);
  font-weight: 300;

  line-height: 1rem;
  max-height: 2rem;
  overflow: hidden;

  display: -webkit-box;
  -webkit-box-orient: vertical;
  -webkit-line-clamp: 2;

  word-break: break-word;
  overflow-wrap: anywhere;
}
</style>