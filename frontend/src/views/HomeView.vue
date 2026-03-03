<script setup>
import {computed, onMounted} from 'vue'
import {useI18n} from 'vue-i18n'
import {useRawMaterialStore} from '@/stores/rawMaterialStore'
import {useProductStore} from '@/stores/productStore'
import BaseCard from '@/components/common/BaseCard.vue'
import {formatCurrency, formatNumber} from '@/utils/formatters'

const { t } = useI18n()
const rawMaterialStore = useRawMaterialStore()
const productStore = useProductStore()

onMounted(async () => {
  await Promise.all([rawMaterialStore.fetchAll(), productStore.fetchAll()])
})

/* ── Raw Material computed ── */
const totalStock = computed(() =>
  rawMaterialStore.items.reduce((s, i) => s + (i.stockQuantity || 0), 0),
)
const lowStockCount = computed(() =>
  rawMaterialStore.items.filter((i) => (i.stockQuantity || 0) > 0 && (i.stockQuantity || 0) <= 10).length,
)
const zeroStockCount = computed(() =>
  rawMaterialStore.items.filter((i) => (i.stockQuantity || 0) === 0).length,
)
const topStockMaterials = computed(() =>
  [...rawMaterialStore.items].sort((a, b) => (b.stockQuantity || 0) - (a.stockQuantity || 0)).slice(0, 3),
)
const bottomStockMaterials = computed(() =>
  [...rawMaterialStore.items].sort((a, b) => (a.stockQuantity || 0) - (b.stockQuantity || 0)).slice(0, 3),
)

/* ── Product computed ── */
const totalProductValue = computed(() =>
  productStore.items.reduce((s, p) => s + (p.price || 0), 0),
)
const topPriceProducts = computed(() =>
  [...productStore.items].sort((a, b) => (b.price || 0) - (a.price || 0)).slice(0, 3),
)
const bottomPriceProducts = computed(() =>
  [...productStore.items].sort((a, b) => (a.price || 0) - (b.price || 0)).slice(0, 3),
)
const noCompositionProducts = computed(() =>
  productStore.items.filter((p) => !p.compositions || p.compositions.length === 0),
)

function getStockBadgeClass(qty) {
  if (qty === 0) return 'bg-red-100 text-red-800'
  if (qty <= 10) return 'bg-yellow-100 text-yellow-800'
  if (qty <= 50) return 'bg-blue-100 text-blue-800'
  return 'bg-green-100 text-green-800'
}
</script>

<template>
  <div class="space-y-8">
    <!-- Hero -->
    <div class="bg-linear-to-r from-blue-600 to-purple-600 rounded-2xl p-8 text-white shadow-lg">
      <h1 class="text-3xl font-bold">{{ t('home.welcome') }}</h1>
      <p class="mt-2 text-blue-100 text-lg">{{ t('home.description') }}</p>
    </div>

    <!-- Quick actions (at the top) -->
    <div class="grid grid-cols-1 sm:grid-cols-3 gap-4">
      <router-link
        to="/raw-materials/new"
        class="flex items-center space-x-3 bg-white rounded-xl shadow-sm border border-gray-200 p-5 hover:shadow-md hover:border-blue-300 transition-all group"
      >
        <div class="w-12 h-12 bg-blue-100 rounded-xl flex items-center justify-center text-2xl group-hover:scale-110 transition-transform">📦</div>
        <div>
          <p class="font-semibold text-gray-800">{{ t('home.newRawMaterial') }}</p>
          <p class="text-xs text-gray-500">{{ t('home.newRawMaterialDesc') }}</p>
        </div>
      </router-link>
      <router-link
        to="/products/new"
        class="flex items-center space-x-3 bg-white rounded-xl shadow-sm border border-gray-200 p-5 hover:shadow-md hover:border-green-300 transition-all group"
      >
        <div class="w-12 h-12 bg-green-100 rounded-xl flex items-center justify-center text-2xl group-hover:scale-110 transition-transform">🏭</div>
        <div>
          <p class="font-semibold text-gray-800">{{ t('home.newProduct') }}</p>
          <p class="text-xs text-gray-500">{{ t('home.newProductDesc') }}</p>
        </div>
      </router-link>
      <router-link
        to="/production/optimize"
        class="flex items-center space-x-3 bg-white rounded-xl shadow-sm border border-gray-200 p-5 hover:shadow-md hover:border-purple-300 transition-all group"
      >
        <div class="w-12 h-12 bg-purple-100 rounded-xl flex items-center justify-center text-2xl group-hover:scale-110 transition-transform">📈</div>
        <div>
          <p class="font-semibold text-gray-800">{{ t('home.viewOptimization') }}</p>
          <p class="text-xs text-gray-500">{{ t('home.viewOptimizationDesc') }}</p>
        </div>
      </router-link>
    </div>

    <!-- Overview stats -->
    <div class="grid grid-cols-2 sm:grid-cols-4 gap-4">
      <div class="bg-white rounded-xl shadow-sm border border-gray-200 p-5 text-center">
        <p class="text-3xl font-bold text-blue-600">{{ rawMaterialStore.items.length }}</p>
        <p class="text-xs text-gray-500 mt-1">{{ t('home.rawMaterialsCount') }}</p>
      </div>
      <div class="bg-white rounded-xl shadow-sm border border-gray-200 p-5 text-center">
        <p class="text-3xl font-bold text-green-600">{{ productStore.items.length }}</p>
        <p class="text-xs text-gray-500 mt-1">{{ t('home.productsCount') }}</p>
      </div>
      <div class="bg-white rounded-xl shadow-sm border border-gray-200 p-5 text-center">
        <p class="text-3xl font-bold text-gray-800">{{ formatNumber(totalStock) }}</p>
        <p class="text-xs text-gray-500 mt-1">{{ t('home.totalStock') }}</p>
      </div>
      <div class="bg-white rounded-xl shadow-sm border border-gray-200 p-5 text-center">
        <p class="text-3xl font-bold text-purple-600">{{ formatCurrency(totalProductValue) }}</p>
        <p class="text-xs text-gray-500 mt-1">{{ t('home.totalProductValue') }}</p>
      </div>
    </div>

    <!-- Stock alerts -->
    <div v-if="zeroStockCount > 0 || lowStockCount > 0" class="bg-yellow-50 border border-yellow-200 rounded-xl p-5">
      <h3 class="font-semibold text-yellow-800 flex items-center gap-2 mb-3">⚠️ {{ t('home.stockAlerts') }}</h3>
      <div class="flex flex-wrap gap-3 text-sm">
        <span v-if="zeroStockCount > 0" class="inline-flex items-center px-3 py-1.5 bg-red-100 text-red-800 rounded-full font-medium">
          🚫 {{ t('home.zeroStockAlert', { count: zeroStockCount }) }}
        </span>
        <span v-if="lowStockCount > 0" class="inline-flex items-center px-3 py-1.5 bg-yellow-100 text-yellow-800 rounded-full font-medium">
          📉 {{ t('home.lowStockAlert', { count: lowStockCount }) }}
        </span>
        <span v-if="noCompositionProducts.length > 0" class="inline-flex items-center px-3 py-1.5 bg-orange-100 text-orange-800 rounded-full font-medium">
          🧩 {{ t('home.noCompositionAlert', { count: noCompositionProducts.length }) }}
        </span>
      </div>
    </div>

    <!-- Top / Bottom raw materials -->
    <div class="grid grid-cols-1 lg:grid-cols-2 gap-6">
      <!-- Top 3 stock -->
      <BaseCard>
        <template #header>
          <h3 class="text-lg font-semibold text-gray-800 flex items-center gap-2">📈 {{ t('home.topStockMaterials') }}</h3>
        </template>
        <div v-if="topStockMaterials.length === 0" class="text-center py-6 text-gray-400 text-sm">{{ t('common.noData') }}</div>
        <ul v-else class="divide-y divide-gray-100">
          <li v-for="(item, idx) in topStockMaterials" :key="item.id" class="flex items-center justify-between py-3">
            <div class="flex items-center space-x-3">
              <span :class="[
                'w-8 h-8 rounded-full flex items-center justify-center text-sm font-bold text-white',
                idx === 0 ? 'bg-yellow-500' : idx === 1 ? 'bg-gray-400' : 'bg-amber-700',
              ]">{{ idx + 1 }}</span>
              <div>
                <p class="text-sm font-medium text-gray-800">{{ item.name }}</p>
                <p class="text-xs text-gray-400 font-mono">{{ item.code }}</p>
              </div>
            </div>
            <div class="text-right">
              <p class="text-sm font-bold text-gray-800">{{ formatNumber(item.stockQuantity) }}</p>
              <p class="text-xs text-gray-400">{{ item.unitOfMeasure || '' }}</p>
            </div>
          </li>
        </ul>
      </BaseCard>

      <!-- Bottom 3 stock -->
      <BaseCard>
        <template #header>
          <h3 class="text-lg font-semibold text-gray-800 flex items-center gap-2">📉 {{ t('home.bottomStockMaterials') }}</h3>
        </template>
        <div v-if="bottomStockMaterials.length === 0" class="text-center py-6 text-gray-400 text-sm">{{ t('common.noData') }}</div>
        <ul v-else class="divide-y divide-gray-100">
          <li v-for="(item, idx) in bottomStockMaterials" :key="item.id" class="flex items-center justify-between py-3">
            <div class="flex items-center space-x-3">
              <span :class="[
                'w-8 h-8 rounded-full flex items-center justify-center text-sm font-bold text-white',
                idx === 0 ? 'bg-red-500' : idx === 1 ? 'bg-orange-400' : 'bg-yellow-500',
              ]">{{ idx + 1 }}</span>
              <div>
                <p class="text-sm font-medium text-gray-800">{{ item.name }}</p>
                <p class="text-xs text-gray-400 font-mono">{{ item.code }}</p>
              </div>
            </div>
            <div class="text-right flex items-center space-x-2">
              <span
                :class="getStockBadgeClass(item.stockQuantity)"
                class="inline-flex items-center px-2 py-0.5 rounded-full text-xs font-medium"
              >
                {{ formatNumber(item.stockQuantity) }} {{ item.unitOfMeasure || '' }}
              </span>
            </div>
          </li>
        </ul>
      </BaseCard>
    </div>

    <!-- Top / Bottom products -->
    <div class="grid grid-cols-1 lg:grid-cols-2 gap-6">
      <!-- Most expensive -->
      <BaseCard>
        <template #header>
          <h3 class="text-lg font-semibold text-gray-800 flex items-center gap-2">💰 {{ t('home.topPriceProducts') }}</h3>
        </template>
        <div v-if="topPriceProducts.length === 0" class="text-center py-6 text-gray-400 text-sm">{{ t('common.noData') }}</div>
        <ul v-else class="divide-y divide-gray-100">
          <li v-for="(item, idx) in topPriceProducts" :key="item.id" class="flex items-center justify-between py-3">
            <div class="flex items-center space-x-3">
              <span :class="[
                'w-8 h-8 rounded-full flex items-center justify-center text-sm font-bold text-white',
                idx === 0 ? 'bg-yellow-500' : idx === 1 ? 'bg-gray-400' : 'bg-amber-700',
              ]">{{ idx + 1 }}</span>
              <div>
                <p class="text-sm font-medium text-gray-800">{{ item.name }}</p>
                <p class="text-xs text-gray-400 font-mono">{{ item.code }}</p>
              </div>
            </div>
            <p class="text-sm font-bold text-green-600">{{ formatCurrency(item.price) }}</p>
          </li>
        </ul>
      </BaseCard>

      <!-- Cheapest -->
      <BaseCard>
        <template #header>
          <h3 class="text-lg font-semibold text-gray-800 flex items-center gap-2">🏷️ {{ t('home.bottomPriceProducts') }}</h3>
        </template>
        <div v-if="bottomPriceProducts.length === 0" class="text-center py-6 text-gray-400 text-sm">{{ t('common.noData') }}</div>
        <ul v-else class="divide-y divide-gray-100">
          <li v-for="(item, idx) in bottomPriceProducts" :key="item.id" class="flex items-center justify-between py-3">
            <div class="flex items-center space-x-3">
              <span :class="[
                'w-8 h-8 rounded-full flex items-center justify-center text-sm font-bold text-white',
                idx === 0 ? 'bg-blue-500' : idx === 1 ? 'bg-blue-400' : 'bg-blue-300',
              ]">{{ idx + 1 }}</span>
              <div>
                <p class="text-sm font-medium text-gray-800">{{ item.name }}</p>
                <p class="text-xs text-gray-400 font-mono">{{ item.code }}</p>
              </div>
            </div>
            <p class="text-sm font-bold text-gray-700">{{ formatCurrency(item.price) }}</p>
          </li>
        </ul>
      </BaseCard>
    </div>

    <!-- Optimization CTA -->
    <div class="bg-white rounded-xl shadow-sm border border-gray-200 p-8 flex flex-col sm:flex-row items-center justify-between gap-6">
      <div class="flex items-center space-x-4">
        <div class="w-14 h-14 bg-purple-100 rounded-xl flex items-center justify-center text-3xl">🚀</div>
        <div>
          <h3 class="text-lg font-bold text-gray-800">{{ t('home.ctaTitle') }}</h3>
          <p class="text-sm text-gray-500 max-w-md">{{ t('home.ctaDescription') }}</p>
        </div>
      </div>
      <router-link
        to="/production/optimize"
        class="inline-flex items-center px-6 py-3 bg-purple-600 text-white font-medium rounded-lg hover:bg-purple-700 transition-colors shrink-0"
      >
        📈 {{ t('home.viewOptimization') }}
      </router-link>
    </div>
  </div>
</template>

