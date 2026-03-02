<script setup>
import {useI18n} from 'vue-i18n'
import BaseTable from '@/components/common/BaseTable.vue'
import {formatNumber} from '@/utils/formatters'

const { t } = useI18n()

defineProps({
  items: { type: Array, default: () => [] },
  loading: { type: Boolean, default: false },
})

defineEmits(['edit', 'delete'])

const columns = [
  { key: 'code', label: t('rawMaterial.code') },
  { key: 'name', label: t('rawMaterial.name') },
  {
    key: 'stockQuantity',
    label: t('rawMaterial.stockQuantity'),
    formatter: (val, row) => {
      const unit = row?.unitOfMeasure || ''
      return `${formatNumber(val)} ${unit}`
    },
    class: 'text-right',
  },
  {
    key: 'unitOfMeasure',
    label: t('rawMaterial.unitOfMeasure'),
    formatter: (val) => {
      const key = `rawMaterial.units.${val}`
      const translated = t(key)
      return translated !== key ? translated : val || '-'
    },
  },
]
</script>

<template>
  <BaseTable
    :columns="columns"
    :rows="items"
    :loading="loading"
    @edit="$emit('edit', $event)"
    @delete="$emit('delete', $event)"
  />
</template>

