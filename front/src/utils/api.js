import { ref } from "vue"
import { mergeChats } from '@/utils/chat.js'
import router from "@/router/index.js"

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

  if (res.data?.error === "Necesitas autenticarte") {
    const refresh = await rawFetch("/auth/refresh", null, "POST")

    console.log("refresh: ",refresh);

    if (!refresh.ok) {
      router.push({ name:"login" })
    }
    else {
      return rawFetch(url, body, method)
    }
  }

  return res
}

export const listaChats = ref([])

export const cargarChats = async () => {
  const res = await apiFetch('/chat')

  if (!res.ok) {
    console.error('Error cargando chats', res.data)
    return
  }

  console.log(listaChats.value);
  

  mergeChats(listaChats.value, res.data)
}