import {defineStore} from 'pinia'
import productService from '@/services/productService'

export const useProductStore = defineStore('product', {
  state: () => ({
    items: [],
    current: null,
    loading: false,
    error: null,
  }),

  getters: {
    getById: (state) => (id) => state.items.find((item) => item.id === id),
  },

  actions: {
    async fetchAll() {
      this.loading = true
      this.error = null
      try {
        const { data } = await productService.getAll()
        this.items = data
      } catch (err) {
        this.error = err.response?.data?.message || err.message
        throw err
      } finally {
        this.loading = false
      }
    },

    async fetchById(id) {
      this.loading = true
      this.error = null
      try {
        const { data } = await productService.getById(id)
        this.current = data
        return data
      } catch (err) {
        this.error = err.response?.data?.message || err.message
        throw err
      } finally {
        this.loading = false
      }
    },

    async create(payload) {
      this.loading = true
      this.error = null
      try {
        const { data } = await productService.create(payload)
        this.items.push(data)
        return data
      } catch (err) {
        this.error = err.response?.data?.message || err.message
        throw err
      } finally {
        this.loading = false
      }
    },

    async update(id, payload) {
      this.loading = true
      this.error = null
      try {
        const { data } = await productService.update(id, payload)
        const index = this.items.findIndex((item) => item.id === id)
        if (index !== -1) this.items[index] = data
        return data
      } catch (err) {
        this.error = err.response?.data?.message || err.message
        throw err
      } finally {
        this.loading = false
      }
    },

    async remove(id) {
      this.loading = true
      this.error = null
      try {
        await productService.delete(id)
        this.items = this.items.filter((item) => item.id !== id)
      } catch (err) {
        this.error = err.response?.data?.message || err.message
        throw err
      } finally {
        this.loading = false
      }
    },
  },
})

