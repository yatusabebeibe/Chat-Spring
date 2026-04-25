<template>
  <nav>
    <h1>{{ nombreApp }}</h1>
    <div>
      <MenuIconButon src="buscar.svg"/>
      <MenuIconLink :to="{name: 'newChat'}" src="añadir.svg"/>
    </div>
  </nav>
  <main>
    <router-view name="lateral"/>
  </main>
</template>

<script setup>
import MenuIconButon from '@/components/MenuIconButon.vue';
import MenuIconLink from '@/components/MenuIconLink.vue';

const nombreApp = import.meta.env.VITE_NOMBRE_APP;
</script>

<style scoped>
nav {
  display: flex;
  --pading: 10px;
  height: var(--headerWidth);
  padding: calc(var(--pading)/1.5) var(--pading);
  justify-content: space-between;
}
main {
  height: 100%;
  overflow-y: scroll;
  border-top: 2px solid rgba(0, 0, 0, 0.333);

  scrollbar-width: thin;
  scrollbar-color: var(--colorPrincipal) rgba(0, 0, 0, 0.333);
}

nav > h1 {
  line-height: calc(var(--headerWidth) - var(--pading)*1.5);
}

nav > div,
nav > div > * {
  height: 100%;
}
</style>