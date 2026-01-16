import { createContext, useContext, useEffect, useState, useCallback } from "react";
import { useMsal } from "@azure/msal-react";
import { acquireToken } from "./authUtils";
import { useNavigate } from "react-router-dom";
import { googleLogout } from "@react-oauth/google";

const AuthContext = createContext();

export const AuthProvider = ({ children }) => {
    const { instance, accounts } = useMsal();
    const navigate = useNavigate();
    const [token, setToken] = useState(null);
    const [provider, setProviderState] = useState(localStorage.getItem("auth_provider"));
    const [userState, setUserState] = useState({ status: "loading", user: null });
    const API_BASE = import.meta.env.VITE_API_URL;

    

    const setProvider = (p) => {
        setProviderState(p);
        if (p) localStorage.setItem("auth_provider", p);
        else localStorage.removeItem("auth_provider");
    };

    const logout = useCallback(() => {
        setToken(null);
        setProvider(null);
        setUserState({ status: "unauthenticated", user: null });
        
        if (provider === "microsoft") {
            instance.logoutRedirect();
        } else {
            googleLogout();
            setToken(null);
            setUserState({ status: "unauthenticated", user: null });
        }
    }, [instance, provider]);

   
    const refreshToken = useCallback(async () => {
        const newToken = await acquireToken(instance, accounts[0]);
        if (newToken) {
            setToken(newToken);
            return newToken;
        }
        logout();
        return null;
    }, [instance, accounts, logout]);

    const handleGoogleSuccess = (credentialResponse) => {
        const idToken = credentialResponse.credential;
        setToken(idToken);
    };

    
    useEffect(() => {
        if (accounts.length > 0) {
            acquireToken(instance, accounts[0]).then(t => {
                if (t) setToken(t);
                else setUserState({ status: "unauthenticated", user: null });
            });
        } else {
            setUserState({ status: "unauthenticated", user: null });
        }
    }, [accounts, instance]);

    
    useEffect(() => {
        if (!token) return;
        if (provider === "microsoft" && accounts.length === 0) return;

        const checkUser = async () => {
            try {
                const res = await fetch(`${API_BASE}/mynews/api/users/check`, {
                    headers: { Authorization: `Bearer ${token}` }
                });

                if (res.status === 401) {
                    
                    return;
                }

                const data = await res.json();
                if (data.exists) {
                    setUserState({ status: "existing", user: data.user });
                } else {
                    const email = provider === "microsoft" ? accounts[0].username : 'gmail';
                    setUserState({ status: "new", user: { email: email } });
                    navigate("/create-user");
                }
            } catch (e) {
                setUserState({ status: "error", user: null });
            }
        };
        checkUser();
    }, [token, accounts, navigate, refreshToken]);

    return (
        <AuthContext.Provider value={{ token, userState, logout, setProvider,refreshToken,handleGoogleSuccess }}>
            {children}
        </AuthContext.Provider>
    );
};

export const useAuth = () => useContext(AuthContext);