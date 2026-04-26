import { actualizarChatUltimoMensaje, añadirMensajesFinal, listaChats, mergeChats } from "./chat.js"
import { ref } from "vue"

export const socket = ref(null)

export const conectarSocket = () => {
  socket.value = new WebSocket(import.meta.env.VITE_WS_URL)

  socket.value.onmessage = (event) => {
    const data = JSON.parse(event.data)

    if (data.type === "chat_update") {
      mergeChats(listaChats.value, [data.chat])
    }

    // IMPORTANTE: evento del backend
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
}




addEventListener("ws-new-message", async (e) => {
  console.log(e.detail);
  añadirMensajesFinal([e.detail])
  actualizarChatUltimoMensaje(e.detail)
})