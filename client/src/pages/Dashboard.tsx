import React, { useEffect, useState } from 'react';
import { useAuth } from '../AuthContext';
import { accountApi } from '../api';
import type { Account } from '../types';
import Transactions from './Transactions';

const Dashboard: React.FC = () => {
    const { user, selectedAccountId, setSelectedAccountId } = useAuth();
    const [accounts, setAccounts] = useState<Account[]>([]);
    const [loading, setLoading] = useState(true);
    const [editingAccountId, setEditingAccountId] = useState<number | null>(null);
    const [editName, setEditName] = useState('');

    useEffect(() => {
        if (!user) return;
        const load = async () => {
            try {
                const accRes = await accountApi.get(user.id);
                setAccounts(accRes.data);
                if (accRes.data.length > 0 && selectedAccountId === null) {
                    setSelectedAccountId(accRes.data[0].id);
                }
            } finally {
                setLoading(false);
            }
        };
        load();
    }, [user]); // Note: selectedAccountId intentionally excluded to avoid refetch loop

    const handleSaveName = async (accountId: number, e?: React.FormEvent | React.MouseEvent | React.KeyboardEvent) => {
        if (e) e.preventDefault();
        try {
            await accountApi.updateName(accountId, editName);
            setAccounts(accounts.map(a => a.id === accountId ? { ...a, name: editName } : a));
            setEditingAccountId(null);
        } catch (error) {
            console.error('Failed to update account name', error);
        }
    };

    if (loading) return <div className="loading"><div className="spinner" /></div>;

    const totalBalance = accounts.reduce((sum, acc) => sum + acc.balance, 0);
    const activeAccount = accounts.find(a => a.id === selectedAccountId);

    return (
        <div className="fade-in">
            <div className="page-header">
                <h2>Welcome back, {user?.firstName} 👋</h2>
                <p>Here's an overview of your finances</p>
            </div>

            <div className="card-grid" style={{ marginBottom: 24 }}>
                <div className="stat-card">
                    <div className="stat-label">Total Balance</div>
                    <div className="stat-value">
                        EUR {totalBalance.toLocaleString('en', { minimumFractionDigits: 2 })}
                    </div>
                </div>

                {accounts.length > 0 && (
                    <div className="card account-selector-card">
                        <label htmlFor="account-select">Select Account</label>
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
            </div>

            {activeAccount && (
                <div className="card-grid" style={{ marginBottom: 24 }}>
                    <div key={activeAccount.id} className="stat-card stat-card-active">
                        <div className="stat-label stat-card-header">
                            {editingAccountId === activeAccount.id ? (
                                <form style={{ display: 'flex', gap: '8px', zIndex: 10 }} onSubmit={(e) => handleSaveName(activeAccount.id, e)}>
                                    <input
                                        type="text"
                                        value={editName}
                                        onChange={e => setEditName(e.target.value)}
                                        className="form-control"
                                        style={{ padding: '4px 8px', fontSize: '0.9rem' }}
                                        autoFocus
                                        onKeyDown={e => e.key === 'Enter' && handleSaveName(activeAccount.id, e)}
                                    />
                                    <button className="btn btn-primary btn-sm" onClick={(e) => handleSaveName(activeAccount.id, e)}>Save</button>
                                    <button className="btn btn-secondary btn-sm" onClick={(e) => { e.preventDefault(); setEditingAccountId(null); }}>Cancel</button>
                                </form>
                            ) : (
                                <span style={{ display: 'flex', alignItems: 'center', gap: '8px' }}>
                                    {activeAccount.name || 'Account'}
                                    <button
                                        onClick={() => {
                                            setEditName(activeAccount.name || '');
                                            setEditingAccountId(activeAccount.id);
                                        }}
                                        style={{ background: 'none', border: 'none', cursor: 'pointer', fontSize: '1rem', padding: '0 4px' }}
                                        title="Edit Account Name"
                                    >
                                        ✏️
                                    </button>
                                </span>
                            )}
                        </div>
                        <div className="stat-card-iban">
                            {activeAccount.iban?.match(/.{1,4}/g)?.join(' ') || activeAccount.iban || 'N/A'}
                        </div>
                        <div className="stat-label">Available Balance</div>
                        <div className="stat-value stat-card-amount">
                            {activeAccount.currency} {activeAccount.balance.toLocaleString('en', { minimumFractionDigits: 2 })}
                        </div>
                    </div>
                </div>
            )}
            <Transactions key={`${activeAccount?.id || 'all'}-${activeAccount?.balance ?? 0}`} hideHeader accountId={activeAccount?.id} />
        </div>
    );
};

export default Dashboard;
