import {beforeEach, describe, expect, it, vi} from 'vitest'
import {createPinia, setActivePinia} from 'pinia'
import {useProductionStore} from '@/stores/productionStore'
import productionService from '@/services/productionService'

vi.mock('@/services/productionService', () => ({
  default: {
    optimize: vi.fn(),
  },
}))

describe('productionStore', () => {
  beforeEach(() => {
    setActivePinia(createPinia())
    vi.clearAllMocks()
  })

  it('fetchOptimization sets suggestions and calculated flag', async () => {
    const data = [
      { productCode: 'PRD001', productName: 'Pão', quantity: 5, unitPrice: 12.5, totalValue: 62.5 },
      { productCode: 'PRD002', productName: 'Bolo', quantity: 2, unitPrice: 35.0, totalValue: 70.0 },
    ]
    productionService.optimize.mockResolvedValue({ data })

    const store = useProductionStore()
    await store.fetchOptimization()

    expect(store.suggestions).toEqual(data)
    expect(store.calculated).toBe(true)
    expect(store.loading).toBe(false)
    expect(store.error).toBeNull()
  })

  it('fetchOptimization sets error on failure', async () => {
    productionService.optimize.mockRejectedValue(new Error('Server error'))

    const store = useProductionStore()
    await expect(store.fetchOptimization()).rejects.toThrow()

    expect(store.error).toBe('Server error')
    expect(store.calculated).toBe(false)
    expect(store.loading).toBe(false)
  })

  it('grandTotal getter sums all totalValues', () => {
    const store = useProductionStore()
    store.suggestions = [
      { totalValue: 62.5 },
      { totalValue: 70.0 },
    ]

    expect(store.grandTotal).toBe(132.5)
  })

  it('totalProducts getter returns suggestion count', () => {
    const store = useProductionStore()
    store.suggestions = [
      { productCode: 'PRD001' },
      { productCode: 'PRD002' },
    ]

    expect(store.totalProducts).toBe(2)
  })

  it('totalUnits getter sums all quantities', () => {
    const store = useProductionStore()
    store.suggestions = [
      { quantity: 5 },
      { quantity: 2 },
    ]

    expect(store.totalUnits).toBe(7)
  })

  it('reset clears suggestions and flags', () => {
    const store = useProductionStore()
    store.suggestions = [{ productCode: 'PRD001' }]
    store.calculated = true
    store.error = 'some error'

    store.reset()

    expect(store.suggestions).toEqual([])
    expect(store.calculated).toBe(false)
    expect(store.error).toBeNull()
  })

  it('grandTotal returns 0 when no suggestions', () => {
    const store = useProductionStore()
    expect(store.grandTotal).toBe(0)
  })
})

