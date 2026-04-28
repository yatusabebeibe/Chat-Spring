<template>
  <router-view/>
</template>

<script setup>
import { apiFetch } from '@/utils/api.js'
import { mapaUsuariosChatActual } from '@/utils/chat.js'
import { onBeforeMount, onBeforeUnmount } from 'vue'
import { onBeforeRouteUpdate, useRoute } from 'vue-router'

const route = useRoute()

let listarIntervalo

const cargarUsuarios = async (chatId) => {
  const res = await apiFetch(`/usuario?chatId=${chatId}`)

  if (!res.ok) return

  res.data.forEach(usr => {
    mapaUsuariosChatActual.value.set(usr.id, usr)
  })
  console.log(res.data);
}

onBeforeMount(async () => {
  if (route.params.chatId) {
    await cargarUsuarios(route.params.chatId)
    listarIntervalo = setInterval(() => cargarUsuarios(route.params.chatId), 5000)
  }
})

onBeforeRouteUpdate(async (to, from, next) => {
  mapaUsuariosChatActual.value = new Map()
  clearInterval(listarIntervalo)
  if (to.params.chatId) {
    await cargarUsuarios(to.params.chatId)
    listarIntervalo = setInterval(() => cargarUsuarios(to.params.chatId), 5000)
  }
  next()
})

onBeforeUnmount(() => {
  clearInterval (listarIntervalo)
  mapaUsuariosChatActual.value = new Map()
})
</script>