<script setup>
import {computed, onMounted, ref} from 'vue'
import {useI18n} from 'vue-i18n'
import {useProductionStore} from '@/stores/productionStore'
import {useRawMaterialStore} from '@/stores/rawMaterialStore'
import {useProductStore} from '@/stores/productStore'
import BaseCard from '@/components/common/BaseCard.vue'
import BaseButton from '@/components/common/BaseButton.vue'
import BaseAlert from '@/components/common/BaseAlert.vue'
import StockOverview from '@/components/production/StockOverview.vue'
import ProductionResult from '@/components/production/ProductionResult.vue'
import {formatCurrency} from '@/utils/formatters'
import {getErrorI18nKey, parseApiError} from '@/utils/errorHandler'

const { t } = useI18n()
const productionStore = useProductionStore()
const rawMaterialStore = useRawMaterialStore()
const productStore = useProductStore()

const alert = ref({ show: false, type: 'error', message: '' })

onMounted(() => {
  rawMaterialStore.fetchAll()
  productStore.fetchAll()
})

const canOptimize = computed(() => {
  return rawMaterialStore.items.length > 0 && productStore.items.length > 0
})

const hasStock = computed(() => {
  return rawMaterialStore.items.some((i) => (i.stockQuantity || 0) > 0)
})

async function handleOptimize() {
  alert.value.show = false
  try {
    await productionStore.fetchOptimization()
  } catch (err) {
    const errorInfo = parseApiError(err)
    const key = getErrorI18nKey(errorInfo, 'production', 'optimize')
    alert.value = { show: true, type: 'error', message: t(key) }
  }
}

function handleReset() {
  productionStore.reset()
  alert.value.show = false
}
</script>

<template>
  <div class="space-y-8">
    <!-- Hero Section -->
    <div class="bg-linear-to-r from-purple-600 to-blue-600 rounded-2xl p-8 text-white shadow-lg">
      <div class="max-w-3xl">
        <h1 class="text-3xl font-bold flex items-center gap-3">
          📈 {{ t('production.title') }}
        </h1>
        <p class="mt-3 text-purple-100 text-lg leading-relaxed">
          {{ t('production.heroDescription') }}
        </p>
      </div>
    </div>

    <!-- How it works -->
    <BaseCard>
      <template #header>
        <h3 class="text-lg font-semibold text-gray-800 flex items-center gap-2">
          💡 {{ t('production.howItWorks') }}
        </h3>
      </template>
      <div class="grid grid-cols-1 md:grid-cols-3 gap-6">
        <div class="flex flex-col items-center text-center p-4">
          <div class="w-14 h-14 bg-blue-100 rounded-full flex items-center justify-center text-2xl mb-3">📦</div>
          <h4 class="font-semibold text-gray-800 mb-1">{{ t('production.step1Title') }}</h4>
          <p class="text-sm text-gray-500">{{ t('production.step1Desc') }}</p>
        </div>
        <div class="flex flex-col items-center text-center p-4">
          <div class="w-14 h-14 bg-purple-100 rounded-full flex items-center justify-center text-2xl mb-3">⚙️</div>
          <h4 class="font-semibold text-gray-800 mb-1">{{ t('production.step2Title') }}</h4>
          <p class="text-sm text-gray-500">{{ t('production.step2Desc') }}</p>
        </div>
        <div class="flex flex-col items-center text-center p-4">
          <div class="w-14 h-14 bg-green-100 rounded-full flex items-center justify-center text-2xl mb-3">💰</div>
          <h4 class="font-semibold text-gray-800 mb-1">{{ t('production.step3Title') }}</h4>
          <p class="text-sm text-gray-500">{{ t('production.step3Desc') }}</p>
        </div>
      </div>
    </BaseCard>

    <!-- Pre-requisites info -->
    <BaseAlert
      v-if="!canOptimize"
      type="warning"
      :message="t('production.prerequisitesWarning')"
    />
    <BaseAlert
      v-else-if="!hasStock"
      type="warning"
      :message="t('production.noStockWarning')"
    />

    <!-- Action area -->
    <div class="bg-white rounded-xl shadow-sm border border-gray-200 p-8">
      <div class="flex flex-col items-center text-center space-y-4">
        <div class="w-20 h-20 bg-purple-50 rounded-full flex items-center justify-center text-4xl">
          🚀
        </div>
        <h2 class="text-xl font-bold text-gray-800">{{ t('production.readyToOptimize') }}</h2>
        <p class="text-gray-500 max-w-lg">{{ t('production.actionDescription') }}</p>
        <div class="flex items-center gap-3">
          <BaseButton
            variant="primary"
            size="lg"
            :loading="productionStore.loading"
            :disabled="!canOptimize || !hasStock"
            @click="handleOptimize"
          >
            📈 {{ productionStore.loading ? t('production.optimizing') : t('production.optimize') }}
          </BaseButton>
          <BaseButton
            v-if="productionStore.calculated"
            variant="secondary"
            size="lg"
            @click="handleReset"
          >
            🔄 {{ t('production.recalculate') }}
          </BaseButton>
        </div>
      </div>
    </div>

    <!-- Error -->
    <BaseAlert
      v-if="alert.show"
      type="error"
      :message="alert.message"
      dismissible
      @dismiss="alert.show = false"
    />

    <!-- Results -->
    <template v-if="productionStore.calculated">
      <!-- Summary cards -->
      <div>
        <h2 class="text-xl font-bold text-gray-800 mb-4 flex items-center gap-2">
          📊 {{ t('production.resultSummary') }}
        </h2>
        <div class="grid grid-cols-1 sm:grid-cols-3 gap-6">
          <div class="bg-white rounded-xl shadow-sm border border-gray-200 p-6">
            <div class="flex items-center space-x-4">
              <div class="w-12 h-12 bg-green-100 rounded-xl flex items-center justify-center text-2xl">💰</div>
              <div>
                <p class="text-sm text-gray-500">{{ t('production.grandTotal') }}</p>
                <p class="text-2xl font-bold text-green-600">
                  {{ formatCurrency(productionStore.grandTotal) }}
                </p>
              </div>
            </div>
          </div>
          <div class="bg-white rounded-xl shadow-sm border border-gray-200 p-6">
            <div class="flex items-center space-x-4">
              <div class="w-12 h-12 bg-blue-100 rounded-xl flex items-center justify-center text-2xl">🏭</div>
              <div>
                <p class="text-sm text-gray-500">{{ t('production.suggestedProducts') }}</p>
                <p class="text-2xl font-bold text-blue-600">{{ productionStore.totalProducts }}</p>
              </div>
            </div>
          </div>
          <div class="bg-white rounded-xl shadow-sm border border-gray-200 p-6">
            <div class="flex items-center space-x-4">
              <div class="w-12 h-12 bg-purple-100 rounded-xl flex items-center justify-center text-2xl">📦</div>
              <div>
                <p class="text-sm text-gray-500">{{ t('production.totalUnits') }}</p>
                <p class="text-2xl font-bold text-purple-600">{{ productionStore.totalUnits }}</p>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- Result table -->
      <BaseCard>
        <template #header>
          <h3 class="text-lg font-semibold text-gray-800 flex items-center gap-2">
            📋 {{ t('production.result') }}
          </h3>
        </template>
        <template v-if="productionStore.suggestions.length > 0">
          <ProductionResult :suggestions="productionStore.suggestions" />
          <!-- Grand total row -->
          <div class="mt-4 pt-4 border-t border-gray-200 flex justify-end">
            <div class="text-right">
              <p class="text-sm text-gray-500">{{ t('production.grandTotal') }}</p>
              <p class="text-2xl font-bold text-green-600">{{ formatCurrency(productionStore.grandTotal) }}</p>
            </div>
          </div>
        </template>
        <template v-else>
          <div class="text-center py-12">
            <div class="text-5xl mb-4">😕</div>
            <p class="text-gray-500 text-lg">{{ t('production.noSuggestions') }}</p>
            <p class="text-gray-400 text-sm mt-2">{{ t('production.noSuggestionsHint') }}</p>
          </div>
        </template>
      </BaseCard>
    </template>

    <!-- Stock overview - at the bottom -->
    <BaseCard>
      <template #header>
        <h3 class="text-lg font-semibold text-gray-800 flex items-center gap-2">
          📦 {{ t('production.stockOverview') }}
        </h3>
      </template>
      <StockOverview :items="rawMaterialStore.items" :loading="rawMaterialStore.loading" />
    </BaseCard>
  </div>
</template>

