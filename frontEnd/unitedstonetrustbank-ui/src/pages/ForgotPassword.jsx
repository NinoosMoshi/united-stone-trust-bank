import { useState } from "react";
import AuthService from "../services/authService";
import { Link } from "react-router-dom";


export default function ForgotPassword() {

    const [email, setEmail] = useState("");
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState('');
    const [success, setSuccess] = useState('');


    const handleSubmit = async (e) => {
        e.preventDefault();
        setLoading(true);
        setError('');
        setSuccess('');

        try {
            const response = await AuthService.forgetPassword({ email });

            if (response.data.statusCode === 200) {
                setSuccess('Password reset link has been sent to your email.');
                setEmail('');
            } else {
                setError(response.data.message || 'Failed to send password reset link.');
            }

        } catch (error) {
            setError(error.response?.data?.message || 'An error occurred while resetting password');
        } finally {
            setLoading(false);
        }

    }



    return (
        <div className="auth-container">
            <div className="auth-form">
                <h2>Forgot Password</h2>
                <p className="auth-subtitle">Enter your email address to receive a password reset code</p>

                {error && <div className="error-message">{error}</div>}
                {success && <div className="success-message">{success}</div>}

                <form onSubmit={handleSubmit}>
                    <div className="form-group">
                        <label htmlFor="email">Email Address</label>
                        <input
                            type="email"
                            id="email"
                            name="email"
                            value={email}
                            onChange={(e) => setEmail(e.target.value)}
                            placeholder="Enter your email"
                            required
                        />
                    </div>

                    <button
                        type="submit"
                        className="auth-button"
                        disabled={loading}
                    >
                        {loading ? 'Sending...' : 'Send Reset Code'}
                    </button>
                </form>

                <div className="auth-link">
                    Remember your password? <Link to="/login">Login here</Link>
                </div>

                <div className="auth-link">
                    Don't have an account? <Link to="/register">Sign up</Link>
                </div>
            </div>
        </div>
    )
}
