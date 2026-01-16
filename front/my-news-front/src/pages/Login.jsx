import React from "react";
import { useMsal } from "@azure/msal-react";
import { loginRequest } from "../authConfig";
import "./Login.css"; 
import { GoogleLogin } from "@react-oauth/google";
import { useAuth } from "../AuthFlow";
import MicrosoftIcon from '../assets/microsoft.png';
const Login = () => {
    const { instance } = useMsal();
    const { handleGoogleSuccess, setProvider } = useAuth();
    const handleLogin = () => {
        setProvider("microsoft");
        instance.loginRedirect(loginRequest);
    };

    const handleGoogleLoginSuccess = (res) => {
        setProvider("google");
        handleGoogleSuccess(res);
    };

    return (
        <div className="login-wrapper">
            <div className="login-card">
                <h1 className="app-title">My News</h1>
                <p className="subtitle">Your personalized news aggregator</p>

                <p className="login-label">Sign in with</p>

                <div className="icons-container">
                    
                    <button className="ms-icon-btn" onClick={handleLogin} title="Sign in with Microsoft">
                        <img
                            src={MicrosoftIcon}
                            alt="Microsoft"
                            className="ms-logo-icon"
                        />
                    </button>

                    
                    <div className="google-icon-wrapper">
                        <GoogleLogin 
                            onSuccess={(res) => handleGoogleSuccess(res)} 
                            onError={() => console.log("Login Failed")}
                            type="icon"
                            shape="circle"
                            theme="outline"
                            size="large"
                            useOneTap={false}
                        />
                    </div>
                </div>
            </div>
        </div>
    );
};

export default Login;
