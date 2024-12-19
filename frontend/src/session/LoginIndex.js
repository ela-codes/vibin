import { useSearchParams } from "react-router-dom";
import LoginButton from "./LoginButton";
import { Alert } from "react-bootstrap";


function LoginIndex() {
    const [queryParams] = useSearchParams();
    const errorMsg = queryParams.get("error");


    return (
        <div className="container d-flex justify-content-center">
            <div className="">
                {errorMsg && ErrorAlert(errorMsg)}
                <h1 className="pb-5">There's a song for that <em>vibe</em>.</h1>
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