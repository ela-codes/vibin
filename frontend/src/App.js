import HomeIndex from './home/HomeIndex';
import LoginIndex from './session/LoginIndex';
import { Route, RouterProvider, createBrowserRouter, createRoutesFromElements } from 'react-router-dom';


const router = createBrowserRouter(createRoutesFromElements(
    <>
        <Route path="/" element={<LoginIndex />} />

        {/* Render home page if user is logged in */}
        <Route path="/home" element={<HomeIndex />} />
    </>
));

function App() {

    return (
        <div className="App h-100 row align-items-center">
            <RouterProvider router={router}>
                {/* The routes will be rendered here */}
            </RouterProvider>
        </div>

    );
}

export default App;
