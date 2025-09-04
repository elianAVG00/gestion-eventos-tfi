import {useNavigate} from "react-router-dom";
import {RefreshCcw} from "lucide-react";
import axios from "axios";
import "../styles/SyncButton.css";

const SyncButton: React.FC = () => {
    const navigate = useNavigate();

    const handleSync = async() => {
        const apiUrl = 'http://localhost:9090/admin/glpi/sync'
        try {
            await axios.post(apiUrl);

            window.location.reload();

        } catch (error) {
            console.error("Error al sincronizar:", error);

            alert("Hubo un problema al realizar la actualización de los eventos. ")
        }
        
    }


    return (
        <button className="sync-button" onClick={handleSync}>
            <RefreshCcw size={18} />
            <span>Sync</span>
        </button>
    )
}

export default SyncButton; 