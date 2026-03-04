import React, { createContext, useContext, useState, useEffect, type ReactNode } from 'react';
import type { User } from './types';

interface AuthContextType {
    user: User | null;
    token: string | null;
    login: (token: string, user: User) => void;
    logout: () => void;
    isAuthenticated: boolean;
    selectedAccountId: number | null;
    setSelectedAccountId: (id: number | null) => void;
}

const AuthContext = createContext<AuthContextType>({
    user: null,
    token: null,
    login: () => { },
    logout: () => { },
    isAuthenticated: false,
    selectedAccountId: null,
    setSelectedAccountId: () => { },
});

export const useAuth = () => useContext(AuthContext);

export const AuthProvider: React.FC<{ children: ReactNode }> = ({ children }) => {
    const [user, setUser] = useState<User | null>(null);
    const [token, setToken] = useState<string | null>(null);
    const [selectedAccountId, setSelectedAccountId] = useState<number | null>(null);

    useEffect(() => {
        const savedToken = sessionStorage.getItem('token');
        const savedUser = sessionStorage.getItem('user');
        if (savedToken && savedUser) {
            setToken(savedToken);
            setUser(JSON.parse(savedUser));
        }
    }, []);

    const login = (newToken: string, newUser: User) => {
        sessionStorage.setItem('token', newToken);
        sessionStorage.setItem('user', JSON.stringify(newUser));
        setToken(newToken);
        setUser(newUser);
    };

    const logout = () => {
        sessionStorage.removeItem('token');
        sessionStorage.removeItem('user');
        setToken(null);
        setUser(null);
        setSelectedAccountId(null);
    };

    return (
        <AuthContext.Provider value={{ user, token, login, logout, isAuthenticated: !!token, selectedAccountId, setSelectedAccountId }}>
            {children}
        </AuthContext.Provider>
    );
};
