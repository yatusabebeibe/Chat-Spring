import { chatConfig } from "@/config.js"
import router from "@/router/index.js"
import { listaMensajesChatActual, mergeChats } from '@/utils/chat.js'


// funcion base sin logica de retry
async function rawFetch(url, body = null, method = "GET") {
  try {
    const res = await fetch(import.meta.env.VITE_API_URL + url, {
      method: method,
      headers: {
        'Content-Type': 'application/json',
      },
      body: body ? JSON.stringify(body) : null,
      credentials: 'include',
    })

    const data = await res.json()

    return {
      ok: res.ok,
      status: res.status,
      data
    }
  } catch (error) {
    return {
      ok: false,
      status: 500,
      data: { error: "Error del servidor" }
    }
  }
}

// funcion principal con retry + refresh
export async function apiFetch(url, body = null, method = "GET") {
  const res = await rawFetch(url, body, method)

  // console.log("fech 1", res);

  if (res.data?.error === "Necesitas autenticarte") {
    const refresh = await rawFetch("/auth/refresh", null, "POST")

    console.log("fetch 2 refresh: ",refresh);

    const res2 = await rawFetch(url, body, method)
    console.log("fech 3", res2);
    if (!res2.ok) {
      router.push({ name:"login" })
    }
    else {
      return res2
    }
  }

  return res
}

export async function apiFetchArchivos(url, file, method = "POST") {
  try {
    const formData = new FormData()
    formData.append("file", file)

    const res = await fetch(import.meta.env.VITE_ARCHIVOS_URL + url, {
      method: method,
      body: formData,
      credentials: "include",
    })

    const data = await res.text() // tu backend devuelve string

    return {
      ok: res.ok,
      status: res.status,
      data
    }
  } catch (error) {
    return {
      ok: false,
      status: 500,
      data: "Error del servidor"
    }
  }
}

export const cargarChats = async () => {
  const res = await apiFetch('/chat')

  if (!res.ok) {
    console.error('Error cargando chats', res.data)
    return
  }

  mergeChats(res.data)
}

export const cargarMensajesChats = async (chatId) => {
  const res = await apiFetch(`/msg?chatId=${chatId}&limite=${chatConfig.MAX_MENSAJES.value}`)

  if (!res.ok) {
    console.error('Error cargando mensajes', res.data)
    router.push({name: "app"})
    return
  }

  listaMensajesChatActual.value = res.data;
}

export const obtenerImgPorDefecto = (esGrupo) => {
  return esGrupo
    ? "/icons/chat/grupos.svg"
    : "/icons/chat/conversaciones.svg"
}

export const obtenerMsgEspecifico = async (id, chatId) => {
  try {
    const res = await apiFetch(`/msg?chatId=${chatId}&msgId=${id}`)
    if (res.ok) {
      return res.data[0]
    }
  } catch (e) {
    console.error("Error cargando mensaje respuesta:", e)
  }
}