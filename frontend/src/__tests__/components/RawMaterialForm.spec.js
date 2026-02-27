import {describe, expect, it} from 'vitest'
import {mount} from '@vue/test-utils'
import RawMaterialForm from '@/components/rawMaterial/RawMaterialForm.vue'
import {createI18n} from 'vue-i18n'
import ptBR from '@/i18n/locales/pt-BR.json'

const i18n = createI18n({
  legacy: false,
  locale: 'pt-BR',
  messages: { 'pt-BR': ptBR },
})

function mountForm(props = {}) {
  return mount(RawMaterialForm, {
    props,
    global: {
      plugins: [i18n],
    },
  })
}

describe('RawMaterialForm', () => {
  it('renders all input fields', () => {
    const wrapper = mountForm()
    const inputs = wrapper.findAll('input')
    expect(inputs.length).toBe(3) // code, name, stockQuantity
  })

  it('populates fields with initialData', async () => {
    const wrapper = mountForm({
      initialData: { code: 'MP001', name: 'Farinha', stockQuantity: 500 },
    })
    await wrapper.vm.$nextTick()
    const inputs = wrapper.findAll('input')
    expect(inputs[0].element.value).toBe('MP001')
    expect(inputs[1].element.value).toBe('Farinha')
    expect(inputs[2].element.value).toBe('500')
  })

  it('shows validation errors for empty fields on submit', async () => {
    const wrapper = mountForm()
    await wrapper.find('form').trigger('submit')
    expect(wrapper.text()).toContain(ptBR.common.required)
  })

  it('emits submit with valid data', async () => {
    const wrapper = mountForm()
    const inputs = wrapper.findAll('input')
    await inputs[0].setValue('MP001')
    await inputs[1].setValue('Farinha')
    await inputs[2].setValue('500')
    await wrapper.find('form').trigger('submit')
    expect(wrapper.emitted('submit')).toBeTruthy()
    expect(wrapper.emitted('submit')[0][0]).toEqual({
      code: 'MP001',
      name: 'Farinha',
      stockQuantity: 500,
    })
  })

  it('emits cancel when cancel button is clicked', async () => {
    const wrapper = mountForm()
    const cancelBtn = wrapper.findAll('button').find((b) => b.text() === ptBR.common.cancel)
    await cancelBtn.trigger('click')
    expect(wrapper.emitted('cancel')).toBeTruthy()
  })
})

