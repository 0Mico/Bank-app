import React, { useState, useEffect } from 'react';
import { useAuth } from '../AuthContext';
import { accountApi, cardApi } from '../api';
import type { Account, Card } from '../types';

const Cards: React.FC = () => {
    const { user } = useAuth();
    const [accounts, setAccounts] = useState<Account[]>([]);
    const [selectedAccountId, setSelectedAccountId] = useState<number | ''>('');
    const [cards, setCards] = useState<Card[]>([]);
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState<string | null>(null);
    const [hiddenCards, setHiddenCards] = useState<Set<number>>(new Set());

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
            if (response.data.length > 0) {
                setSelectedAccountId(response.data[0].id);
            }
        } catch (err: any) {
            setError(err.response?.data?.message || 'Failed to load accounts');
        } finally {
            setLoading(false);
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

    const handleCreateCard = async (e: React.FormEvent) => {
        e.preventDefault();
        if (!selectedAccountId) {
            setError('Please select an account first');
            return;
        }

        try {
            setLoading(true);
            const response = await cardApi.create(selectedAccountId as number, {});
            setCards([...cards, response.data]);
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
                    <h2>Cards Management</h2>
                    <p>View and add cards to your accounts</p>
                </div>
            </header>

            {error && <div className="error-message">{error}</div>}

            <div className="cards-content">
                <div className="card-card account-selector-card">
                    <h3>Select Account</h3>
                    <div className="form-group">
                        <select
                            id="account-selector"
                            value={selectedAccountId}
                            onChange={(e) => setSelectedAccountId(Number(e.target.value))}
                            className="form-control"
                            disabled={loading || accounts.length === 0}
                        >
                            <option value="">-- Select an account --</option>
                            {accounts.map(acc => (
                                <option key={acc.id} value={acc.id}>
                                    {acc.name || 'Account'} - {acc.iban} (Balance: €{acc.balance.toFixed(2)})
                                </option>
                            ))}
                        </select>
                    </div>
                </div>

                {selectedAccountId && (
                    <div className="cards-split-layout" style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '2rem', marginTop: '2rem' }}>
                        <div className="card-card existing-cards-section">
                            <h3>Your Cards</h3>
                            {loading && !cards.length ? (
                                <p>Loading cards...</p>
                            ) : cards.length > 0 ? (
                                <div className="cards-list" style={{ display: 'flex', flexDirection: 'column', gap: '1rem' }}>
                                    {cards.map(card => (
                                        <div key={card.id} style={{ position: 'relative' }}>
                                            <div className={`credit-card ${card.blocked ? 'blocked' : ''}`}>
                                                {/* Visibility toggle icon */}
                                                <button
                                                    onClick={() => toggleVisibility(card.id)}
                                                    title={hiddenCards.has(card.id) ? 'Show card info' : 'Hide card info'}
                                                    className="card-visibility-toggle"
                                                >
                                                    {hiddenCards.has(card.id) ? (
                                                        <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
                                                            <path d="M17.94 17.94A10.07 10.07 0 0 1 12 20c-7 0-11-8-11-8a18.45 18.45 0 0 1 5.06-5.94M9.9 4.24A9.12 9.12 0 0 1 12 4c7 0 11 8 11 8a18.5 18.5 0 0 1-2.16 3.19m-6.72-1.07a3 3 0 1 1-4.24-4.24"></path>
                                                            <line x1="1" y1="1" x2="23" y2="23"></line>
                                                        </svg>
                                                    ) : (
                                                        <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
                                                            <path d="M1 12s4-8 11-8 11 8 11 8-4 8-11 8-11-8-11-8z"></path>
                                                            <circle cx="12" cy="12" r="3"></circle>
                                                        </svg>
                                                    )}
                                                </button>

                                                <div className="card-bank-name">
                                                    <span>NexusBank</span>
                                                    <span style={{ fontSize: '1.2rem', fontStyle: 'italic', fontWeight: 'bold' }}>VISA</span>
                                                </div>
                                                <div className="card-chip"></div>
                                                <div className="card-number" style={{ letterSpacing: hiddenCards.has(card.id) ? '4px' : undefined, transition: 'letter-spacing 0.2s' }}>
                                                    {hiddenCards.has(card.id) ? '•••• •••• •••• ••••' : card.cardNumber}
                                                </div>
                                                <div className="card-details">
                                                    <div className="card-holder">
                                                        <span className="card-label">Cardholder</span>
                                                        <span className="card-value" style={{ transition: 'opacity 0.2s' }}>
                                                            {hiddenCards.has(card.id) ? '••••••••••••' : `${user?.firstName} ${user?.lastName}`}
                                                        </span>
                                                    </div>
                                                    <div className="card-expires">
                                                        <span className="card-label">Expires</span>
                                                        <span className="card-value">
                                                            {hiddenCards.has(card.id) ? '••/••' : new Date(card.expiration).toLocaleDateString(undefined, { month: '2-digit', year: '2-digit' })}
                                                        </span>
                                                    </div>
                                                </div>
                                            </div>
                                            <div style={{ display: 'flex', gap: '8px', marginTop: '12px' }}>
                                                <button
                                                    onClick={() => handleToggleBlock(card.id)}
                                                    className={`btn btn-sm ${card.blocked ? 'btn-primary' : 'btn-secondary'}`}
                                                    disabled={loading}
                                                >
                                                    {card.blocked ? 'Unblock' : 'Block'}
                                                </button>
                                                <button
                                                    onClick={() => handleDelete(card.id)}
                                                    className="btn btn-sm btn-danger"
                                                    disabled={loading}
                                                >
                                                    Delete
                                                </button>
                                            </div>
                                            {card.blocked && (
                                                <div className="card-blocked-badge">
                                                    BLOCKED
                                                </div>
                                            )}
                                        </div>
                                    ))}
                                </div>
                            ) : (
                                <p className="no-data">No cards associated with this account.</p>
                            )}
                        </div>

                        <div className="card-card add-card-section">
                            <h3>Add New Card</h3>
                            <p style={{ marginBottom: '1rem', color: 'var(--text-muted)' }}>
                                A new card will be generated and associated with your selected account automatically.
                            </p>
                            <form onSubmit={handleCreateCard} className="form">
                                <button type="submit" className="btn btn-primary" disabled={loading} style={{ width: '100%' }}>
                                    {loading ? 'Adding...' : 'Generate and Add Card'}
                                </button>
                            </form>
                        </div>
                    </div>
                )}
            </div>
        </div>
    );
};

export default Cards;
