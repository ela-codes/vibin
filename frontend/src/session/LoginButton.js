import Button from 'react-bootstrap/Button';



export default function LoginButton() {

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

            <Button variant="success" onClick={handleLogin} size="lg">
                Login with Spotify
            </Button>
        </div>
    );
}
