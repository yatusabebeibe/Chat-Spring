import router from "@/router/index.js"
import { mergeChats } from '@/utils/chat.js'
import { computed, ref } from "vue"


export const chatsMap = ref(new Map())


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

  console.log("fech 1", res);
  

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

export const cargarChats = async () => {
  const res = await apiFetch('/chat')

  if (!res.ok) {
    console.error('Error cargando chats', res.data)
    return
  }

  mergeChats(chatsMap.value, res.data)
}

export const listaChats = computed(() => {
  return Array.from(chatsMap.value.values())
    .sort((a, b) => obtenerFechaChats(b) - obtenerFechaChats(a))
})