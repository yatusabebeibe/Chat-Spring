<template>
  <span class="error2">{{ errorMsg }}</span>
  <form @submit.prevent="registro" method="post">

    <Inputs
      v-model="usuarioInput"
      :texto="'Usuario'"
      :tipo="'text'"
      :name="'usuario'"
      :errorMsg="campoError.usuario"
    />

    <Inputs
      v-model="nombreInput"
      :texto="'Nombre'"
      :tipo="'text'"
      :name="'nombre'"
      :errorMsg="campoError.nombre"
    />

    <Inputs
      v-model="contrasenaInput"
      :texto="'Contraseña'"
      :tipo="'password'"
      :name="'contraseña'"
      :errorMsg="campoError.password"
    />

    <button type="submit">Registrarse</button>
  </form>
</template>

<script setup>
import Inputs from '@/components/Inputs.vue'
import { ref } from 'vue'
import "@/css/loginRegistro.css"
import router from '@/router/index.js';
import { useAuth } from '@/utils/auth.js'

const usuarioInput = ref('')
const nombreInput = ref('')
const contrasenaInput = ref('')

const { errorMsg, campoError, requestAuth } = useAuth()

async function registro() {
  const res = await requestAuth('/auth/registro', {
    usuario: usuarioInput.value,
    nombre: nombreInput.value,
    password: contrasenaInput.value
  })

  if (res.ok) {
    router.push('/app')
  }
}
</script>