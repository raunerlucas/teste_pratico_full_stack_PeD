<script setup>
import {useI18n} from 'vue-i18n'

const { t } = useI18n()

defineProps({
  columns: {
    type: Array,
    required: true,
    // Each column: { key, label, formatter?, class? }
  },
  rows: { type: Array, default: () => [] },
  loading: { type: Boolean, default: false },
  showActions: { type: Boolean, default: true },
})

defineEmits(['edit', 'delete'])
</script>

<template>
  <div class="overflow-x-auto rounded-lg border border-gray-200 shadow-sm">
    <table class="min-w-full divide-y divide-gray-200">
      <thead class="bg-gray-50">
        <tr>
          <th
            v-for="col in columns"
            :key="col.key"
            class="px-6 py-3 text-left text-xs font-semibold text-gray-600 uppercase tracking-wider"
            :class="col.class"
          >
            {{ col.label }}
          </th>
          <th
            v-if="showActions"
            class="px-6 py-3 text-center text-xs font-semibold text-gray-600 uppercase tracking-wider"
          >
            {{ t('common.actions') }}
          </th>
        </tr>
      </thead>
      <tbody class="bg-white divide-y divide-gray-200">
        <tr v-if="loading">
          <td :colspan="columns.length + (showActions ? 1 : 0)" class="px-6 py-8 text-center">
            <div class="flex items-center justify-center space-x-2 text-gray-500">
              <svg class="animate-spin h-5 w-5" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24">
                <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4" />
                <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4z" />
              </svg>
              <span>{{ t('common.loading') }}</span>
            </div>
          </td>
        </tr>
        <tr v-else-if="rows.length === 0">
          <td
            :colspan="columns.length + (showActions ? 1 : 0)"
            class="px-6 py-8 text-center text-gray-500"
          >
            {{ t('common.noData') }}
          </td>
        </tr>
        <tr
          v-else
          v-for="(row, idx) in rows"
          :key="row.id || idx"
          class="hover:bg-gray-50 transition-colors"
        >
          <td
            v-for="col in columns"
            :key="col.key"
            class="px-6 py-4 whitespace-nowrap text-sm text-gray-800"
            :class="col.class"
          >
            {{ col.formatter ? col.formatter(row[col.key], row) : row[col.key] }}
          </td>
          <td v-if="showActions" class="px-6 py-4 whitespace-nowrap text-center text-sm space-x-2">
            <button
              class="text-blue-600 hover:text-blue-800 font-medium cursor-pointer"
              @click="$emit('edit', row)"
            >
              {{ t('common.edit') }}
            </button>
            <button
              class="text-red-600 hover:text-red-800 font-medium cursor-pointer"
              @click="$emit('delete', row)"
            >
              {{ t('common.delete') }}
            </button>
          </td>
        </tr>
      </tbody>
    </table>
  </div>
</template>

