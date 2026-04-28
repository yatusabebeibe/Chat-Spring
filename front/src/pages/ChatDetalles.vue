<template>
  <div class="chat-details" v-if="chatActual">
    <div class="header">
      <img :src="imagenChat" @error="e => e.target.src = imgPorDefecto" class="avatar" @click="seleccionarImagen" />

      <input
        ref="fileInput"
        type="file"
        accept="image/*"
        style="display: none"
        @change="cambiarImagen"
      />

      <div>
        <h2 class="nombre">{{ chatActual.nombre }}</h2>
        <p class="tipo">{{ chatActual.tipo }}</p>
      </div>
    </div>

    <div class="info">
      <div class="item">
        <span>Id del chat</span>
        <p>{{ chatActual.id }}</p>
      </div>

      <div class="item">
        <span>Creador</span>
        <p>{{ chatActual.idCreador }}</p>
      </div>

      <div class="item">
        <span>Fecha de creación</span>
        <p>{{ formatoFecha(chatActual.fechaCreacion) }}</p>
      </div>
    </div>

    <div class="acciones">
      <button>Ver miembros</button>
      <button class="salir">Salir del grupo</button>
    </div>
  </div>
</template>

<script setup>
import { apiFetchArchivos, obtenerImgPorDefecto } from "@/utils/api.js"
import { getChatActual } from "@/utils/chat.js"
import { computed, ref } from "vue"
import { useRoute } from "vue-router"

const route = useRoute()
const fileInput = ref(null)

const chatActual = computed(() => getChatActual(route.params.chatId).value)

const imagenChat = computed(() => {
  const ext = chatActual.value?.extensionImagen || "jpg"
  return `${import.meta.env.VITE_ARCHIVOS_URL}/${route.params.chatId}/0.${ext}`
})
const imgPorDefecto = computed(() =>
  obtenerImgPorDefecto(chatActual.value?.tipo === "GRUPO")
)

const seleccionarImagen = () => {
  fileInput.value.click()
}

const cambiarImagen = async (e) => {
  const file = e.target.files[0]
  if (!file) return

  // preview instantaneo
  const reader = new FileReader()
  reader.onload = (ev) => {
    imagenChat.value = ev.target.result
  }
  reader.readAsDataURL(file)

  // subir al backend
  const res = await apiFetchArchivos(`/${route.params.chatId}`, file)

  if (!res.ok) {
    console.error("Error subiendo imagen", res.data)
    return
  }

  // forzar recarga de imagen real (evitar cache)
  imagenChat.value = `${import.meta.env.VITE_ARCHIVOS_URL}/${route.params.chatId}?t=${Date.now()}`
}

const formatoFecha = (fecha) => {
  return new Date(fecha).toLocaleString()
}
</script>

<style scoped>
.chat-details {
  padding: 20px;
  display: flex;
  flex-direction: column;
  gap: 20px;
  max-width: 650px;
  width: 100%;
  margin: 0 auto;
}

.header {
  display: flex;
  text-align: center;
}

.avatar {
  width: 90px;
  height: 90px;
  border-radius: 50%;
  object-fit: cover;
}

.nombre {
  margin: 10px 0 0;
}

.tipo {
  color: gray;
  font-size: 12px;
}

.info {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.item span {
  font-size: 12px;
  color: gray;
}

.item p {
  margin: 2px 0 0;
  word-break: break-all;
}

.acciones {
  display: flex;
  flex-direction: column;
  gap: 10px;
  margin-top: auto;
}

button {
  padding: 10px;
  border: none;
  border-radius: 8px;
  cursor: pointer;
}

.salir {
  background: #ff4d4d;
  color: white;
}
</style>