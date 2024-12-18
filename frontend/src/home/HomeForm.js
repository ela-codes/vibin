import Form from 'react-bootstrap/Form';
import { useEffect, useState } from 'react';


function HomeForm() {
    let [userMood, setUserMood] = useState('');

    const handleChange = (e) => {
        e.preventDefault();
        setUserMood(e.target.value);
    }


    const handleError = (error) => {
        console.error('Error fetching tracks:', error);
        throw error;
    }

    async function handleSubmit(e) {
        e.preventDefault();

        console.log('Submitted mood:', userMood);

        userMood = encodeURIComponent(userMood.trim()); // remove whitespace from user input

        const isEmpty = userMood.length === 0;
        const isShort = userMood.length < 3; // shortest word could be "sad";

        // validate user input
        if (isEmpty || isShort) {
            alert('Please enter a valid mood'); // TODO - replace with modal
            return;
        }
        const response = await fetch(`http://localhost:8080/api/get-tracks?submission=${userMood}`, {
            method: "GET",
            credentials: "include", // Important to send cookies
        }).catch(handleError);

        if (response.status === 503) {
            alert('Service is currently unavailable. Please try again later.');
            return;
        }

        const data = await response.json().catch(handleError);
        console.log(data);


        if (response.ok) {
            // Pass the data as a prop to the result page
            window.location.href = `/result?data=${encodeURIComponent(JSON.stringify(data))}`;
        } else {
            alert('Failed to fetch tracks. Please try again.');
        }


    }


    return (
        <>
            <Form onSubmit={handleSubmit}>
                <Form.Group controlId="formUserMood">
                    <Form.Label hidden>What vibe are you looking for?</Form.Label>
                    <Form.Control type="text" onChange={handleChange} value={userMood} placeholder="Describe how you feel" />
                    <Form.Text className="text-muted"><i>"winter makes me feel like falling in love"</i></Form.Text>
                </Form.Group>
            </Form>
        </>
    );
}

export default HomeForm;