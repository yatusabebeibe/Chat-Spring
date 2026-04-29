<template>

  <header>
    <div id="menu">
      <div class="arriba">
        <MenuIconLink :to="{ name:'app' }" src="chat.svg" tooltip="Lista chats"/>
      </div>
      <div class="abajo">
        <MenuIconLink :to="{ name:'me' }" src="usuario2.svg" />
        <MenuIconLink :to="{ name:'logout'}" src="logout.svg" />
      </div>
    </div>
  </header>

  <main><router-view/></main>

</template>

<script setup>
import MenuIconLink from "@/components/MenuIconLink.vue";
</script>

<style scoped>
main {
  display: grid;
  grid-template-columns: minmax(275px, 100%) 0;
  grid-auto-rows: 100%;
  max-height: calc(100% - var(--headerWidth))
}

@media (min-width: 692px) {
  main {
    grid-template-columns: clamp(275px, 25%, 415px) 1fr;
    max-height: 100%
  }
}
</style>