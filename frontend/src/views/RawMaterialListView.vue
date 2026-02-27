<script setup>
import {onMounted, ref} from 'vue'
import {useRouter} from 'vue-router'
import {useI18n} from 'vue-i18n'
import {useRawMaterialStore} from '@/stores/rawMaterialStore'
import RawMaterialTable from '@/components/rawMaterial/RawMaterialTable.vue'
import BaseButton from '@/components/common/BaseButton.vue'
import BaseModal from '@/components/common/BaseModal.vue'
import BaseAlert from '@/components/common/BaseAlert.vue'

const { t } = useI18n()
const router = useRouter()
const store = useRawMaterialStore()

const showDeleteModal = ref(false)
const itemToDelete = ref(null)
const alert = ref({ show: false, type: 'success', message: '' })

onMounted(() => {
  store.fetchAll()
})

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
  } catch {
    showAlert('error', t('common.error'))
  } finally {
    showDeleteModal.value = false
    itemToDelete.value = null
  }
}

function showAlert(type, message) {
  alert.value = { show: true, type, message }
  setTimeout(() => {
    alert.value.show = false
  }, 3000)
}
</script>

<template>
  <div class="space-y-6">
    <div class="flex items-center justify-between">
      <h1 class="text-2xl font-bold text-gray-900">{{ t('rawMaterial.title') }}</h1>
      <BaseButton variant="primary" @click="router.push('/raw-materials/new')">
        + {{ t('rawMaterial.new') }}
      </BaseButton>
    </div>

    <BaseAlert
      v-if="alert.show"
      :type="alert.type"
      :message="alert.message"
      dismissible
      @dismiss="alert.show = false"
    />

    <BaseAlert v-if="store.error" type="error" :message="store.error" />

    <RawMaterialTable
      :items="store.items"
      :loading="store.loading"
      @edit="handleEdit"
      @delete="handleDeleteClick"
    />

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

