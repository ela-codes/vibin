import HomeIndex from './home/HomeIndex';
import LoginIndex from './session/LoginIndex';
import ResultIndex from './result/ResultIndex';
import About from './About';
import TopNavbar from './navigation/TopNavbar';
import Footer from './navigation/Footer';
import { Route, Routes } from 'react-router-dom';


function App() {
    return (
        <div className="App h-100 d-flex flex-column justify-content-center align-items-center">
            <TopNavbar />
            <Routes>
                <Route path="/" element={<LoginIndex />} /> {/* renders component when the url matches */}
                <Route path="/home" element={<HomeIndex />} />
                <Route path="/result" element={<ResultIndex />} />
                <Route path="/about" element={<About />} />
            </Routes>
            <Footer />
        </div>
    );
}

export default App;