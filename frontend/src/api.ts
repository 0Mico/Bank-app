import axios from 'axios';
import type { AuthResponse, LoginRequest, RegisterRequest, User, Account, Transaction, Payment, FavoriteOperation, Card } from './types';

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
    updateProfile: (id: number, data: Partial<User>) => api.put<User>(`/auth/users/${id}`, data),
    deleteProfile: (id: number) => api.delete(`/auth/users/${id}`),
    getByEmail: (email: string) => api.get<User>(`/auth/users/email?email=${encodeURIComponent(email)}`),
    getByIban: (iban: string) => api.get<User>(`/auth/users/iban?iban=${encodeURIComponent(iban)}`),
    changePassword: (id: number, data: { currentPassword: string; newPassword: string }) => api.post(`/auth/users/${id}/password`, data),
};

// Transactions
export const transactionApi = {
    list: (params?: Record<string, string>) => api.get<Transaction[]>('/transactions', { params }),
    categories: () => api.get<string[]>('/transactions/categories'),
};

// Payments
export const paymentApi = {
    process: (data: Partial<Payment>) => api.post<Payment>('/payments', data),
    //list: (userId: number) => api.get<Payment[]>(`/payments?userId=${userId}`),
    //get: (id: number) => api.get<Payment>(`/payments/${id}`),
    favorites: {
        list: (accountId: number) => api.get<FavoriteOperation[]>(`/payments/favorites/account/${accountId}`),
        create: (data: Partial<FavoriteOperation>) => api.post<FavoriteOperation>('/payments/favorites', data),
        delete: (id: number) => api.delete(`/payments/favorites/${id}`),
    }
};

// Accounts
export const accountApi = {
    getById: (accountId: number) => api.get<Account>(`/accounts/${accountId}`),
    getByUserId: (userId: number) => api.get<Account[]>(`/accounts/userId?userId=${userId}`),
    getByIban: (iban: string) => api.get<Account>(`/accounts/iban?iban=${encodeURIComponent(iban)}`),
    create: (data: Partial<Account>) => api.post<Account>('/accounts', data),
    deposit: (accountId: number, amount: number) => api.post<Account>(`/accounts/${accountId}/deposit`, { amount }),
    updateName: (accountId: number, name: string) => api.patch<Account>(`/accounts/${accountId}/name`, { name }),
    delete: (accountId: number) => api.delete(`/accounts/${accountId}`),
};

// Cards
export const cardApi = {
    list: (accountId: number) => api.get<Card[]>(`/accounts/cards/${accountId}/cards`),
    create: (accountId: number, data: Partial<Card>) => api.post<Card>(`/accounts/cards/${accountId}/cards`, data),
    toggleBlock: (accountId: number, cardId: number) => api.patch<Card>(`/accounts/cards/${accountId}/cards/${cardId}/block`),
    delete: (accountId: number, cardId: number) => api.delete(`/accounts/cards/${accountId}/cards/${cardId}`),
};

export default api;
