
export default function ResultTrack({ index, track }) {
    return (
        <iframe key={index} title={track.name} style={{ borderRadius: '12px' }} src={`https://open.spotify.com/embed/track/${track.id}?utm_source=generator&theme=0`} width="100%" height="152" allowFullScreen="" allow="autoplay; clipboard-write; encrypted-media; fullscreen; picture-in-picture" loading="lazy">
        </iframe>
    )
    
}