import React from "react";
import { useMsal } from "@azure/msal-react";
import { loginRequest } from "../authConfig";
import "./Login.css"; // ⬅️ σημαντικό

const Login = () => {
    const { instance } = useMsal();

    const handleLogin = () => {
        instance.loginRedirect(loginRequest);
    };

    return (
        <div className="login-wrapper">
            <div className="login-card">
                <h1 className="app-title">My News</h1>
                <p className="subtitle">
                    Your personalized news aggregator
                </p>

                <button className="ms-login-btn" onClick={handleLogin}>
                    <img
                        src="src/assets/microsoft.png"
                        alt="Microsoft"
                        className="ms-logo"
                    />
                    Sign in with Microsoft
                </button>
            </div>
        </div>
    );
};

export default Login;
