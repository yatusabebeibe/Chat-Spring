<template>
  <div class="a" :class="{secundario: !pagsBarraLateral.includes($route.name)}" id="panel"><BarraLateral/></div>
  <div class="b" :class="{secundario:  pagsBarraLateral.includes($route.name)}" id="chat" ><Contenido   /></div>
</template>

<script setup>
import BarraLateral from '@/layouts/BarraLateral.vue';
import Contenido from '@/layouts/Contenido.vue';
import { ref } from 'vue';

const pagsBarraLateral = ref([
  "app", "config"
])
</script>

<style scoped>
:scope > div { position: relative;}
.a { background-color: rgba(255, 0, 0, 0.3);}
.b { background-color: rgba(0, 0, 255, 0.3);}
div {--display: flex;}
.secundario { --display: none; }
@media (min-width: 635px) {
  .secundario { --display: flex; }
}

#panel, #chat {
  display: var(--display);
  justify-content: space-between;
  flex-flow: column;
}

@media (min-width: 635px) {
  #panel {
    padding-top: 0;
  }
}
</style>