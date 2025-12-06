import React from "react";
import { useMsal } from "@azure/msal-react";
import { loginRequest } from "../authConfig";

const Login = () => {
    const { instance } = useMsal();

    const handleLogin = () => {
        instance.loginRedirect(loginRequest); // ή loginPopup για popup
    };

    return (
        <div className="login-container">
            <h1>Welcome to My News</h1>
            <button onClick={handleLogin}>Sign in with Microsoft</button>
        </div>
    );
};

export default Login;