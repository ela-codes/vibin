import Row from 'react-bootstrap/Row';
import HomeForm from './HomeForm';
import LogoutButton from "../session/LogoutButton";
import { useEffect, useState } from "react";

function HomeIndex() {

    const [user, setUser] = useState({ userId: null, displayName: null });

    useEffect(() => {
        async function fetchUserDetails() {
            const response = await fetch("http://localhost:8080/api/user-details", {
                credentials: "include",
            });
            if (response.ok) {
                const data = await response.json();
                setUser({ userId: data.userId, displayName: data.displayName });
            }
        }
        fetchUserDetails();
    }, []);

    return (
        <Row className="justify-content-center">
            <div className="col-6">
                <h1>Hey, {user.displayName}!</h1>
                <br />

                <h4>What's your mood right now?</h4>
                <HomeForm />
            </div>
        </Row>
    )
}

export default HomeIndex;