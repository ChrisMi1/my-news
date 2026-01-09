import { useMsal } from "@azure/msal-react";
import { useEffect, useState } from "react";
import Login from "./pages/Login";
import CreateUser from "./pages/CreateUser";
import NewsPage from "./pages/NewsPage";
import { acquireToken } from "./authUtils"; 

function App() {
    const { instance, accounts } = useMsal();
    const [token, setToken] = useState(null);
    const [userState, setUserState] = useState({
        status: "unauthenticated", 
        user: null
    });

    
    useEffect(() => {
        if (accounts && accounts.length > 0) {
            const initToken = async () => {
                const accessToken = await acquireToken(instance, accounts[0]);
                if (accessToken) {
                    setToken(accessToken);
                } else {
                    setUserState({ status: "unauthenticated", user: null });
                }
            };
            initToken();
        } else {
            setUserState({ status: "unauthenticated", user: null });
        }
    }, [accounts, instance]);

    
    useEffect(() => {
        
        if (!accounts?.length || !token) return;

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

                if (res.status === 401) {
                    
                    const newToken = await acquireToken(instance, accounts[0]);
                    if (newToken) {
                        setToken(newToken);
                        return; 
                    }
                }

                const data = await res.json();
                if (data.exists) {
                    setUserState({ status: "existing", user: data.user });
                } else {
                    setUserState({ status: "new", user: { email: accounts[0].username } });
                }
            } catch (e) {
                console.error(e);
                setUserState({ status: "error", user: null });
            }
        };

        checkUser();
    }, [accounts, token, instance]);

    // --- RENDER ---
    if (userState.status === "unauthenticated") return <Login />;
    
    if ((accounts?.length > 0 && !token) || userState.status === "loading") {
        return <div>Loading...</div>;
    }

    if (userState.status === "error") return <div>Something went wrong</div>;

    if (userState.status === "new") {
        return <CreateUser user={userState.user} token={token} />;
    }

    if (userState.status === "existing") {
        return <NewsPage token={token} />;
    }

    return null;
}

export default App;