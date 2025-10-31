/**
 * useModal Hook
 * 
 * Modal state management hook
 * 
 * ✅ Tên rõ ràng: useModal - diễn đạt rõ tác dụng
 * ✅ Type-safe: Trả về type rõ ràng
 * ✅ Clean API: Trả về object rõ ràng với tên gợi nghĩa
 * ✅ Không side-effect ẩn: Chỉ quản lý state, không render JSX
 */

import { useState, useCallback } from 'react'

export interface UseModalReturn {
  isOpen: boolean
  open: () => void
  close: () => void
  toggle: () => void
}

/**
 * useModal - Modal state management hook
 * 
 * @param initialState Initial open state (default: false)
 * @returns Object with modal state and control functions
 */
export function useModal(initialState = false): UseModalReturn {
  const [isOpen, setIsOpen] = useState(initialState)

  const open = useCallback(() => {
    setIsOpen(true)
  }, [])

  const close = useCallback(() => {
    setIsOpen(false)
  }, [])

  const toggle = useCallback(() => {
    setIsOpen((prev) => !prev)
  }, [])

  return {
    isOpen,
    open,
    close,
    toggle,
  }
}
