<template>
  <div class="user-details" v-if="usuario">
    <div class="header">
      <img
        :src="imagenFinal"
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
          {{ usuario.nombre }}
        </h2>

        <input
          v-else
          v-model="nombreEdit"
          class="nombre-input"
          type="text"
          @keyup.enter="guardarNombre"
        />

        <p class="nick">@{{ usuario.usuario }}</p>
      </div>
    </div>

    <div class="info">
      <div class="item">
        <span>ID</span>
        <p>{{ usuario.id }}</p>
      </div>

      <div class="item">
        <span>Fecha de creación</span>
        <p>{{ formatoFecha(usuario.fechaCreacion) }}</p>
      </div>

      <div class="item">
        <span>Última conexión</span>
        <p>{{ formatoFecha(usuario.fechaUltimaConexion) }}</p>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onBeforeUnmount } from "vue"
import { apiFetch, apiFetchArchivos, obtenerImgPorDefecto } from "@/utils/api.js"

const usuario = ref(null)
const fileInput = ref(null)

/* CARGA USUARIO */
const cargarUsuario = async () => {
  const res = await apiFetch("/usuario/me")

  if (!res.ok) return

  usuario.value = res.data
}

onMounted(cargarUsuario)

/* IMAGEN */
const imagenUsuario = computed(() => {
  if (!usuario.value) return ""
  return `${import.meta.env.VITE_ARCHIVOS_URL}/usuario/${usuario.value.id}`
})

const imagenPreview = ref(null)

const imagenFinal = computed(() => {
  return imagenPreview.value || imagenUsuario.value
})

const imgPorDefecto = computed(() =>
  obtenerImgPorDefecto(false)
)

const seleccionarImagen = () => {
  fileInput.value.click()
}

const cambiarImagen = async (e) => {
  const file = e.target.files[0]
  if (!file) return

  const reader = new FileReader()
  reader.onload = (ev) => {
    imagenPreview.value = ev.target.result
  }
  reader.readAsDataURL(file)

  const res = await apiFetchArchivos(`/usuario`, file)

  if (!res.ok) return

  imagenPreview.value = null
  await cargarUsuario()
}

/* FECHA */
const formatoFecha = (fecha) => {
  if (!fecha) return "—"
  return new Date(fecha).toLocaleString()
}

/* EDITAR NOMBRE */
const editandoNombre = ref(false)
const nombreEdit = ref("")
const nombreBox = ref(null)

const activarEdicionNombre = () => {
  if (!usuario.value) return
  nombreEdit.value = usuario.value.nombre
  editandoNombre.value = true
}

const guardarNombre = async () => {
  const payload = {
    id: usuario.value.id,
    nombre: nombreEdit.value
  }

  const res = await apiFetch("/usuario", payload, "PUT")

  if (!res.ok) return

  usuario.value.nombre = nombreEdit.value
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
.user-details {
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
  align-items: center;
  gap: 15px;
}

.avatar {
  width: 90px;
  height: 90px;
  border-radius: 50%;
  object-fit: cover;
  cursor: pointer;
}

.nombre {
  margin: 0;
  cursor: pointer;
}

.nick {
  color: gray;
  font-size: 13px;
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
</style>