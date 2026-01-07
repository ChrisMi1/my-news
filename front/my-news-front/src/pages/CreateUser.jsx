import { useEffect, useState } from "react";
import SourceSelection from "./SourceSelection.jsx";

export default function CreateUser({ user, token }) {

    const [createdUser, setCreatedUser] = useState(null);
    

    // 1. Create User
    useEffect(() => {
        console.log("useEffect fired")
        const createUser = async () => {
            const res = await fetch("http://localhost:8080/mynews/api/users", {
                method: "POST",
                headers: {
                    "Content-Type": "application/json",
                    "Authorization": `Bearer ${token}`
                }
            });

            const newUser = await res.json();
            console.log(newUser.email)
            setCreatedUser(newUser);
        };

        createUser();
    }, [user.email, token]);

    // 2. Fetch sources after user created
    // useEffect(() => {
    //     if (!createdUser) return;

    //     const fetchSources = async () => {
    //         const res = await fetch("http://localhost:8080/mynews/api/sources/search", {
    //             method: "POST",
    //             headers: {
    //                 "Content-Type": "application/json",
    //                 "Authorization": `Bearer ${token}`
    //             },
    //             body: JSON.stringify({
    //                 searchCriteria: {},
    //                 pageNumber: 0
    //             })
    //         });

    //         const data = await res.json();
    //         setSources(data.content);
    //     };

    //     fetchSources();
    // }, [createdUser]);


    if (!createdUser) return <div>Creating user…</div>;

    return (
        <SourceSelection user={createdUser} token={token} />
    );
}
