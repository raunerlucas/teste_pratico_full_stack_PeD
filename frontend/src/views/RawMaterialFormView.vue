<script setup>
import {computed, onMounted, ref} from 'vue'
import {useRoute, useRouter} from 'vue-router'
import {useI18n} from 'vue-i18n'
import {useRawMaterialStore} from '@/stores/rawMaterialStore'
import RawMaterialForm from '@/components/rawMaterial/RawMaterialForm.vue'
import BaseCard from '@/components/common/BaseCard.vue'
import BaseAlert from '@/components/common/BaseAlert.vue'

const { t } = useI18n()
const route = useRoute()
const router = useRouter()
const store = useRawMaterialStore()

const alert = ref({ show: false, type: 'success', message: '' })
const initialData = ref(null)

const isEditing = computed(() => !!route.params.id)
const pageTitle = computed(() => (isEditing.value ? t('rawMaterial.edit') : t('rawMaterial.new')))

onMounted(async () => {
  if (isEditing.value) {
    try {
      const data = await store.fetchById(route.params.id)
      initialData.value = data
    } catch {
      showAlert('error', t('common.error'))
    }
  }
})

async function handleSubmit(formData) {
  try {
    if (isEditing.value) {
      await store.update(route.params.id, formData)
      showAlert('success', t('rawMaterial.updated'))
    } else {
      await store.create(formData)
      showAlert('success', t('rawMaterial.created'))
    }
    setTimeout(() => router.push('/raw-materials'), 1000)
  } catch {
    showAlert('error', t('common.error'))
  }
}

function showAlert(type, message) {
  alert.value = { show: true, type, message }
}
</script>

<template>
  <div class="max-w-2xl mx-auto space-y-6">
    <div class="flex items-center space-x-3">
      <button
        class="p-2 rounded-lg hover:bg-gray-100 transition-colors cursor-pointer"
        @click="router.push('/raw-materials')"
      >
        ← {{ t('common.back') }}
      </button>
      <h1 class="text-2xl font-bold text-gray-900">{{ pageTitle }}</h1>
    </div>

    <BaseAlert
      v-if="alert.show"
      :type="alert.type"
      :message="alert.message"
      dismissible
      @dismiss="alert.show = false"
    />

    <BaseCard>
      <RawMaterialForm
        :initial-data="initialData"
        :loading="store.loading"
        @submit="handleSubmit"
        @cancel="router.push('/raw-materials')"
      />
    </BaseCard>
  </div>
</template>

