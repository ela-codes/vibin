import Button from 'react-bootstrap/Button';
import { useState } from 'react';


export default function LoginButton() {
    const [errorMsg, setErrorMsg] = useState('');

    async function handleLogin() {
        const response = await fetch("http://localhost:8080/api/login", {
            method: "GET",
            credentials: "include", // Important to send cookies
        });
        const spotifyLoginUrl = await response.text();
        window.location.href = spotifyLoginUrl;
    }


    return (
        <div>
            {errorMsg && <div className="alert alert-danger">{errorMsg}</div>}
            <Button variant="success" onClick={handleLogin} size="lg">
                Login with Spotify
            </Button>
        </div>
    );
}
