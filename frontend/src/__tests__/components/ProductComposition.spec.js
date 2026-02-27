import {describe, expect, it} from 'vitest'
import {mount} from '@vue/test-utils'
import ProductComposition from '@/components/product/ProductComposition.vue'
import {createI18n} from 'vue-i18n'
import ptBR from '@/i18n/locales/pt-BR.json'

const i18n = createI18n({
  legacy: false,
  locale: 'pt-BR',
  messages: { 'pt-BR': ptBR },
})

const rawMaterials = [
  { id: 1, code: 'MP001', name: 'Farinha', stockQuantity: 500 },
  { id: 2, code: 'MP002', name: 'Açúcar', stockQuantity: 200 },
]

function mountComp(props = {}) {
  return mount(ProductComposition, {
    props: {
      compositions: [],
      rawMaterials,
      ...props,
    },
    global: {
      plugins: [i18n],
    },
  })
}

describe('ProductComposition', () => {
  it('shows empty state when no compositions', () => {
    const wrapper = mountComp()
    expect(wrapper.text()).toContain(ptBR.product.noCompositions)
  })

  it('renders composition rows', () => {
    const wrapper = mountComp({
      compositions: [
        { rawMaterialId: 1, requiredQuantity: 200 },
      ],
    })
    const selects = wrapper.findAll('select')
    expect(selects.length).toBe(1)
  })

  it('emits add event when add button is clicked', async () => {
    const wrapper = mountComp()
    const addBtn = wrapper.findAll('button').find((b) => b.text().includes(ptBR.product.addComposition))
    await addBtn.trigger('click')
    expect(wrapper.emitted('add')).toBeTruthy()
  })

  it('emits remove event when remove button is clicked', async () => {
    const wrapper = mountComp({
      compositions: [{ rawMaterialId: 1, requiredQuantity: 200 }],
    })
    const removeBtn = wrapper.findAll('button').find((b) => b.text().includes(ptBR.product.removeComposition))
    await removeBtn.trigger('click')
    expect(wrapper.emitted('remove')).toBeTruthy()
    expect(wrapper.emitted('remove')[0][0]).toBe(0)
  })
})

