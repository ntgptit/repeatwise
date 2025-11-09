import { createContext } from 'react'

export type SidebarContextValue = {
  sidebarToggle: boolean
  toggleSidebar: () => void
  closeSidebar: () => void
}

export const SidebarContext = createContext<SidebarContextValue | undefined>(undefined)

