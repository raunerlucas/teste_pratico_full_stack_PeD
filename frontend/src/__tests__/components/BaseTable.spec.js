import {describe, expect, it} from 'vitest'
import {mount} from '@vue/test-utils'
import BaseTable from '@/components/common/BaseTable.vue'
import {createI18n} from 'vue-i18n'
import ptBR from '@/i18n/locales/pt-BR.json'

const i18n = createI18n({
  legacy: false,
  locale: 'pt-BR',
  messages: { 'pt-BR': ptBR },
})

function mountTable(props = {}) {
  return mount(BaseTable, {
    props: {
      columns: [
        { key: 'code', label: 'Código' },
        { key: 'name', label: 'Nome' },
      ],
      ...props,
    },
    global: {
      plugins: [i18n],
    },
  })
}

describe('BaseTable', () => {
  it('renders column headers', () => {
    const wrapper = mountTable()
    const headers = wrapper.findAll('th')
    expect(headers.length).toBe(3) // 2 columns + actions
    expect(headers[0].text()).toBe('Código')
    expect(headers[1].text()).toBe('Nome')
  })

  it('renders rows correctly', () => {
    const wrapper = mountTable({
      rows: [
        { id: 1, code: 'MP001', name: 'Farinha' },
        { id: 2, code: 'MP002', name: 'Açúcar' },
      ],
    })
    const rows = wrapper.findAll('tbody tr')
    expect(rows.length).toBe(2)
    expect(rows[0].text()).toContain('MP001')
    expect(rows[0].text()).toContain('Farinha')
  })

  it('shows no data message when rows is empty', () => {
    const wrapper = mountTable({ rows: [] })
    expect(wrapper.text()).toContain(ptBR.common.noData)
  })

  it('emits edit event on edit button click', async () => {
    const row = { id: 1, code: 'MP001', name: 'Farinha' }
    const wrapper = mountTable({ rows: [row] })
    const editBtn = wrapper.findAll('button').find((b) => b.text() === ptBR.common.edit)
    await editBtn.trigger('click')
    expect(wrapper.emitted('edit')).toBeTruthy()
    expect(wrapper.emitted('edit')[0][0]).toEqual(row)
  })

  it('emits delete event on delete button click', async () => {
    const row = { id: 1, code: 'MP001', name: 'Farinha' }
    const wrapper = mountTable({ rows: [row] })
    const deleteBtn = wrapper.findAll('button').find((b) => b.text() === ptBR.common.delete)
    await deleteBtn.trigger('click')
    expect(wrapper.emitted('delete')).toBeTruthy()
    expect(wrapper.emitted('delete')[0][0]).toEqual(row)
  })

  it('hides actions column when showActions is false', () => {
    const wrapper = mountTable({ showActions: false })
    const headers = wrapper.findAll('th')
    expect(headers.length).toBe(2)
  })

  it('applies formatter to column values', () => {
    const wrapper = mountTable({
      columns: [
        { key: 'value', label: 'Valor', formatter: (v) => `R$ ${v}` },
      ],
      rows: [{ id: 1, value: 100 }],
    })
    expect(wrapper.text()).toContain('R$ 100')
  })
})

