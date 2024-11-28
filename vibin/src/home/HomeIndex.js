import Row from 'react-bootstrap/Row';
import HomeForm from './HomeForm';

function HomeIndex() {
    return (
        <Row className="justify-content-md-center">
            <h1>What's your vibe?</h1>
            <HomeForm />
        </Row>
    )
}

export default HomeIndex;