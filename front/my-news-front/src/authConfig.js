export const msalConfig = {
    auth: {
        clientId: "ba46cf4b-c99a-4677-b890-2f0602935961", // το Application (client) ID από Azure
        authority: "https://login.microsoftonline.com/255fdcf4-230a-4d11-a29d-a5f3bb500bd2", // ο tenant σου
        redirectUri: "http://localhost:5174/", // η redirect URL του dev server
    },
    cache: {
        cacheLocation: "sessionStorage", // ή localStorage
        storeAuthStateInCookie: false,
    }
};

export const loginRequest = {
    scopes: [
        "openid",
        "profile",
        "email",
        "api://ba46cf4b-c99a-4677-b890-2f0602935961/Users.Read"
    ]
};