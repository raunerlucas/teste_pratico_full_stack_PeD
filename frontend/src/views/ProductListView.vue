<script setup>
import {onMounted, ref} from 'vue'
import {useRouter} from 'vue-router'
import {useI18n} from 'vue-i18n'
import {useProductStore} from '@/stores/productStore'
import ProductTable from '@/components/product/ProductTable.vue'
import ProductDetailModal from '@/components/product/ProductDetailModal.vue'
import BaseButton from '@/components/common/BaseButton.vue'
import BaseModal from '@/components/common/BaseModal.vue'
import BaseAlert from '@/components/common/BaseAlert.vue'
import {getErrorI18nKey, parseApiError} from '@/utils/errorHandler'

const { t } = useI18n()
const router = useRouter()
const store = useProductStore()

const showDeleteModal = ref(false)
const itemToDelete = ref(null)
const showDetailModal = ref(false)
const selectedProduct = ref(null)
const alert = ref({ show: false, type: 'success', message: '' })

onMounted(() => {
  store.fetchAll()
})

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
    <div class="flex items-center justify-between">
      <h1 class="text-2xl font-bold text-gray-900">{{ t('product.title') }}</h1>
      <BaseButton variant="primary" @click="router.push('/products/new')">
        + {{ t('product.new') }}
      </BaseButton>
    </div>

    <BaseAlert
      v-if="alert.show"
      :type="alert.type"
      :message="alert.message"
      dismissible
      @dismiss="alert.show = false"
    />

    <BaseAlert v-if="store.error && !alert.show" type="error" :message="t('product.errorLoad')" />

    <ProductTable
      :items="store.items"
      :loading="store.loading"
      @edit="handleEdit"
      @delete="handleDeleteClick"
      @row-click="handleRowClick"
    />

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

    <ProductDetailModal
      :visible="showDetailModal"
      :product="selectedProduct"
      @close="showDetailModal = false"
    />
  </div>
</template>

