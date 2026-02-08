import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import { isAuthenticated } from './lib/auth';
import AuthPage from './routes/AuthPage';

function PublicRoute({ children }: { children: React.ReactNode }) {
  if (isAuthenticated()) {
    return <Navigate to='/contacts' replace />;
  }
  return <>{children}</>;
}

function App() {
  return (
    <BrowserRouter>
      <Routes>
        <Route
          path='/'
          element={
            <PublicRoute>
              <AuthPage />
            </PublicRoute>
          }
        />
        <Route path='*' element={<Navigate to='/' replace />} />
      </Routes>
    </BrowserRouter>
  );
}

export default App;
