import { create } from 'zustand'

import { createUiSlice, type UiSlice } from './slices/ui.slice'

export const useUiStore = create<UiSlice>()((...args) => ({
  ...createUiSlice(...args),
}))

export type { SupportedLanguage, SupportedTheme } from './slices/ui.slice'
