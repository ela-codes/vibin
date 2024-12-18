import React from 'react';
import ResultTrack from "./ResultTrack";

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

    return (
        <div>
            <h1>Here's what we curated for you</h1>
            <div id="trackContainer">
                {data.length > 0 ? (
                    data.map((track, index) => (
                        <ResultTrack key={index} track={track} />
                    ))
                ) : (
                    <p>No data available</p>
                )}
            </div>
        </div>
    );
}