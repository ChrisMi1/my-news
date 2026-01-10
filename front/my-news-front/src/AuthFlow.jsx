import { createContext, useContext, useEffect, useState, useCallback } from "react";
import { useMsal } from "@azure/msal-react";
import { acquireToken } from "./authUtils";
import { useNavigate } from "react-router-dom";

const AuthContext = createContext();

export const AuthProvider = ({ children }) => {
    const { instance, accounts } = useMsal();
    const navigate = useNavigate();
    const [token, setToken] = useState(null);
    const [userState, setUserState] = useState({ status: "loading", user: null });

    const logout = useCallback(() => {
        instance.logoutRedirect();
    }, [instance]);

   
    const refreshToken = useCallback(async () => {
        const newToken = await acquireToken(instance, accounts[0]);
        if (newToken) {
            setToken(newToken);
            return newToken;
        }
        logout();
        return null;
    }, [instance, accounts, logout]);

    
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
        if (!token || !accounts.length) return;

        const checkUser = async () => {
            try {
                const res = await fetch("http://localhost:8080/mynews/api/users/check", {
                    headers: { Authorization: `Bearer ${token}` }
                });

                if (res.status === 401) {
                    await refreshToken();
                    return;
                }

                const data = await res.json();
                if (data.exists) {
                    setUserState({ status: "existing", user: data.user });
                } else {
                    setUserState({ status: "new", user: { email: accounts[0].username } });
                    navigate("/create-user");
                }
            } catch (e) {
                setUserState({ status: "error", user: null });
            }
        };
        checkUser();
    }, [token, accounts, navigate, refreshToken]);

    return (
        <AuthContext.Provider value={{ token, userState, logout, refreshToken }}>
            {children}
        </AuthContext.Provider>
    );
};

export const useAuth = () => useContext(AuthContext);