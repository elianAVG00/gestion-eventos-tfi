import { BrowserRouter, Routes, Route } from "react-router-dom";
import LandingPage from "./components/LandingPage";
import InteractiveCalendar from "./components/Calendar"; // ✅ nombre real del default export
import EventList from "./components/EventList";
import Login from "./components/Login";

export default function App() {
  return (
    <BrowserRouter>
      <Routes>
        <Route path="/" element={<LandingPage />} />
        <Route path="/login" element={<Login />} />
        <Route path="/calendar" element={<InteractiveCalendar />} />
        <Route path="/events" element={<EventList />} />
      </Routes>
    </BrowserRouter>
  );
}

