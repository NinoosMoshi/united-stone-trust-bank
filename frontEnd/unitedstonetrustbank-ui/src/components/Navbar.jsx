import { useState } from 'react'
import AuthService from './../services/authService';
import { Link, useNavigate } from 'react-router-dom';

export default function Navbar() {

    const isAuthenticated = AuthService.isAuthenticated();
    const isAdmin = AuthService.isAdmin();
    const isAuditor = AuthService.isAuditor();

    const [showModel, setShowModel] = useState(false);
    const navigate = useNavigate();

    const handleLogout = () => {
        setShowModel(true);
    }

    const confirmLogout = () => {
        AuthService.logout();
        setShowModel(false);
        navigate('/login');
    }

    const cancelLogout = () => {
        setShowModel(false);
    }


    return (
        <nav className='navbar'>
            <div className="navbar-container">
                <Link to={"/"} className="navbar-logo">United Stone Trust Bank</Link>
                <ul className='navbar-menu'>
                    <li className='navbar-item'>
                        <Link to="/home" className="navbar-link">Home</Link>
                    </li>
                    {isAuthenticated ? (
                        <>
                            <li className='navbar-item'>
                                <Link to="/profile" className="navbar-link">Profile</Link>
                            </li>
                            <li className='navbar-item'>
                                <Link to="/transfer" className="navbar-link">Transfer</Link>
                            </li>
                            <li className='navbar-item'>
                                <Link to="/transactions" className="navbar-link">Transactions</Link>
                            </li>
                            {(isAdmin || isAuditor) && (
                                <li className='navbar-item'>
                                    <Link to="/auditor-dashboard" className="navbar-link">Auditor Dashboard</Link>
                                </li>
                            )}
                            <li className='navbar-item'>
                                <button onClick={handleLogout} className="navbar-link logout-btn">Logout</button>
                            </li>
                        </>
                    ) : (
                        <>
                            <li className='navbar-item'>
                                <Link to="/login" className="navbar-link">Login</Link>
                            </li>
                            <li className='navbar-item'>
                                <Link to="/register" className="navbar-link">Register</Link>
                            </li>
                        </>
                    )}
                </ul>
            </div>

            {showModel && (
                <div className="modal-backdrop">
                    <div className="modal">
                        <p>Are you sure your want to logout?</p>
                        <div className='modal-actions'>
                            <button onClick={confirmLogout} className="btn-confirm">Yes</button>
                            <button onClick={cancelLogout} className="btn-cancel">No</button>
                        </div>
                    </div>
                </div>
            )}
        </nav>
    )
}
