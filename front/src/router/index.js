import { createRouter, createWebHistory } from "vue-router";
import Inicio from "@/pages/Inicio.vue";
import AuthLayout from "@/layouts/AuthLayout.vue";
import Login from "@/pages/Login.vue";
import Registro from "@/pages/Registro.vue";
import Nose from "../pages/Nose.vue";
import AppLayout from "@/layouts/AppLayout.vue";
import EstructuraApp from "@/pages/EstructuraApp.vue";
import ListaChats from "@/components/ListaChats.vue";
import Contenido from "@/layouts/Contenido.vue";
import ChatRoom from "@/pages/ChatRoom.vue";
import NewChat from "@/pages/NewChat.vue";

const router = createRouter({
  history: createWebHistory( import.meta.env.BASE_URL ),
  routes: [
    { // Login
      path: "/app/login", component: AuthLayout,
      children: [{ name: "login", path: "", component: Login }],
    },
    { // Registro
      path: "/app/registro", component: AuthLayout,
      children: [{ name: "registro", path: "", component: Registro }]
    },
    { name: "inicio", path: "/", component: Inicio },
    {
      path: "/app",
      component: AppLayout,
      children: [
        { path: "", component: EstructuraApp, children: [
          { name: "app", path: "", components: {lateral: ListaChats, default: Contenido}, children: [ // app
            { name: "chatRoom", path: "chat/:chatId", components: {default: ChatRoom} },
            { name: "newChat", path: "chat/new", components: {default: NewChat} },
          ] },
        ] },
        { name: "nosequenombreponer", path: "configuracion", component: Nose },
        { name: "nosequenombreponer", path: "logout", component: Nose },
        { path: "/:pathMatch(.*)*", redirect: { name: "app" } } // si se escribe una ruta invalida redirige a app
      ]
    },
  ],
});

export default router;
