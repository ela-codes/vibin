
import Button from 'react-bootstrap/Button';
import { createClient } from "@supabase/supabase-js"


const supabaseUrl = process.env.REACT_APP_SUPABASE_URL;
const supabaseAnonKey = process.env.REACT_APP_SUPABASE_KEY;

const supabase = createClient(supabaseUrl, supabaseAnonKey);

function LoginButton() {
    async function handleLogin() {
        const { data, error } = await supabase.auth.signInWithOAuth(
            {
                provider: "spotify",
            }
        );

        if (error) {
            console.error("Error during login:", error.message);
        } else {
            console.log("Redirecting to Spotify...");
            console.log(data);
        }
    }
    return (
        <Button variant="success" onClick={handleLogin} size="lg">
            Login with Spotify
        </Button>
    );
}

export default LoginButton;