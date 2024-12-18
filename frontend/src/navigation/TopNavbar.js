import { NavLink } from 'react-router-dom';
import { Container, Nav, Navbar } from 'react-bootstrap';

export default function TopNavbar() {
    return (
        <Navbar className="fixed-top" expand="sm">
            <Container className="d-flex">
                <Navbar.Brand>
                    <Nav.Link as={NavLink} to="/">
                        VIBIN
                    </Nav.Link>
                </Navbar.Brand>

                <Navbar.Toggle aria-controls="top-navbar" />
                <Navbar.Collapse id="top-navbar">
                    <Nav>

                        <Nav.Link as={NavLink} to="/home" className="mx-3">
                            Home
                        </Nav.Link>

                        <Nav.Link as={NavLink} to="/about" className="mx-3">
                            About
                        </Nav.Link>

                    </Nav>
                </Navbar.Collapse>
            </Container>
        </Navbar>
    );
}
