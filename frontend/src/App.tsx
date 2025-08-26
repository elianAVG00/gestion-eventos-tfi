import { BrowserRouter, Routes, Route } from "react-router-dom";
import LandingPage from "./components/LandingPage";
import InteractiveCalendar from "./components/Calendar"; // ✅ nombre real del default export

export default function App() {
  return (
    <BrowserRouter>
      <Routes>
        <Route path="/" element={<LandingPage />} />
        <Route path="/calendar" element={<InteractiveCalendar />} />
      </Routes>
    </BrowserRouter>
  );
}
