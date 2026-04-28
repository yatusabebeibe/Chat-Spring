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

    <div class="usuarios">
      <h3>Miembros</h3>

      <div
        v-for="[id, usuario] in usuarios"
        :key="id"
        class="usuario"
      >
        <p class="nombre">
          {{ usuario.nombre }}

          <span v-if="usuario.rol === 'ADMIN'" class="badge admin">
            Admin
          </span>

          <span v-if="id === chatActual.idCreador && chatActual.tipo === 'GRUPO'" class="badge creador">
            Creador
          </span>
        </p>

        <span class="nick">@{{ usuario.usuario }}</span>
      </div>
    </div>

    <div class="acciones">
      <button class="salir">Salir del grupo</button>
    </div>
  </div>
</template>

<script setup>
import { apiFetchArchivos, obtenerImgPorDefecto } from "@/utils/api.js"
import { getChatActual, mapaUsuariosChatActual } from "@/utils/chat.js"
import { computed, ref } from "vue"
import { useRoute } from "vue-router"

const route = useRoute()
const fileInput = ref(null)

const chatActual = computed(() => getChatActual(route.params.chatId).value)

const usuarios = computed(() => {
  return Array.from(mapaUsuariosChatActual.value.entries())
})

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

  const reader = new FileReader()
  reader.onload = (ev) => {
    imagenChat.value = ev.target.result
  }
  reader.readAsDataURL(file)

  const res = await apiFetchArchivos(`/${route.params.chatId}`, file)

  if (!res.ok) {
    console.error("Error subiendo imagen", res.data)
    return
  }

  imagenChat.value = `${import.meta.env.VITE_ARCHIVOS_URL}/${route.params.chatId}`
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

.usuarios {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.usuario {
  padding: 10px;
  border-radius: 8px;
  border: 1px solid #f5f5f5;
}

.nick {
  font-size: 12px;
  color: gray;
}

.badge {
  font-size: 10px;
  padding: 2px 6px;
  margin-left: 6px;
  border-radius: 6px;
  color: white;
}

.admin {
  background: #4d79ff;
}

.creador {
  background: #ffb84d;
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