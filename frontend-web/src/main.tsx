import { createRoot } from 'react-dom/client'
import { BrowserRouter } from 'react-router-dom'

import 'nprogress/nprogress.css'
import App from 'src/App'
import { SidebarProvider } from 'src/contexts/SidebarContext'

const container = document.getElementById('root')

if (!container) {
  throw new Error('Root container element with id="root" was not found.')
}

const root = createRoot(container)

root.render(
  <SidebarProvider>
    <BrowserRouter>
      <App />
    </BrowserRouter>
  </SidebarProvider>
)
