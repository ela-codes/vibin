import Form from 'react-bootstrap/Form';
import { useEffect, useState } from 'react';


function HomeForm() {
    const [userMood, setUserMood] = useState('');

    const handleChange = (e) => {
        e.preventDefault();
        setUserMood(e.target.value);
    }

    useEffect(() => {
        console.log('User mood:', userMood);
    }, [userMood]);

    const handleSubmit = (e) => {
        e.preventDefault();
        console.log('Submitted mood:', userMood);
        const isEmpty = userMood.trim().length === 0;
        const isShort = userMood.trim().length < 3; // arbritrary number, shortest word could be "sad";

        if(isEmpty || isShort) {
            alert('Please enter a valid mood');
            return;
        } else {
            // send song recommendations request to backend
            // await 8080/api/get-tracks?submission=userMood
        }
    }


    return (
        <>
            <Form onSubmit={handleSubmit}>
                <Form.Group controlId="formUserMood">
                    <Form.Label hidden>How are you feeling today?</Form.Label>
                    <Form.Control type="text" onChange={handleChange} value={userMood} placeholder="Enter your mood" />
                    <Form.Text className="text-muted"><i>"i'm feeling cozy while it's snowing outside"</i></Form.Text>
                </Form.Group>
            </Form>
        </>
    );
}

export default HomeForm;