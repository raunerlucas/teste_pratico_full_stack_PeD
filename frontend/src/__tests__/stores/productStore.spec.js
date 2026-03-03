import {beforeEach, describe, expect, it, vi} from 'vitest'
import {createPinia, setActivePinia} from 'pinia'
import {useProductStore} from '@/stores/productStore'
import productService from '@/services/productService'

vi.mock('@/services/productService', () => ({
  default: {
    getAll: vi.fn(),
    getById: vi.fn(),
    create: vi.fn(),
    update: vi.fn(),
    delete: vi.fn(),
  },
}))

describe('productStore', () => {
  beforeEach(() => {
    setActivePinia(createPinia())
    vi.clearAllMocks()
  })

  it('fetchAll sets items', async () => {
    const data = [
      { id: 1, code: 'PRD001', name: 'Pão', price: 12.5, compositions: [] },
    ]
    productService.getAll.mockResolvedValue({ data })

    const store = useProductStore()
    await store.fetchAll()

    expect(store.items).toEqual(data)
    expect(store.loading).toBe(false)
    expect(store.error).toBeNull()
  })

  it('fetchAll sets error on failure', async () => {
    productService.getAll.mockRejectedValue(new Error('Network error'))

    const store = useProductStore()
    await expect(store.fetchAll()).rejects.toThrow()

    expect(store.error).toBe('Network error')
    expect(store.loading).toBe(false)
  })

  it('fetchById sets current and returns data', async () => {
    const product = { id: 1, code: 'PRD001', name: 'Pão', price: 12.5 }
    productService.getById.mockResolvedValue({ data: product })

    const store = useProductStore()
    const result = await store.fetchById(1)

    expect(result).toEqual(product)
    expect(store.current).toEqual(product)
  })

  it('create adds item to items', async () => {
    const newItem = { id: 2, code: 'PRD002', name: 'Bolo', price: 35.0, compositions: [] }
    productService.create.mockResolvedValue({ data: newItem })

    const store = useProductStore()
    await store.create({ code: 'PRD002', name: 'Bolo', price: 35.0 })

    expect(store.items).toContainEqual(newItem)
  })

  it('create sets error on duplicate code failure', async () => {
    const err = new Error('Conflict')
    err.response = { data: { message: "Product with code 'PRD001' already exists." } }
    productService.create.mockRejectedValue(err)

    const store = useProductStore()
    await expect(store.create({ code: 'PRD001' })).rejects.toThrow()

    expect(store.error).toBe("Product with code 'PRD001' already exists.")
  })

  it('update replaces item in items', async () => {
    const updated = { id: 1, code: 'PRD001', name: 'Pão Premium', price: 15.0 }
    productService.update.mockResolvedValue({ data: updated })

    const store = useProductStore()
    store.items = [{ id: 1, code: 'PRD001', name: 'Pão', price: 12.5 }]
    await store.update(1, { name: 'Pão Premium', price: 15.0 })

    expect(store.items[0].name).toBe('Pão Premium')
    expect(store.items[0].price).toBe(15.0)
  })

  it('remove filters item from items', async () => {
    productService.delete.mockResolvedValue({})

    const store = useProductStore()
    store.items = [
      { id: 1, code: 'PRD001', name: 'Pão', price: 12.5 },
      { id: 2, code: 'PRD002', name: 'Bolo', price: 35.0 },
    ]
    await store.remove(1)

    expect(store.items.length).toBe(1)
    expect(store.items[0].id).toBe(2)
  })

  it('getById getter returns item by id', () => {
    const store = useProductStore()
    store.items = [
      { id: 1, code: 'PRD001', name: 'Pão' },
      { id: 2, code: 'PRD002', name: 'Bolo' },
    ]

    expect(store.getById(1).code).toBe('PRD001')
    expect(store.getById(99)).toBeUndefined()
  })
})

