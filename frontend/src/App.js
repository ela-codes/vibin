import HomeIndex from './home/HomeIndex';
import LoginIndex from './session/LoginIndex';
import ResultIndex from './result/ResultIndex';
import About from './About';
import TopNavbar from './navigation/TopNavbar';
import Footer from './navigation/Footer';
import { Route, Routes } from 'react-router-dom';
import { Container } from 'react-bootstrap';


function App() {
    return (
        <div className="App h-100 row align-items-center">
            <TopNavbar />
            
            <Container>
                <Routes>
                    <Route path="/" element={<LoginIndex />} /> {/* renders component when the url matches */}
                    <Route path="/home" element={<HomeIndex />} />
                    <Route path="/result" element={<ResultIndex />} />
                    <Route path="/about" element={<About />} />
                </Routes>
            </Container>
            <Footer />
        </div>
    );
}

export default App;