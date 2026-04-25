export const obtenerFechaChat = (chat) => {
  return new Date(
    chat.ultimoMensaje?.fechaEnvio ||
    chat.fechaCreacion ||
    0
  ).getTime()
}

export const buscarIndexPorId = (lista, id) => {
  return lista.findIndex(item => item.id === id)
}

export const mergeChats = (listaActual, nuevosChats) => {
  nuevosChats.forEach((nuevoChat) => {
    const index = buscarIndexPorId(listaActual, nuevoChat.id)

    if (index === -1) {
      listaActual.push(nuevoChat)
      return
    }

    const actual = listaActual[index]

    if (obtenerFechaChat(nuevoChat) > obtenerFechaChat(actual)) {
      listaActual[index] = nuevoChat
    }
  })

  listaActual.sort((a, b) => obtenerFechaChat(b) - obtenerFechaChat(a))
}