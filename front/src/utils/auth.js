import { ref } from 'vue'
import { apiFetch } from '@/utils/api'

export function useAuth() {
  const errorMsg = ref('')
  const campoError = ref({})

  async function requestAuth(url, body) {
    errorMsg.value = ''
    campoError.value = {}

    const res = await apiFetch(url, body, "POST")

    if (res.data.error || !res.ok) {
      const error = res.data.error

      if (typeof error === 'object') {
        campoError.value = error
      } else {
        errorMsg.value = error || 'Error desconocido'
      }

      return { ok: false }
    }

    return { ok: true, data: res.data }
  }

  return {
    errorMsg,
    campoError,
    requestAuth
  }
}