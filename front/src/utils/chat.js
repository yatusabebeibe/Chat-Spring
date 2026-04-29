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

const esMasNuevo = (actual, nuevo) => {
  if (!actual) return true
  if (!nuevo) return false

  const fechaActual = new Date(actual.fechaEnvio)
  const fechaNuevo = new Date(nuevo.fechaEnvio)

  return fechaNuevo > fechaActual
}

// export const mergeChats = (nuevosChats) => {
//   nuevosChats.forEach((nuevo) => {
//     const actual = mapaChats.value.get(nuevo.id)

//     mapaChats.value.set(nuevo.id, {
//       ...actual,
//       ...nuevo,
//       ultimoMensaje: esMasNuevo(actual?.ultimoMensaje, nuevo?.ultimoMensaje)
//         ? nuevo.ultimoMensaje
//         : actual?.ultimoMensaje
//     })
//   })
// }
export const mergeChats = (nuevosChats) => {
  mapaChats.value = new Map(
    nuevosChats.map(chat => [chat.id, chat])
  )
}

export const añadirMensajesPrincipio = (nuevosMensajes) => {
  if (!Array.isArray(nuevosMensajes)) return

  const actuales = listaMensajesChatActual.value

  let merged = [...nuevosMensajes, ...actuales]
  listaMensajesChatActual.value = merged
}
export const añadirMensajesFinal = (nuevosMensajes) => {
  if (!Array.isArray(nuevosMensajes)) return

  const actuales = listaMensajesChatActual.value.map(m => ({ ...m }))

    let merged = [...actuales, ...nuevosMensajes]
  listaMensajesChatActual.value = merged
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