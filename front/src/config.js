import { ref, watch } from 'vue'

const DEFAULTS = {
  MAX_MENSAJES: 50
}

const key = 'MAX_MENSAJES'

// si no existe, lo guardas
if (localStorage.getItem(key) === null) {
  localStorage.setItem(key, DEFAULTS.MAX_MENSAJES)
}

export const chatConfig = {
  MAX_MENSAJES: ref(Number(localStorage.getItem(key)))
}

watch(chatConfig.MAX_MENSAJES, (val) => {
  localStorage.setItem(key, val)
})