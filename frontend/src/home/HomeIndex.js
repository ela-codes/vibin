import Row from 'react-bootstrap/Row';
import HomeForm from './HomeForm';
import { useNavigate } from "react-router-dom";
import { useEffect, useState } from "react";


function HomeIndex() {

    const [user, setUser] = useState({ userId: null, displayName: null });
    const navigate = useNavigate();

    useEffect(() => {
        async function fetchUserDetails() {
            const response = await fetch("http://localhost:8080/api/user-details", {
                method: "GET",
                credentials: "include",
            });
            if (response.ok) { // authorized user
                const data = await response.json();
                setUser({ userId: data.userId, displayName: data.displayName });
            } else if (response.status === 401) { // unauthorized access
                navigate("/"); // go back to login page
            }
        }
        fetchUserDetails();
    });

    return (
        <Row className="justify-content-center">
            <div className="col-6">
                <h1>Hey, {user.displayName}!</h1>
                <br />

                <h4>What vibe are you looking for?</h4>
                <HomeForm />
            </div>
        </Row>
    )
}

export default HomeIndex;