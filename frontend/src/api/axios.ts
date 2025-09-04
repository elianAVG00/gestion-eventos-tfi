import axios from "axios";
import { API_BASE, API_TOKEN  } from "../config"; 

export const api = axios.create({ 
    baseURL: API_BASE, 
    timeout: 15000,
}); 

api.interceptors.request.use((config) => {
    // Ensure config.headers is an AxiosHeaders instance
    if (config.headers && typeof config.headers.set === "function") {
        config.headers.set("Content-Type", "application/json");
        if (API_TOKEN) {
            config.headers.set("Authorization", `Bearer ${API_TOKEN}`);
        }
    }
    return config;
});

api.interceptors.response.use(
    (res) => res, 
    (error) => {
        //Normalizar el error para que siempre tenga status/message

        const status = error?.response?.status ?? 0;
        const message = error?.response?.data?.message ?? error?.message ?? "Error desconocido";
        return Promise.reject({ status, message, details: error?.response?.data });
    }
);

