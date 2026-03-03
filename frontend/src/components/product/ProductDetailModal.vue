<script setup>
import {useI18n} from 'vue-i18n'
import {formatCurrency} from '@/utils/formatters'

const { t } = useI18n()

defineProps({
  visible: { type: Boolean, default: false },
  product: { type: Object, default: null },
})

defineEmits(['close'])
</script>

<template>
  <Teleport to="body">
    <Transition name="modal">
      <div
        v-if="visible && product"
        class="fixed inset-0 z-50 flex items-center justify-center p-4"
      >
        <!-- Overlay -->
        <div
          class="absolute inset-0 bg-black/50 transition-opacity"
          @click="$emit('close')"
        />

        <!-- Modal -->
        <div class="relative bg-white rounded-xl shadow-2xl max-w-lg w-full z-10 overflow-hidden">
          <!-- Header -->
          <div class="bg-blue-600 px-6 py-4">
            <div class="flex items-center justify-between">
              <h3 class="text-lg font-semibold text-white">{{ t('product.detail') }}</h3>
              <button
                class="text-white/80 hover:text-white transition-colors cursor-pointer"
                @click="$emit('close')"
              >
                ✕
              </button>
            </div>
            <p class="text-blue-100 text-sm mt-1">{{ product.code }}</p>
          </div>

          <!-- Body -->
          <div class="p-6 space-y-4 max-h-[70vh] overflow-y-auto">
            <!-- Name & Price -->
            <div class="grid grid-cols-2 gap-4">
              <div>
                <p class="text-xs font-medium text-gray-500 uppercase tracking-wide">{{ t('product.name') }}</p>
                <p class="text-lg font-semibold text-gray-900 mt-1">{{ product.name }}</p>
              </div>
              <div>
                <p class="text-xs font-medium text-gray-500 uppercase tracking-wide">{{ t('product.price') }}</p>
                <p class="text-lg font-semibold text-green-600 mt-1">{{ formatCurrency(product.price) }}</p>
              </div>
            </div>

            <!-- Description -->
            <div>
              <p class="text-xs font-medium text-gray-500 uppercase tracking-wide">{{ t('product.description') }}</p>
              <p class="text-sm text-gray-700 mt-1">
                {{ product.description || t('product.noDescription') }}
              </p>
            </div>

            <!-- Compositions -->
            <div>
              <p class="text-xs font-medium text-gray-500 uppercase tracking-wide mb-2">
                {{ t('product.compositions') }} ({{ product.compositions?.length || 0 }})
              </p>

              <div v-if="product.compositions && product.compositions.length > 0" class="space-y-2">
                <div
                  v-for="comp in product.compositions"
                  :key="comp.id || comp.rawMaterial?.id"
                  class="flex items-center justify-between bg-gray-50 rounded-lg px-4 py-2"
                >
                  <div class="flex items-center space-x-2">
                    <span class="text-xs font-mono text-gray-500">{{ comp.rawMaterial?.code }}</span>
                    <span class="text-sm font-medium text-gray-800">{{ comp.rawMaterial?.name }}</span>
                  </div>
                  <span class="text-sm text-gray-600">
                    {{ comp.requiredQuantity }} {{ comp.rawMaterial?.unitOfMeasure || '' }}
                  </span>
                </div>
              </div>
              <p v-else class="text-sm text-gray-400 italic">{{ t('product.noCompositions') }}</p>
            </div>
          </div>

          <!-- Footer -->
          <div class="px-6 py-4 bg-gray-50 flex justify-end">
            <button
              class="px-4 py-2 text-sm font-medium text-gray-700 bg-white border border-gray-300 rounded-lg hover:bg-gray-100 transition-colors cursor-pointer"
              @click="$emit('close')"
            >
              {{ t('product.close') }}
            </button>
          </div>
        </div>
      </div>
    </Transition>
  </Teleport>
</template>

<style scoped>
.modal-enter-active,
.modal-leave-active {
  transition: opacity 0.2s ease;
}
.modal-enter-from,
.modal-leave-to {
  opacity: 0;
}
</style>

