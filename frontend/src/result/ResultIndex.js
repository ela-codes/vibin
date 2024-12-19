import React from 'react';
import ResultTrack from "./ResultTrack";
import { motion } from 'framer-motion';

export default function ResultsIndex() {
    const searchParams = new URLSearchParams(window.location.search);
    const dataParam = searchParams.get('data');
    let data = [];

    if (dataParam) {
        try {
            data = JSON.parse(decodeURIComponent(dataParam));
        } catch (error) {
            console.error('Failed to parse data parameter:', error);
        }
    }

    // Animation variants
    const containerVariants = {
        hidden: { opacity: 0, y: -50 },
        visible: {
            opacity: 1,
            y: 0,
            transition: { duration: 1, ease: "easeOut" },
        },
    };

    const h1Variants = {
        hidden: { opacity: 0, y: 20 },
        visible: {
            opacity: 1,
            y: 0,
            transition: { delay: 1.3, duration: 1 },
        },
    };

    const itemVariants = {
        hidden: { opacity: 0, y: 20 },
        visible: {
            opacity: 1,
            y: 0,
            transition: { delay: 2.5, duration: 2 },
        },
    };

    return (
        <motion.div
            className="container overflow-auto py-3"
            initial="hidden"
            animate="visible"
            variants={containerVariants}
        >
            <motion.h1 variants={h1Variants}>
                Here's what we curated for you
            </motion.h1>
            <div id="trackContainer">
                {data.length > 0 ? (
                    data.map((track, index) => (
                        <motion.div
                            key={index}
                            initial="hidden"
                            animate="visible"
                            variants={itemVariants}
                        >
                            <ResultTrack track={track} />
                        </motion.div>
                    ))
                ) : (
                    <motion.p
                        initial="hidden"
                        animate="visible"
                        variants={itemVariants}
                    >
                        No data available
                    </motion.p>
                )}
            </div>
        </motion.div>
    );
}
