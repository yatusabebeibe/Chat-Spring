import { mergeChats } from "./chat.js"

export const conectarSocket = () => {
  const socket = new WebSocket(import.meta.env.VITE_WS_URL)

  socket.onmessage = (event) => {
    const data = JSON.parse(event.data)

    if (data.type === 'chat_update') {
      mergeChats(listaChats.value, [data.chat])
    }

    if (data.type === 'chat_bulk') {
      mergeChats(listaChats.value, data.chats)
    }
  }

  return socket
}