import React from 'react';
import { motion } from 'framer-motion';
import aboutPageImg from "./style/image-from-rawpixel-id-16837520.png";


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

    const imgVariants = {
        hidden: { opacity: 0, y: 20 },
        visible: {
            opacity: 1,
            y: 0,
            transition: { delay: 1, duration: 1 },
        },
    };

    const pVariant1= {
        hidden: { opacity: 0, y: 20 },
        visible: {
            opacity: 1,
            y: 0,
            transition: { delay: 1, duration: 1.5 },
        },
    };

    const pVariant2 = {
        hidden: { opacity: 0, y: 20 },
        visible: {
            opacity: 1,
            y: 0,
            transition: { delay: 2, duration: 1.5 },
        },
    };

    const pVariant3 = {
        hidden: { opacity: 0, y: 20 },
        visible: {
            opacity: 1,
            y: 0,
            transition: { delay: 4, duration: 1.5 },
        },
    };

    const pVariant4 = {
        hidden: { opacity: 0, y: 20 },
        visible: {
            opacity: 1,
            y: 0,
            transition: { delay: 6, duration: 2 },
        },
    };


    return (
        <motion.div
            className="row overflow-auto py-3 align-items-center justify-content-center"
            initial="hidden"
            animate="visible"
            variants={containerVariants}>
            <motion.h1 variants={h1Variants}>About VIBIN.</motion.h1>
            <motion.div variants={imgVariants} className='pb-4'>
            
                <img src={aboutPageImg} alt="Woman collage cutouts portrait abstract rainbow by rawpixel.com" id="aboutPageImage" />
            </motion.div>
            <div  className="w-75">
                <motion.p variants={pVariant1} className='fs-5'><strong>Imagine this.</strong></motion.p>
                <motion.p variants={pVariant2} >You're the main character of a movie. <br />What music is playing in the background right now?</motion.p>
                <motion.p variants={pVariant3}><strong>VIBIN.</strong> bridges the gap between emotion and music by delivering meaningful song recommendations. </motion.p>
                <motion.p  variants={pVariant4} id="aboutPageSubText"><hr />Designed to explore API integrations, scalable application design and unit testing, this full stack project is both a creative experiment and a technical showcase for me! Around Dec. 2024, I challenged myself to build a minimum viable product (MVP) in 2-3 weeks, which mimics a typical sprint. I learned about gathering requirements and building a web application without a step-by-step tutorial to follow. I researched, asked my peers for advice, and went through a lot of trial and error. <br />I'm so proud of the outcome and I hope you find joy in a new tune you've discovered today~
                </motion.p>
            </div>
        </motion.div>

    );
}
