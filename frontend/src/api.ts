import Axios from "axios";

const URL = process.env.production || "http://localhost:8080";

const api = Axios.create({
    baseURL: URL,
    headers: {
        "Accept": "application/json",
        "Content-Type": "application/json"
    },
});

export default api;