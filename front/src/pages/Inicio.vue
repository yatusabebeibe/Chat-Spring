<template>
  <section class="home">

    <div class="bloque">
      <h1>Hola, soy Jesus.</h1>
      <p>He estudiado DAW (Desarrollo de Aplicaciones Web) en el IES Los Sauces de Benavente.</p>
      <p>Mi proyecto es una aplicacion fullstack de chat en tiempo real.</p>
    </div>

    <div class="bloque">
      <h2>Requisitos funcionales</h2>

      <div class="periodico">
          <p><b>1.</b> Interfaz con cabecera y navegación común</p>
          <p><b>2.</b> Registro de usuarios</p>
          <p><b>3.</b> Login de usuarios</p>
          <p><b>4.</b> Lista de conversaciones del usuario</p>
          <p><b>5.</b> Acceso solo a conversaciones propias</p>
          <p><b>6.</b> Acceso permanente a conversaciones</p>
          <p><b>7.</b> Persistencia de mensajes</p>
          <p><b>8.</b> Autenticación obligatoria</p>
          <p><b>9.</b> Mensajes en tiempo real</p>
          <p><b>10.</b> Estructura de mensajes</p>

          <p><b>11.</b> Navegación entre conversaciones</p>
          <p><b>12.</b> Envío de mensajes</p>
          <p><b>13.</b> Chats privados y grupales</p>
          <p><b>14.</b> Validación de datos</p>
          <p><b>15.</b> Validación de login</p>
          <p><b>16.</b> Registro de errores</p>
          <p><b>17.</b> Contraseñas seguras</p>
          <p><b>18.</b> Configuración de la app</p>
          <p><b>19.</b> JWT para guardar sesión</p>
          <p><b>20.</b> Edición de perfil</p>

          <p><b>21.</b> Historial de mensajes</p>
          <p><b>22.</b> Subida de archivos</p>
          <p><b>23.</b> Actualización en tiempo real</p>
          <p><b>24.</b> Gestión de grupos</p>
          <p><b>25.</b> Control de acceso a chats</p>
          <p><b>26.</b> IA integrada</p>
          <p><b>27.</b> Paginación de mensajes</p>
          <p><b>28.</b> Respuestas a mensajes</p>
          <p><b>29.</b> Acceso a mensaje original</p>
          <p><b>30.</b> Edición de perfil</p>
      </div>
    </div>

    <div class="grupo">
      <div class="bloque">
        <h2>Tecnologias</h2>
        <ul>
          <li>Frontend: Vue</li>
          <li>Backend: Spring Boot</li>
          <li>Autenticacion: JWT en cookies</li>
          <li>WebSockets</li>
          <li>Lombok</li>
          <li>MapStruct</li>
        </ul>
      </div>

      <div class="bloque">
        <h2>Funcionalidades</h2>
        <ul>
          <li>Chats privados y grupos</li>
          <li>Mandar mensajes y archivos en tiempo real</li>
          <li>Paginacion de mensajes con limite</li>
          <li>Responder mensajes</li>
          <li>Preview de mensaje al que has respondido</li>
          <li>Actualizacion de datos en tiempo real</li>
          <li>IA (resumen, respuesta, transcripcion)</li>
          <li>Control de acceso y validar permiso de endpoints</li>
          <li>Excepciones personalizadas para datos incorrectos</li>
          <li>JWT verificados en BD</li>
        </ul>
      </div>
    </div>

    <RouterLink class="login" :to="{ name:'login' }">
      Ir a login
    </RouterLink>

  </section>
</template>

<script setup>
import { RouterLink } from "vue-router";
</script>

<style scoped>
.home {
  height: 100%;
  width: 100%;
  overflow-y: auto;
  overflow-x: hidden;

  /* ocultar scrollbar */
  scrollbar-width: none; /* firefox */
}
.home::-webkit-scrollbar {
  display: none; /* chrome */
}

.home {
  padding: 40px 30px;

  display: flex;
  flex-direction: column;
  gap: 28px;

  max-width: 900px;
  margin: 0 auto;
}

.grupo {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 20px;
}

/* bloques tipo tarjeta elegante */
.bloque {
  padding: 20px 22px;

  border-radius: var(--radio);
  border: 1px solid #ffffff15;

  background: linear-gradient(
    145deg,
    #ffffff08,
    #ffffff02
  );

  box-shadow: 0 8px 25px #00000040;

  display: flex;
  flex-direction: column;
  gap: 12px;

  transition: all 0.25s ease;
}

.bloque:hover {
  border-color: #ffffff30;
  transform: translateY(-2px);
  box-shadow: 0 12px 30px #00000060;
}

/* títulos */
h1 {
  font-size: 2.6rem;
  font-weight: 500;
  letter-spacing: 1px;
}

h2 {
  font-size: 1.5rem;
  opacity: 0.9;
  border-bottom: 1px solid #ffffff20;
  padding-bottom: 6px;
}

/* texto */
p {
  font-size: 0.95rem;
  opacity: 0.75;
  line-height: 1.7;
}

/* listas más limpias */
ul {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

li {
  font-size: 0.9rem;
  opacity: 0.85;
  padding-left: 14px;
  position: relative;
}

/* marcador elegante */
li::before {
  content: "";
  position: absolute;
  left: 0;
  top: 50%;
  transform: translateY(-50%);

  width: 5px;
  height: 5px;
  border-radius: 50%;
  background: var(--colorPrincipal);
  opacity: 0.8;
}

/* botón login estilo profesional */
.login {
  margin-top: 10px;
  padding: 14px;

  border-radius: var(--radio);
  border: 1px solid var(--colorPrincipal);

  text-align: center;

  background: transparent;

  transition: all 0.25s ease;
}

.login:hover {
  background: var(--colorPrincipal);
  color: #000;
  box-shadow: 0 0 12px var(--colorPrincipal);
}
.requisitos-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 25px;

  margin-top: 15px;
}

.periodico {
  column-count: 3;
  column-gap: 40px;

  margin-top: 15px;
}

/* cada “línea de periódico” */
.periodico p {
  display: flex;
  font-size: 0.9rem;
  line-height: 1.6;

  margin: 0 0 10px 0;

  break-inside: avoid;
  opacity: 0.85;
}

/* número estilo artículo */
.periodico b {
  color: lightgreen;
  margin-right: 6px;
  font-weight: 600;
}

/* responsive */
@media (max-width: 900px) {
  .periodico {
    column-count: 1;
  }
}
</style>