<template>
  <form id="formNewChat" @submit.prevent="handleSubmit" autocomplete="off">
    
    <!-- Selector -->
    <select v-model="tipoChat">
      <option disabled value="">Selecciona tipo</option>
      <option value="grupo">Grupo</option>
      <option value="conversacion">Conversacion</option>
    </select>

    <!-- Si es grupo -->
    <Inputs
      v-if="tipoChat === 'grupo'"
      v-model="nombreGrupo"
      texto="Nombre del grupo"
      name="nombreGrupo"
      tipo="text"
      :errorMsg="errorNombreGrupo"
    />

    <!-- Si es conversacion -->
    <div v-if="tipoChat === 'conversacion'">

      <!-- INPUT solo si NO hay usuario seleccionado -->
      <div v-if="!usuarioSeleccionado">
        <Inputs
          v-model="usuario"
          texto="¿Con que usuario?"
          name="usuario"
          tipo="text"
          autocomplete="off"
          :errorMsg="errorUsuario"
        />

        <!-- lista sugerencias -->
        <ul v-if="usuariosEncontrados.length">
          <li
            v-for="u in usuariosEncontrados"
            :key="u.id"
            @click="seleccionarUsuario(u)"
            style="cursor:pointer"
          >
            {{ u.nombre }} (@{{ u.usuario }})
          </li>
        </ul>
      </div>

      <!-- USUARIO SELECCIONADO -->
      <div v-else style="display:flex; align-items:center; gap:8px; position: relative;">
        <span>
          {{ usuarioSeleccionado.nombre }} (@{{ usuarioSeleccionado.usuario }})
        </span>

        <button type="button" @click="deseleccionarUsuario">X</button>
        <span v-if="errorUsuario && usuarioSeleccionado" class="error">
          {{ errorUsuario }}
        </span>
      </div>
    </div>

    <button v-if="tipoChat !== ''" type="submit">Crear</button>

  </form>
</template>

<script setup>
import { ref, watch } from 'vue'
import Inputs from '@/components/Inputs.vue'
import { apiFetch, cargarChats } from '@/utils/api.js'

const tipoChat = ref('')
const nombreGrupo = ref('')
const usuario = ref('')

const usuariosEncontrados = ref([])
const usuarioSeleccionado = ref(null)

const errorNombreGrupo = ref('')
const errorUsuario = ref('')

watch(usuario, async (buscado) => {
  errorUsuario.value = ''
  if (!buscado) {
    usuariosEncontrados.value = []
    return
  }

  let req = await apiFetch(`/usuario?buscar=${encodeURIComponent(buscado)}`)

  usuariosEncontrados.value = req.data
})

const seleccionarUsuario = (u) => {
  usuario.value = u.usuario
  usuarioSeleccionado.value = u
  usuariosEncontrados.value = []
}
const deseleccionarUsuario = () => {
  usuarioSeleccionado.value = null
  usuario.value = ''
  errorUsuario.value = ''
}

async function handleSubmit() {
  // reset errores
  errorNombreGrupo.value = ''
  errorUsuario.value = ''

  let hayError = false

  if (tipoChat.value === 'grupo') {
    if (!nombreGrupo.value.trim()) {
      errorNombreGrupo.value = 'El nombre es obligatorio'
      hayError = true
    }
  }

  if (tipoChat.value === 'conversacion') {
    if (!usuarioSeleccionado.value) {
      errorUsuario.value = 'Selecciona un usuario valido'
      hayError = true
    }
  }

  if (hayError) return

  const payload = {
    nombre: tipoChat.value === 'grupo' ? nombreGrupo.value : null,
    tipo: tipoChat.value === 'grupo' ? 'GRUPO' : 'CONVERSACION',
    idParticipante: tipoChat.value === 'conversacion'
      ? usuarioSeleccionado.value.id
      : null
  }

  const res = await apiFetch("/chat", payload, "POST")

  if (!res.ok) {
    console.log(res);
    
    if (res.data?.error.nombre) {errorNombreGrupo.value = res.data?.error.nombre}
    if (res.data?.error.conversacion) {errorUsuario.value = res.data?.error.conversacion}
    return
  }

  cargarChats()

  // reset
  tipoChat.value = ''
  nombreGrupo.value = ''
  usuario.value = ''
  usuarioSeleccionado.value = null
}
</script>