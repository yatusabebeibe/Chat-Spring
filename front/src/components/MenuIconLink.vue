<template>
  <RouterLink :to="to" class="icon-link">
    <img
      :src="'/icons/menu/' + src"
      class="icon"
      :title="tooltip"
    />
  </RouterLink>
</template>

<script setup>
defineProps({
  to: {
    type: [String, Object],
    required: true
  },
  src: {
    type: String,
    required: true
  },
  tooltip: {
    type: String,
    required: false
  }
})
</script>

