<script setup>
defineProps({
  type: {
    type: String,
    default: 'info',
    validator: (v) => ['info', 'success', 'warning', 'error'].includes(v),
  },
  dismissible: { type: Boolean, default: false },
  message: { type: String, default: '' },
})

defineEmits(['dismiss'])
</script>

<template>
  <div
    :class="[
      'rounded-lg px-4 py-3 text-sm flex items-center justify-between',
      {
        'bg-blue-50 text-blue-800 border border-blue-200': type === 'info',
        'bg-green-50 text-green-800 border border-green-200': type === 'success',
        'bg-yellow-50 text-yellow-800 border border-yellow-200': type === 'warning',
        'bg-red-50 text-red-800 border border-red-200': type === 'error',
      },
    ]"
  >
    <span>
      <slot>{{ message }}</slot>
    </span>
    <button
      v-if="dismissible"
      class="ml-4 text-lg leading-none opacity-60 hover:opacity-100 cursor-pointer"
      @click="$emit('dismiss')"
    >
      &times;
    </button>
  </div>
</template>

