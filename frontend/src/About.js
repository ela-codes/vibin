import React from 'react';
import { Container } from 'react-bootstrap';
import { motion } from 'framer-motion';

export default function About() {

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
            transition: { delay: 0.2, duration: 1 },
        },
    };

    const pVariants = {
        hidden: { opacity: 0, y: 20 },
        visible: {
            opacity: 1,
            y: 0,
            transition: { delay: 1, duration: 1 },
        },
    };


    return (
        <motion.div
            className="container overflow-auto py-3 col-10"
            initial="hidden"
            animate="visible"
            variants={containerVariants}>
            <motion.h1 variants={h1Variants}>About Page</motion.h1>
            <motion.div variants={pVariants} className="small">
                <p>Typically when we search for songs, we already feel a certain type of way. We know what genres or artists to look for to satisfy that <i>vibe</i>.<br /><strong>VIBIN.</strong> bridges that gap between emotion and music by delivering meaningful song recommendations. </p>
                <p>Designed to explore API integrations, scalable application design and unit testing, this full stack project is both a creative experiment and a technical showcase for me!</p>
                <p>Around Dec. 2024, I challenged myself to build a minimum viable product (MVP) in 2-3 weeks, which mimics a typical sprint. I learned about gathering requirements and building a web application without a step-by-step tutorial to follow. I researched, asked my peers for advice, and went through a lot of trial and error. I'm so proud of the outcome!</p>
                <p>I hope you find joy in a new song you've discovered today~ </p>
            </motion.div>
        </motion.div>

    );
}
