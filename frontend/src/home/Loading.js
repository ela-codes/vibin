import { motion } from 'framer-motion';
import Spinner from 'react-bootstrap/Spinner';

export default function Loading() {
    const messages = [
        "This input? It's giving main character energy.",
        "Stay tuned, we're manifesting answers.",
        "Lowkey processing... highkey worth the wait.",
        "Spinning up magic just for you.",
        "Cooking up some fire results.",
        "We're making moves—your input's about to pop off.",
        "This isn’t just processing, it’s an art form."
    ];

    const loadingVariants = {
        hidden: { opacity: 0, y: 50 },
        visible: {
            opacity: 1,
            y: 0,
            transition: { delay: 1, duration: 1, ease: 'easeOut' },
        },
    };

    return (
        <motion.div
            initial="hidden"
            animate="visible"
            variants={loadingVariants}
            className="d-flex justify-content-center align-items-center py-3">

                <Spinner animation="border" variant="light" size="sm" role="status">
                    <span className="visually-hidden">Loading...</span>
                </Spinner>
                <p className="mt-3 ms-1 text-light">{messages[Math.floor(Math.random() * messages.length)]}</p>
        </motion.div>
    )
}