import { Routes, Route, Navigate } from "react-router-dom";
import { AuthProvider, useAuth } from "./AuthFlow";
import Login from "./pages/Login";
import CreateUser from "./pages/CreateUser";
import NewsPage from "./pages/NewsPage";
import SourceSelection from "./pages/SourceSelection";

function AppRoutes() {
    const { userState, token } = useAuth();

    if (userState.status === "unauthenticated") return <Login />;
    if (userState.status === "loading") return <div className="loader">Loading...</div>;
    if (userState.status === "error") return <div>Something went wrong. Please login again.</div>;
    if (userState.status === "new") {
        return <CreateUser user={userState.user} token={token} />;
    }
    return (
        <Routes>
            <Route path="/" element={<NewsPage />} />
            <Route path="/edit-sources" element={<SourceSelection />} />
            <Route path="*" element={<Navigate to="/" />} />
        </Routes>
    );
}

export default function App() {
    return (
        <AuthProvider>
            <AppRoutes />
        </AuthProvider>
    );
}