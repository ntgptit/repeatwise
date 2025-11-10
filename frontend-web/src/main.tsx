import { createRoot } from 'react-dom/client'
import { BrowserRouter } from 'react-router-dom'
import { QueryClient, QueryClientProvider } from '@tanstack/react-query'
import { ReactQueryDevtools } from '@tanstack/react-query-devtools'

import 'nprogress/nprogress.css'
import App from 'src/App'
import { SidebarProvider } from 'src/contexts/SidebarContext'

const queryClient = new QueryClient({
  defaultOptions: {
    queries: {
      refetchOnWindowFocus: false,
      retry: 1,
      staleTime: 60 * 1000,
    },
    mutations: {
      retry: 0,
    },
  },
})

const container = document.getElementById('root')

if (!container) {
  throw new Error('Root container element with id="root" was not found.')
}

const root = createRoot(container)

root.render(
  <QueryClientProvider client={queryClient}>
    <SidebarProvider>
      <BrowserRouter>
        <App />
      </BrowserRouter>
    </SidebarProvider>
    {import.meta.env.DEV ? <ReactQueryDevtools initialIsOpen={false} /> : null}
  </QueryClientProvider>
)
