<script setup>
import {computed, onMounted, ref} from 'vue'
import {useRouter} from 'vue-router'
import {useI18n} from 'vue-i18n'
import {useProductStore} from '@/stores/productStore'
import ProductDetailModal from '@/components/product/ProductDetailModal.vue'
import BaseButton from '@/components/common/BaseButton.vue'
import BaseModal from '@/components/common/BaseModal.vue'
import BaseAlert from '@/components/common/BaseAlert.vue'
import BaseCard from '@/components/common/BaseCard.vue'
import {formatCurrency} from '@/utils/formatters'
import {getErrorI18nKey, parseApiError} from '@/utils/errorHandler'

const { t } = useI18n()
const router = useRouter()
const store = useProductStore()

const showDeleteModal = ref(false)
const itemToDelete = ref(null)
const showDetailModal = ref(false)
const selectedProduct = ref(null)
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
      item.description?.toLowerCase().includes(q),
  )
})

const stats = computed(() => {
  const total = store.items.length
  const totalCompositions = store.items.reduce((sum, p) => sum + (p.compositions?.length || 0), 0)
  const avgPrice = total > 0
    ? store.items.reduce((sum, p) => sum + (p.price || 0), 0) / total
    : 0
  const maxPrice = total > 0
    ? Math.max(...store.items.map((p) => p.price || 0))
    : 0
  return { total, totalCompositions, avgPrice, maxPrice }
})

function getCompositionBadgeClass(count) {
  if (count === 0) return 'bg-red-100 text-red-800'
  if (count <= 2) return 'bg-yellow-100 text-yellow-800'
  return 'bg-green-100 text-green-800'
}

function getCompositionLabel(count) {
  if (count === 0) return t('product.statusNoComposition')
  if (count <= 2) return `${count} ${count === 1 ? t('product.statusMaterial') : t('product.statusMaterials')}`
  return `${count} ${t('product.statusMaterials')}`
}

function handleEdit(item) {
  router.push(`/products/${item.id}/edit`)
}

function handleRowClick(item) {
  selectedProduct.value = item
  showDetailModal.value = true
}

function handleDeleteClick(item) {
  itemToDelete.value = item
  showDeleteModal.value = true
}

async function confirmDelete() {
  try {
    await store.remove(itemToDelete.value.id)
    showAlert('success', t('product.deleted'))
  } catch (err) {
    const errorInfo = parseApiError(err)
    const key = getErrorI18nKey(errorInfo, 'product', 'delete')
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
        <h1 class="text-2xl font-bold text-gray-900">{{ t('product.title') }}</h1>
        <p class="mt-1 text-gray-500 text-sm">{{ t('product.listDescription') }}</p>
      </div>
      <BaseButton variant="primary" @click="router.push('/products/new')">
        + {{ t('product.new') }}
      </BaseButton>
    </div>

    <!-- Stats cards -->
    <div class="grid grid-cols-2 sm:grid-cols-4 gap-4">
      <div class="bg-white rounded-xl shadow-sm border border-gray-200 p-4">
        <div class="flex items-center space-x-3">
          <div class="w-10 h-10 bg-blue-100 rounded-lg flex items-center justify-center text-lg">🏭</div>
          <div>
            <p class="text-2xl font-bold text-gray-800">{{ stats.total }}</p>
            <p class="text-xs text-gray-500">{{ t('product.statsTotal') }}</p>
          </div>
        </div>
      </div>
      <div class="bg-white rounded-xl shadow-sm border border-gray-200 p-4">
        <div class="flex items-center space-x-3">
          <div class="w-10 h-10 bg-green-100 rounded-lg flex items-center justify-center text-lg">💰</div>
          <div>
            <p class="text-2xl font-bold text-green-600">{{ formatCurrency(stats.avgPrice) }}</p>
            <p class="text-xs text-gray-500">{{ t('product.statsAvgPrice') }}</p>
          </div>
        </div>
      </div>
      <div class="bg-white rounded-xl shadow-sm border border-gray-200 p-4">
        <div class="flex items-center space-x-3">
          <div class="w-10 h-10 bg-purple-100 rounded-lg flex items-center justify-center text-lg">🏆</div>
          <div>
            <p class="text-2xl font-bold text-purple-600">{{ formatCurrency(stats.maxPrice) }}</p>
            <p class="text-xs text-gray-500">{{ t('product.statsMaxPrice') }}</p>
          </div>
        </div>
      </div>
      <div class="bg-white rounded-xl shadow-sm border border-gray-200 p-4">
        <div class="flex items-center space-x-3">
          <div class="w-10 h-10 bg-orange-100 rounded-lg flex items-center justify-center text-lg">🧩</div>
          <div>
            <p class="text-2xl font-bold text-orange-600">{{ stats.totalCompositions }}</p>
            <p class="text-xs text-gray-500">{{ t('product.statsTotalCompositions') }}</p>
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
    <BaseAlert v-if="store.error && !alert.show" type="error" :message="t('product.errorLoad')" />

    <!-- Search + Table card -->
    <BaseCard>
      <!-- Search bar -->
      <div class="mb-4">
        <div class="relative">
          <span class="absolute inset-y-0 left-0 flex items-center pl-3 text-gray-400">🔍</span>
          <input
            v-model="search"
            type="text"
            :placeholder="t('product.searchPlaceholder')"
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
              <th class="px-4 py-3 text-left text-xs font-semibold text-gray-600 uppercase tracking-wider">{{ t('product.code') }}</th>
              <th class="px-4 py-3 text-left text-xs font-semibold text-gray-600 uppercase tracking-wider">{{ t('product.name') }}</th>
              <th class="px-4 py-3 text-right text-xs font-semibold text-gray-600 uppercase tracking-wider">{{ t('product.price') }}</th>
              <th class="px-4 py-3 text-center text-xs font-semibold text-gray-600 uppercase tracking-wider">{{ t('product.compositions') }}</th>
              <th class="px-4 py-3 text-center text-xs font-semibold text-gray-600 uppercase tracking-wider">{{ t('common.actions') }}</th>
            </tr>
          </thead>
          <tbody class="bg-white divide-y divide-gray-200">
            <tr v-if="filteredItems.length === 0">
              <td colspan="5" class="px-4 py-8 text-center text-gray-500 text-sm">
                {{ search ? t('product.noSearchResults') : t('common.noData') }}
              </td>
            </tr>
            <tr
              v-for="item in filteredItems"
              :key="item.id"
              class="hover:bg-gray-50 transition-colors cursor-pointer"
              @click="handleRowClick(item)"
            >
              <td class="px-4 py-3 whitespace-nowrap text-sm font-mono text-gray-700">{{ item.code }}</td>
              <td class="px-4 py-3 whitespace-nowrap text-sm text-gray-800 font-medium">{{ item.name }}</td>
              <td class="px-4 py-3 whitespace-nowrap text-sm text-right text-gray-800 font-medium">
                {{ formatCurrency(item.price) }}
              </td>
              <td class="px-4 py-3 whitespace-nowrap text-center">
                <span
                  :class="getCompositionBadgeClass(item.compositions?.length || 0)"
                  class="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium"
                >
                  {{ getCompositionLabel(item.compositions?.length || 0) }}
                </span>
              </td>
              <td class="px-4 py-3 whitespace-nowrap text-center text-sm space-x-2">
                <button
                  class="text-blue-600 hover:text-blue-800 font-medium cursor-pointer"
                  @click.stop="handleEdit(item)"
                >
                  {{ t('common.edit') }}
                </button>
                <button
                  class="text-red-600 hover:text-red-800 font-medium cursor-pointer"
                  @click.stop="handleDeleteClick(item)"
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
        {{ t('product.showingResults', { count: filteredItems.length, total: store.items.length }) }}
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
      <p>{{ t('product.deleteConfirm', { name: itemToDelete?.name }) }}</p>
    </BaseModal>

    <!-- Detail modal -->
    <ProductDetailModal
      :visible="showDetailModal"
      :product="selectedProduct"
      @close="showDetailModal = false"
    />
  </div>
</template>

