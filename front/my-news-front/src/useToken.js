import { useMsal } from "@azure/msal-react";
import { loginRequest } from "./authConfig";
import { useState, useEffect } from "react";

export function useToken() {
    const { instance, accounts } = useMsal();
    const [token, setToken] = useState(null);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);

    useEffect(() => {
        if (!accounts || accounts.length === 0) {
            setLoading(false);
            return;
        }

        const account = accounts[0];

        const getToken = async () => {
            try {
                const response = await instance.acquireTokenSilent({
                    scopes: loginRequest.scopes,
                    account: account
                });
                setToken(response.accessToken);
            } catch (err) {
                console.error("Token acquisition failed:", err);
                setError(err);
            } finally {
                setLoading(false);
            }
        };

        getToken();
    }, [instance, accounts]);

    return { token, loading, error };
}
