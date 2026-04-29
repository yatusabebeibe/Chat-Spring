<template>
  <div v-if="!usuarioSeleccionado">
    <Inputs
      v-model="usuario"
      :texto="texto"
      name="usuario"
      tipo="text"
      autocomplete="off"
      :errorMsg="error"
    />

    <ul v-if="usuariosEncontrados.length" class="suggestions">
      <li
        v-for="u in usuariosEncontrados"
        :key="u.id"
        @click="seleccionar(u)"
      >
        {{ u.nombre }} (@{{ u.usuario }})
      </li>
    </ul>
  </div>

  <div v-else>
    <div class="selected-user">
      <span>
        {{ usuarioSeleccionado.nombre }} (@{{ usuarioSeleccionado.usuario }})
      </span>
      <button type="button" @click="deseleccionar">X</button>
    </div>

    <span v-if="error" class="error2">
      {{ error }}
    </span>
  </div>
</template>

<script setup>
import { ref, watch } from 'vue'
import Inputs from '@/components/Inputs.vue'
import { apiFetch } from '@/utils/api.js'

const props = defineProps({
  texto: {
    type: String,
    default: 'Buscar usuario'
  }
})

const usuario = ref('')
const usuariosEncontrados = ref([])
const usuarioSeleccionado = ref(null)
const error = ref('')

watch(usuario, async (buscado) => {
  error.value = ''

  if (!buscado) {
    usuariosEncontrados.value = []
    return
  }

  const req = await apiFetch(`/usuario?buscar=${encodeURIComponent(buscado)}`)
  usuariosEncontrados.value = req.data
})

const seleccionar = (u) => {
  usuario.value = u.usuario
  usuarioSeleccionado.value = u
  usuariosEncontrados.value = []
}

const deseleccionar = () => {
  usuarioSeleccionado.value = null
  usuario.value = ''
  usuariosEncontrados.value = []
}


const setError = (msg) => {
  error.value = msg
}

const reset = () => {
  usuario.value = ''
  usuarioSeleccionado.value = null
  usuariosEncontrados.value = []
  error.value = ''
}

defineExpose({
  usuarioSeleccionado,
  setError,
  reset
})
</script>

<style scoped>
.suggestions{
  list-style: none;
  margin: 0;
  margin-top: 1.1rem;
  padding: 0;

  border: 1px solid #aaa;
  border-radius: 6px;
}

.suggestions li{
  padding: 8px;
  cursor: pointer;
  font-size: 12px;
}

.suggestions li:hover{
  background: #f5f5f555;
}

.selected-user{
  display: flex;
  justify-content: space-between;
  align-items: center;

  padding: 8px;
  border: 1px solid #aaa;
  border-radius: 6px;
}

.selected-user button{
  margin-top: 0;
  padding: 2px 6px;
  font-size: 12px;
}
</style>