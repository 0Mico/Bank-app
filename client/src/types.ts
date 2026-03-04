export interface User {
    id: number;
    email: string;
    firstName: string;
    lastName: string;
    phone?: string;
    role: 'USER' | 'ADMIN';
    createdAt: string;
}

export interface AuthResponse {
    token: string;
    user: User;
}

export interface Account {
    id: number;
    userId: number;
    iban: string;
    balance: number;
    currency: string;
    name?: string;
    createdAt: string;
}

export interface Card {
    id: number;
    accountId: number;
    cardNumber: string;
    expiration: string;
    blocked: boolean;
}

export interface Transaction {
    id: number;
    userId: number;
    type: 'CREDIT' | 'DEBIT';
    category: string;
    amount: number;
    description?: string;
    referenceId?: string;
    counterpartyIban?: string;
    createdAt: string;
}

export interface Payment {
    id: number;
    fromAccountId: number;
    toAccountId: number;
    toIban: string;
    fromUserId: number;
    toUserId: number;
    amount: number;
    currency: string;
    status: 'PENDING' | 'COMPLETED' | 'FAILED' | 'CANCELLED';
    category?: string;
    description?: string;
    createdAt: string;
}

export interface LoginRequest {
    email: string;
    password: string;
}

export interface RegisterRequest {
    email: string;
    password: string;
    firstName: string;
    lastName: string;
    phone?: string;
}
