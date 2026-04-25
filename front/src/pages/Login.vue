<template>
  <span class="error">{{ errorMsg }}</span>
  <form @submit.prevent="login" method="post">

    <Inputs
      v-model="usuarioInput"
      :texto="'Usuario'"
      :tipo="'text'"
      :name="'usuario'"
      :errorMsg="campoError.usuario"
    />

    <Inputs
      v-model="contrasenaInput"
      :texto="'Contraseña'"
      :tipo="'password'"
      :name="'contrasena'"
      :errorMsg="campoError.password"
    />

    <button type="submit">Iniciar Sesion</button>
  </form>
</template>

<script setup>
import Inputs from '@/components/Inputs.vue'
import { ref } from 'vue'
import "@/css/loginRegistro.css"
import router from '@/router/index.js'
import { useAuth } from '@/utils/auth.js'

const usuarioInput = ref('')
const contrasenaInput = ref('')

const { errorMsg, campoError, requestAuth } = useAuth()

async function login() {
  const res = await requestAuth('/auth/login', {
    usuario: usuarioInput.value,
    password: contrasenaInput.value
  })

  if (res.ok) {
    router.push('/app')
  }
}
</script>

<style lang="css">
.error {
  color: rgb(255, 128, 128);
}
</style>