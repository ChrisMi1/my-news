import { useEffect, useState } from "react";
import SourceSelection from "./SourceSelection.jsx";

export default function CreateUser({ user, token }) {

    const [createdUser, setCreatedUser] = useState(null);
    const API_BASE = import.meta.env.VITE_API_URL;


    // 1. Create User
    useEffect(() => {
        const createUser = async () => {
            const res = await fetch(`${API_BASE}/mynews/api/users`, {
                method: "POST",
                headers: {
                    "Content-Type": "application/json",
                    "Authorization": `Bearer ${token}`
                }
            });

            const newUser = await res.json();
            console.log(newUser.email)
            setCreatedUser(newUser);
        };

        createUser();
    }, [user.email, token]);

 


    if (!createdUser) return <div>Creating user…</div>;

    return (
        <SourceSelection user={createdUser} token={token} />
    );
}
