<script setup>
import {onMounted} from 'vue'
import {useI18n} from 'vue-i18n'
import {useProductionStore} from '@/stores/productionStore'
import {useRawMaterialStore} from '@/stores/rawMaterialStore'
import BaseCard from '@/components/common/BaseCard.vue'
import BaseButton from '@/components/common/BaseButton.vue'
import BaseAlert from '@/components/common/BaseAlert.vue'
import StockOverview from '@/components/production/StockOverview.vue'
import ProductionResult from '@/components/production/ProductionResult.vue'
import {formatCurrency} from '@/utils/formatters'

const { t } = useI18n()
const productionStore = useProductionStore()
const rawMaterialStore = useRawMaterialStore()

onMounted(() => {
  rawMaterialStore.fetchAll()
})

async function handleOptimize() {
  try {
    await productionStore.fetchOptimization()
  } catch {
    // error handled in store
  }
}
</script>

<template>
  <div class="space-y-8">
    <div>
      <h1 class="text-2xl font-bold text-gray-900">{{ t('production.title') }}</h1>
      <p class="mt-1 text-gray-600">{{ t('production.description') }}</p>
    </div>

    <!-- Stock overview -->
    <BaseCard :title="t('production.stockOverview')">
      <StockOverview :items="rawMaterialStore.items" :loading="rawMaterialStore.loading" />
    </BaseCard>

    <!-- Optimize button -->
    <div class="flex justify-center">
      <BaseButton
        variant="primary"
        size="lg"
        :loading="productionStore.loading"
        @click="handleOptimize"
      >
        📈 {{ productionStore.loading ? t('production.optimizing') : t('production.optimize') }}
      </BaseButton>
    </div>

    <!-- Error -->
    <BaseAlert v-if="productionStore.error" type="error" :message="productionStore.error" />

    <!-- Results -->
    <template v-if="productionStore.calculated">
      <!-- Summary cards -->
      <div class="grid grid-cols-1 sm:grid-cols-3 gap-6">
        <BaseCard>
          <div class="text-center">
            <p class="text-sm text-gray-500">{{ t('production.grandTotal') }}</p>
            <p class="text-3xl font-bold text-green-600 mt-1">
              {{ formatCurrency(productionStore.grandTotal) }}
            </p>
          </div>
        </BaseCard>
        <BaseCard>
          <div class="text-center">
            <p class="text-sm text-gray-500">{{ t('production.suggestedProducts') }}</p>
            <p class="text-3xl font-bold text-blue-600 mt-1">{{ productionStore.totalProducts }}</p>
          </div>
        </BaseCard>
        <BaseCard>
          <div class="text-center">
            <p class="text-sm text-gray-500">{{ t('production.quantity') }}</p>
            <p class="text-3xl font-bold text-purple-600 mt-1">{{ productionStore.totalUnits }}</p>
          </div>
        </BaseCard>
      </div>

      <!-- Result table -->
      <BaseCard :title="t('production.result')">
        <template v-if="productionStore.suggestions.length > 0">
          <ProductionResult :suggestions="productionStore.suggestions" />
        </template>
        <template v-else>
          <p class="text-gray-500 text-center py-8">{{ t('production.noSuggestions') }}</p>
        </template>
      </BaseCard>
    </template>
  </div>
</template>

