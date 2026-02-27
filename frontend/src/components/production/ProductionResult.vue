<script setup>
import {useI18n} from 'vue-i18n'
import BaseTable from '@/components/common/BaseTable.vue'
import {formatCurrency} from '@/utils/formatters'

const { t } = useI18n()

defineProps({
  suggestions: { type: Array, default: () => [] },
})

const columns = [
  { key: 'productCode', label: t('production.productCode') },
  { key: 'productName', label: t('production.productName') },
  { key: 'quantity', label: t('production.quantity'), class: 'text-center' },
  {
    key: 'unitPrice',
    label: t('production.unitPrice'),
    formatter: (val) => formatCurrency(val),
    class: 'text-right',
  },
  {
    key: 'totalValue',
    label: t('production.totalValue'),
    formatter: (val) => formatCurrency(val),
    class: 'text-right',
  },
]
</script>

<template>
  <div>
    <BaseTable :columns="columns" :rows="suggestions" :show-actions="false" />
  </div>
</template>

