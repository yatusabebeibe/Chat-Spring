import { chatConfig } from "@/config.js"
import { computed, ref } from "vue"

const mapaChats = ref(new Map())
export const listaMensajesChatActual = ref([])
export const mapaUsuariosChatActual = ref(new Map())

export const getChatActual = (chatId) => {
  return computed(() => mapaChats.value.get(chatId))
}

export const listaChats = computed(() => {
  return Array.from(mapaChats.value.values())
    .sort((a, b) => obtenerFechaChats(b) - obtenerFechaChats(a))
})

export const obtenerFechaChats = (chat) => {
  return new Date(
    chat.ultimoMensaje?.fechaEnvio ||
    chat.fechaCreacion ||
    0
  ).getTime()
}

export const buscarIndexPorId = (lista, id) => {
  return lista.findIndex(item => item.id === id)
}

export const mergeChats = (nuevosChats) => {
  nuevosChats.forEach((nuevoChat) => {
    const actual = mapaChats.value.get(nuevoChat.id)

    if (!actual) {
      mapaChats.value.set(nuevoChat.id, nuevoChat)
      return
    }

    if (obtenerFechaChats(nuevoChat) > obtenerFechaChats(actual)) {
      mapaChats.value.set(nuevoChat.id, nuevoChat)
    }
  })
}

export const añadirMensajesPrincipio = (nuevosMensajes) => {
  if (!Array.isArray(nuevosMensajes)) return

  listaMensajesChatActual.value = [
    ...nuevosMensajes,
    ...listaMensajesChatActual.value
  ].slice(0, chatConfig.MAX_MENSAJES)
}
export const añadirMensajesFinal = (nuevosMensajes) => {
  if (!Array.isArray(nuevosMensajes)) return

  listaMensajesChatActual.value = [
    ...listaMensajesChatActual.value,
    ...nuevosMensajes
  ].slice(-chatConfig.MAX_MENSAJES)
}

export const actualizarChatUltimoMensaje = (mensaje) => {
  const chat = mapaChats.value.get(mensaje.chatId)

  if (!chat) return

  const chatActualizado = {
    ...chat,
    ultimoMensaje: mensaje
  }

  mapaChats.value.set(mensaje.chatId, chatActualizado)
}