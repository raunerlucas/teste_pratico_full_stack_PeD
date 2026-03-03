<script setup>
import {computed, ref} from 'vue'
import {useI18n} from 'vue-i18n'
import {formatNumber} from '@/utils/formatters'

const { t } = useI18n()

const props = defineProps({
  items: { type: Array, default: () => [] },
  loading: { type: Boolean, default: false },
})

const search = ref('')

const filteredItems = computed(() => {
  if (!search.value.trim()) return props.items
  const q = search.value.toLowerCase()
  return props.items.filter(
    (item) =>
      item.code?.toLowerCase().includes(q) ||
      item.name?.toLowerCase().includes(q) ||
      item.unitOfMeasure?.toLowerCase().includes(q),
  )
})

const stockStats = computed(() => {
  const total = props.items.length
  const low = props.items.filter((i) => (i.stockQuantity || 0) <= 10).length
  const zero = props.items.filter((i) => (i.stockQuantity || 0) === 0).length
  return { total, low, zero }
})

function getStockLevel(quantity) {
  if (quantity === 0) return 'empty'
  if (quantity <= 10) return 'low'
  if (quantity <= 50) return 'medium'
  return 'high'
}

function getStockBadgeClass(quantity) {
  const level = getStockLevel(quantity)
  const map = {
    empty: 'bg-red-100 text-red-800',
    low: 'bg-yellow-100 text-yellow-800',
    medium: 'bg-blue-100 text-blue-800',
    high: 'bg-green-100 text-green-800',
  }
  return map[level]
}

function getStockLabel(quantity) {
  const level = getStockLevel(quantity)
  const map = {
    empty: t('production.stockEmpty'),
    low: t('production.stockLow'),
    medium: t('production.stockMedium'),
    high: t('production.stockHigh'),
  }
  return map[level]
}
</script>

<template>
  <div>
    <!-- Mini stats -->
    <div class="grid grid-cols-3 gap-3 mb-4">
      <div class="text-center p-3 bg-gray-50 rounded-lg">
        <p class="text-2xl font-bold text-gray-800">{{ stockStats.total }}</p>
        <p class="text-xs text-gray-500">{{ t('production.stockTotalItems') }}</p>
      </div>
      <div class="text-center p-3 bg-yellow-50 rounded-lg">
        <p class="text-2xl font-bold text-yellow-600">{{ stockStats.low }}</p>
        <p class="text-xs text-gray-500">{{ t('production.stockLowItems') }}</p>
      </div>
      <div class="text-center p-3 bg-red-50 rounded-lg">
        <p class="text-2xl font-bold text-red-600">{{ stockStats.zero }}</p>
        <p class="text-xs text-gray-500">{{ t('production.stockZeroItems') }}</p>
      </div>
    </div>

    <!-- Search -->
    <div class="mb-3">
      <div class="relative">
        <span class="absolute inset-y-0 left-0 flex items-center pl-3 text-gray-400">
          🔍
        </span>
        <input
          v-model="search"
          type="text"
          :placeholder="t('production.searchStock')"
          class="w-full pl-10 pr-4 py-2 border border-gray-300 rounded-lg text-sm focus:ring-2 focus:ring-blue-500 focus:border-blue-500 transition-colors"
        />
      </div>
    </div>

    <!-- Loading -->
    <div v-if="loading" class="flex items-center justify-center py-8 text-gray-500">
      <svg class="animate-spin h-5 w-5 mr-2" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24">
        <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4" />
        <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4z" />
      </svg>
      {{ t('common.loading') }}
    </div>

    <!-- Table -->
    <div v-else class="max-h-80 overflow-y-auto overflow-x-auto rounded-lg border border-gray-200 shadow-sm">
      <table class="min-w-full divide-y divide-gray-200">
        <thead class="bg-gray-50 sticky top-0 z-10">
          <tr>
            <th class="px-4 py-3 text-left text-xs font-semibold text-gray-600 uppercase tracking-wider">
              {{ t('rawMaterial.code') }}
            </th>
            <th class="px-4 py-3 text-left text-xs font-semibold text-gray-600 uppercase tracking-wider">
              {{ t('rawMaterial.name') }}
            </th>
            <th class="px-4 py-3 text-right text-xs font-semibold text-gray-600 uppercase tracking-wider">
              {{ t('rawMaterial.stockQuantity') }}
            </th>
            <th class="px-4 py-3 text-center text-xs font-semibold text-gray-600 uppercase tracking-wider">
              {{ t('production.stockStatus') }}
            </th>
          </tr>
        </thead>
        <tbody class="bg-white divide-y divide-gray-200">
          <tr v-if="filteredItems.length === 0">
            <td colspan="4" class="px-4 py-8 text-center text-gray-500 text-sm">
              {{ search ? t('production.noStockResults') : t('common.noData') }}
            </td>
          </tr>
          <tr
            v-for="item in filteredItems"
            :key="item.id"
            class="hover:bg-gray-50 transition-colors"
          >
            <td class="px-4 py-3 whitespace-nowrap text-sm font-mono text-gray-700">
              {{ item.code }}
            </td>
            <td class="px-4 py-3 whitespace-nowrap text-sm text-gray-800">
              {{ item.name }}
            </td>
            <td class="px-4 py-3 whitespace-nowrap text-sm text-right text-gray-800 font-medium">
              {{ formatNumber(item.stockQuantity) }} {{ item.unitOfMeasure || '' }}
            </td>
            <td class="px-4 py-3 whitespace-nowrap text-center">
              <span
                :class="getStockBadgeClass(item.stockQuantity)"
                class="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium"
              >
                {{ getStockLabel(item.stockQuantity) }}
              </span>
            </td>
          </tr>
        </tbody>
      </table>
    </div>

    <!-- Count indicator -->
    <p v-if="!loading && search && filteredItems.length > 0" class="text-xs text-gray-400 mt-2 text-right">
      {{ t('production.showingResults', { count: filteredItems.length, total: items.length }) }}
    </p>
  </div>
</template>

