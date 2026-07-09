import SideBar from "./pages/sidebar/SideBar.jsx";
import { BrowserRouter, Outlet, Route, Routes } from "react-router-dom";
import Dashboard from "./pages/dashboard/Dashboard.jsx";
import ComingSoon from "./pages/ComingSoon.jsx";

const MainLayout = () => {
  return (
    <div className="flex gap-1 h-screen bg-slate-50 overflow-hidden  p-4">
      {/* Sidebar Container */}
      <aside className="w-64  shrink-0 sticky top-4 self-start h-[calc(100vh-2rem)]">
        <SideBar />
      </aside>

      {/* Main Content Viewport */}
      <main className="flex-1 min-h-0 overflow-y-auto">
        <Outlet />
      </main>
    </div>
  );
};

export default function App() {
  return (
    <BrowserRouter>
      <Routes>
        <Route path="/" element={<MainLayout />}>
          <Route index element={<Dashboard />} />

          <Route path="transaction" element={<ComingSoon />} />

          <Route path="wallet" element={<ComingSoon />} />
          <Route path="goals" element={<ComingSoon />} />
          <Route path="settings" element={<ComingSoon />} />

          {/* CATCH-ALL GARBAGE ROUTE */}
          <Route path="*" element={<ComingSoon />} />
        </Route>
      </Routes>
    </BrowserRouter>
  );
}