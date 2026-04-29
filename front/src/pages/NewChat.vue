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
      />

      <button v-if="tipoChat !== ''" type="submit">Crear</button>
    </form>


    <!-- FORM UNIRSE A GRUPO -->
    <form class="card" @submit.prevent="handleJoinGroup">
      
      <h3>Unirse a grupo</h3>

      <Inputs
        v-model="codigoGrupo"
        texto="Nombre o codigo del grupo"
        name="codigoGrupo"
        tipo="text"
        :errorMsg="errorJoinGrupo"
      />

      <button type="submit">Unirse</button>
    </form>

  </div>
</template>


<script setup>
import { ref } from 'vue'
import Inputs from '@/components/Inputs.vue'
import BusquedaUsuarios from '@/components/BusquedaUsuarios.vue'
import { apiFetch, cargarChats } from '@/utils/api.js'

const tipoChat = ref('')
const nombreGrupo = ref('')
const codigoGrupo = ref('')

const errorNombreGrupo = ref('')
const errorJoinGrupo = ref('')

const userSearch = ref(null)

/* CREAR CHAT */
async function handleSubmit() {
  errorNombreGrupo.value = ''

  let hayError = false

  if (tipoChat.value === 'grupo' && !nombreGrupo.value.trim()) {
    errorNombreGrupo.value = 'El nombre es obligatorio'
    hayError = true
  }

  if (tipoChat.value === 'conversacion') {
    const usuario = userSearch.value?.usuarioSeleccionado

    if (!usuario) {
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

  if (!res.ok) return

  cargarChats()

  tipoChat.value = ''
  nombreGrupo.value = ''
}

/* UNIRSE A GRUPO */
async function handleJoinGroup() {
  errorJoinGrupo.value = ''

  if (!codigoGrupo.value.trim()) {
    errorJoinGrupo.value = 'Introduce un grupo'
    return
  }

  const res = await apiFetch("/chat/unirse", {
    grupo: codigoGrupo.value
  }, "POST")

  if (!res.ok) {
    errorJoinGrupo.value = res.data?.error || 'Error al unirse'
    return
  }

  cargarChats()
  codigoGrupo.value = ''
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