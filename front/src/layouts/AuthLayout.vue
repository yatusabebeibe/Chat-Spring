<template>
  <main><div>

    <div class="links">
      <router-link :to="{ name:'registro' }"><h1>Registro</h1></router-link>
      <span>/</span>
      <router-link :to="{ name:'login' }"><h1>Login</h1></router-link>
    </div>

    <router-view/>

  </div></main>
</template>


<script setup>
import { RouterLink } from 'vue-router';

</script>


<style scoped>
main { width: 100%; height: 100%; }
main > div {
  display: flex;
  flex-flow: column nowrap;
  align-items: center;
  width: 100%; height: 100%;
  min-width: 300px;
  padding: 20px;
  text-align: center;
  &> * {width: 100%;}
}

.links {
  display: grid;
  grid-template-columns: 1fr max-content 1fr;
  gap: 8px;
  font-size: 30px;
  color: gray;
  margin-top: 10px;
  margin-bottom: 28px;
  & h1 {transition: all .3s ease;}
}
.links h1:hover {
  color: white;
  font-weight: 700;
}
.router-link-active {
  color: hsl(0, 0%, 95%);
  font-weight: 600;
}

@media (min-width: 500px) {
  main {
    display: flex;
    align-items: center;
    justify-content: center;
  }
  main>div {
    width: 550px; height: min-content;
    background: var(--colorPrincipal);
    border-radius: var(--radio);
    margin: 15px;
  }
  .links {color: black;}
}
</style>