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
    <main class="chat-main">
      <!-- aquí irá ChatMessages.vue -->
    </main>

    <!-- FOOTER (input de mensaje) -->
    <footer class="chat-footer">

      <input
        v-model="texto"
        placeholder="Escribe un mensaje..."
        class="input"
      />

      <input
        type="file"
        multiple
        @change="onFiles"
        class="file"
      />

      <button class="send" @click="enviar">
        Enviar
      </button>

    </footer>

  </div>
</template>

<script setup>
import { ref } from "vue"
import { socket } from "@/utils/ws.js"
import MenuIconButon from "@/components/MenuIconButon.vue"
import { useRoute } from "vue-router"

const route = useRoute()

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
</style>