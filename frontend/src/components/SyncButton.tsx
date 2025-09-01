import {useNavigate} from "react-router-dom";
import {Home} from "lucide-react";
import "../styles/SyncButton.css";

const SyncButton: React.FC = () => {
    const navigate = useNavigate();

    return (
        <button className="home-btn-floating" onClick={() => navigate("/sync")}>
            <Home size={18} />
            <span>Sync</span>
        </button>
    )
}

export default SyncButton; 