import { ref, watch } from 'vue'

const DEFAULTS = {
  MAX_MENSAJES: 10,
  MAX_MENSAJES_LISTA: 50,
  SCROLL_UMBRAL: 500 // pixeles
}

// keys
const keys = {
  MAX_MENSAJES: 'MAX_MENSAJES',
  MAX_MENSAJES_LISTA: 'MAX_MENSAJES_LISTA',
  SCROLL_UMBRAL: 'SCROLL_UMBRAL'
}

// init localStorage
Object.entries(keys).forEach(([k, key]) => {
  if (localStorage.getItem(key) === null) {
    localStorage.setItem(key, DEFAULTS[k])
  }
})


export const chatConfig = {
  MAX_MENSAJES: ref(Number(localStorage.getItem(keys.MAX_MENSAJES))),
  MAX_MENSAJES_LISTA: ref(Number(localStorage.getItem(keys.MAX_MENSAJES_LISTA))),
  SCROLL_UMBRAL: ref(Number(localStorage.getItem(keys.SCROLL_UMBRAL)))
}



// watchers
watch(chatConfig.MAX_MENSAJES, (val) => {
  localStorage.setItem(keys.MAX_MENSAJES, val)
})

watch(chatConfig.MAX_MENSAJES_LISTA, (val) => {
  localStorage.setItem(keys.MAX_MENSAJES_LISTA, val)
})

watch(chatConfig.SCROLL_UMBRAL, (val) => {
  localStorage.setItem(keys.SCROLL_UMBRAL, val)
})