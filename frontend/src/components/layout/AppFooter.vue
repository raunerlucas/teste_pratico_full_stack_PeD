<script setup>
import {useI18n} from 'vue-i18n'

const { t } = useI18n()
</script>

<template>
  <footer class="border-t border-gray-200 bg-white px-6 py-4">
    <div class="text-center text-sm text-gray-500">
      <p>{{ t('footer.text') }} &copy; {{ new Date().getFullYear() }}. {{ t('footer.rights') }}</p>
    </div>
  </footer>
</template>

