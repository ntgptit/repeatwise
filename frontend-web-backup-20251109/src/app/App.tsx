import { RouterProvider } from 'react-router-dom';
import { QueryProvider, ThemeProvider, AuthProvider } from './providers';
import { router } from './router';
import { Toaster } from 'sonner';

function App() {
  return (
    <ThemeProvider>
      <QueryProvider>
        <AuthProvider>
          <RouterProvider router={router} />
          <Toaster position="top-right" />
        </AuthProvider>
      </QueryProvider>
    </ThemeProvider>
  );
}

export default App;
