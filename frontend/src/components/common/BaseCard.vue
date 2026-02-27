<script setup>
defineProps({
  title: { type: String, default: '' },
})
</script>

<template>
  <div class="bg-white rounded-xl shadow-sm border border-gray-200 overflow-hidden">
    <div v-if="title || $slots.header" class="px-6 py-4 border-b border-gray-200 bg-gray-50">
      <slot name="header">
        <h3 class="text-lg font-semibold text-gray-800">{{ title }}</h3>
      </slot>
    </div>
    <div class="p-6">
      <slot />
    </div>
    <div v-if="$slots.footer" class="px-6 py-4 border-t border-gray-200 bg-gray-50">
      <slot name="footer" />
    </div>
  </div>
</template>

