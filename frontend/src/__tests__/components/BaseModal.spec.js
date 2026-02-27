import {afterEach, describe, expect, it} from 'vitest'
import {mount} from '@vue/test-utils'
import BaseModal from '@/components/common/BaseModal.vue'

let wrapper

afterEach(() => {
  wrapper?.unmount()
  document.body.innerHTML = ''
})

function mountModal(props = {}) {
  wrapper = mount(BaseModal, {
    props: {
      visible: true,
      title: 'Confirm Delete',
      confirmText: 'Yes',
      cancelText: 'No',
      ...props,
    },
    slots: {
      default: 'Do you want to delete this item?',
    },
    attachTo: document.body,
  })
  return wrapper
}

describe('BaseModal', () => {
  it('renders title and slot content when visible', () => {
    mountModal()
    const modal = document.body.querySelector('.fixed')
    expect(modal).not.toBeNull()
    expect(modal.textContent).toContain('Confirm Delete')
    expect(modal.textContent).toContain('Do you want to delete this item?')
  })

  it('does not render when visible is false', () => {
    mountModal({ visible: false })
    const modal = document.body.querySelector('.fixed')
    expect(modal).toBeNull()
  })

  it('emits confirm event on confirm button click', async () => {
    mountModal()
    const buttons = document.body.querySelectorAll('button')
    const confirmBtn = Array.from(buttons).find((b) => b.textContent.trim() === 'Yes')
    await confirmBtn.click()
    await wrapper.vm.$nextTick()
    expect(wrapper.emitted('confirm')).toBeTruthy()
  })

  it('emits cancel event on cancel button click', async () => {
    mountModal()
    const buttons = document.body.querySelectorAll('button')
    const cancelBtn = Array.from(buttons).find((b) => b.textContent.trim() === 'No')
    await cancelBtn.click()
    await wrapper.vm.$nextTick()
    expect(wrapper.emitted('cancel')).toBeTruthy()
  })
})
