import React, { useEffect, useState } from 'react';
import { useAuth } from '../AuthContext';
import { paymentApi, accountApi } from '../api';
import type { Payment, Account } from '../types';

const CATEGORIES = ['SALARY', 'TRANSFER', 'PAYMENT', 'FOOD', 'TRANSPORT', 'ENTERTAINMENT', 'UTILITIES', 'HEALTHCARE', 'SHOPPING', 'OTHER'];

const Payments: React.FC = () => {
    const { user, selectedAccountId, setSelectedAccountId } = useAuth();
    const [accounts, setAccounts] = useState<Account[]>([]);
    const [loading, setLoading] = useState(true);
    const [showForm, setShowForm] = useState(false);
    const [form, setForm] = useState({ toIban: '', amount: '', category: '', description: '' });
    const [depositForm, setDepositForm] = useState({ amount: '' });
    const [showDepositForm, setShowDepositForm] = useState(false);
    const [error, setError] = useState('');
    const [success, setSuccess] = useState('');
    const [submitting, setSubmitting] = useState(false);
    const [depositing, setDepositing] = useState(false);

    const [transferMode, setTransferMode] = useState<'INTERNAL' | 'EXTERNAL'>('INTERNAL');
    const [selectedDestinationAccountId, setSelectedDestinationAccountId] = useState<number | ''>('');

    const load = async () => {
        if (!user) return;
        try {
            const accRes = await accountApi.get(user.id);
            if (accRes.data) {
                const fetchedAccounts = accRes.data;
                setAccounts(fetchedAccounts);
                if (fetchedAccounts.length > 0 && selectedAccountId === null) {
                    setSelectedAccountId(fetchedAccounts[0].id);
                }
            }
        } finally {
            setLoading(false);
        }
    };

    useEffect(() => { load(); }, [user]); // selectedAccountId excluded to avoid refetch loop

    const activeAccount = accounts.find(a => a.id === selectedAccountId);
    const availableDestinationAccounts = accounts.filter(a => a.id !== activeAccount?.id);

    // Reset destination account if active account changes
    useEffect(() => {
        if (transferMode === 'INTERNAL' && availableDestinationAccounts.length > 0) {
           setSelectedDestinationAccountId(availableDestinationAccounts[0].id);
        } else {
           setSelectedDestinationAccountId('');
        }
    }, [activeAccount, transferMode, accounts.length]);

    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault();
        if (!activeAccount) return;
        setError('');
        setSuccess('');
        setSubmitting(true);
        
        try {
            let targetIban = form.toIban.trim();
            let finalDescription = form.description;

            if (transferMode === 'INTERNAL') {
                const destAccount = accounts.find(a => a.id === selectedDestinationAccountId);
                if (!destAccount || !destAccount.iban) {
                    throw new Error("Invalid destination account selected");
                }
                targetIban = destAccount.iban;
                if (!finalDescription) {
                    finalDescription = `Internal Transfer to ${destAccount.name || 'Account #' + destAccount.id}`;
                }
            }

            await paymentApi.process({
                fromAccountId: activeAccount.id,
                toIban: targetIban,
                amount: Number(form.amount),
                category: form.category || 'TRANSFER',
                description: finalDescription,
            });
            setSuccess('Payment sent successfully!');
            setForm({ toIban: '', amount: '', category: '', description: '' });
            setShowForm(false);
            load();
        } catch (err: any) {
            setError(err.response?.data?.message || err.message || 'Payment failed');
        } finally {
            setSubmitting(false);
        }
    };

    const handleDeposit = async (e: React.FormEvent) => {
        e.preventDefault();
        if (!activeAccount) return;
        setError('');
        setSuccess('');
        setDepositing(true);
        try {
            await accountApi.deposit(activeAccount.id, Number(depositForm.amount));
            setSuccess('Deposit successful!');
            setDepositForm({ amount: '' });
            setShowDepositForm(false);
            load();
        } catch (err: any) {
            setError(err.response?.data?.message || 'Deposit failed');
        } finally {
            setDepositing(false);
        }
    };

    if (loading) return <div className="loading"><div className="spinner" /></div>;

    return (
        <div className="fade-in">
            <div className="page-header">
                <h2>Payments & Transfers</h2>
                <p>Send money to other users or transfer between your own accounts</p>
            </div>

            {accounts.length > 0 && (
                <div className="card account-selector-card">
                    <label htmlFor="account-select">Select Primary Account</label>
                    <select
                        id="account-select"
                        className="form-control account-selector-select"
                        value={selectedAccountId || ''}
                        onChange={(e) => setSelectedAccountId(Number(e.target.value))}
                    >
                        {accounts.map(acc => (
                            <option key={acc.id} value={acc.id}>
                                {acc.name ? `${acc.name} - ${acc.iban || `Account #${acc.id}`}` : (acc.iban || `Account #${acc.id}`)} - {acc.currency} {acc.balance.toLocaleString('en', { minimumFractionDigits: 2 })}
                            </option>
                        ))}
                    </select>
                </div>
            )}

            {activeAccount && (
                <div className="card-grid">
                    <div className="stat-card">
                        <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                            <div>
                                <div className="stat-label">Available Balance</div>
                                <div className="stat-value">{activeAccount.currency} {activeAccount.balance.toLocaleString('en', { minimumFractionDigits: 2 })}</div>
                            </div>
                            <button className="btn btn-primary btn-sm" onClick={() => setShowDepositForm(!showDepositForm)}>
                                {showDepositForm ? 'Cancel' : 'Deposit'}
                            </button>
                        </div>
                        {showDepositForm && (
                            <form onSubmit={handleDeposit} style={{ marginTop: 16 }}>
                                <div className="form-group" style={{ display: 'flex', gap: 8 }}>
                                    <input type="number" step="0.01" min="0.01" placeholder="Amount" value={depositForm.amount} onChange={e => setDepositForm({ amount: e.target.value })} required />
                                    <button className="btn btn-primary btn-sm" type="submit" disabled={depositing}>
                                        {depositing ? '...' : 'Confirm'}
                                    </button>
                                </div>
                            </form>
                        )}
                    </div>
                </div>
            )}

            {accounts.length === 0 && (
                <div className="card" style={{ marginBottom: 24 }}>
                    <div className="empty-state">
                        <div className="icon">🏦</div>
                        <p>You don't have an active account yet. Please contact support.</p>
                    </div>
                </div>
            )}

            {error && <div className="alert alert-error">{error}</div>}
            {success && <div className="alert alert-success">{success}</div>}

            {activeAccount && (
                <div className="card" style={{ marginBottom: 24 }}>
                    <div className="card-header">
                        <div style={{ display: 'flex', gap: '8px', alignItems: 'center' }}>
                            <h3>New Transaction</h3>
                        </div>
                        <button className="btn btn-secondary btn-sm" onClick={() => setShowForm(!showForm)}>
                            {showForm ? 'Cancel' : '+ New Transaction'}
                        </button>
                    </div>
                    {showForm && (
                        <div>
                            <div className="tabs" style={{ display: 'flex', gap: '8px', marginBottom: '16px', borderBottom: '1px solid var(--border)', paddingBottom: '8px' }}>
                                <button 
                                    type="button"
                                    className={`btn btn-sm ${transferMode === 'INTERNAL' ? 'btn-primary' : 'btn-secondary'}`}
                                    onClick={() => setTransferMode('INTERNAL')}
                                    style={{ background: transferMode === 'INTERNAL' ? undefined : 'transparent', border: transferMode === 'INTERNAL' ? undefined : 'none' }}
                                >
                                    Internal Transfer
                                </button>
                                <button 
                                    type="button"
                                    className={`btn btn-sm ${transferMode === 'EXTERNAL' ? 'btn-primary' : 'btn-secondary'}`}
                                    onClick={() => setTransferMode('EXTERNAL')}
                                    style={{ background: transferMode === 'EXTERNAL' ? undefined : 'transparent', border: transferMode === 'EXTERNAL' ? undefined : 'none' }}
                                >
                                    External Payment
                                </button>
                            </div>
                            
                            <form onSubmit={handleSubmit}>
                                <div className="form-row">
                                    <div className="form-group">
                                        {transferMode === 'INTERNAL' ? (
                                            <>
                                                <label htmlFor="destAccount">Destination Account</label>
                                                {availableDestinationAccounts.length > 0 ? (
                                                    <select 
                                                        id="destAccount" 
                                                        className="form-control"
                                                        value={selectedDestinationAccountId || ''}
                                                        onChange={(e) => setSelectedDestinationAccountId(Number(e.target.value))}
                                                        required
                                                    >
                                                        {availableDestinationAccounts.map(acc => (
                                                            <option key={acc.id} value={acc.id}>
                                                                {acc.name ? `${acc.name} - ${acc.iban || `Account #${acc.id}`}` : (acc.iban || `Account #${acc.id}`)}
                                                            </option>
                                                        ))}
                                                    </select>
                                                ) : (
                                                    <div style={{ padding: '12px', background: 'var(--bg-input)', borderRadius: '4px', color: 'var(--danger)' }}>
                                                        You don't have any other accounts to transfer to.
                                                    </div>
                                                )}
                                            </>
                                        ) : (
                                            <>
                                                <label htmlFor="toIban">Recipient IBAN</label>
                                                <input id="toIban" type="text" placeholder="e.g. IT60NEXS0123456789012345678" value={form.toIban} onChange={e => setForm({ ...form, toIban: e.target.value })} required />
                                            </>
                                        )}
                                    </div>
                                    <div className="form-group">
                                        <label htmlFor="pay-amount">Amount ({activeAccount?.currency})</label>
                                        <input id="pay-amount" type="number" step="0.01" min="0.01" placeholder="0.00" value={form.amount} onChange={e => setForm({ ...form, amount: e.target.value })} required disabled={transferMode === 'INTERNAL' && availableDestinationAccounts.length === 0} />
                                    </div>
                                </div>
                                <div className="form-group">
                                    <label htmlFor="pay-category">Category</label>
                                    <select id="pay-category" style={{ width: "100%", padding: 8, borderRadius: 4, border: "1px solid #ddd" }} value={form.category} onChange={e => setForm({ ...form, category: e.target.value })} disabled={transferMode === 'INTERNAL' && availableDestinationAccounts.length === 0}>
                                        <option value="">Default (Transfer)</option>
                                        {CATEGORIES.map(c => <option key={c} value={c}>{c}</option>)}
                                    </select>
                                </div>
                                <div className="form-group">
                                    <label htmlFor="pay-desc">Description (Optional)</label>
                                    <input id="pay-desc" placeholder={transferMode === 'INTERNAL' ? "e.g. Savings transfer" : "Payment description"} value={form.description} onChange={e => setForm({ ...form, description: e.target.value })} disabled={transferMode === 'INTERNAL' && availableDestinationAccounts.length === 0} />
                                </div>
                                <button className="btn btn-primary" type="submit" disabled={submitting || (transferMode === 'INTERNAL' && availableDestinationAccounts.length === 0)}>
                                    {submitting ? 'Processing...' : (transferMode === 'INTERNAL' ? 'Complete Transfer' : 'Send Payment')}
                                </button>
                            </form>
                        </div>
                    )}
                </div>
            )}
        </div>
    );
};

export default Payments;
