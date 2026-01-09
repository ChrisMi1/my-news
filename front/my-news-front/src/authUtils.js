import { loginRequest } from "./authConfig";

export const acquireToken = async (instance, account) => {
    if (!account) return null;

    try {
        
        const response = await instance.acquireTokenSilent({
            scopes: loginRequest.scopes,
            account: account
        });
        return response.accessToken;
    } catch (err) {
        console.error("Token acquisition failed:", err);
        return null;
    }
};