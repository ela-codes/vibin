import Button from "react-bootstrap/Button";
import { createClient } from "@supabase/supabase-js";
import { useNavigate } from "react-router-dom";

const supabaseUrl = process.env.REACT_APP_SUPABASE_URL;
const supabaseKey = process.env.REACT_APP_SUPABASE_SECRET_KEY;

const supabase = createClient(supabaseUrl, supabaseKey);

export default function LogoutButton() {
    const navigate = useNavigate();

    async function handleLogout() {
        const { error } = await supabase.auth.signOut();
        if (error) {
            console.error("Error logging out:", error.message);
        } else {
            // Redirect to main page after successful logout
            navigate("/");
        }
    }

    return (
        <Button variant="dark" onClick={handleLogout} size="lg">
            Logout
        </Button>
    );
}