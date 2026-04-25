<template>

    <div>
      <RouterLink :to="{ name:'login' }"><h1> -> Login <- </h1></RouterLink>
    </div>

</template>

<script setup>
import { RouterLink } from "vue-router";

</script>