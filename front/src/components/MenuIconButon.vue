<template>
  <button class="icon-button" @click="onClick">
    <img
      :src="'/icons/menu/' + src"
      class="icon"
      :title="tooltip"
    />
  </button>
</template>

<script setup>
defineProps({
  src: {
    type: String,
    required: true
  },
  onClick: {
    type: Function,
    required: true
  },
  tooltip: {
    type: String,
    required: false
  }
})
</script>

<style scoped>

.icon-button{
  background: none;
  border: none;
  margin: 0;
  padding: 0;
}
</style>