import { useSearchParams } from "react-router-dom";
import LoginButton from "./LoginButton";
import { Alert } from "react-bootstrap";


function LoginIndex() {
    const [queryParams] = useSearchParams();
    const errorMsg = queryParams.get("error");


    return (
        <div className="row justify-content-center">
            <div className="col-6">
                {errorMsg && ErrorAlert(errorMsg)}
                <h1>Connect your Spotify</h1>
                <LoginButton />
            </div>
        </div>
    )
}

function ErrorAlert(errorMsg) {
    return (
        <Alert variant='danger' key='loginError' dismissible={true}>
            <Alert.Heading>Oh snap! You got an error!</Alert.Heading>
            <p>{errorMsg}. Please try logging in again.</p>
        </Alert>
    )
}

export default LoginIndex;