import Button from 'react-bootstrap/Button';
import { motion } from "motion/react";
import spotifyLogo from "../style/Spotify_Primary_Logo_RGB_White.png";

export default function LoginButton() {

    async function handleLogin() {
        const response = await fetch(`${process.env.REACT_APP_BACKEND_URL}/api/login`, {
            method: "GET",
            credentials: "include", // Important to send cookies
        });
        const spotifyLoginUrl = await response.text();
        window.location.href = spotifyLoginUrl;
    }


    return (
        <motion.div 
        initial={{ scale: 1 }} 
        transition={{
            duration: 3, 
            ease: "easeInOut",
            repeat: Infinity,
            repeatDelay: 5, 
            }} 
        animate={{
            scale: [1.05, 1.1, 1.05],
        }} >

            <Button onClick={handleLogin} size="lg" id="loginButton">
                Login with <span id="spotifyText"> Spotify <img src={spotifyLogo} alt="spotify green logo" id="spotifyLogo"/></span>
            </Button>
        </motion.div>
    );
}
