import {defineStore} from 'pinia'
import rawMaterialService from '@/services/rawMaterialService'

export const useRawMaterialStore = defineStore('rawMaterial', {
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
        const { data } = await rawMaterialService.getAll()
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
        const { data } = await rawMaterialService.getById(id)
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
        const { data } = await rawMaterialService.create(payload)
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
        const { data } = await rawMaterialService.update(id, payload)
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
        await rawMaterialService.delete(id)
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

