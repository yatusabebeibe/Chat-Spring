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

export const mergeChats = (mapActual, nuevosChats) => {
  nuevosChats.forEach((nuevoChat) => {
    const actual = mapActual.get(nuevoChat.id)

    if (!actual) {
      mapActual.set(nuevoChat.id, nuevoChat)
      return
    }

    if (obtenerFechaChats(nuevoChat) > obtenerFechaChats(actual)) {
      mapActual.set(nuevoChat.id, nuevoChat)
    }
  })
}