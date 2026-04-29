<template></template>

<script setup>
import router from '@/router/index.js';
import { apiFetch } from '@/utils/api.js';
import { cerrarSocket } from '@/utils/ws.js';

cerrarSocket()

apiFetch("/auth/logout", null, "POST")

router.push({name:'login'})
</script>