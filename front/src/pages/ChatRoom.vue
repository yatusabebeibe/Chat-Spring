<template>
  <div class="chat-room">

    <!-- NAV -->
    <nav class="chat-nav">

      <RouterLink :to="{ name: 'app' }" class="back">
        ← App
      </RouterLink>

      <div class="chat-info">
        <img class="chat-avatar" :src="avatarUrl" />

        <span class="chat-title">
          {{ tituloChat }}
        </span>
      </div>

      <MenuIconButon
        src="menu.png"
        :onClick="abrirMenu"
        tooltip="Menu"
      />

    </nav>

    <!-- MAIN (mensajes luego lo haces componente) -->
    <main class="chat-main" ref="chatMain">
      <Mensaje v-for="mensaje in listaMensajesChatActual" :mensaje="mensaje" :key="mensaje.id"  />
    </main>

    <!-- FOOTER (input de mensaje) -->
    <footer class="chat-footer">

      <div v-if="pendingFiles.length" class="files-preview">
        <div v-for="(file, i) in pendingFiles" :key="i" class="file-chip" @click="eliminarArchivo(i)">
          {{ file.name }}
        </div>
      </div>

      <div>
        <label class="file-btn">
          📎
          <input
            type="file"
            multiple
            @change="onFiles"
            hidden
          />
        </label>

        <textarea
          v-model="texto"
          placeholder="Escribe un mensaje..."
          class="input textarea"
          rows="1"
          ref="textareaRef"
          @input="autoResize"
          @keydown.enter.exact.prevent="enviar"
        />

        <button class="send" @click="enviar">
          ➤
        </button>
      </div>

    </footer>

  </div>
</template>

<script setup>
import Mensaje from "@/components/Mensaje.vue"
import MenuIconButon from "@/components/MenuIconButon.vue"
import { cargarMensajesChats } from "@/utils/api.js"
import { listaMensajesChatActual } from "@/utils/chat.js"
import { socket } from "@/utils/ws.js"
import { nextTick, onBeforeMount, onBeforeUnmount, ref } from "vue"
import { onBeforeRouteUpdate, useRoute } from "vue-router"

const route = useRoute()

const chatMain = ref(null)

const texto = ref("")
const archivos = ref([])
const textareaRef = ref(null)

const tituloChat = ref("Chat")
const avatarUrl = ref("/default.png")

const pendingFiles = ref([])

const abrirMenu = () => {
  console.log("menu")
}

const onFiles = (e) => {
  pendingFiles.value = Array.from(e.target.files)
}

const enviar = () => {
  if (!socket.value || socket.value.readyState !== 1) return
  if (!texto.value && !pendingFiles.value.length) return

  const payload = {
    chatId: route.params.chatId, // luego lo conectas al chat seleccionado
    mensaje: texto.value,
    archivos: pendingFiles.value.map(f => ({
      nombre: f.name,
      tipo: f.type
    }))
  }

  socket.value.send(JSON.stringify(payload))
}

window.addEventListener("ws-message-ready", async () => {
  for (const file of pendingFiles.value) {
    const buffer = await file.arrayBuffer()
    socket.value.send(buffer)
  }

  pendingFiles.value = []
  texto.value = ""
})

const scrollAbajo = async () => {
  await nextTick()
  if (chatMain.value) {
    chatMain.value.scrollTop = chatMain.value.scrollHeight
  }
}

const autoResize = () => {
  const el = textareaRef.value
  if (!el) return

  el.style.height = "auto"
  const maxHeight = 24 * 3 + 20 // ~3 líneas

  el.style.height = Math.min(el.scrollHeight, maxHeight) + "px"
}

const eliminarArchivo = (index) => {
  pendingFiles.value.splice(index, 1)
}

onBeforeMount(async () => {
  await cargarMensajesChats(route.params.chatId)
  await scrollAbajo()
})

onBeforeRouteUpdate(async (to, from, next) => {
  listaMensajesChatActual.value = []
  await cargarMensajesChats(to.params.chatId)
  await scrollAbajo()
  next()
})

onBeforeUnmount(() => {
  listaMensajesChatActual.value = []
})
</script>

<style scoped>
.chat-room {
  height: 100%;
  display: flex;
  flex-direction: column;
}

/* NAV */
.chat-nav {
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 10px;
  border-bottom: 1px solid #ddd;
  background: hsla(0, 0%, 10%, 0.33);
}

.chat-info {
  display: flex;
  align-items: center;
  gap: 10px;
}

.chat-avatar {
  width: 35px;
  height: 35px;
  border-radius: 50%;
}

/* MAIN */
.chat-main {
  flex: 1;
  overflow-y: auto;
  padding: 10px;
}

.file-btn {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  background: #eee;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  font-size: 18px;
}

.file-btn:hover {
  background: #dddddd4b;
}

/* FOOTER */
.chat-footer {
  display: flex;
  flex-direction: column; /* 👈 clave */
  gap: 6px;
  padding: 8px;
  border-top: 1px solid #ddd;
  background: hsla(0, 0%, 10%, 0.33);
}

/* contenedor inferior (input + botones) */
.chat-footer > div:last-child {
  display: flex;
  align-items: flex-end;
  gap: 8px;
}

/* textarea */
.input {
  flex: 1;
  padding: 10px 14px;
  border-radius: 20px;
  border: 1px solid #ccc;
  outline: none;
}

.input:focus {
  border-color: #888;
}

/* botón enviar */
.send {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  border: none;
  background: #4caf50;
  color: white;
  cursor: pointer;
  font-size: 16px;
}

.send:hover {
  background: #43a047;
}

/* botón archivo */
.file-btn {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  background: #eee;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  font-size: 18px;
}

.file-btn:hover {
  background: #dddddd4b;
}

/* textarea comportamiento */
.textarea {
  resize: none;
  overflow-y: auto;
  max-height: 100px;
}

/* preview archivos arriba */
.files-preview {
  display: flex;
  gap: 6px;
  flex-wrap: wrap;
  padding: 4px 2px;
}

/* chips */
.file-chip {
  background: #ddd;
  padding: 4px 8px;
  border-radius: 12px;
  font-size: 12px;
  cursor: pointer;
  transition: 0.2s;
}

.file-chip:hover {
  background: #bbb;
}
</style>