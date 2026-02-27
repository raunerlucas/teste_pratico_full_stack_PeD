<script setup>
import {useI18n} from 'vue-i18n'
import BaseTable from '@/components/common/BaseTable.vue'
import {formatCurrency} from '@/utils/formatters'

const { t } = useI18n()

defineProps({
  items: { type: Array, default: () => [] },
  loading: { type: Boolean, default: false },
})

defineEmits(['edit', 'delete'])

const columns = [
  { key: 'code', label: t('product.code') },
  { key: 'name', label: t('product.name') },
  {
    key: 'price',
    label: t('product.price'),
    formatter: (val) => formatCurrency(val),
    class: 'text-right',
  },
  {
    key: 'compositions',
    label: t('product.compositionCount'),
    formatter: (val) => (val ? val.length : 0),
    class: 'text-center',
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

