import React from "react";
import { useNavigate } from "react-router-dom";
import { Home } from "lucide-react";
import "../styles/HomeButton.css";

const HomeButton: React.FC = () => {
  const navigate = useNavigate();

  return (
    <button className="home-btn-floating" onClick={() => navigate("/")}>
      <Home size={18} />
      <span>Inicio</span>
    </button>
  );
};

export default HomeButton;
