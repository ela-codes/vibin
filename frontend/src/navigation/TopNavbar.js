import { NavLink } from 'react-router-dom';
import { Container, Nav, Navbar, Col } from 'react-bootstrap';

export default function TopNavbar() {
    return (
        <Navbar className="container mb-auto" expand="sm">
            <Container>

                <Col className="text-start col-9">
                    <Navbar.Brand className="me-auto">
                        <Nav.Link as={NavLink} to="/" className="text-white">
                            <h3 id="brandName">VIBIN.</h3>
                        </Nav.Link>
                    </Navbar.Brand>
                </Col>

                <Navbar.Toggle aria-controls="top-navbar" id="navbarToggler" />
                <Navbar.Collapse id="top-navbar">
                    <Nav>
                        <Nav.Link as={NavLink} to="/home" className="mx-3 text-white">
                            Home
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
