import { StrictMode } from "react";
import { createRoot } from "react-dom/client";
import App from "./App.jsx";
import { PublicClientApplication } from "@azure/msal-browser";
import { MsalProvider } from "@azure/msal-react";
import { msalConfig } from "./authConfig";
import { BrowserRouter } from "react-router-dom";
import { GoogleOAuthProvider } from "@react-oauth/google";
const msalInstance = new PublicClientApplication(msalConfig);

// Initialize before rendering
msalInstance.initialize().then(() => {
    createRoot(document.getElementById("root")).render(
        <GoogleOAuthProvider clientId={'713573000206-hp730dr0cj3eqsuq5fbnococ52dvkl61.apps.googleusercontent.com'}>
            <MsalProvider instance={msalInstance}>
                <BrowserRouter>
                    <App />
                </BrowserRouter>
            </MsalProvider>
        </GoogleOAuthProvider>
    );
});
