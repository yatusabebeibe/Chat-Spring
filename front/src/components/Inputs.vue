<template>
  <div class="inputElement">
    <p>{{ texto }}:</p>
    <input
      v-model="modelValueLocal"
      :type="tipo"
      :name="name"
      :id="name"
      :class="{ 'input-error': showError }"
      @focus="clearInputError"
    >
    <span v-if="errorMsgInterno">{{ errorMsgInterno }}</span>
  </div>
</template>

<script setup>
import { ref, watch } from 'vue'

const props = defineProps({ // los parametros del componnete
  modelValue: String,
  texto: { type: String, required: true },
  name: { type: String, required: true },
  tipo: { type: String, required: true },
  errorMsg: String
})

const emit = defineEmits(['update:modelValue'])

// ref local que se sincroniza con el prop para v-model
const modelValueLocal = ref(props.modelValue)
const errorMsgInterno = ref(props.errorMsg)
const showError = ref(!!props.errorMsg)

// Quitar borde rojo al enfocar
function clearInputError() {
  showError.value = false
}

// Mantener sincronizado el valor con el padre
watch(modelValueLocal, (nuevo) => {
  emit('update:modelValue', nuevo)
})

// Mantener sincronizado el error si cambia desde el padre
watch(() => props.errorMsg, (nuevo) => {
  errorMsgInterno.value = nuevo
  showError.value = !!nuevo
})

// Sincronizar si el padre cambia modelValue externamente
watch(() => props.modelValue, (nuevo) => {
  modelValueLocal.value = nuevo
})
</script>

<style>
.inputElement > * {
  display: block;
  width: 100%;
  text-align: left;
}
.inputElement {
  position: relative;
  display: flex;
  flex-flow: column nowrap;
}
.inputElement > span {
  position: absolute;
  top: 100%;
  color: rgb(255, 128, 128);
  font-weight: bolder;
  font-size: .7rem;
}

input:not([type="submit"]) {
  outline: none;
  border: none ;
}

input.input-error {
  background-color: rgb(255, 210, 210);
}
</style>
