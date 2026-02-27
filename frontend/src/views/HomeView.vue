<script setup>
import {onMounted} from 'vue'
import {useI18n} from 'vue-i18n'
import {useRawMaterialStore} from '@/stores/rawMaterialStore'
import {useProductStore} from '@/stores/productStore'
import BaseCard from '@/components/common/BaseCard.vue'

const { t } = useI18n()
const rawMaterialStore = useRawMaterialStore()
const productStore = useProductStore()

onMounted(async () => {
  await Promise.all([rawMaterialStore.fetchAll(), productStore.fetchAll()])
})
</script>

<template>
  <div class="space-y-8">
    <!-- Welcome -->
    <div>
      <h1 class="text-2xl font-bold text-gray-900">{{ t('home.welcome') }}</h1>
      <p class="mt-2 text-gray-600">{{ t('home.description') }}</p>
    </div>

    <!-- Stats cards -->
    <div class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-6">
      <BaseCard>
        <div class="flex items-center space-x-4">
          <div class="flex-shrink-0 w-12 h-12 bg-blue-100 rounded-xl flex items-center justify-center text-2xl">
            📦
          </div>
          <div>
            <p class="text-sm text-gray-500">{{ t('home.rawMaterialsCount') }}</p>
            <p class="text-2xl font-bold text-gray-900">{{ rawMaterialStore.items.length }}</p>
          </div>
        </div>
      </BaseCard>

      <BaseCard>
        <div class="flex items-center space-x-4">
          <div class="flex-shrink-0 w-12 h-12 bg-green-100 rounded-xl flex items-center justify-center text-2xl">
            🏭
          </div>
          <div>
            <p class="text-sm text-gray-500">{{ t('home.productsCount') }}</p>
            <p class="text-2xl font-bold text-gray-900">{{ productStore.items.length }}</p>
          </div>
        </div>
      </BaseCard>

      <BaseCard>
        <div class="flex items-center space-x-4">
          <div class="flex-shrink-0 w-12 h-12 bg-purple-100 rounded-xl flex items-center justify-center text-2xl">
            📈
          </div>
          <div>
            <p class="text-sm text-gray-500">{{ t('home.optimizeProduction') }}</p>
            <router-link
              to="/production/optimize"
              class="text-sm font-medium text-blue-600 hover:text-blue-800"
            >
              {{ t('home.viewOptimization') }} →
            </router-link>
          </div>
        </div>
      </BaseCard>
    </div>

    <!-- Quick actions -->
    <BaseCard :title="t('home.quickActions')">
      <div class="flex flex-wrap gap-3">
        <router-link
          to="/raw-materials/new"
          class="inline-flex items-center px-4 py-2 bg-blue-600 text-white text-sm font-medium rounded-lg hover:bg-blue-700 transition-colors"
        >
          + {{ t('home.newRawMaterial') }}
        </router-link>
        <router-link
          to="/products/new"
          class="inline-flex items-center px-4 py-2 bg-green-600 text-white text-sm font-medium rounded-lg hover:bg-green-700 transition-colors"
        >
          + {{ t('home.newProduct') }}
        </router-link>
        <router-link
          to="/production/optimize"
          class="inline-flex items-center px-4 py-2 bg-purple-600 text-white text-sm font-medium rounded-lg hover:bg-purple-700 transition-colors"
        >
          📈 {{ t('home.viewOptimization') }}
        </router-link>
      </div>
    </BaseCard>
  </div>
</template>

