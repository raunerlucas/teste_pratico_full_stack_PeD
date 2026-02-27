<script setup>
import {ref} from 'vue'
import {useI18n} from 'vue-i18n'

const { t, locale } = useI18n()

const mobileMenuOpen = ref(false)

function toggleLocale() {
  const next = locale.value === 'pt-BR' ? 'en' : 'pt-BR'
  locale.value = next
  localStorage.setItem('locale', next)
}

defineEmits(['toggle-sidebar'])
</script>

<template>
  <header class="bg-white border-b border-gray-200 sticky top-0 z-30">
    <div class="flex items-center justify-between h-16 px-4 lg:px-6">
      <!-- Left: hamburger + logo -->
      <div class="flex items-center space-x-4">
        <button
          class="lg:hidden p-2 rounded-lg hover:bg-gray-100 cursor-pointer"
          @click="$emit('toggle-sidebar')"
        >
          <svg class="w-6 h-6 text-gray-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4 6h16M4 12h16M4 18h16" />
          </svg>
        </button>
        <router-link to="/" class="flex items-center space-x-2">
          <span class="text-xl">🏭</span>
          <span class="text-lg font-bold text-gray-800 hidden sm:inline">{{ t('app.title') }}</span>
        </router-link>
      </div>

      <!-- Right: locale switch -->
      <div class="flex items-center space-x-3">
        <button
          class="flex items-center space-x-1 px-3 py-1.5 text-sm font-medium text-gray-600 bg-gray-100 rounded-lg hover:bg-gray-200 transition-colors cursor-pointer"
          @click="toggleLocale"
          :title="locale === 'pt-BR' ? 'Switch to English' : 'Mudar para Português'"
        >
          <span>{{ locale === 'pt-BR' ? '🇧🇷' : '🇺🇸' }}</span>
          <span>{{ locale === 'pt-BR' ? 'PT' : 'EN' }}</span>
        </button>
      </div>
    </div>
  </header>
</template>

