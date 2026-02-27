<script setup>
import {ref} from 'vue'
import AppHeader from '@/components/layout/AppHeader.vue'
import AppSidebar from '@/components/layout/AppSidebar.vue'
import AppFooter from '@/components/layout/AppFooter.vue'

const sidebarOpen = ref(false)

function toggleSidebar() {
  sidebarOpen.value = !sidebarOpen.value
}
</script>

<template>
  <div class="min-h-screen bg-gray-50 flex flex-col">
    <AppHeader @toggle-sidebar="toggleSidebar" />

    <div class="flex flex-1">
      <AppSidebar :open="sidebarOpen" @close="sidebarOpen = false" />

      <main class="flex-1 p-6 lg:p-8 overflow-auto">
        <router-view />
      </main>
    </div>

    <AppFooter />
  </div>
</template>


