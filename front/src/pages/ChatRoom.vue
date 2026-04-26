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

      <label class="file-btn">
        📎
        <input
          type="file"
          multiple
          @change="onFiles"
          hidden
        />
      </label>

      <input
        v-model="texto"
        placeholder="Escribe un mensaje..."
        class="input"
      />

      <button class="send" @click="enviar">
        ➤
      </button>

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

const tituloChat = ref("Chat")
const avatarUrl = ref("/default.png")

let pendingFiles = []

const abrirMenu = () => {
  console.log("menu")
}

const onFiles = (e) => {
  pendingFiles = Array.from(e.target.files)
}

const enviar = () => {
  if (!socket.value || socket.value.readyState !== 1) return

  const payload = {
    chatId: route.params.chatId, // luego lo conectas al chat seleccionado
    mensaje: texto.value,
    archivos: pendingFiles.map(f => ({
      nombre: f.name,
      tipo: f.type
    }))
  }

  socket.value.send(JSON.stringify(payload))
}

window.addEventListener("ws-message-ready", async () => {
  for (const file of pendingFiles) {
    const buffer = await file.arrayBuffer()
    socket.value.send(buffer)
  }

  pendingFiles = []
  texto.value = ""
})

const scrollAbajo = async () => {
  await nextTick()
  if (chatMain.value) {
    chatMain.value.scrollTop = chatMain.value.scrollHeight
  }
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

/* FOOTER */
.chat-footer {
  display: flex;
  gap: 10px;
  padding: 10px;
  border-top: 1px solid #ddd;
}

.input {
  flex: 1;
  padding: 8px;
}

.send {
  padding: 8px 12px;
}

.chat-footer {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 8px;
  border-top: 1px solid #ddd;
  background: hsla(0, 0%, 10%, 0.33);
}

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
</style>