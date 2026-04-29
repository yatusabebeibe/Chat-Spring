import { chatConfig } from "@/config.js"
import { apiFetch } from "./api.js"
import { añadirMensajesFinal, añadirMensajesPrincipio, listaMensajesChatActual } from "./chat.js"
import { nextTick } from "vue"

let cargandoArriba = false
let cargandoAbajo = false

const esperaLimitar = 200

// estas funciones las defines aqui directamente
async function cargarArriba(elem) {
  const lista = listaMensajesChatActual.value
  if (!lista.length) return

  const primer = lista[0]

  const res = await apiFetch(
    `/msg?chatId=${primer.chatId}&limite=${chatConfig.MAX_MENSAJES.value}&msgId=${primer.id}&sentido=ANT`
  )

  // 🔥 guardar posición actual
  const scrollAntes = elem.scrollHeight

  añadirMensajesPrincipio(res.data)

  await nextTick()

  const scrollDespues = elem.scrollHeight
  const diff = scrollDespues - scrollAntes

  elem.scrollTop += diff
}

async function cargarAbajo() {
  const lista = listaMensajesChatActual.value
  if (!lista.length) return

  const ultimo = lista[lista.length - 1]

  const res = await apiFetch(
    `/msg?chatId=${ultimo.chatId}&limite=${chatConfig.MAX_MENSAJES.value}&msgId=${ultimo.id}&sentido=POS`
  )

  añadirMensajesFinal(res.data)
}


function limitarArriba() {
  const max = chatConfig.MAX_MENSAJES_LISTA.value
  const lista = listaMensajesChatActual.value

  if (lista.length <= max) return

  listaMensajesChatActual.value = lista.slice(0, max)
}
function limitarAbajo() {
  const max = chatConfig.MAX_MENSAJES_LISTA.value
  const lista = listaMensajesChatActual.value

  if (lista.length <= max) return

  listaMensajesChatActual.value = lista.slice(-max)
}



export function scrollInfinito(elem) {
  const handler = () => {
    const umbral = chatConfig.SCROLL_UMBRAL.value

    const distanciaArriba = elem.scrollTop
    const alturaVisible = elem.clientHeight
    const alturaTotal = elem.scrollHeight

    const distanciaAbajo = alturaTotal - (distanciaArriba + alturaVisible)

    if (distanciaArriba <= umbral && !cargandoArriba) {
      cargandoArriba = true
      cargarArriba(elem).finally(() => {
        cargandoArriba = false
        setTimeout(() => limitarArriba, esperaLimitar);
      })
    }

    if (distanciaAbajo <= umbral && !cargandoAbajo) {
      cargandoAbajo = true
      cargarAbajo(elem).finally(() => {
        cargandoAbajo = false
      })
    }

    if (distanciaArriba <= umbral * 2.5) {
      limitarArriba()
    }
    if (distanciaAbajo <= umbral * 2.5) {
      limitarAbajo()
    }
  }

  elem.addEventListener("scroll", handler)

  return {
    destroy() {
      elem.removeEventListener("scroll", handler)
    }
  }
}