import React, { useState, useEffect } from 'react';
import { useAuth } from '../AuthContext';
import { accountApi, cardApi } from '../api';
import type { Account, Card } from '../types';

const Accounts: React.FC = () => {
    const { user } = useAuth();
    const [accounts, setAccounts] = useState<Account[]>([]);
    const [selectedAccountId, setSelectedAccountId] = useState<number | ''>('');
    const [cards, setCards] = useState<Card[]>([]);
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState<string | null>(null);
    const [hiddenCards, setHiddenCards] = useState<Set<number>>(new Set());

    // Account Management State
    const [addingAccount, setAddingAccount] = useState(false);
    const [accountError, setAccountError] = useState('');
    const [renamingId, setRenamingId] = useState<number | null>(null);
    const [renameValue, setRenameValue] = useState('');
    const [renameSaving, setRenameSaving] = useState(false);

    const toggleVisibility = (cardId: number) => {
        setHiddenCards(prev => {
            const next = new Set(prev);
            next.has(cardId) ? next.delete(cardId) : next.add(cardId);
            return next;
        });
    };

    useEffect(() => {
        if (user?.id) {
            fetchAccounts(user.id);
        }
    }, [user?.id]);

    useEffect(() => {
        if (selectedAccountId) {
            fetchCards(selectedAccountId as number);
        } else {
            setCards([]);
        }
    }, [selectedAccountId]);

    const fetchAccounts = async (userId: number) => {
        try {
            setLoading(true);
            const response = await accountApi.getByUserId(userId);
            setAccounts(response.data);
        } catch (err: any) {
            setError(err.response?.data?.message || 'Failed to load accounts');
        } finally {
            setLoading(false);
        }
    };

    const handleAddAccount = async () => {
        if (!user) return;
        setAddingAccount(true);
        setAccountError('');
        try {
            await accountApi.create({ userId: user.id, currency: 'EUR' });
            await fetchAccounts(user.id);
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
            if (selectedAccountId === accountId) {
                setSelectedAccountId('');
            }
        } catch (err: any) {
            setAccountError(err.response?.data?.message || 'Failed to delete account');
        }
    };

    const fetchCards = async (accountId: number) => {
        try {
            setLoading(true);
            const response = await cardApi.list(accountId);
            setCards(response.data);
            setError(null);
        } catch (err: any) {
            console.error('Error fetching cards:', err);
            // It's not necessarily an error if there are no cards
            setCards([]);
        } finally {
            setLoading(false);
        }
    };

    const handleCreateCard = async (e?: React.FormEvent) => {
        if (e) e.preventDefault();
        if (!selectedAccountId) {
            setError('Please select an account first');
            return;
        }

        try {
            setLoading(true);
            const response = await cardApi.create(selectedAccountId as number, {});
            setCards(prev => [...prev, response.data]);
            setError(null);
        } catch (err: any) {
            setError(err.response?.data?.message || 'Failed to add card');
        } finally {
            setLoading(false);
        }
    };

    const handleToggleBlock = async (cardId: number) => {
        if (!selectedAccountId) return;
        try {
            setLoading(true);
            const response = await cardApi.toggleBlock(selectedAccountId as number, cardId);
            setCards(cards.map(c => c.id === cardId ? response.data : c));
            setError(null);
        } catch (err: any) {
            setError(err.response?.data?.message || 'Failed to toggle block state');
        } finally {
            setLoading(false);
        }
    };

    const handleDelete = async (cardId: number) => {
        if (!selectedAccountId) return;
        if (!window.confirm('Are you sure you want to delete this card?')) return;
        try {
            setLoading(true);
            await cardApi.delete(selectedAccountId as number, cardId);
            setCards(cards.filter(c => c.id !== cardId));
            setError(null);
        } catch (err: any) {
            setError(err.response?.data?.message || 'Failed to delete card');
        } finally {
            setLoading(false);
        }
    };

    return (
        <div className="cards-page">
            <header className="page-header">
                <div className="header-content">
                    <h2>Accounts Management</h2>
                    <p>View and manage your accounts and associated cards</p>
                </div>
            </header>

            {error && <div className="error-message">{error}</div>}

            <div className="cards-content" style={{ display: 'flex', flexDirection: 'column', gap: '2rem', width: '100%', minWidth: 0 }}>
                <div className="account-management-section" style={{ minWidth: 0, width: '100%' }}>
                    <div className="card" style={{ marginBottom: '2rem', minWidth: 0, width: '100%', boxSizing: 'border-box' }}>
                        <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: 20, flexWrap: 'wrap', gap: '1rem' }}>
                            <div style={{ minWidth: '200px' }}>
                                <h3 style={{ margin: 0, color: 'var(--text-primary)' }}>My Accounts</h3>
                                <p style={{ margin: '4px 0 0', color: 'var(--text-secondary)', fontSize: '0.875rem' }}>
                                    Manage your accounts and their balances
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

                        {loading && accounts.length === 0 ? (
                            <div style={{ color: 'var(--text-secondary)', textAlign: 'center', padding: '24px 0' }}>Loading accounts…</div>
                        ) : accounts.length === 0 ? (
                            <div style={{ color: 'var(--text-secondary)', textAlign: 'center', padding: '24px 0' }}>
                                No accounts yet. Click <strong>+ Add Account</strong> to create one.
                            </div>
                        ) : (
                            <div style={{ display: 'flex', flexDirection: 'column', gap: 12 }}>
                                {accounts.map(account => (
                                    <div key={account.id} style={{ display: 'flex', flexDirection: 'column', gap: '1rem' }}>
                                        <div className="profile-account-item" style={{ padding: '16px', border: '1px solid var(--border)', borderRadius: 'var(--radius-md)', display: 'flex', justifyContent: 'space-between', alignItems: 'center', background: selectedAccountId === account.id ? 'var(--bg-card-hover)' : 'transparent', cursor: 'pointer', flexWrap: 'wrap', gap: '12px' }} onClick={() => setSelectedAccountId(selectedAccountId === account.id ? '' : account.id)}>
                                            <div style={{ flex: '1 1 200px', minWidth: 0 }}>
                                                {renamingId === account.id ? (
                                                    <div style={{ display: 'flex', gap: 8, alignItems: 'center' }} onClick={e => e.stopPropagation()}>
                                                        <input
                                                            value={renameValue}
                                                            onChange={e => setRenameValue(e.target.value)}
                                                            className="account-rename-input"
                                                            autoFocus
                                                            style={{ background: 'var(--bg-input)', border: '1px solid var(--accent)', color: 'var(--text-primary)', padding: '4px 8px', borderRadius: '4px', width: '100%', maxWidth: '200px' }}
                                                        />
                                                        <button className="btn btn-primary btn-sm" onClick={() => handleRename(account.id)} disabled={renameSaving}>{renameSaving ? '...' : '✓'}</button>
                                                        <button className="btn btn-secondary btn-sm" onClick={() => setRenamingId(null)}>✕</button>
                                                    </div>
                                                ) : (
                                                    <div style={{ display: 'flex', alignItems: 'center', gap: 8, marginBottom: 4 }}>
                                                        <span style={{ fontWeight: 600, color: 'var(--text-primary)', fontSize: '1.05rem' }}>{account.name || 'Unnamed Account'}</span>
                                                        <button
                                                            className="btn-icon-sm"
                                                            onClick={(e) => { e.stopPropagation(); handleStartRename(account); }}
                                                            title="Rename account"
                                                            style={{ opacity: 0.6, fontSize: '0.8rem' }}
                                                        >
                                                            ✏️
                                                        </button>
                                                    </div>
                                                )}
                                                <div style={{ fontFamily: 'monospace', fontSize: '0.85rem', color: 'var(--text-secondary)', overflow: 'hidden', textOverflow: 'ellipsis', whiteSpace: 'nowrap' }}>{account.iban}</div>
                                            </div>
                                            <div style={{ textAlign: 'right', display: 'flex', flexDirection: 'column', alignItems: 'flex-end', gap: 8, flex: '0 0 auto' }}>
                                                <div style={{ fontSize: '1.2rem', fontWeight: 700, color: 'var(--text-primary)' }}>€{account.balance.toFixed(2)}</div>
                                                <button className="btn btn-danger btn-sm" style={{ padding: '4px 8px', fontSize: '0.75rem' }} onClick={(e) => { e.stopPropagation(); handleDeleteAccount(account.id); }}>🗑️</button>
                                            </div>
                                        </div>

                                        {/* Cards for this account if selected */}
                                        {selectedAccountId === account.id && (
                                            <div style={{ marginLeft: '1rem', padding: '1rem', borderLeft: '2px solid var(--accent)', background: 'var(--bg-secondary)', borderRadius: '0 var(--radius-md) var(--radius-md) 0', minWidth: 0 }}>
                                                <h4 style={{ margin: '0 0 1rem', fontSize: '0.9rem', color: 'var(--text-secondary)', textTransform: 'uppercase', letterSpacing: '0.05em' }}>Cards associated with this account</h4>
                                                <div className="horizontal-cards-container" style={{ display: 'flex', gap: '1.5rem', overflowX: 'auto', paddingBottom: '1rem', scrollbarWidth: 'thin', maxWidth: '100%', boxSizing: 'border-box' }}>
                                                    {loading && !cards.length ? (
                                                        <p style={{ color: 'var(--text-secondary)' }}>Loading cards...</p>
                                                    ) : cards.length > 0 ? (
                                                        <>
                                                            {cards.map(card => (
                                                                <div key={card.id} style={{ flex: '0 0 auto', width: '300px' }}>
                                                                    <div className={`credit-card ${card.blocked ? 'blocked' : ''}`} style={{ height: '180px', padding: '16px' }}>
                                                                        <button
                                                                            onClick={() => toggleVisibility(card.id)}
                                                                            title={hiddenCards.has(card.id) ? 'Show info' : 'Hide info'}
                                                                            className="card-visibility-toggle"
                                                                            style={{ top: '10px', right: '10px' }}
                                                                        >
                                                                            {hiddenCards.has(card.id) ? (
                                                                                <svg width="12" height="12" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2"><path d="M17.94 17.94A10.07 10.07 0 0 1 12 20c-7 0-11-8-11-8a18.45 18.45 0 0 1 5.06-5.94M9.9 4.24A9.12 9.12 0 0 1 12 4c7 0 11 8 11 8a18.5 18.5 0 0 1-2.16 3.19m-6.72-1.07a3 3 0 1 1-4.24-4.24"></path><line x1="1" y1="1" x2="23" y2="23"></line></svg>
                                                                            ) : (
                                                                                <svg width="12" height="12" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2"><path d="M1 12s4-8 11-8 11 8 11 8-4 8-11 8-11-8-11-8z"></path><circle cx="12" cy="12" r="3"></circle></svg>
                                                                            )}
                                                                        </button>
                                                                        <div className="card-bank-name" style={{ fontSize: '0.8rem' }}>NexusBank</div>
                                                                        <div className="card-number" style={{ fontSize: '1rem', margin: '12px 0' }}>
                                                                            {hiddenCards.has(card.id) ? '•••• •••• •••• ••••' : card.cardNumber}
                                                                        </div>
                                                                        <div className="card-details" style={{ fontSize: '0.7rem' }}>
                                                                            <div className="card-holder">
                                                                                <span className="card-label">Holder</span>
                                                                                <span className="card-value">{hiddenCards.has(card.id) ? '••••' : `${user?.firstName} ${user?.lastName}`}</span>
                                                                            </div>
                                                                            <div className="card-expires">
                                                                                <span className="card-label">Exp</span>
                                                                                <span className="card-value">{hiddenCards.has(card.id) ? '••/••' : new Date(card.expiration).toLocaleDateString(undefined, { month: '2-digit', year: '2-digit' })}</span>
                                                                            </div>
                                                                        </div>
                                                                        {card.blocked && <div className="card-blocked-badge" style={{ fontSize: '0.6rem', padding: '2px 6px' }}>BLOCKED</div>}
                                                                    </div>
                                                                    <div style={{ display: 'flex', gap: '8px', marginTop: '10px' }}>
                                                                        <button onClick={() => handleToggleBlock(card.id)} className={`btn btn-xs ${card.blocked ? 'btn-primary' : 'btn-secondary'}`} style={{ padding: '2px 8px', fontSize: '0.7rem' }}>{card.blocked ? 'Unblock' : 'Block'}</button>
                                                                        <button onClick={() => handleDelete(card.id)} className="btn btn-xs btn-danger" style={{ padding: '2px 8px', fontSize: '0.7rem' }}>Delete</button>
                                                                    </div>
                                                                </div>
                                                            ))}
                                                        </>
                                                    ) : !loading && (
                                                        <p style={{ color: 'var(--text-secondary)', fontSize: '0.875rem' }}>No cards yet.</p>
                                                    )}
                                                    
                                                    {/* Add New Card Placeholder/Button in horizontal flow */}
                                                    <div 
                                                        onClick={handleCreateCard}
                                                        style={{ 
                                                            flex: '0 0 auto', 
                                                            width: '200px', 
                                                            height: '180px', 
                                                            border: '2px dashed var(--border)', 
                                                            borderRadius: '16px', 
                                                            display: 'flex', 
                                                            flexDirection: 'column', 
                                                            alignItems: 'center', 
                                                            justifyContent: 'center', 
                                                            cursor: loading ? 'not-allowed' : 'pointer',
                                                            background: 'rgba(255, 255, 255, 0.05)',
                                                            transition: 'all 0.2s ease',
                                                            color: 'var(--text-secondary)'
                                                        }}
                                                        onMouseOver={(e) => e.currentTarget.style.borderColor = 'var(--accent)'}
                                                        onMouseOut={(e) => e.currentTarget.style.borderColor = 'var(--border)'}
                                                    >
                                                        <span style={{ fontSize: '2rem', marginBottom: '8px' }}>+</span>
                                                        <span style={{ fontSize: '0.8rem', fontWeight: 600 }}>{loading ? 'Adding...' : 'Add New Card'}</span>
                                                    </div>
                                                </div>
                                            </div>
                                        )}
                                    </div>
                                ))}
                            </div>
                        )}
                    </div>
                </div>
            </div>
        </div>
    );
};

export default Accounts;
