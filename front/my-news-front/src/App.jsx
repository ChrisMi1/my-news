import { useMsal } from "@azure/msal-react";
import { useEffect, useState } from "react";
import Login from "./pages/Login";
import { useToken } from "./useToken";
import CreateUser from "./pages/CreateUser";

function App() {
    const { accounts } = useMsal();
    const { token, loading: tokenLoading, error: tokenError } = useToken();

    const [userState, setUserState] = useState({
        status: "unauthenticated", // unauthenticated | loading | new | existing | error
        user: null
    });

    // 1️⃣ Δεν έχει login → Landing
    useEffect(() => {
        if (!accounts || accounts.length === 0) {
            setUserState({ status: "unauthenticated", user: null });
        }
    }, [accounts]);

    // 2️⃣ Έχει login + token → check user
    useEffect(() => {
        if (!accounts?.length || !token || tokenLoading) return;

        const checkUser = async () => {
            try {
                setUserState(prev => ({ ...prev, status: "loading" }));

                const res = await fetch(
                    "http://localhost:8080/mynews/api/users/check",
                    {
                        method: "GET",
                        headers: {
                            "Content-Type": "application/json",
                            "Authorization": `Bearer ${token}`
                        }
                    }
                );

                const data = await res.json();

                if (data.exists) {
                    setUserState({
                        status: "existing",
                        user: data.user
                    });
                } else {
                    setUserState({
                        status: "new",
                        user: { email: accounts[0].username }
                    });
                }
            } catch (e) {
                console.error(e);
                setUserState({ status: "error", user: null });
            }
        };

        checkUser();
    }, [accounts, token, tokenLoading]);

    // --- RENDER ---

    if (userState.status === "unauthenticated") {
        return <Login />;
    }

    if (tokenLoading || userState.status === "loading") {
        return <div>Loading...</div>;
    }

    if (tokenError || userState.status === "error") {
        return <div>Something went wrong</div>;
    }

    if (userState.status === "new") {
        return <CreateUser user={userState.user} token={token} />;
    }

    if (userState.status === "existing") {
        return <div>📰 News page (coming soon)</div>;
    }

    return null;
}

export default App;
