import axios from 'axios';
import type { AuthResponse, LoginRequest, RegisterRequest, User, Account, Transaction, Payment, Card } from './types';

const api = axios.create({
    baseURL: '/api',
    headers: { 'Content-Type': 'application/json' },
});

api.interceptors.request.use((config) => {
    const token = sessionStorage.getItem('token');
    if (token) {
        config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
});

api.interceptors.response.use(
    (response) => response,
    (error) => {
        if (error.response?.status === 401) {
            const message = error.response.data?.message || 'Session expired. Please log in again.';
            alert(message);
        }
        return Promise.reject(error);
    }
);

// Auth
export const authApi = {
    login: (data: LoginRequest) => api.post<AuthResponse>('/auth/login', data),
    register: (data: RegisterRequest) => api.post<AuthResponse>('/auth/register', data),
};

// Users
export const userApi = {
    getProfile: (id: number) => api.get<User>(`/auth/users/${id}`),
    updateProfile: (id: number, data: Partial<User>) => api.put<User>(`/auth/users/${id}`, data),
    deleteProfile: (id: number) => api.delete(`/auth/users/${id}`),
    getByEmail: (email: string) => api.get<User>(`/auth/users/by-email?email=${encodeURIComponent(email)}`),
    getByIban: (iban: string) => api.get<User>(`/auth/users/by-iban?iban=${encodeURIComponent(iban)}`),
    getByAccountId: (accountId: number) => api.get<User>(`/auth/users/by-account?accountId=${accountId}`),
    changePassword: (id: number, data: { currentPassword: string; newPassword: string }) => api.post(`/auth/users/${id}/password`, data),
};

// Transactions
export const transactionApi = {
    list: (params?: Record<string, string>) => api.get<Transaction[]>('/transactions', { params }),
    get: (id: number) => api.get<Transaction>(`/transactions/${id}`),
    create: (data: Partial<Transaction>) => api.post<Transaction>('/transactions', data),
    update: (id: number, data: Partial<Transaction>) => api.put<Transaction>(`/transactions/${id}`, data),
    delete: (id: number) => api.delete(`/transactions/${id}`),
    categories: () => api.get<string[]>('/transactions/categories'),
};

export interface FavoriteOperation {
    id: number;
    accountId: number;
    name: string;
    recipientIban: string;
    amount: number;
    category: string;
    description: string;
    type?: 'INTERNAL' | 'EXTERNAL';
    recipientAccountName?: string;
}

// Payments
export const paymentApi = {
    process: (data: Partial<Payment>) => api.post<Payment>('/payments', data),
    list: (userId: number) => api.get<Payment[]>(`/payments?userId=${userId}`),
    get: (id: number) => api.get<Payment>(`/payments/${id}`),
    favorites: {
        list: (accountId: number) => api.get<FavoriteOperation[]>(`/payments/favorites/account/${accountId}`),
        create: (data: Partial<FavoriteOperation>) => api.post<FavoriteOperation>('/payments/favorites', data),
        delete: (id: number) => api.delete(`/payments/favorites/${id}`),
    }
};

// Accounts
export const accountApi = {
    get: (userId: number) => api.get<Account[]>(`/payments/accounts/${userId}`),
    create: (data: Partial<Account>) => api.post<Account>('/payments/accounts', data),
    deposit: (accountId: number, amount: number) => api.post<Account>(`/payments/accounts/${accountId}/deposit`, { amount }),
    updateName: (accountId: number, name: string) => api.patch<Account>(`/payments/accounts/${accountId}/name`, { name }),
    getByIban: (iban: string) => api.get<Account>(`/payments/accounts/by-iban?iban=${encodeURIComponent(iban)}`),
};

// Cards
export const cardApi = {
    list: (accountId: number) => api.get<Card[]>(`/payments/accounts/${accountId}/cards`),
    create: (accountId: number, data: Partial<Card>) => api.post<Card>(`/payments/accounts/${accountId}/cards`, data),
    toggleBlock: (accountId: number, cardId: number) => api.patch<Card>(`/payments/accounts/${accountId}/cards/${cardId}/block`),
    delete: (accountId: number, cardId: number) => api.delete(`/payments/accounts/${accountId}/cards/${cardId}`),
};

export default api;
