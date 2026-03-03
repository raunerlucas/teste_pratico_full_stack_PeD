import {afterEach, describe, expect, it} from 'vitest'
import {mount} from '@vue/test-utils'
import ProductDetailModal from '@/components/product/ProductDetailModal.vue'
import {createI18n} from 'vue-i18n'
import ptBR from '@/i18n/locales/pt-BR.json'

const i18n = createI18n({
  legacy: false,
  locale: 'pt-BR',
  messages: { 'pt-BR': ptBR },
})

let wrapper

afterEach(() => {
  wrapper?.unmount()
  document.body.innerHTML = ''
})

const productWithDescription = {
  id: 1,
  code: 'PRD001',
  name: 'Pão Francês',
  price: 12.5,
  description: 'Pão crocante feito com farinha de trigo',
  compositions: [
    {
      id: 1,
      rawMaterial: { id: 1, code: 'MP001', name: 'Farinha', unitOfMeasure: 'kg' },
      requiredQuantity: 200,
    },
    {
      id: 2,
      rawMaterial: { id: 2, code: 'MP002', name: 'Leite', unitOfMeasure: 'caixas' },
      requiredQuantity: 50,
    },
  ],
}

const productWithoutDescription = {
  id: 2,
  code: 'PRD002',
  name: 'Bolo',
  price: 35.0,
  description: null,
  compositions: [],
}

function mountModal(props = {}) {
  wrapper = mount(ProductDetailModal, {
    props: {
      visible: true,
      product: productWithDescription,
      ...props,
    },
    global: {
      plugins: [i18n],
    },
    attachTo: document.body,
  })
  return wrapper
}

describe('ProductDetailModal', () => {
  it('renders product details when visible', () => {
    mountModal()
    const modal = document.body.querySelector('.fixed')
    expect(modal).not.toBeNull()
    expect(modal.textContent).toContain('PRD001')
    expect(modal.textContent).toContain('Pão Francês')
    expect(modal.textContent).toContain('Pão crocante feito com farinha de trigo')
  })

  it('does not render when visible is false', () => {
    mountModal({ visible: false })
    const modal = document.body.querySelector('.fixed')
    expect(modal).toBeNull()
  })

  it('does not render when product is null', () => {
    mountModal({ product: null })
    const modal = document.body.querySelector('.fixed')
    expect(modal).toBeNull()
  })

  it('displays formatted price', () => {
    mountModal()
    const modal = document.body.querySelector('.fixed')
    // formatCurrency should render something like "R$ 12,50"
    expect(modal.textContent).toContain('12')
  })

  it('displays product description', () => {
    mountModal()
    const modal = document.body.querySelector('.fixed')
    expect(modal.textContent).toContain('Pão crocante feito com farinha de trigo')
  })

  it('displays "no description" when description is null', () => {
    mountModal({ product: productWithoutDescription })
    const modal = document.body.querySelector('.fixed')
    expect(modal.textContent).toContain(ptBR.product.noDescription)
  })

  it('displays compositions with raw material info', () => {
    mountModal()
    const modal = document.body.querySelector('.fixed')
    expect(modal.textContent).toContain('MP001')
    expect(modal.textContent).toContain('Farinha')
    expect(modal.textContent).toContain('200')
    expect(modal.textContent).toContain('kg')
    expect(modal.textContent).toContain('MP002')
    expect(modal.textContent).toContain('Leite')
    expect(modal.textContent).toContain('50')
  })

  it('displays "no compositions" when compositions list is empty', () => {
    mountModal({ product: productWithoutDescription })
    const modal = document.body.querySelector('.fixed')
    expect(modal.textContent).toContain(ptBR.product.noCompositions)
  })

  it('displays composition count', () => {
    mountModal()
    const modal = document.body.querySelector('.fixed')
    expect(modal.textContent).toContain('(2)')
  })

  it('emits close event when close button is clicked', async () => {
    mountModal()
    const closeBtn = document.body.querySelector('.bg-gray-50 button')
    await closeBtn.click()
    await wrapper.vm.$nextTick()
    expect(wrapper.emitted('close')).toBeTruthy()
  })

  it('emits close event when overlay is clicked', async () => {
    mountModal()
    const overlay = document.body.querySelector('.bg-black\\/50')
    await overlay.click()
    await wrapper.vm.$nextTick()
    expect(wrapper.emitted('close')).toBeTruthy()
  })

  it('emits close event when X button is clicked', async () => {
    mountModal()
    const xBtn = document.body.querySelector('.bg-blue-600 button')
    await xBtn.click()
    await wrapper.vm.$nextTick()
    expect(wrapper.emitted('close')).toBeTruthy()
  })
})

