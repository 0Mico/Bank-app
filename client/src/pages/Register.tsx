import React, { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { useAuth } from '../AuthContext';
import { authApi } from '../api';

const Register: React.FC = () => {
    const [form, setForm] = useState({ email: '', password: '', firstName: '', lastName: '', phone: '' });
    const [error, setError] = useState('');
    const [loading, setLoading] = useState(false);
    const navigate = useNavigate();

    const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        setForm({ ...form, [e.target.name]: e.target.value });
    };

    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault();
        setError('');
        setLoading(true);
        try {
            await authApi.register(form);
            navigate('/login');
        } catch (err: any) {
            const valErrors = err.response?.data?.validationErrors;
            const msg = valErrors
                ? Object.values(valErrors).join('. ')
                : (err.response?.data?.message || 'Registration failed');
            setError(msg);
        } finally {
            setLoading(false);
        }
    };

    return (
        <div className="auth-container">
            <div className="auth-card fade-in">
                <h1>NexusBank</h1>
                <p className="subtitle">Create your account</p>
                {error && <div className="alert alert-error">{error}</div>}
                <form onSubmit={handleSubmit}>
                    <div className="form-row">
                        <div className="form-group">
                            <label htmlFor="firstName">First Name</label>
                            <input id="firstName" name="firstName" placeholder="John" value={form.firstName} onChange={handleChange} required />
                        </div>
                        <div className="form-group">
                            <label htmlFor="lastName">Last Name</label>
                            <input id="lastName" name="lastName" placeholder="Doe" value={form.lastName} onChange={handleChange} required />
                        </div>
                    </div>
                    <div className="form-group">
                        <label htmlFor="reg-email">Email</label>
                        <input id="reg-email" name="email" type="email" placeholder="you@example.com" value={form.email} onChange={handleChange} required />
                    </div>
                    <div className="form-group">
                        <label htmlFor="reg-password">Password</label>
                        <input id="reg-password" name="password" type="password" placeholder="Min. 6 characters" value={form.password} onChange={handleChange} required minLength={6} />
                    </div>
                    <div className="form-group">
                        <label htmlFor="phone">Phone (optional)</label>
                        <input id="phone" name="phone" type="tel" placeholder="+1 234 567 890" value={form.phone} onChange={handleChange} />
                    </div>
                    <button className="btn btn-primary btn-block" type="submit" disabled={loading}>
                        {loading ? 'Creating account...' : 'Create Account'}
                    </button>
                </form>
                <div className="auth-link">
                    Already have an account? <Link to="/login">Sign in</Link>
                </div>
            </div>
        </div>
    );
};

export default Register;
