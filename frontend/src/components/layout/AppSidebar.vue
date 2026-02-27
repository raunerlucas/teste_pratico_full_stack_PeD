<script setup>
import {useI18n} from 'vue-i18n'
import {useRoute} from 'vue-router'

const { t } = useI18n()
const route = useRoute()

defineProps({
  open: { type: Boolean, default: false },
})

defineEmits(['close'])

const navItems = [
  { to: '/', icon: '🏠', labelKey: 'nav.home' },
  { to: '/raw-materials', icon: '📦', labelKey: 'nav.rawMaterials' },
  { to: '/products', icon: '🏭', labelKey: 'nav.products' },
  { to: '/production/optimize', icon: '📈', labelKey: 'nav.production' },
]
</script>

<template>
  <!-- Mobile overlay -->
  <div
    v-if="open"
    class="fixed inset-0 bg-black/40 z-40 lg:hidden"
    @click="$emit('close')"
  />

  <!-- Sidebar -->
  <aside
    :class="[
      'fixed top-16 left-0 z-40 h-[calc(100vh-4rem)] w-64 bg-white border-r border-gray-200 transition-transform duration-300 lg:translate-x-0 lg:static lg:z-auto',
      open ? 'translate-x-0' : '-translate-x-full',
    ]"
  >
    <nav class="p-4 space-y-1">
      <router-link
        v-for="item in navItems"
        :key="item.to"
        :to="item.to"
        :class="[
          'flex items-center space-x-3 px-4 py-3 rounded-lg text-sm font-medium transition-colors',
          route.path === item.to || (item.to !== '/' && route.path.startsWith(item.to))
            ? 'bg-blue-50 text-blue-700'
            : 'text-gray-600 hover:bg-gray-50 hover:text-gray-900',
        ]"
        @click="$emit('close')"
      >
        <span class="text-lg">{{ item.icon }}</span>
        <span>{{ t(item.labelKey) }}</span>
      </router-link>
    </nav>
  </aside>
</template>

