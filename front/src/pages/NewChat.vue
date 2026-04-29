<template>
  <div class="container">

    <!-- FORM CREAR CHAT -->
    <form class="card" @submit.prevent="handleSubmit" autocomplete="off">
      
      <h3>Crear chat</h3>

      <select v-model="tipoChat">
        <option disabled value="">Selecciona tipo</option>
        <option value="grupo">Grupo</option>
        <option value="conversacion">Conversacion</option>
      </select>

      <Inputs
        v-if="tipoChat === 'grupo'"
        v-model="nombreGrupo"
        texto="Nombre del grupo"
        name="nombreGrupo"
        tipo="text"
        :errorMsg="errorNombreGrupo"
      />

      <BusquedaUsuarios
        v-if="tipoChat === 'conversacion'"
        ref="userSearch"
        texto="¿Con que usuario?"
        :errorMsg="errorUsuario"
      />

      <button v-if="tipoChat !== ''" type="submit">Crear</button>
    </form>

  </div>
</template>


<script setup>
import { ref } from 'vue'
import Inputs from '@/components/Inputs.vue'
import BusquedaUsuarios from '@/components/BusquedaUsuarios.vue'
import { apiFetch, cargarChats } from '@/utils/api.js'
import router from '@/router/index.js'

const tipoChat = ref('')
const nombreGrupo = ref('')
const userSearch = ref(null)

const errorNombreGrupo = ref('')

/* CREAR CHAT */
async function handleSubmit() {

  let hayError = false

  // limpiar errores en hijos
  userSearch.value?.setError('')
  
  if (tipoChat.value === 'grupo') {
    if (!nombreGrupo.value.trim()) {
      hayError = true
    }
  }

  if (tipoChat.value === 'conversacion') {
    const usuario = userSearch.value?.usuarioSeleccionado

    if (!usuario) {
      userSearch.value?.setError('Selecciona un usuario')
      hayError = true
    }
  }

  if (hayError) return

  const usuario = userSearch.value?.usuarioSeleccionado

  const payload = {
    nombre: tipoChat.value === 'grupo' ? nombreGrupo.value : null,
    tipo: tipoChat.value === 'grupo' ? 'GRUPO' : 'CONVERSACION',
    idParticipante: tipoChat.value === 'conversacion'
      ? usuario.id
      : null
  }

  const res = await apiFetch("/chat", payload, "POST")

  if (!res.ok) {

    if (tipoChat.value === 'grupo') {
      // aquí podrías usar Inputs si tienes setError en Inputs
      errorNombreGrupo.value = res.data || 'Error creando grupo'
    }

    if (tipoChat.value === 'conversacion') {
      userSearch.value?.setError(res.data?.error || 'Error creando conversación')
    }

    return
  }

  cargarChats()
  router.push({ name:'app' })

  tipoChat.value = ''
  nombreGrupo.value = ''
}
</script>


<style scoped>
.container{
  display: grid;
  gap: 18px;
  /* max-width: 520px; */

  justify-content: center; /* centra horizontal */
  align-content: center;   /* centra vertical dentro del espacio disponible */

  min-height: 100vh;       /* ocupa toda la pantalla */
}

/* CARD igual estilo mensaje */
.card{
  border: 1px solid #aaa;
  padding: 15px 25px;
  border-radius: 6px;
  width: 350px;

  display: flex;
  flex-direction: column;
  gap: 8px;
}

/* TITULOS igual que header (simple y suave) */
h3{
  margin: 0;
  font-size: 12px;
  color: lightgray;
  font-weight: normal;
}

/* INPUTS / SELECT estilo limpio */
select,
input{
  border: 1px solid #aaa;
  border-radius: 6px;
  padding: 8px;
  font-size: 12px;
}

/* BOTONES estilo mensaje */
button{
  margin-top: 16px;
  font-size: 12px;

  border: 1px solid #aaa;
  background: white;
  border-radius: 6px;

  padding: 6px 10px;
  cursor: pointer;
}

button:hover{
  background: #f5f5f5;
}
</style>