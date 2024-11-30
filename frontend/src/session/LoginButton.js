import Button from 'react-bootstrap/Button';
import { createClient } from "@supabase/supabase-js"
import { useNavigate } from 'react-router-dom'
import { useState, useEffect, useRef } from 'react';


const supabaseUrl = process.env.REACT_APP_SUPABASE_URL;
const supabaseKey = process.env.REACT_APP_SUPABASE_SECRET_KEY;

const supabase = createClient(supabaseUrl, supabaseKey);

export default function LoginButton() {
    const navigate = useNavigate();
    const [errorMsg, setErrorMsg] = useState('');
    const hasHandledCallback = useRef(false); // Ensure callback logic runs only once


    useEffect(() => {
        // Handle errors returned in the callback URL
        const queryParams = new URLSearchParams(window.location.search);
        const error = queryParams.get('error');
        const errorDescription = queryParams.get('error_description');
        if (error) {
            setErrorMsg(errorDescription);
            return;
        }

        // Handle successful authentication callback
        if (!hasHandledCallback.current) {
            hasHandledCallback.current = true; // Mark callback as handled
            handleCallback();
        }
    }, []);

    // Handle successful authentication callback
    async function handleCallback() {
        const session = await supabase.auth.getSession();
        if (session.data.session) {
            console.log("Session obtained:", session.data.session);
            const token = session.data.session.provider_token;

            try {
                const userProfile = await fetchSpotifyProfile(token);
                console.log(userProfile);

                await saveUserToDatabase(userProfile);

                navigate("/home");
            } catch (error) {
                console.error("Error during callback processing:", error.message);
            }
        }
    }


    async function handleLogin() {
        const { error } = await supabase.auth.signInWithOAuth({
            provider: "spotify",
        });

        if (error) {
            console.error("Error during login:", error.message);
        }
    }

    async function fetchSpotifyProfile(token) {
        const response = await fetch('https://api.spotify.com/v1/me', {
            headers: {
                Authorization: `Bearer ${token}`,
            },
        });
        if (!response.ok) {
            throw new Error('Failed to fetch Spotify profile');
        }
        return response.json();
    }

    async function saveUserToDatabase(userProfile) {
        const { data, error } = await supabase
            .from('user')
            .insert({
                email: userProfile.email,
            });
        if (error) {
            console.error('Failed to save user to database:', error.message);
        }
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
