import {
  useCallback,
  useMemo,
  useState,
  type ReactElement,
  type ReactNode,
} from 'react'

import { SidebarContext, type SidebarContextValue } from './sidebar-context'

type SidebarProviderProps = {
  children: ReactNode
}

export function SidebarProvider({ children }: SidebarProviderProps): ReactElement {
  const [sidebarToggle, setSidebarToggle] = useState(false)

  const toggleSidebar = useCallback(() => {
    setSidebarToggle(prev => !prev)
  }, [])

  const closeSidebar = useCallback(() => {
    setSidebarToggle(false)
  }, [])

  const value = useMemo<SidebarContextValue>(
    () => ({
      sidebarToggle,
      toggleSidebar,
      closeSidebar,
    }),
    [sidebarToggle, toggleSidebar, closeSidebar]
  )

  return <SidebarContext.Provider value={value}>{children}</SidebarContext.Provider>
}

