<script setup>
import {useI18n} from 'vue-i18n'
import BaseButton from '@/components/common/BaseButton.vue'

const { t } = useI18n()

const props = defineProps({
  compositions: { type: Array, default: () => [] },
  rawMaterials: { type: Array, default: () => [] },
})

const emit = defineEmits(['add', 'remove', 'update'])

function handleAdd() {
  emit('add')
}

function handleRemove(index) {
  emit('remove', index)
}

function handleUpdate(index, field, value) {
  emit('update', { index, field, value })
}
</script>

<template>
  <div class="space-y-3">
    <div class="flex items-center justify-between">
      <h4 class="text-sm font-semibold text-gray-700">{{ t('product.compositions') }}</h4>
      <BaseButton size="sm" variant="outline" @click="handleAdd">
        + {{ t('product.addComposition') }}
      </BaseButton>
    </div>

    <div v-if="compositions.length === 0" class="text-sm text-gray-500 py-4 text-center border border-dashed border-gray-300 rounded-lg">
      {{ t('product.noCompositions') }}
    </div>

    <div
      v-for="(comp, index) in compositions"
      :key="index"
      class="flex items-end space-x-3 p-3 bg-gray-50 rounded-lg border border-gray-200"
    >
      <div class="flex-1">
        <label class="block text-xs font-medium text-gray-600 mb-1">
          {{ t('product.selectRawMaterial') }}
        </label>
        <select
          :value="comp.rawMaterialId"
          class="w-full px-3 py-2 border border-gray-300 rounded-lg text-sm bg-white focus:outline-none focus:ring-2 focus:ring-blue-500"
          @change="handleUpdate(index, 'rawMaterialId', Number($event.target.value))"
        >
          <option value="" disabled>{{ t('product.selectRawMaterial') }}</option>
          <option v-for="rm in rawMaterials" :key="rm.id" :value="rm.id">
            {{ rm.code }} - {{ rm.name }} ({{ rm.stockQuantity }})
          </option>
        </select>
      </div>

      <div class="w-32">
        <label class="block text-xs font-medium text-gray-600 mb-1">
          {{ t('product.requiredQuantity') }}
        </label>
        <input
          type="number"
          :value="comp.requiredQuantity"
          :placeholder="t('product.quantityPlaceholder')"
          min="0"
          step="0.01"
          class="w-full px-3 py-2 border border-gray-300 rounded-lg text-sm focus:outline-none focus:ring-2 focus:ring-blue-500"
          @input="handleUpdate(index, 'requiredQuantity', Number($event.target.value))"
        />
      </div>

      <button
        type="button"
        class="px-3 py-2 text-sm text-red-600 hover:text-red-800 hover:bg-red-50 rounded-lg transition-colors cursor-pointer"
        @click="handleRemove(index)"
      >
        {{ t('product.removeComposition') }}
      </button>
    </div>
  </div>
</template>


