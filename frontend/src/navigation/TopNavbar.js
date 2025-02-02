import { NavLink } from 'react-router-dom';
import { Container, Nav, Navbar, Col } from 'react-bootstrap';
import { useState, useEffect } from 'react';
import { useLocation } from 'react-router-dom';

export default function TopNavbar() {
    let [loggedInNavItemUrl, setLoggedInNavItemUrl] = useState('/');
    let [loggedInNavItemName, setloggedInNavItemName] = useState('Login');
    const location = useLocation();

    useEffect(() => {
        if (localStorage.getItem('spotifyUser')) {
            setLoggedInNavItemUrl('/home');
            setloggedInNavItemName('Home');
        }
    }, [location.pathname]);

    return (
        <Navbar className="container mb-auto" expand="sm">
            <Container>

                <Col className="text-start col-9">
                    <Navbar.Brand className="me-auto">
                        <Nav.Link as={NavLink} to={loggedInNavItemUrl} className="text-white">
                            <h3 id="brandName">VIBIN.</h3>
                        </Nav.Link>
                    </Navbar.Brand>
                </Col>

                <Navbar.Toggle aria-controls="top-navbar" id="navbarToggler" />
                <Navbar.Collapse id="top-navbar">
                    <Nav>
                        <Nav.Link as={NavLink} to={loggedInNavItemUrl} className="mx-3 text-white">
                            {loggedInNavItemName}
                        </Nav.Link>
                        <Nav.Link as={NavLink} to="/about" className="mx-3 text-white">
                            About
                        </Nav.Link>
                    </Nav>
                </Navbar.Collapse>

            </Container>
        </Navbar>
    );
}
