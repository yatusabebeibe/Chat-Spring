import { ref } from "vue"
import { actualizarChatUltimoMensaje, añadirMensajesFinal, listaChats, mergeChats } from "./chat.js"

export const socket = ref(null)

let reconnectTimeout = null
let desconectManual = false

export const conectarSocket = () => {
  desconectManual = false

  socket.value = new WebSocket(import.meta.env.VITE_WS_URL)

  socket.value.onmessage = (event) => {
    const data = JSON.parse(event.data)

    if (data.type === "chat_update") {
      mergeChats(listaChats.value, [data.chat])
    }

    if (data.type === "MESSAGE_READY") {
      window.dispatchEvent(new CustomEvent("ws-message-ready", {
        detail: data
      }))
    }

    if (data.type === "NEW_MESSAGE") {
      window.dispatchEvent(new CustomEvent("ws-new-message", {
        detail: data.message
      }))
    }
  }

  socket.value.onclose = () => {
    console.warn("WebSocket desconectado")

    if (desconectManual) return

    if (reconnectTimeout) clearTimeout(reconnectTimeout)

    reconnectTimeout = setTimeout(() => {
      console.log("Reintentando conexión WebSocket...")
      conectarSocket()
    }, 3000)
  }

  socket.value.onerror = (err) => {
    console.error("WebSocket error:", err)
    socket.value.close()
  }
}

// opcional: para cerrar manualmente sin reconectar
export const cerrarSocket = () => {
  desconectManual = true
  if (reconnectTimeout) clearTimeout(reconnectTimeout)
  socket.value?.close()
}

addEventListener("ws-new-message", async (e) => {
  console.log(e.detail)
  añadirMensajesFinal([e.detail])
  actualizarChatUltimoMensaje(e.detail)
})