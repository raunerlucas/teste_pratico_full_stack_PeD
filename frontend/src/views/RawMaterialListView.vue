<script setup>
import {computed, onMounted, ref} from 'vue'
import {useRouter} from 'vue-router'
import {useI18n} from 'vue-i18n'
import {useRawMaterialStore} from '@/stores/rawMaterialStore'
import BaseButton from '@/components/common/BaseButton.vue'
import BaseModal from '@/components/common/BaseModal.vue'
import BaseAlert from '@/components/common/BaseAlert.vue'
import BaseCard from '@/components/common/BaseCard.vue'
import {formatNumber} from '@/utils/formatters'
import {getErrorI18nKey, parseApiError} from '@/utils/errorHandler'

const { t } = useI18n()
const router = useRouter()
const store = useRawMaterialStore()

const showDeleteModal = ref(false)
const itemToDelete = ref(null)
const alert = ref({ show: false, type: 'success', message: '' })
const search = ref('')

onMounted(() => {
  store.fetchAll()
})

const filteredItems = computed(() => {
  if (!search.value.trim()) return store.items
  const q = search.value.toLowerCase()
  return store.items.filter(
    (item) =>
      item.code?.toLowerCase().includes(q) ||
      item.name?.toLowerCase().includes(q) ||
      item.unitOfMeasure?.toLowerCase().includes(q),
  )
})

const stats = computed(() => {
  const total = store.items.length
  const lowStock = store.items.filter((i) => (i.stockQuantity || 0) > 0 && (i.stockQuantity || 0) <= 10).length
  const zeroStock = store.items.filter((i) => (i.stockQuantity || 0) === 0).length
  const totalStock = store.items.reduce((sum, i) => sum + (i.stockQuantity || 0), 0)
  return { total, lowStock, zeroStock, totalStock }
})

function getStockLevel(quantity) {
  if (quantity === 0) return 'empty'
  if (quantity <= 10) return 'low'
  if (quantity <= 50) return 'medium'
  return 'high'
}

function getStockBadgeClass(quantity) {
  const map = {
    empty: 'bg-red-100 text-red-800',
    low: 'bg-yellow-100 text-yellow-800',
    medium: 'bg-blue-100 text-blue-800',
    high: 'bg-green-100 text-green-800',
  }
  return map[getStockLevel(quantity)]
}

function getStockLabel(quantity) {
  const map = {
    empty: t('rawMaterial.statusEmpty'),
    low: t('rawMaterial.statusLow'),
    medium: t('rawMaterial.statusMedium'),
    high: t('rawMaterial.statusAdequate'),
  }
  return map[getStockLevel(quantity)]
}

function handleEdit(item) {
  router.push(`/raw-materials/${item.id}/edit`)
}

function handleDeleteClick(item) {
  itemToDelete.value = item
  showDeleteModal.value = true
}

async function confirmDelete() {
  try {
    await store.remove(itemToDelete.value.id)
    showAlert('success', t('rawMaterial.deleted'))
  } catch (err) {
    const errorInfo = parseApiError(err)
    const key = getErrorI18nKey(errorInfo, 'rawMaterial', 'delete')
    showAlert('error', t(key))
  } finally {
    showDeleteModal.value = false
    itemToDelete.value = null
  }
}

function showAlert(type, message) {
  alert.value = { show: true, type, message }
  setTimeout(() => {
    alert.value.show = false
  }, 5000)
}
</script>

<template>
  <div class="space-y-6">
    <!-- Header -->
    <div class="flex items-center justify-between">
      <div>
        <h1 class="text-2xl font-bold text-gray-900">{{ t('rawMaterial.title') }}</h1>
        <p class="mt-1 text-gray-500 text-sm">{{ t('rawMaterial.listDescription') }}</p>
      </div>
      <BaseButton variant="primary" @click="router.push('/raw-materials/new')">
        + {{ t('rawMaterial.new') }}
      </BaseButton>
    </div>

    <!-- Stats cards -->
    <div class="grid grid-cols-2 sm:grid-cols-4 gap-4">
      <div class="bg-white rounded-xl shadow-sm border border-gray-200 p-4">
        <div class="flex items-center space-x-3">
          <div class="w-10 h-10 bg-blue-100 rounded-lg flex items-center justify-center text-lg">📦</div>
          <div>
            <p class="text-2xl font-bold text-gray-800">{{ stats.total }}</p>
            <p class="text-xs text-gray-500">{{ t('rawMaterial.statsTotal') }}</p>
          </div>
        </div>
      </div>
      <div class="bg-white rounded-xl shadow-sm border border-gray-200 p-4">
        <div class="flex items-center space-x-3">
          <div class="w-10 h-10 bg-green-100 rounded-lg flex items-center justify-center text-lg">📊</div>
          <div>
            <p class="text-2xl font-bold text-green-600">{{ formatNumber(stats.totalStock) }}</p>
            <p class="text-xs text-gray-500">{{ t('rawMaterial.statsTotalStock') }}</p>
          </div>
        </div>
      </div>
      <div class="bg-white rounded-xl shadow-sm border border-gray-200 p-4">
        <div class="flex items-center space-x-3">
          <div class="w-10 h-10 bg-yellow-100 rounded-lg flex items-center justify-center text-lg">⚠️</div>
          <div>
            <p class="text-2xl font-bold text-yellow-600">{{ stats.lowStock }}</p>
            <p class="text-xs text-gray-500">{{ t('rawMaterial.statsLowStock') }}</p>
          </div>
        </div>
      </div>
      <div class="bg-white rounded-xl shadow-sm border border-gray-200 p-4">
        <div class="flex items-center space-x-3">
          <div class="w-10 h-10 bg-red-100 rounded-lg flex items-center justify-center text-lg">🚫</div>
          <div>
            <p class="text-2xl font-bold text-red-600">{{ stats.zeroStock }}</p>
            <p class="text-xs text-gray-500">{{ t('rawMaterial.statsZeroStock') }}</p>
          </div>
        </div>
      </div>
    </div>

    <!-- Alerts -->
    <BaseAlert
      v-if="alert.show"
      :type="alert.type"
      :message="alert.message"
      dismissible
      @dismiss="alert.show = false"
    />
    <BaseAlert v-if="store.error && !alert.show" type="error" :message="t('rawMaterial.errorLoad')" />

    <!-- Search + Table card -->
    <BaseCard>
      <!-- Search bar -->
      <div class="mb-4">
        <div class="relative">
          <span class="absolute inset-y-0 left-0 flex items-center pl-3 text-gray-400">🔍</span>
          <input
            v-model="search"
            type="text"
            :placeholder="t('rawMaterial.searchPlaceholder')"
            class="w-full pl-10 pr-4 py-2 border border-gray-300 rounded-lg text-sm focus:ring-2 focus:ring-blue-500 focus:border-blue-500 transition-colors"
          />
        </div>
      </div>

      <!-- Loading -->
      <div v-if="store.loading" class="flex items-center justify-center py-8 text-gray-500">
        <svg class="animate-spin h-5 w-5 mr-2" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24">
          <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4" />
          <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4z" />
        </svg>
        {{ t('common.loading') }}
      </div>

      <!-- Table -->
      <div v-else class="overflow-x-auto rounded-lg border border-gray-200 shadow-sm">
        <table class="min-w-full divide-y divide-gray-200">
          <thead class="bg-gray-50">
            <tr>
              <th class="px-4 py-3 text-left text-xs font-semibold text-gray-600 uppercase tracking-wider">{{ t('rawMaterial.code') }}</th>
              <th class="px-4 py-3 text-left text-xs font-semibold text-gray-600 uppercase tracking-wider">{{ t('rawMaterial.name') }}</th>
              <th class="px-4 py-3 text-right text-xs font-semibold text-gray-600 uppercase tracking-wider">{{ t('rawMaterial.stockQuantity') }}</th>
              <th class="px-4 py-3 text-center text-xs font-semibold text-gray-600 uppercase tracking-wider">{{ t('rawMaterial.unitOfMeasure') }}</th>
              <th class="px-4 py-3 text-center text-xs font-semibold text-gray-600 uppercase tracking-wider">{{ t('rawMaterial.statusLabel') }}</th>
              <th class="px-4 py-3 text-center text-xs font-semibold text-gray-600 uppercase tracking-wider">{{ t('common.actions') }}</th>
            </tr>
          </thead>
          <tbody class="bg-white divide-y divide-gray-200">
            <tr v-if="filteredItems.length === 0">
              <td colspan="6" class="px-4 py-8 text-center text-gray-500 text-sm">
                {{ search ? t('rawMaterial.noSearchResults') : t('common.noData') }}
              </td>
            </tr>
            <tr
              v-for="item in filteredItems"
              :key="item.id"
              class="hover:bg-gray-50 transition-colors"
            >
              <td class="px-4 py-3 whitespace-nowrap text-sm font-mono text-gray-700">{{ item.code }}</td>
              <td class="px-4 py-3 whitespace-nowrap text-sm text-gray-800 font-medium">{{ item.name }}</td>
              <td class="px-4 py-3 whitespace-nowrap text-sm text-right text-gray-800 font-medium">
                {{ formatNumber(item.stockQuantity) }}
              </td>
              <td class="px-4 py-3 whitespace-nowrap text-sm text-center text-gray-600">
                {{ item.unitOfMeasure || '-' }}
              </td>
              <td class="px-4 py-3 whitespace-nowrap text-center">
                <span
                  :class="getStockBadgeClass(item.stockQuantity)"
                  class="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium"
                >
                  {{ getStockLabel(item.stockQuantity) }}
                </span>
              </td>
              <td class="px-4 py-3 whitespace-nowrap text-center text-sm space-x-2">
                <button
                  class="text-blue-600 hover:text-blue-800 font-medium cursor-pointer"
                  @click="handleEdit(item)"
                >
                  {{ t('common.edit') }}
                </button>
                <button
                  class="text-red-600 hover:text-red-800 font-medium cursor-pointer"
                  @click="handleDeleteClick(item)"
                >
                  {{ t('common.delete') }}
                </button>
              </td>
            </tr>
          </tbody>
        </table>
      </div>

      <!-- Result count -->
      <p v-if="!store.loading && search && filteredItems.length > 0" class="text-xs text-gray-400 mt-2 text-right">
        {{ t('rawMaterial.showingResults', { count: filteredItems.length, total: store.items.length }) }}
      </p>
    </BaseCard>

    <!-- Delete modal -->
    <BaseModal
      :visible="showDeleteModal"
      :title="t('common.confirmDeleteTitle')"
      :confirm-text="t('common.yes')"
      :cancel-text="t('common.no')"
      @confirm="confirmDelete"
      @cancel="showDeleteModal = false"
    >
      <p>{{ t('rawMaterial.deleteConfirm', { name: itemToDelete?.name }) }}</p>
    </BaseModal>
  </div>
</template>

