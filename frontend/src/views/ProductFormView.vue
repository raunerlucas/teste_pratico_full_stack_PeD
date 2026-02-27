<script setup>
import {computed, onMounted, reactive, ref} from 'vue'
import {useRoute, useRouter} from 'vue-router'
import {useI18n} from 'vue-i18n'
import {useProductStore} from '@/stores/productStore'
import {useRawMaterialStore} from '@/stores/rawMaterialStore'
import ProductComposition from '@/components/product/ProductComposition.vue'
import BaseInput from '@/components/common/BaseInput.vue'
import BaseButton from '@/components/common/BaseButton.vue'
import BaseCard from '@/components/common/BaseCard.vue'
import BaseAlert from '@/components/common/BaseAlert.vue'
import {hasErrors, validateProduct} from '@/utils/validators'

const { t } = useI18n()
const route = useRoute()
const router = useRouter()
const productStore = useProductStore()
const rawMaterialStore = useRawMaterialStore()

const alert = ref({ show: false, type: 'success', message: '' })
const errors = reactive({})

const form = reactive({
  code: '',
  name: '',
  price: '',
  compositions: [],
})

const isEditing = computed(() => !!route.params.id)
const pageTitle = computed(() => (isEditing.value ? t('product.edit') : t('product.new')))

onMounted(async () => {
  await rawMaterialStore.fetchAll()

  if (isEditing.value) {
    try {
      const data = await productStore.fetchById(route.params.id)
      form.code = data.code || ''
      form.name = data.name || ''
      form.price = data.price ?? ''
      form.compositions = (data.compositions || []).map((c) => ({
        rawMaterialId: c.rawMaterial?.id || c.rawMaterialId,
        requiredQuantity: c.requiredQuantity,
      }))
    } catch {
      showAlert('error', t('common.error'))
    }
  }
})

function addComposition() {
  form.compositions.push({ rawMaterialId: '', requiredQuantity: 0 })
}

function removeComposition(index) {
  form.compositions.splice(index, 1)
}

function updateComposition({ index, field, value }) {
  form.compositions[index][field] = value
}

function clearErrors() {
  Object.keys(errors).forEach((key) => delete errors[key])
}

async function handleSubmit() {
  clearErrors()
  const validation = validateProduct(form)
  if (hasErrors(validation)) {
    Object.assign(errors, validation)
    return
  }

  const payload = {
    code: form.code.trim(),
    name: form.name.trim(),
    price: Number(form.price),
    compositions: form.compositions
      .filter((c) => c.rawMaterialId)
      .map((c) => ({
        rawMaterialId: Number(c.rawMaterialId),
        requiredQuantity: Number(c.requiredQuantity),
      })),
  }

  try {
    if (isEditing.value) {
      await productStore.update(route.params.id, payload)
      showAlert('success', t('product.updated'))
    } else {
      await productStore.create(payload)
      showAlert('success', t('product.created'))
    }
    setTimeout(() => router.push('/products'), 1000)
  } catch {
    showAlert('error', t('common.error'))
  }
}

function showAlert(type, message) {
  alert.value = { show: true, type, message }
}
</script>

<template>
  <div class="max-w-3xl mx-auto space-y-6">
    <div class="flex items-center space-x-3">
      <button
        class="p-2 rounded-lg hover:bg-gray-100 transition-colors cursor-pointer"
        @click="router.push('/products')"
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
      <form @submit.prevent="handleSubmit" class="space-y-6">
        <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
          <BaseInput
            v-model="form.code"
            :label="t('product.code')"
            :placeholder="t('product.codePlaceholder')"
            :error="errors.code ? t('common.' + errors.code) : ''"
            required
          />
          <BaseInput
            v-model="form.name"
            :label="t('product.name')"
            :placeholder="t('product.namePlaceholder')"
            :error="errors.name ? t('common.' + errors.name) : ''"
            required
          />
        </div>

        <BaseInput
          v-model="form.price"
          :label="t('product.price')"
          :placeholder="t('product.pricePlaceholder')"
          :error="errors.price ? t('common.' + errors.price) : ''"
          type="number"
          required
        />

        <hr class="border-gray-200" />

        <ProductComposition
          :compositions="form.compositions"
          :raw-materials="rawMaterialStore.items"
          @add="addComposition"
          @remove="removeComposition"
          @update="updateComposition"
        />

        <div class="flex items-center space-x-3 pt-4">
          <BaseButton type="submit" variant="primary" :loading="productStore.loading">
            {{ t('common.save') }}
          </BaseButton>
          <BaseButton variant="outline" @click="router.push('/products')">
            {{ t('common.cancel') }}
          </BaseButton>
        </div>
      </form>
    </BaseCard>
  </div>
</template>

