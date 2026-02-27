<script setup>
import {reactive, watch} from 'vue'
import {useI18n} from 'vue-i18n'
import BaseInput from '@/components/common/BaseInput.vue'
import BaseButton from '@/components/common/BaseButton.vue'
import {hasErrors, validateRawMaterial} from '@/utils/validators'

const { t } = useI18n()

const props = defineProps({
  initialData: { type: Object, default: null },
  loading: { type: Boolean, default: false },
})

const emit = defineEmits(['submit', 'cancel'])

const form = reactive({
  code: '',
  name: '',
  stockQuantity: 0,
})

const errors = reactive({})

watch(
  () => props.initialData,
  (data) => {
    if (data) {
      form.code = data.code || ''
      form.name = data.name || ''
      form.stockQuantity = data.stockQuantity ?? 0
    }
  },
  { immediate: true },
)

function clearErrors() {
  Object.keys(errors).forEach((key) => delete errors[key])
}

function handleSubmit() {
  clearErrors()
  const validation = validateRawMaterial(form)
  if (hasErrors(validation)) {
    Object.assign(errors, validation)
    return
  }
  emit('submit', {
    code: form.code.trim(),
    name: form.name.trim(),
    stockQuantity: Number(form.stockQuantity),
  })
}
</script>

<template>
  <form @submit.prevent="handleSubmit" class="space-y-4">
    <BaseInput
      v-model="form.code"
      :label="t('rawMaterial.code')"
      :placeholder="t('rawMaterial.codePlaceholder')"
      :error="errors.code ? t('common.' + errors.code) : ''"
      required
    />
    <BaseInput
      v-model="form.name"
      :label="t('rawMaterial.name')"
      :placeholder="t('rawMaterial.namePlaceholder')"
      :error="errors.name ? t('common.' + errors.name) : ''"
      required
    />
    <BaseInput
      v-model="form.stockQuantity"
      :label="t('rawMaterial.stockQuantity')"
      :placeholder="t('rawMaterial.stockPlaceholder')"
      :error="errors.stockQuantity ? t('common.' + errors.stockQuantity) : ''"
      type="number"
      required
    />
    <div class="flex items-center space-x-3 pt-4">
      <BaseButton type="submit" variant="primary" :loading="loading">
        {{ t('common.save') }}
      </BaseButton>
      <BaseButton variant="outline" @click="$emit('cancel')">
        {{ t('common.cancel') }}
      </BaseButton>
    </div>
  </form>
</template>

