import React, { useState, useEffect } from 'react';
import { useAuth } from '../AuthContext';
import { userApi } from '../api';

const Profile: React.FC = () => {
    const { user, login, token, logout } = useAuth();
    const [editing, setEditing] = useState(false);
    const [deleting, setDeleting] = useState(false);
    const [form, setForm] = useState({
        firstName: user?.firstName || '',
        lastName: user?.lastName || '',
        phone: user?.phone || '',
    });
    const [passwordForm, setPasswordForm] = useState({
        currentPassword: '',
        newPassword: '',
        confirmPassword: '',
    });
    const [changingPassword, setChangingPassword] = useState(false);
    const [passwordSuccess, setPasswordSuccess] = useState('');
    const [passwordError, setPasswordError] = useState('');
    const [showPassword, setShowPassword] = useState(false);
    const [success, setSuccess] = useState('');
    const [error, setError] = useState('');
    const [saving, setSaving] = useState(false);



    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault();
        if (!user || !token) return;
        setSaving(true);
        setError('');
        setSuccess('');
        try {
            const res = await userApi.updateProfile(user.id, form);
            login(token, { ...user, ...res.data });
            setSuccess('Profile updated successfully');
            setEditing(false);
        } catch (err: any) {
            setError(err.response?.data?.message || 'Update failed');
        } finally {
            setSaving(false);
        }
    };

    const handlePasswordSubmit = async (e: React.FormEvent) => {
        e.preventDefault();
        if (!user) return;
        if (passwordForm.newPassword !== passwordForm.confirmPassword) {
            setPasswordError('New passwords do not match');
            return;
        }
        setChangingPassword(true);
        setPasswordError('');
        setPasswordSuccess('');
        try {
            await userApi.changePassword(user.id, {
                currentPassword: passwordForm.currentPassword,
                newPassword: passwordForm.newPassword,
            });
            setPasswordSuccess('Password changed successfully');
            setPasswordForm({ currentPassword: '', newPassword: '', confirmPassword: '' });
        } catch (err: any) {
            setPasswordError(err.response?.data?.message || 'Password change failed');
        } finally {
            setChangingPassword(false);
        }
    };

    const handleDelete = async () => {
        if (!window.confirm("Are you sure you want to delete your profile? This action cannot be undone.")) return;
        setDeleting(true);
        setError('');
        try {
            await userApi.deleteProfile(user!.id);
            logout();
        } catch (err: any) {
            setError(err.response?.data?.message || 'Delete failed');
            setDeleting(false);
        }
    };



    if (!user) return null;

    return (
        <div className="fade-in">
            <div className="page-header" style={{ marginBottom: '1.5rem' }}>
                <h2 style={{ fontSize: '1.5rem', marginBottom: '0.25rem' }}>Profile</h2>
                <p style={{ fontSize: '0.9rem' }}>Account details and security</p>
            </div>

            <div className="profile-grid">
                {/* User Profile Card */}
                <div className="card profile-card" style={{ padding: '20px' }}>
                    <div className="profile-avatar" style={{ width: '70px', height: '70px', fontSize: '1.75rem', marginBottom: '16px' }}>
                        {user.firstName[0]}{user.lastName[0]}
                    </div>
                    
                    <h3 style={{ textAlign: 'center', marginBottom: '16px', color: 'var(--text-primary)', fontSize: '1.1rem' }}>Personal Information</h3>

                    {success && <div className="alert alert-success" style={{ padding: '8px 12px', fontSize: '0.85rem' }}>{success}</div>}
                    {error && <div className="alert alert-error" style={{ padding: '8px 12px', fontSize: '0.85rem' }}>{error}</div>}

                    {!editing ? (
                        <>
                            <div style={{ marginBottom: '12px' }}>
                                <div className="form-group" style={{ marginBottom: '10px' }}>
                                    <label style={{ fontSize: '0.75rem', marginBottom: '2px' }}>Email</label>
                                    <div style={{ color: 'var(--text-primary)', padding: '4px 0', fontSize: '0.95rem' }}>{user.email}</div>
                                </div>
                                <div className="form-row" style={{ gap: '12px', marginBottom: '10px' }}>
                                    <div className="form-group" style={{ marginBottom: 0 }}>
                                        <label style={{ fontSize: '0.75rem', marginBottom: '2px' }}>First Name</label>
                                        <div style={{ color: 'var(--text-primary)', padding: '4px 0', fontSize: '0.95rem' }}>{user.firstName}</div>
                                    </div>
                                    <div className="form-group" style={{ marginBottom: 0 }}>
                                        <label style={{ fontSize: '0.75rem', marginBottom: '2px' }}>Last Name</label>
                                        <div style={{ color: 'var(--text-primary)', padding: '4px 0', fontSize: '0.95rem' }}>{user.lastName}</div>
                                    </div>
                                </div>
                                <div className="form-group" style={{ marginBottom: '10px' }}>
                                    <label style={{ fontSize: '0.75rem', marginBottom: '2px' }}>Phone</label>
                                    <div style={{ color: 'var(--text-primary)', padding: '4px 0', fontSize: '0.95rem' }}>{user.phone || 'Not set'}</div>
                                </div>
                                <div className="form-row" style={{ gap: '12px' }}>
                                    <div className="form-group" style={{ marginBottom: 0 }}>
                                        <label style={{ fontSize: '0.75rem', marginBottom: '2px' }}>Role</label>
                                        <div style={{ padding: '4px 0' }}>
                                            <span className="badge badge-completed" style={{ fontSize: '0.7rem', padding: '2px 6px' }}>{user.role}</span>
                                        </div>
                                    </div>
                                    <div className="form-group" style={{ marginBottom: 0 }}>
                                        <label style={{ fontSize: '0.75rem', marginBottom: '2px' }}>Member Since</label>
                                        <div style={{ color: 'var(--text-primary)', padding: '4px 0', fontSize: '0.95rem' }}>{new Date(user.createdAt).toLocaleDateString()}</div>
                                    </div>
                                </div>
                            </div>
                            <div style={{ display: 'flex', gap: '8px', marginTop: 'auto', paddingTop: '12px' }}>
                                <button className="btn btn-primary btn-sm" onClick={() => setEditing(true)} style={{ flex: 1 }}>Edit Profile</button>
                                <button
                                    className="btn btn-danger btn-sm"
                                    onClick={handleDelete}
                                    disabled={deleting}
                                    style={{ flex: 1 }}
                                >
                                    {deleting ? 'Deleting...' : 'Delete Account'}
                                </button>
                            </div>
                        </>
                    ) : (
                        <form onSubmit={handleSubmit} style={{ display: 'flex', flexDirection: 'column', height: '100%' }}>
                            <div className="form-row" style={{ gap: '12px', marginBottom: '8px' }}>
                                <div className="form-group" style={{ marginBottom: 0 }}>
                                    <label htmlFor="prof-firstName" style={{ fontSize: '0.75rem' }}>First Name</label>
                                    <input id="prof-firstName" value={form.firstName} onChange={e => setForm({ ...form, firstName: e.target.value })} required style={{ padding: '6px 10px' }} />
                                </div>
                                <div className="form-group" style={{ marginBottom: 0 }}>
                                    <label htmlFor="prof-lastName" style={{ fontSize: '0.75rem' }}>Last Name</label>
                                    <input id="prof-lastName" value={form.lastName} onChange={e => setForm({ ...form, lastName: e.target.value })} required style={{ padding: '6px 10px' }} />
                                </div>
                            </div>
                            <div className="form-group" style={{ marginBottom: '12px' }}>
                                <label htmlFor="prof-phone" style={{ fontSize: '0.75rem' }}>Phone</label>
                                <input id="prof-phone" value={form.phone} onChange={e => setForm({ ...form, phone: e.target.value })} style={{ padding: '6px 10px' }} />
                            </div>
                            <div style={{ display: 'flex', gap: '8px', marginTop: 'auto', paddingTop: '12px' }}>
                                <button className="btn btn-primary btn-sm" type="submit" disabled={saving} style={{ flex: 1 }}>{saving ? 'Saving...' : 'Save Changes'}</button>
                                <button className="btn btn-secondary btn-sm" type="button" onClick={() => setEditing(false)} style={{ flex: 1 }}>Cancel</button>
                            </div>
                        </form>
                    )}
                </div>

                {/* Change Password Card */}
                <div className="card profile-card" style={{ padding: '20px' }}>
                    <h3 style={{ margin: '0 0 12px', color: 'var(--text-primary)', fontSize: '1.1rem' }}>Security Settings</h3>
                    <p style={{ color: 'var(--text-secondary)', marginBottom: '16px', fontSize: '0.85rem', lineHeight: '1.4' }}>
                        Update your password to keep your account secure.
                    </p>
                    
                    {passwordSuccess && <div className="alert alert-success" style={{ padding: '8px 12px', fontSize: '0.85rem', marginBottom: '12px' }}>{passwordSuccess}</div>}
                    {passwordError && <div className="alert alert-error" style={{ padding: '8px 12px', fontSize: '0.85rem', marginBottom: '12px' }}>{passwordError}</div>}

                    <form onSubmit={handlePasswordSubmit} style={{ display: 'flex', flexDirection: 'column', height: '100%' }}>
                        <div className="form-group" style={{ marginBottom: '10px' }}>
                            <label htmlFor="currentPassword" style={{ fontSize: '0.75rem', marginBottom: '4px' }}>Current Password</label>
                            <div style={{ display: 'flex', gap: '6px' }}>
                                <input
                                    id="currentPassword"
                                    type={showPassword ? "text" : "password"}
                                    value={passwordForm.currentPassword}
                                    onChange={e => setPasswordForm({ ...passwordForm, currentPassword: e.target.value })}
                                    required
                                    style={{ flex: 1, padding: '6px 10px' }}
                                />
                                <button
                                    type="button"
                                    className="btn btn-secondary btn-sm"
                                    onClick={() => setShowPassword(!showPassword)}
                                    style={{ padding: '0 8px', minWidth: '50px' }}
                                >
                                    {showPassword ? "Hide" : "Show"}
                                </button>
                            </div>
                        </div>
                        <div className="form-group" style={{ marginBottom: '10px' }}>
                            <label htmlFor="newPassword" style={{ fontSize: '0.75rem', marginBottom: '4px' }}>New Password</label>
                            <input
                                id="newPassword"
                                type={showPassword ? "text" : "password"}
                                value={passwordForm.newPassword}
                                onChange={e => setPasswordForm({ ...passwordForm, newPassword: e.target.value })}
                                required
                                minLength={6}
                                placeholder="Min. 6 chars"
                                style={{ padding: '6px 10px' }}
                            />
                        </div>
                        <div className="form-group" style={{ marginBottom: '16px' }}>
                            <label htmlFor="confirmPassword" style={{ fontSize: '0.75rem', marginBottom: '4px' }}>Confirm Password</label>
                            <input
                                id="confirmPassword"
                                type={showPassword ? "text" : "password"}
                                value={passwordForm.confirmPassword}
                                onChange={e => setPasswordForm({ ...passwordForm, confirmPassword: e.target.value })}
                                required
                                minLength={6}
                                placeholder="Repeat new"
                                style={{ padding: '6px 10px' }}
                            />
                        </div>
                        <div style={{ marginTop: 'auto', paddingTop: '12px' }}>
                            <button className="btn btn-primary btn-sm" type="submit" disabled={changingPassword} style={{ width: '100%' }}>
                                {changingPassword ? 'Updating...' : 'Update Password'}
                            </button>
                        </div>
                    </form>
                </div>
            </div>
        </div>
    );
};

export default Profile;