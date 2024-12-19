import Form from 'react-bootstrap/Form';
import { useEffect, useState } from 'react';
import { motion } from 'framer-motion';

function HomeForm() {
    const [userMood, setUserMood] = useState('');

    const handleChange = (e) => {
        e.preventDefault();
        setUserMood(e.target.value);
    };

    const handleError = (error) => {
        console.error('Error fetching tracks:', error);
        throw error;
    };

    async function handleSubmit(e) {
        e.preventDefault();

        console.log('Submitted mood:', userMood);

        const mood = encodeURIComponent(userMood.trim()); // remove whitespace from user input

        if (mood.length === 0 || mood.length < 3) {
            alert('Please enter a valid mood'); // TODO - replace with modal
            return;
        }

        const response = await fetch(`http://localhost:8080/api/get-tracks?submission=${mood}`, {
            method: 'GET',
            credentials: 'include', // Important to send cookies
        }).catch(handleError);

        if (response.status === 503) {
            alert('Service is currently unavailable. Please try again later.');
            return;
        }

        const data = await response.json().catch(handleError);

        if (response.ok) {
            // Pass the data as a prop to the result page
            window.location.href = `/result?data=${encodeURIComponent(JSON.stringify(data))}`;
        } else {
            alert('Failed to fetch tracks. Please try again.');
        }
    }

    // Animation variants
    const formVariants = {
        hidden: { opacity: 0, y: 50 },
        visible: {
            opacity: 1,
            y: 0,
            transition: { delay: 3, duration: 1, ease: 'easeOut' },
        },
    };

    const inputVariants = {
        hidden: { opacity: 0, y: 20 },
        visible: {
            opacity: 1,
            y: 0,
            transition: { delay: 1.5, duration: 0.6 },
        },
    };

    const textVariants = {
        hidden: { opacity: 0 },
        visible: {
            opacity: 1,
            transition: { delay: 1.5, duration: 0.9 },
        },
    };

    return (
        <motion.div
            initial="hidden"
            animate="visible"
            variants={formVariants}
            className="d-flex justify-content-center align-items-center"
        >
            <Form onSubmit={handleSubmit}>
                <Form.Group controlId="formUserMood">

                    <motion.label variants={textVariants}>
                        <Form.Label hidden>Describe how you feel</Form.Label>
                    </motion.label>

                    <motion.div variants={inputVariants}>
                        <Form.Control
                            type="text"
                            onChange={handleChange}
                            value={userMood}
                            autoComplete="off"
                            placeholder='"I miss my grandma"'
                            required
                            className="form-control-lg"
                        />
                    </motion.div>
                </Form.Group>
            </Form>

        </motion.div>
    );
}

export default HomeForm;
