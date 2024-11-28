import Form from 'react-bootstrap/Form';


function HomeForm() {
    return (
        <>
            <Form.Group controlId="formUserMood">
                <Form.Label hidden>How are you feeling today?</Form.Label>
                <Form.Control type="text" placeholder="Enter your mood" />
                <Form.Text className="text-muted"><i>"i'm feeling cozy while it's snowing outside"</i></Form.Text>
            </Form.Group>
        </>
    );
}

export default HomeForm;