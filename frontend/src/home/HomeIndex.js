import Row from 'react-bootstrap/Row';
import HomeForm from './HomeForm';
import LogoutButton from "../session/LogoutButton";

function HomeIndex() {
    return (
        <Row className="justify-content-center">
            <div className="col-6">
                <LogoutButton />
                <h1>What's your vibe?</h1>
                <HomeForm />
            </div>
        </Row>
    )
}

export default HomeIndex;