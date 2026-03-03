import {beforeEach, describe, expect, it, vi} from 'vitest'
import {createPinia, setActivePinia} from 'pinia'
import {useRawMaterialStore} from '@/stores/rawMaterialStore'
import rawMaterialService from '@/services/rawMaterialService'

vi.mock('@/services/rawMaterialService', () => ({
  default: {
    getAll: vi.fn(),
    getById: vi.fn(),
    getNextCode: vi.fn(),
    create: vi.fn(),
    update: vi.fn(),
    delete: vi.fn(),
  },
}))

describe('rawMaterialStore', () => {
  beforeEach(() => {
    setActivePinia(createPinia())
    vi.clearAllMocks()
  })

  it('fetchAll sets items', async () => {
    const data = [
      { id: 1, code: 'MP001', name: 'Farinha', stockQuantity: 500 },
    ]
    rawMaterialService.getAll.mockResolvedValue({ data })

    const store = useRawMaterialStore()
    await store.fetchAll()

    expect(store.items).toEqual(data)
    expect(store.loading).toBe(false)
    expect(store.error).toBeNull()
  })

  it('fetchAll sets error on failure', async () => {
    rawMaterialService.getAll.mockRejectedValue(new Error('Network error'))

    const store = useRawMaterialStore()
    await expect(store.fetchAll()).rejects.toThrow()

    expect(store.error).toBe('Network error')
    expect(store.loading).toBe(false)
  })

  it('fetchNextCode returns next code from service', async () => {
    rawMaterialService.getNextCode.mockResolvedValue({ data: { nextCode: 'MP006' } })

    const store = useRawMaterialStore()
    const result = await store.fetchNextCode()

    expect(result).toBe('MP006')
  })

  it('fetchNextCode returns MP001 on failure', async () => {
    rawMaterialService.getNextCode.mockRejectedValue(new Error('fail'))

    const store = useRawMaterialStore()
    const result = await store.fetchNextCode()

    expect(result).toBe('MP001')
  })

  it('create adds item to items', async () => {
    const newItem = { id: 2, code: 'MP002', name: 'Açúcar', stockQuantity: 200 }
    rawMaterialService.create.mockResolvedValue({ data: newItem })

    const store = useRawMaterialStore()
    await store.create({ code: 'MP002', name: 'Açúcar', stockQuantity: 200 })

    expect(store.items).toContainEqual(newItem)
  })

  it('update replaces item in items', async () => {
    const updated = { id: 1, code: 'MP001', name: 'Farinha Atualizada', stockQuantity: 600 }
    rawMaterialService.update.mockResolvedValue({ data: updated })

    const store = useRawMaterialStore()
    store.items = [{ id: 1, code: 'MP001', name: 'Farinha', stockQuantity: 500 }]
    await store.update(1, { name: 'Farinha Atualizada', stockQuantity: 600 })

    expect(store.items[0].name).toBe('Farinha Atualizada')
  })

  it('remove filters item from items', async () => {
    rawMaterialService.delete.mockResolvedValue({})

    const store = useRawMaterialStore()
    store.items = [
      { id: 1, code: 'MP001', name: 'Farinha', stockQuantity: 500 },
      { id: 2, code: 'MP002', name: 'Açúcar', stockQuantity: 200 },
    ]
    await store.remove(1)

    expect(store.items.length).toBe(1)
    expect(store.items[0].id).toBe(2)
  })
})

