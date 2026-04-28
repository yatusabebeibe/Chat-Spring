<template>
  <div class="chat-details" v-if="chatActual">
    <div class="header">
      <img
        :src="imagenChat"
        @error="e => e.target.src = imgPorDefecto"
        class="avatar"
        @click="seleccionarImagen"
      />

      <input
        ref="fileInput"
        type="file"
        accept="image/*"
        style="display: none"
        @change="cambiarImagen"
      />

      <div ref="nombreBox">
        <h2
          v-if="!editandoNombre"
          class="nombre"
          @click="activarEdicionNombre"
        >
          {{ chatActual.nombre }}
        </h2>

        <Inputs
          v-else
          v-model="nombreEdit"
          name="nombre"
          tipo="text"
          @keyup.enter="guardarNombre"
          class="nombre"
        />

        <p class="tipo">{{ chatActual.tipo }}</p>
      </div>
    </div>

    <div class="info">
      <div class="item">
        <span>Id del chat</span>
        <p>{{ chatActual.id }}</p>
      </div>

      <div v-if="isGrupo" class="item">
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

          <span v-if="id === chatActual.idCreador && isGrupo" class="badge creador">
            Creador
          </span>
        </p>

        <span class="nick">@{{ usuario.usuario }}</span>
      </div>
    </div>

    <div v-if="isGrupo" class="acciones">
      <button class="salir">Salir del grupo</button>
    </div>
  </div>
</template>

<script setup>
import Inputs from "@/components/Inputs.vue"
import { apiFetchArchivos, obtenerImgPorDefecto, apiFetch } from "@/utils/api.js"
import { getChatActual, mapaUsuariosChatActual } from "@/utils/chat.js"
import { computed, ref, onMounted, onBeforeUnmount } from "vue"
import { useRoute } from "vue-router"

const route = useRoute()
const fileInput = ref(null)

const chatActual = computed(() => getChatActual(route.params.chatId).value)

const usuarios = computed(() => {
  return Array.from(mapaUsuariosChatActual.value.entries())
})

const isGrupo = computed(() => chatActual.value?.tipo === "GRUPO")

const imagenChat = computed(() => {
  const ext = chatActual.value?.extensionImagen || "jpg"
  if (chatActual.value?.tipo === "CONVERSACION") {
    return `${import.meta.env.VITE_ARCHIVOS_URL}/usuario/${chatActual.value.avatarConversacion}`
  }
  return `${import.meta.env.VITE_ARCHIVOS_URL}/${route.params.chatId}/0.${ext}`
})

const imgPorDefecto = computed(() =>
  obtenerImgPorDefecto(isGrupo.value)
)

/* IMAGEN */
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

/* FECHA */
const formatoFecha = (fecha) => {
  return new Date(fecha).toLocaleString()
}

/* EDITAR NOMBRE */
const editandoNombre = ref(false)
const nombreEdit = ref("")
const nombreBox = ref(null)

const activarEdicionNombre = () => {
  if (!isGrupo.value) return
  nombreEdit.value = chatActual.value.nombre
  editandoNombre.value = true
}

const guardarNombre = async () => {
  const payload = {
    id: chatActual.value.id,
    nombre: nombreEdit.value
  }

  const res = await apiFetch("/chat", payload, "PUT")

  if (!res.ok) return

  chatActual.value.nombre = nombreEdit.value
  editandoNombre.value = false
}

/* CLICK FUERA */
const cancelarEdicionSiClickFuera = (e) => {
  if (!editandoNombre.value) return

  const path = e.composedPath?.() || []
  if (nombreBox.value && !path.includes(nombreBox.value)) {
    editandoNombre.value = false
  }
}

onMounted(() => {
  document.addEventListener("click", cancelarEdicionSiClickFuera)
})

onBeforeUnmount(() => {
  document.removeEventListener("click", cancelarEdicionSiClickFuera)
})
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
  gap: 20px;
}

.avatar {
  width: 90px;
  height: 90px;
  border-radius: 50%;
  object-fit: cover;
  cursor: pointer;
}

.nombre {
  margin: 10px 0 0;
  cursor: pointer;
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