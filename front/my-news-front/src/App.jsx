import { useMsal } from "@azure/msal-react";
import { useEffect, useState } from "react";
import Login from "./pages/Login.jsx";
import { useToken } from "./useToken";

function App() {
    const { accounts } = useMsal();
    const { token, loading: tokenLoading, error: tokenError } = useToken();
    const [userState, setUserState] = useState({ status: "loading" });

    
    useEffect(() => {
        
        if (!accounts || accounts.length === 0) return;
        if (!token || tokenLoading) return;

        const account = accounts[0];

        const checkUser = async () => {
            try {
                const res = await fetch("http://localhost:8080/mynews/api/users/check", {
                    method: "POST",
                    headers: {
                        "Content-Type": "application/json",
                        "Authorization": `Bearer ${token}`
                    },
                    body: JSON.stringify({ email: account.username })
                });

                const data = await res.json();

                if (data.exists) {
                    setUserState({ status: "existingUser", user: data.user });
                } else {
                    setUserState({ status: "newUser", user: { email: account.username } });
                }
            } catch (err) {
                console.error("Error checking user:", err);
                setUserState({ status: "error" });
            }
        };

        checkUser();
    }, [accounts, token, tokenLoading]);

    // --- RENDER ---
    if (!accounts || accounts.length === 0) return <Login />; // User not logged in
    if (tokenLoading || userState.status === "loading") return <div>Loading...</div>;
    if (tokenError || userState.status === "error") return <div>Error occurred</div>;
    if (userState.status === "newUser") return <div>New user...</div>;
    if (userState.status === "existingUser") return <div>Existing user...</div>;

    return <Login />; // fallback
}

export default App;