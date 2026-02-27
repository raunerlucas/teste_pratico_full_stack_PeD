import {defineStore} from 'pinia'
import productionService from '@/services/productionService'

export const useProductionStore = defineStore('production', {
  state: () => ({
    suggestions: [],
    loading: false,
    error: null,
    calculated: false,
  }),

  getters: {
    grandTotal: (state) =>
      state.suggestions.reduce((sum, s) => sum + (s.totalValue || 0), 0),
    totalProducts: (state) => state.suggestions.length,
    totalUnits: (state) =>
      state.suggestions.reduce((sum, s) => sum + (s.quantity || 0), 0),
  },

  actions: {
    async fetchOptimization() {
      this.loading = true
      this.error = null
      this.calculated = false
      try {
        const { data } = await productionService.optimize()
        this.suggestions = data
        this.calculated = true
      } catch (err) {
        this.error = err.response?.data?.message || err.message
        throw err
      } finally {
        this.loading = false
      }
    },

    reset() {
      this.suggestions = []
      this.calculated = false
      this.error = null
    },
  },
})

