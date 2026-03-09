import React, { useState, useEffect } from 'react';
import { useAuth } from '../AuthContext';
import { userApi, accountApi } from '../api';
import type { Account } from '../types';

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

    // Accounts state
    const [accounts, setAccounts] = useState<Account[]>([]);
    const [accountsLoading, setAccountsLoading] = useState(true);
    const [addingAccount, setAddingAccount] = useState(false);
    const [accountError, setAccountError] = useState('');
    // Rename state: map of accountId -> draft name
    const [renamingId, setRenamingId] = useState<number | null>(null);
    const [renameValue, setRenameValue] = useState('');
    const [renameSaving, setRenameSaving] = useState(false);

    const fetchAccounts = async () => {
        if (!user) return;
        try {
            const res = await accountApi.getByUserId(user.id);
            setAccounts(res.data);
        } catch {
            // User may not have accounts yet (new user), show empty
            setAccounts([]);
        } finally {
            setAccountsLoading(false);
        }
    };

    useEffect(() => {
        fetchAccounts();
    }, [user?.id]);

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

    const handleAddAccount = async () => {
        if (!user) return;
        setAddingAccount(true);
        setAccountError('');
        try {
            await accountApi.create({ userId: user.id, currency: 'EUR' });
            await fetchAccounts();
        } catch (err: any) {
            setAccountError(err.response?.data?.message || 'Failed to create account');
        } finally {
            setAddingAccount(false);
        }
    };

    const handleStartRename = (account: Account) => {
        setRenamingId(account.id);
        setRenameValue(account.name || '');
    };

    const handleRename = async (accountId: number) => {
        setRenameSaving(true);
        try {
            await accountApi.updateName(accountId, renameValue);
            setAccounts(prev => prev.map(a => a.id === accountId ? { ...a, name: renameValue } : a));
            setRenamingId(null);
        } catch (err: any) {
            setAccountError(err.response?.data?.message || 'Failed to rename account');
        } finally {
            setRenameSaving(false);
        }
    };
    const handleDeleteAccount = async (accountId: number) => {
        if (!window.confirm("Are you sure you want to delete this account? This action cannot be undone.")) return;
        try {
            await accountApi.delete(accountId);
            setAccounts(prev => prev.filter(a => a.id !== accountId));
        } catch (err: any) {
            setAccountError(err.response?.data?.message || 'Failed to delete account');
        }
    };

    if (!user) return null;

    return (
        <div className="fade-in">
            <div className="page-header">
                <h2>Profile</h2>
                <p>Manage your account details</p>
            </div>

            {/* ─── Two-column grid: profile card | accounts card ─── */}
            <div className="profile-grid">

                <div className="card profile-card">
                    <div className="profile-avatar">
                        {user.firstName[0]}{user.lastName[0]}
                    </div>

                    {success && <div className="alert alert-success">{success}</div>}
                    {error && <div className="alert alert-error">{error}</div>}

                    {!editing ? (
                        <>
                            <div style={{ marginBottom: 20 }}>
                                <div className="form-group">
                                    <label>Email</label>
                                    <div style={{ color: 'var(--text-primary)', padding: '12px 0 0' }}>{user.email}</div>
                                </div>
                                <div className="form-row">
                                    <div className="form-group">
                                        <label>First Name</label>
                                        <div style={{ color: 'var(--text-primary)', padding: '12px 0 0' }}>{user.firstName}</div>
                                    </div>
                                    <div className="form-group">
                                        <label>Last Name</label>
                                        <div style={{ color: 'var(--text-primary)', padding: '12px 0 0' }}>{user.lastName}</div>
                                    </div>
                                </div>
                                <div className="form-group">
                                    <label>Phone</label>
                                    <div style={{ color: 'var(--text-primary)', padding: '12px 0 0' }}>{user.phone || 'Not set'}</div>
                                </div>
                                <div className="form-group">
                                    <label>Role</label>
                                    <div style={{ color: 'var(--text-primary)', padding: '12px 0 0' }}>
                                        <span className="badge badge-completed">{user.role}</span>
                                    </div>
                                </div>
                                <div className="form-group">
                                    <label>Member Since</label>
                                    <div style={{ color: 'var(--text-primary)', padding: '12px 0 0' }}>{new Date(user.createdAt).toLocaleDateString()}</div>
                                </div>
                            </div>
                            <div style={{ display: 'flex', gap: 12 }}>
                                <button className="btn btn-primary" onClick={() => setEditing(true)}>Edit Profile</button>
                                <button
                                    className="btn btn-danger"
                                    onClick={handleDelete}
                                    disabled={deleting}
                                >
                                    {deleting ? 'Deleting...' : 'Delete Profile'}
                                </button>
                            </div>
                        </>
                    ) : (
                        <form onSubmit={handleSubmit} style={{ marginBottom: 24 }}>
                            <div className="form-row">
                                <div className="form-group">
                                    <label htmlFor="prof-firstName">First Name</label>
                                    <input id="prof-firstName" value={form.firstName} onChange={e => setForm({ ...form, firstName: e.target.value })} required />
                                </div>
                                <div className="form-group">
                                    <label htmlFor="prof-lastName">Last Name</label>
                                    <input id="prof-lastName" value={form.lastName} onChange={e => setForm({ ...form, lastName: e.target.value })} required />
                                </div>
                            </div>
                            <div className="form-group">
                                <label htmlFor="prof-phone">Phone</label>
                                <input id="prof-phone" value={form.phone} onChange={e => setForm({ ...form, phone: e.target.value })} />
                            </div>
                            <div style={{ display: 'flex', gap: 12 }}>
                                <button className="btn btn-primary" type="submit" disabled={saving}>{saving ? 'Saving...' : 'Save Changes'}</button>
                                <button className="btn btn-secondary" type="button" onClick={() => setEditing(false)}>Cancel</button>
                            </div>
                        </form>
                    )}

                    <hr style={{ margin: '24px 0', border: 'none', borderTop: '1px solid var(--border)' }} />
                    <h3 style={{ margin: '0 0 16px', color: 'var(--text-primary)' }}>Change Password</h3>
                    {passwordSuccess && <div className="alert alert-success">{passwordSuccess}</div>}
                    {passwordError && <div className="alert alert-error">{passwordError}</div>}

                    <form onSubmit={handlePasswordSubmit}>
                        <div className="form-group">
                            <label htmlFor="currentPassword">Current Password</label>
                            <div style={{ display: 'flex', gap: 8 }}>
                                <input
                                    id="currentPassword"
                                    type={showPassword ? "text" : "password"}
                                    value={passwordForm.currentPassword}
                                    onChange={e => setPasswordForm({ ...passwordForm, currentPassword: e.target.value })}
                                    required
                                    style={{ flex: 1 }}
                                />
                                <button
                                    type="button"
                                    className="btn btn-secondary"
                                    onClick={() => setShowPassword(!showPassword)}
                                    style={{ padding: '0 12px' }}
                                    title={showPassword ? "Hide password" : "Show password"}
                                >
                                    {showPassword ? "Hide" : "Show"}
                                </button>
                            </div>
                        </div>
                        <div className="form-row">
                            <div className="form-group">
                                <label htmlFor="newPassword">New Password</label>
                                <input
                                    id="newPassword"
                                    type={showPassword ? "text" : "password"}
                                    value={passwordForm.newPassword}
                                    onChange={e => setPasswordForm({ ...passwordForm, newPassword: e.target.value })}
                                    required
                                    minLength={6}
                                />
                            </div>
                            <div className="form-group">
                                <label htmlFor="confirmPassword">Confirm Password</label>
                                <input
                                    id="confirmPassword"
                                    type={showPassword ? "text" : "password"}
                                    value={passwordForm.confirmPassword}
                                    onChange={e => setPasswordForm({ ...passwordForm, confirmPassword: e.target.value })}
                                    required
                                    minLength={6}
                                />
                            </div>
                        </div>
                        <button className="btn btn-primary btn-sm" type="submit" disabled={changingPassword}>
                            {changingPassword ? 'Changing...' : 'Update Password'}
                        </button>
                    </form>
                </div>

                {/* ── My Accounts card ── */}
                <div className="card">
                    <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: 20 }}>
                        <div>
                            <h3 style={{ margin: 0, color: 'var(--text-primary)' }}>My Accounts</h3>
                            <p style={{ margin: '4px 0 0', color: 'var(--text-secondary)', fontSize: '0.875rem' }}>
                                Each account has its own IBAN and balance
                            </p>
                        </div>
                        <button
                            className="btn btn-primary"
                            onClick={handleAddAccount}
                            disabled={addingAccount}
                        >
                            {addingAccount ? 'Creating…' : '+ Add Account'}
                        </button>
                    </div>

                    {accountError && <div className="alert alert-error" style={{ marginBottom: 16 }}>{accountError}</div>}

                    {accountsLoading ? (
                        <div style={{ color: 'var(--text-secondary)', textAlign: 'center', padding: '24px 0' }}>Loading accounts…</div>
                    ) : accounts.length === 0 ? (
                        <div style={{ color: 'var(--text-secondary)', textAlign: 'center', padding: '24px 0' }}>
                            No accounts yet. Click <strong>+ Add Account</strong> to create one.
                        </div>
                    ) : (
                        <div style={{ display: 'flex', flexDirection: 'column', gap: 12 }}>
                            {accounts.map(account => (
                                <div key={account.id} className="profile-account-item">
                                    <div style={{ flex: 1, minWidth: 0 }}>
                                        {/* Account name / rename */}
                                        {renamingId === account.id ? (
                                            <div style={{ display: 'flex', gap: 8, alignItems: 'center', marginBottom: 8 }}>
                                                <input
                                                    value={renameValue}
                                                    onChange={e => setRenameValue(e.target.value)}
                                                    placeholder="Account name"
                                                    style={{ flex: 1, padding: '6px 10px', borderRadius: 8, border: '1px solid var(--border)', background: 'var(--bg-input)', color: 'var(--text-primary)', fontSize: '0.875rem' }}
                                                    autoFocus
                                                />
                                                <button
                                                    className="btn btn-primary"
                                                    style={{ padding: '6px 14px', fontSize: '0.8rem' }}
                                                    disabled={renameSaving}
                                                    onClick={() => handleRename(account.id)}
                                                >
                                                    {renameSaving ? '…' : 'Save'}
                                                </button>
                                                <button
                                                    className="btn btn-secondary"
                                                    style={{ padding: '6px 14px', fontSize: '0.8rem' }}
                                                    onClick={() => setRenamingId(null)}
                                                >
                                                    Cancel
                                                </button>
                                            </div>
                                        ) : (
                                            <div style={{ display: 'flex', alignItems: 'center', gap: 8, marginBottom: 6 }}>
                                                <span style={{ fontWeight: 600, color: 'var(--text-primary)', fontSize: '1rem' }}>
                                                    {account.name || 'Unnamed Account'}
                                                </span>
                                                <button
                                                    onClick={() => handleStartRename(account)}
                                                    style={{ background: 'none', border: 'none', cursor: 'pointer', color: 'var(--accent)', fontSize: '0.8rem', padding: '2px 6px' }}
                                                    title="Rename"
                                                >
                                                    ✏️ Rename
                                                </button>
                                            </div>
                                        )}
                                        {/* IBAN */}
                                        <div style={{ fontFamily: 'monospace', fontSize: '0.85rem', color: 'var(--text-secondary)', letterSpacing: '0.05em' }}>
                                            {account.iban}
                                        </div>
                                        <div style={{ fontSize: '0.75rem', color: 'var(--text-secondary)', marginTop: 4 }}>
                                            Created {new Date(account.createdAt).toLocaleDateString()}
                                        </div>
                                    </div>
                                    {/* Balance & Delete */}
                                    <div style={{ textAlign: 'right', flexShrink: 0, display: 'flex', flexDirection: 'column', alignItems: 'flex-end', gap: 8 }}>
                                        <div>
                                            <div style={{ fontSize: '1.4rem', fontWeight: 700, color: 'var(--text-primary)' }}>
                                                {Number(account.balance).toFixed(2)}
                                            </div>
                                            <div style={{ fontSize: '0.8rem', color: 'var(--text-secondary)' }}>{account.currency}</div>
                                        </div>
                                        <button
                                            className="btn btn-danger btn-sm"
                                            style={{ padding: '4px 8px', fontSize: '0.75rem' }}
                                            onClick={() => handleDeleteAccount(account.id)}
                                        >
                                            🗑️ Delete
                                        </button>
                                    </div>
                                </div>
                            ))}
                        </div>
                    )}
                </div>
            </div> {/* end two-column grid */}
        </div>
    );
};

export default Profile;