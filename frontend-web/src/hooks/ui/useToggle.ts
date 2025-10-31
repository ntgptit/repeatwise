/**
 * useToggle Hook
 * 
 * Toggle state management hook
 * 
 * ✅ Tên rõ ràng: useToggle - diễn đạt rõ tác dụng
 * ✅ Type-safe: Trả về type rõ ràng
 * ✅ Clean API: Trả về object rõ ràng với tên gợi nghĩa
 * ✅ Không side-effect ẩn: Chỉ quản lý state, không render JSX
 * ✅ Chuẩn: { isOpen, open, close, toggle } (giống useModal)
 */

import { useState, useCallback } from 'react'

export interface UseToggleReturn {
  isOpen: boolean // ✅ Chuẩn: isOpen (giống useModal)
  open: () => void
  close: () => void
  toggle: () => void
}

/**
 * useToggle - Toggle state hook
 * 
 * @param initialState Initial toggle state (default: false)
 * @returns Object with toggle state and control functions
 */
export function useToggle(initialState = false): UseToggleReturn {
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
    isOpen, // ✅ Chuẩn: isOpen
    open,
    close,
    toggle,
  }
}
