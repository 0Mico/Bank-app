import React, { useEffect, useState } from 'react';
import { useAuth } from '../AuthContext';
import { transactionApi, accountApi, userApi } from '../api';
import type { Transaction, User, Account } from '../types';

const TYPES = ['', 'CREDIT', 'DEBIT'];

interface CounterpartyInfo {
    name: string;
    iban: string;
}

const TransactionModal: React.FC<{ transaction: Transaction; onClose: () => void; isInternal: boolean }> = ({ transaction: t, onClose, isInternal }) => {
    const [counterparty, setCounterparty] = useState<CounterpartyInfo | null>(null);
    const [loadingCp, setLoadingCp] = useState(true);

    useEffect(() => {
        const fetchCounterparty = async () => {
            if (!t.counterpartyIban) { setLoadingCp(false); return; }
            try {
                const userRes = await userApi.getByIban(t.counterpartyIban);
                const u: User = userRes.data;
                setCounterparty({ name: `${u.firstName} ${u.lastName}`, iban: t.counterpartyIban });
            } catch {
                setCounterparty({ name: 'Unknown', iban: t.counterpartyIban });
            } finally {
                setLoadingCp(false);
            }
        };
        fetchCounterparty();
    }, [t]);

    const isDebit = t.type === 'DEBIT';
    const counterpartyLabel = isDebit ? 'Recipient' : 'Sender';

    return (
        <div className="modal-overlay" onClick={onClose}>
            <div className="modal-card" onClick={e => e.stopPropagation()}>
                <div className="modal-header">
                    <h3>Transaction Detail</h3>
                    <button className="modal-close" onClick={onClose}>✕</button>
                </div>
                <div className="modal-body">
                    {/* Amount hero */}
                    <div style={{ textAlign: 'center', marginBottom: 24 }}>
                        <span className={`amount ${t.type.toLowerCase()}`} style={{ fontSize: '2rem', fontWeight: 700 }}>
                            {isDebit ? '-' : '+'}€{t.amount.toFixed(2)}
                        </span>
                        <div style={{ marginTop: 6, display: 'flex', gap: '8px', justifyContent: 'center' }}>
                            <span className={`badge badge-${t.type.toLowerCase()}`}>{t.type}</span>
                            <span className={`badge`} style={{ background: isInternal ? 'rgba(99, 102, 241, 0.15)' : 'rgba(100, 116, 139, 0.15)', color: isInternal ? 'var(--accent)' : 'var(--text-secondary)' }}>
                                {isInternal ? 'Internal' : 'External'}
                            </span>
                        </div>
                    </div>

                    {/* Counterparty */}
                    <div className="detail-section">
                        <div className="detail-label">{counterpartyLabel}</div>
                        <div className="detail-value">
                            {loadingCp ? (
                                <span style={{ color: 'var(--text-muted)', fontSize: '0.9rem' }}>Loading…</span>
                            ) : (
                                <strong>{isInternal ? 'Your Account' : (counterparty?.name ?? '—')}</strong>
                            )}
                        </div>
                        {counterparty?.iban && (
                            <>
                                <div className="detail-label" style={{ marginTop: 8 }}>{counterpartyLabel} IBAN</div>
                                <div className="detail-value" style={{ fontFamily: 'monospace', fontSize: '0.85rem' }}>{counterparty.iban}</div>
                            </>
                        )}
                    </div>

                    {/* Other fields */}
                    <div className="detail-section">
                        <div className="detail-row"><span className="detail-label">Category</span><span className="detail-value">{t.category}</span></div>
                        <div className="detail-row"><span className="detail-label">Description</span><span className="detail-value">{t.description || '—'}</span></div>
                        <div className="detail-row"><span className="detail-label">Date</span><span className="detail-value">{new Date(t.createdAt).toLocaleString()}</span></div>
                        <div className="detail-row">
                            <span className="detail-label">Reference ID</span>
                            <span className="detail-value" style={{ fontFamily: 'monospace', fontSize: '0.8rem' }}>{t.referenceId || '—'}</span>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    );
};

const Transactions: React.FC<{ hideHeader?: boolean; accountId?: number }> = ({ hideHeader, accountId }) => {
    const { user } = useAuth();
    const [transactions, setTransactions] = useState<Transaction[]>([]);
    const [categories, setCategories] = useState<string[]>([]);
    const [userIbans, setUserIbans] = useState<Set<string>>(new Set());
    const [loading, setLoading] = useState(true);
    const [filters, setFilters] = useState({ category: '', type: '', from: '', to: '' });
    const [selected, setSelected] = useState<Transaction | null>(null);

    const loadCategories = async () => {
        try {
            const res = await transactionApi.categories();
            setCategories(res.data);
        } catch (error) {
            console.error('Failed to load categories', error);
        }
    };

    const load = async () => {
        if (!user) return;
        setLoading(true);
        try {
            // Fetch user's accounts to determine internal transactions
            const accRes = await accountApi.getByUserId(user.id);
            const ibans = new Set(accRes.data.map((a: Account) => a.iban).filter(Boolean) as string[]);
            setUserIbans(ibans);

            const params: Record<string, string> = { userId: String(user.id) };
            if (accountId) params.accountId = String(accountId);
            if (filters.category) params.category = filters.category;
            if (filters.type) params.type = filters.type;
            if (filters.from) params.from = filters.from + 'T00:00:00';
            if (filters.to) params.to = filters.to + 'T23:59:59';
            const res = await transactionApi.list(params);
            setTransactions(res.data);
        } finally {
            setLoading(false);
        }
    };

    useEffect(() => { 
        load();
        loadCategories();
    }, [user, accountId]);

    const handleFilter = (e: React.FormEvent) => {
        e.preventDefault();
        load();
    };

    return (
        <div className="fade-in">
            {!hideHeader && (
                <div className="page-header">
                    <h2>Transactions</h2>
                    <p>View and filter your transaction history</p>
                </div>
            )}

            <form onSubmit={handleFilter}>
                <div className="filters">
                    <div className="form-group">
                        <label>Category</label>
                        <select value={filters.category} onChange={e => setFilters({ ...filters, category: e.target.value })}>
                            <option value="">All Categories</option>
                            {categories.map(c => <option key={c} value={c}>{c}</option>)}
                        </select>
                    </div>
                    <div className="form-group">
                        <label>Type</label>
                        <select value={filters.type} onChange={e => setFilters({ ...filters, type: e.target.value })}>
                            <option value="">All Types</option>
                            {TYPES.filter(Boolean).map(t => <option key={t} value={t}>{t}</option>)}
                        </select>
                    </div>
                    <div className="form-group">
                        <label>From</label>
                        <input type="date" value={filters.from} onChange={e => setFilters({ ...filters, from: e.target.value })} />
                    </div>
                    <div className="form-group">
                        <label>To</label>
                        <input type="date" value={filters.to} onChange={e => setFilters({ ...filters, to: e.target.value })} />
                    </div>
                    <button className="btn btn-primary btn-sm" type="submit">Apply</button>
                </div>
            </form>

            <div className="card">
                {loading ? (
                    <div className="loading"><div className="spinner" /></div>
                ) : transactions.length === 0 ? (
                    <div className="empty-state">
                        <div className="icon">📋</div>
                        <p>No transactions found</p>
                    </div>
                ) : (
                    <div className="table-container">
                        <table>
                            <thead>
                                <tr><th>Date</th><th>Type</th><th>Transfer</th><th>Category</th><th>To / From IBAN</th><th>Description</th><th>Reference</th><th>Amount</th></tr>
                            </thead>
                            <tbody>
                                {transactions.map(t => {
                                    const isInternal = !!t.counterpartyIban && userIbans.has(t.counterpartyIban);
                                    return (
                                        <tr
                                            key={t.id}
                                            onClick={() => setSelected(t)}
                                            style={{ cursor: 'pointer' }}
                                            className="transaction-row"
                                        >
                                            <td>{new Date(t.createdAt).toLocaleString()}</td>
                                            <td><span className={`badge badge-${t.type.toLowerCase()}`}>{t.type}</span></td>
                                            <td>
                                                <span className={`badge`} style={{ background: isInternal ? 'rgba(99, 102, 241, 0.15)' : 'rgba(100, 116, 139, 0.15)', color: isInternal ? 'var(--accent)' : 'var(--text-secondary)' }}>
                                                    {isInternal ? 'Internal' : 'External'}
                                                </span>
                                            </td>
                                            <td>{t.category}</td>
                                            <td style={{ fontFamily: 'monospace', fontSize: '0.8rem' }}>{t.counterpartyIban || '—'}</td>
                                            <td>{t.description || '—'}</td>
                                            <td style={{ fontSize: '0.8rem', color: 'var(--text-muted)' }}>{t.referenceId?.slice(0, 8) || '—'}</td>
                                            <td><span className={`amount ${t.type.toLowerCase()}`}>
                                                {t.type === 'CREDIT' ? '+' : '-'}€{t.amount.toFixed(2)}
                                            </span></td>
                                        </tr>
                                    );
                                })}
                            </tbody>
                        </table>
                    </div>
                )}
            </div>

            {selected && <TransactionModal transaction={selected} onClose={() => setSelected(null)} isInternal={!!selected.counterpartyIban && userIbans.has(selected.counterpartyIban)} />}
        </div>
    );
};

export default Transactions;
