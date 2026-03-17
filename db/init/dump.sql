--
-- PostgreSQL database dump
--

\restrict VaLPI5VeKbrknJv1T57YckJWMo4kyUAn9Tc1vMTYT4SbTfpDJutSoJv4scdbBeh

-- Dumped from database version 16.13 (Ubuntu 16.13-0ubuntu0.24.04.1)
-- Dumped by pg_dump version 16.13 (Ubuntu 16.13-0ubuntu0.24.04.1)

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

--
-- Name: banking_accounts; Type: SCHEMA; Schema: -; Owner: -
--

CREATE SCHEMA banking_accounts;


--
-- Name: banking_auth; Type: SCHEMA; Schema: -; Owner: -
--

CREATE SCHEMA banking_auth;


--
-- Name: banking_payments; Type: SCHEMA; Schema: -; Owner: -
--

CREATE SCHEMA banking_payments;


--
-- Name: banking_transactions; Type: SCHEMA; Schema: -; Owner: -
--

CREATE SCHEMA banking_transactions;


--
-- Name: user_role; Type: TYPE; Schema: banking_auth; Owner: -
--

CREATE TYPE banking_auth.user_role AS ENUM (
    'ADMIN',
    'USER'
);


--
-- Name: payment_category; Type: TYPE; Schema: banking_payments; Owner: -
--

CREATE TYPE banking_payments.payment_category AS ENUM (
    'DEPOSIT',
    'ENTERTAINMENT',
    'FOOD',
    'HEALTHCARE',
    'OTHER',
    'PAYMENT',
    'SALARY',
    'SHOPPING',
    'TRANSFER',
    'TRANSPORT',
    'UTILITIES'
);


--
-- Name: payment_status; Type: TYPE; Schema: banking_payments; Owner: -
--

CREATE TYPE banking_payments.payment_status AS ENUM (
    'CANCELLED',
    'COMPLETED',
    'FAILED',
    'PENDING'
);


--
-- Name: transaction_category; Type: TYPE; Schema: banking_transactions; Owner: -
--

CREATE TYPE banking_transactions.transaction_category AS ENUM (
    'ENTERTAINMENT',
    'FOOD',
    'HEALTHCARE',
    'OTHER',
    'PAYMENT',
    'SALARY',
    'SHOPPING',
    'TRANSFER',
    'TRANSPORT',
    'UTILITIES',
    'DEPOSIT'
);


--
-- Name: transaction_type; Type: TYPE; Schema: banking_transactions; Owner: -
--

CREATE TYPE banking_transactions.transaction_type AS ENUM (
    'CREDIT',
    'DEBIT'
);


SET default_tablespace = '';

SET default_table_access_method = heap;

--
-- Name: accounts; Type: TABLE; Schema: banking_accounts; Owner: -
--

CREATE TABLE banking_accounts.accounts (
    id bigint NOT NULL,
    balance numeric(19,2) NOT NULL,
    created_at timestamp with time zone NOT NULL,
    currency character varying(3) NOT NULL,
    iban character varying(34) NOT NULL,
    name character varying(255),
    user_id bigint NOT NULL
);


--
-- Name: accounts_id_seq; Type: SEQUENCE; Schema: banking_accounts; Owner: -
--

CREATE SEQUENCE banking_accounts.accounts_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: accounts_id_seq; Type: SEQUENCE OWNED BY; Schema: banking_accounts; Owner: -
--

ALTER SEQUENCE banking_accounts.accounts_id_seq OWNED BY banking_accounts.accounts.id;


--
-- Name: cards; Type: TABLE; Schema: banking_accounts; Owner: -
--

CREATE TABLE banking_accounts.cards (
    id bigint NOT NULL,
    account_id bigint NOT NULL,
    card_number character varying(255) NOT NULL,
    expiration date NOT NULL,
    is_blocked boolean NOT NULL
);


--
-- Name: cards_id_seq; Type: SEQUENCE; Schema: banking_accounts; Owner: -
--

CREATE SEQUENCE banking_accounts.cards_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: cards_id_seq; Type: SEQUENCE OWNED BY; Schema: banking_accounts; Owner: -
--

ALTER SEQUENCE banking_accounts.cards_id_seq OWNED BY banking_accounts.cards.id;


--
-- Name: role_permissions; Type: TABLE; Schema: banking_auth; Owner: -
--

CREATE TABLE banking_auth.role_permissions (
    id bigint NOT NULL,
    allowed_actions character varying(255) NOT NULL,
    resource_pattern character varying(255) NOT NULL,
    role character varying(255) NOT NULL
);


--
-- Name: role_permissions_id_seq; Type: SEQUENCE; Schema: banking_auth; Owner: -
--

CREATE SEQUENCE banking_auth.role_permissions_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: role_permissions_id_seq; Type: SEQUENCE OWNED BY; Schema: banking_auth; Owner: -
--

ALTER SEQUENCE banking_auth.role_permissions_id_seq OWNED BY banking_auth.role_permissions.id;


--
-- Name: users; Type: TABLE; Schema: banking_auth; Owner: -
--

CREATE TABLE banking_auth.users (
    id bigint NOT NULL,
    created_at timestamp with time zone NOT NULL,
    email character varying(255) NOT NULL,
    first_name character varying(255) NOT NULL,
    last_name character varying(255) NOT NULL,
    password_hash character varying(255) NOT NULL,
    phone character varying(255),
    role banking_auth.user_role NOT NULL
);


--
-- Name: users_id_seq; Type: SEQUENCE; Schema: banking_auth; Owner: -
--

CREATE SEQUENCE banking_auth.users_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: users_id_seq; Type: SEQUENCE OWNED BY; Schema: banking_auth; Owner: -
--

ALTER SEQUENCE banking_auth.users_id_seq OWNED BY banking_auth.users.id;


--
-- Name: favorite_operations; Type: TABLE; Schema: banking_payments; Owner: -
--

CREATE TABLE banking_payments.favorite_operations (
    id bigint NOT NULL,
    amount numeric(38,2) NOT NULL,
    category character varying(255),
    description character varying(1000),
    name character varying(255) NOT NULL,
    recipient_iban character varying(255) NOT NULL,
    account_id bigint NOT NULL
);


--
-- Name: favorite_operations_id_seq; Type: SEQUENCE; Schema: banking_payments; Owner: -
--

CREATE SEQUENCE banking_payments.favorite_operations_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: favorite_operations_id_seq; Type: SEQUENCE OWNED BY; Schema: banking_payments; Owner: -
--

ALTER SEQUENCE banking_payments.favorite_operations_id_seq OWNED BY banking_payments.favorite_operations.id;


--
-- Name: payments; Type: TABLE; Schema: banking_payments; Owner: -
--

CREATE TABLE banking_payments.payments (
    id bigint NOT NULL,
    amount numeric(19,2) NOT NULL,
    created_at timestamp with time zone NOT NULL,
    description character varying(255),
    from_account_id bigint NOT NULL,
    status banking_payments.payment_status NOT NULL,
    to_account_id bigint NOT NULL,
    category banking_payments.payment_category
);


--
-- Name: payments_id_seq; Type: SEQUENCE; Schema: banking_payments; Owner: -
--

CREATE SEQUENCE banking_payments.payments_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: payments_id_seq; Type: SEQUENCE OWNED BY; Schema: banking_payments; Owner: -
--

ALTER SEQUENCE banking_payments.payments_id_seq OWNED BY banking_payments.payments.id;


--
-- Name: transactions; Type: TABLE; Schema: banking_transactions; Owner: -
--

CREATE TABLE banking_transactions.transactions (
    id bigint NOT NULL,
    amount numeric(19,2) NOT NULL,
    category banking_transactions.transaction_category NOT NULL,
    created_at timestamp with time zone NOT NULL,
    description character varying(255),
    reference_id character varying(255),
    type banking_transactions.transaction_type NOT NULL,
    user_id bigint NOT NULL,
    account_id bigint,
    counterparty_iban character varying(255)
);


--
-- Name: transactions_id_seq; Type: SEQUENCE; Schema: banking_transactions; Owner: -
--

CREATE SEQUENCE banking_transactions.transactions_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: transactions_id_seq; Type: SEQUENCE OWNED BY; Schema: banking_transactions; Owner: -
--

ALTER SEQUENCE banking_transactions.transactions_id_seq OWNED BY banking_transactions.transactions.id;


--
-- Name: accounts id; Type: DEFAULT; Schema: banking_accounts; Owner: -
--

ALTER TABLE ONLY banking_accounts.accounts ALTER COLUMN id SET DEFAULT nextval('banking_accounts.accounts_id_seq'::regclass);


--
-- Name: cards id; Type: DEFAULT; Schema: banking_accounts; Owner: -
--

ALTER TABLE ONLY banking_accounts.cards ALTER COLUMN id SET DEFAULT nextval('banking_accounts.cards_id_seq'::regclass);


--
-- Name: role_permissions id; Type: DEFAULT; Schema: banking_auth; Owner: -
--

ALTER TABLE ONLY banking_auth.role_permissions ALTER COLUMN id SET DEFAULT nextval('banking_auth.role_permissions_id_seq'::regclass);


--
-- Name: users id; Type: DEFAULT; Schema: banking_auth; Owner: -
--

ALTER TABLE ONLY banking_auth.users ALTER COLUMN id SET DEFAULT nextval('banking_auth.users_id_seq'::regclass);


--
-- Name: favorite_operations id; Type: DEFAULT; Schema: banking_payments; Owner: -
--

ALTER TABLE ONLY banking_payments.favorite_operations ALTER COLUMN id SET DEFAULT nextval('banking_payments.favorite_operations_id_seq'::regclass);


--
-- Name: payments id; Type: DEFAULT; Schema: banking_payments; Owner: -
--

ALTER TABLE ONLY banking_payments.payments ALTER COLUMN id SET DEFAULT nextval('banking_payments.payments_id_seq'::regclass);


--
-- Name: transactions id; Type: DEFAULT; Schema: banking_transactions; Owner: -
--

ALTER TABLE ONLY banking_transactions.transactions ALTER COLUMN id SET DEFAULT nextval('banking_transactions.transactions_id_seq'::regclass);


--
-- Data for Name: accounts; Type: TABLE DATA; Schema: banking_accounts; Owner: -
--

INSERT INTO banking_accounts.accounts (id, balance, created_at, currency, iban, name, user_id) VALUES (7, 0.00, '2026-03-03 13:24:30.631979+01', 'EUR', 'IT40NEXS256310696081520218', '\N', 7);
INSERT INTO banking_accounts.accounts (id, balance, created_at, currency, iban, name, user_id) VALUES (8, 0.00, '2026-03-03 13:29:11.968161+01', 'EUR', 'IT99NEXS171237594386031663', '\N', 8);
INSERT INTO banking_accounts.accounts (id, balance, created_at, currency, iban, name, user_id) VALUES (9, 0.00, '2026-03-03 13:33:56.798461+01', 'EUR', 'IT96NEXS215833562550132564', '\N', 9);
INSERT INTO banking_accounts.accounts (id, balance, created_at, currency, iban, name, user_id) VALUES (10, 0.00, '2026-03-03 13:38:35.275616+01', 'EUR', 'IT20NEXS891033064419172138', '\N', 10);
INSERT INTO banking_accounts.accounts (id, balance, created_at, currency, iban, name, user_id) VALUES (11, 100000507.00, '2026-03-03 13:45:02.566007+01', 'EUR', 'IT78NEXS709563321058228999', 'MarcAccount 1', 11);
INSERT INTO banking_accounts.accounts (id, balance, created_at, currency, iban, name, user_id) VALUES (16, 1820.00, '2026-03-04 13:23:14.567044+01', 'EUR', 'IT84NEXS020386725308339380', 'UserAccount 2', 6);
INSERT INTO banking_accounts.accounts (id, balance, created_at, currency, iban, name, user_id) VALUES (18, 5033.00, '2026-03-07 12:34:18.740708+01', 'EUR', 'IT35NEXS568133426071117329', 'MarcAccount 2', 11);
INSERT INTO banking_accounts.accounts (id, balance, created_at, currency, iban, name, user_id) VALUES (6, 2000.00, '2026-03-03 10:11:16.221213+01', 'EUR', 'IT57NEXS704043974399150956', 'UserAccount 1', 6);
INSERT INTO banking_accounts.accounts (id, balance, created_at, currency, iban, name, user_id) VALUES (20, 1445.00, '2026-03-09 14:49:01.229947+01', 'EUR', 'IT87NEXS749563253715271001', 'MicAccount4', 5);
INSERT INTO banking_accounts.accounts (id, balance, created_at, currency, iban, name, user_id) VALUES (19, 500.00, '2026-03-09 10:05:28.491273+01', 'EUR', 'IT65NEXS934350224722890982', 'MicAccount 3', 5);
INSERT INTO banking_accounts.accounts (id, balance, created_at, currency, iban, name, user_id) VALUES (21, 266.01, '2026-03-09 15:42:32.615223+01', 'EUR', 'IT68NEXS365639249649076887', 'MicAccount5', 5);
INSERT INTO banking_accounts.accounts (id, balance, created_at, currency, iban, name, user_id) VALUES (22, 200.00, '2026-03-11 13:29:45.140097+01', 'EUR', 'IT50NEXS419210706416462172', 'MicAccount6', 5);
INSERT INTO banking_accounts.accounts (id, balance, created_at, currency, iban, name, user_id) VALUES (5, 83000.00, '2026-03-03 10:10:41.038755+01', 'EUR', 'IT73NEXS892582929170888462', 'MicAccount 1', 5);
INSERT INTO banking_accounts.accounts (id, balance, created_at, currency, iban, name, user_id) VALUES (23, 0.00, '2026-03-13 11:47:01.06524+01', 'EUR', 'IT06NEXS331193816428414828', 'UserAccount 3', 6);


--
-- Data for Name: cards; Type: TABLE DATA; Schema: banking_accounts; Owner: -
--

INSERT INTO banking_accounts.cards (id, account_id, card_number, expiration, is_blocked) VALUES (13, 6, '2795-4811-8590-3288', '2031-03-04', false);
INSERT INTO banking_accounts.cards (id, account_id, card_number, expiration, is_blocked) VALUES (14, 6, '0475-5553-8146-2536', '2031-03-04', false);
INSERT INTO banking_accounts.cards (id, account_id, card_number, expiration, is_blocked) VALUES (15, 6, '9875-6750-8303-0303', '2031-03-04', false);
INSERT INTO banking_accounts.cards (id, account_id, card_number, expiration, is_blocked) VALUES (16, 11, '8866-6593-1173-7992', '2031-03-04', false);
INSERT INTO banking_accounts.cards (id, account_id, card_number, expiration, is_blocked) VALUES (18, 14, '8984-3447-7486-5084', '2031-03-07', false);
INSERT INTO banking_accounts.cards (id, account_id, card_number, expiration, is_blocked) VALUES (20, 15, '6669-6472-1309-0505', '2031-03-07', false);
INSERT INTO banking_accounts.cards (id, account_id, card_number, expiration, is_blocked) VALUES (21, 15, '8790-1890-9373-6255', '2031-03-07', false);
INSERT INTO banking_accounts.cards (id, account_id, card_number, expiration, is_blocked) VALUES (29, 5, '5429-6349-8424-3702', '2031-03-09', false);
INSERT INTO banking_accounts.cards (id, account_id, card_number, expiration, is_blocked) VALUES (30, 5, '0996-7540-2509-2352', '2031-03-09', false);
INSERT INTO banking_accounts.cards (id, account_id, card_number, expiration, is_blocked) VALUES (34, 22, '1656-9311-3989-2564', '2031-03-11', false);
INSERT INTO banking_accounts.cards (id, account_id, card_number, expiration, is_blocked) VALUES (1, 5, '0649-9167-2174-9729', '2031-03-12', false);


--
-- Data for Name: role_permissions; Type: TABLE DATA; Schema: banking_auth; Owner: -
--

INSERT INTO banking_auth.role_permissions (id, allowed_actions, resource_pattern, role) VALUES (1, 'GET,POST,PUT,DELETE', '/api/auth/users/**', 'USER');
INSERT INTO banking_auth.role_permissions (id, allowed_actions, resource_pattern, role) VALUES (2, 'GET,POST,PUT,DELETE', '/api/transactions/**', 'USER');
INSERT INTO banking_auth.role_permissions (id, allowed_actions, resource_pattern, role) VALUES (3, 'GET,POST,PUT,PATCH,DELETE', '/api/payments/**', 'USER');
INSERT INTO banking_auth.role_permissions (id, allowed_actions, resource_pattern, role) VALUES (4, 'GET,POST,PUT,PATCH,DELETE', '/api/accounts/**', 'USER');
INSERT INTO banking_auth.role_permissions (id, allowed_actions, resource_pattern, role) VALUES (5, 'GET,POST,PUT,PATCH,DELETE', '/api/**', 'ADMIN');


--
-- Data for Name: users; Type: TABLE DATA; Schema: banking_auth; Owner: -
--

INSERT INTO banking_auth.users (id, created_at, email, first_name, last_name, password_hash, phone, role) VALUES (5, '2026-03-03 10:10:40.82725+01', 'mico@mico.it', 'Mico', 'Mico', '$2a$10$4kh7q9uxIeY0wWRwI2TWKeo.Ok6kAMXq8FNte5izugbI./Z0Jcf2C', '', 'USER');
INSERT INTO banking_auth.users (id, created_at, email, first_name, last_name, password_hash, phone, role) VALUES (6, '2026-03-03 10:11:16.192797+01', 'user@user.it', 'User', 'User', '$2a$10$HgUHeUHml38UKhjlfLiDEeGEIsiStSSGyZryNDzhUttrMSPEAk7NS', '', 'USER');
INSERT INTO banking_auth.users (id, created_at, email, first_name, last_name, password_hash, phone, role) VALUES (11, '2026-03-03 13:45:02.536323+01', 'marco@marco.it', 'Marco', 'Marco', '$2a$10$NcUX1oilfZcauUmgPnp1Ieofx3P.NQT5vdQNPloqL8cu6OXODhHQC', '', 'USER');


--
-- Data for Name: favorite_operations; Type: TABLE DATA; Schema: banking_payments; Owner: -
--

INSERT INTO banking_payments.favorite_operations (id, amount, category, description, name, recipient_iban, account_id) VALUES (2, 52222.00, 'TRANSFER', 'Test internal fav opertion', 'TEST', 'IT73NEXS892582929170888462', 0);
INSERT INTO banking_payments.favorite_operations (id, amount, category, description, name, recipient_iban, account_id) VALUES (3, 52222.00, 'TRANSFER', 'Test internal fav opertion', 'TEST', 'IT11NEXS632111781360241549', 0);
INSERT INTO banking_payments.favorite_operations (id, amount, category, description, name, recipient_iban, account_id) VALUES (4, 28.00, 'TRANSFER', 'Test internal fav opertion', 'TEST', 'IT79NEXS695999735361278337', 0);
INSERT INTO banking_payments.favorite_operations (id, amount, category, description, name, recipient_iban, account_id) VALUES (5, 7.00, 'TRANSPORT', 'Train ticket', 'Train ticket', 'IT78NEXS709563321058228999', 0);
INSERT INTO banking_payments.favorite_operations (id, amount, category, description, name, recipient_iban, account_id) VALUES (6, 815.00, 'TRANSFER', 'New favOps list layout', 'Test', 'IT84NEXS020386725308339380', 0);
INSERT INTO banking_payments.favorite_operations (id, amount, category, description, name, recipient_iban, account_id) VALUES (10, 10000.00, 'UTILITIES', 'Internal Transfer to MicAccount 2', 'Gift', 'IT79NEXS695999735361278337', 15);
INSERT INTO banking_payments.favorite_operations (id, amount, category, description, name, recipient_iban, account_id) VALUES (11, 10000.00, 'UTILITIES', 'Gift', 'GIFT', 'IT79NEXS695999735361278337', 15);
INSERT INTO banking_payments.favorite_operations (id, amount, category, description, name, recipient_iban, account_id) VALUES (12, 5000.00, 'TRANSFER', '', 'RENT', 'IT35NEXS568133426071117329', 15);
INSERT INTO banking_payments.favorite_operations (id, amount, category, description, name, recipient_iban, account_id) VALUES (13, 23.00, 'UTILITIES', '', 'RANDOM', 'IT55NEXS756603083054917901', 19);
INSERT INTO banking_payments.favorite_operations (id, amount, category, description, name, recipient_iban, account_id) VALUES (15, 77.00, 'TRANSFER', 'February rent', 'RENT', 'IT79NEXS695999735361278337', 19);
INSERT INTO banking_payments.favorite_operations (id, amount, category, description, name, recipient_iban, account_id) VALUES (17, 998.00, 'UTILITIES', 'RENT', 'RENT', 'IT87NEXS749563253715271001', 6);


--
-- Data for Name: payments; Type: TABLE DATA; Schema: banking_payments; Owner: -
--

INSERT INTO banking_payments.payments (id, amount, created_at, description, from_account_id, status, to_account_id, category) VALUES (4, 100.00, '2026-03-03 10:11:54.797586+01', 'Test Transfer', 6, 'COMPLETED', 5, 'TRANSFER');
INSERT INTO banking_payments.payments (id, amount, created_at, description, from_account_id, status, to_account_id, category) VALUES (5, 500.00, '2026-03-03 10:16:33.006228+01', 'Salary payment', 5, 'COMPLETED', 6, 'SALARY');
INSERT INTO banking_payments.payments (id, amount, created_at, description, from_account_id, status, to_account_id, category) VALUES (6, 50.00, '2026-03-03 10:26:32.960992+01', 'Dinner', 5, 'COMPLETED', 6, 'FOOD');
INSERT INTO banking_payments.payments (id, amount, created_at, description, from_account_id, status, to_account_id, category) VALUES (7, 10.00, '2026-03-03 10:27:38.150217+01', 'Bus ticket', 5, 'COMPLETED', 6, 'TRANSPORT');
INSERT INTO banking_payments.payments (id, amount, created_at, description, from_account_id, status, to_account_id, category) VALUES (8, 60.00, '2026-03-03 13:19:19.171026+01', 'Test Transfer', 6, 'COMPLETED', 5, 'UTILITIES');
INSERT INTO banking_payments.payments (id, amount, created_at, description, from_account_id, status, to_account_id, category) VALUES (9, 200.00, '2026-03-03 14:31:11.658075+01', 'test account 2', 14, 'COMPLETED', 11, 'ENTERTAINMENT');
INSERT INTO banking_payments.payments (id, amount, created_at, description, from_account_id, status, to_account_id, category) VALUES (10, 60.00, '2026-03-04 13:22:02.4301+01', 'Test Transfer', 6, 'COMPLETED', 5, 'PAYMENT');
INSERT INTO banking_payments.payments (id, amount, created_at, description, from_account_id, status, to_account_id, category) VALUES (11, 15.00, '2026-03-04 14:09:07.489687+01', 'Iban-to-Iban test', 6, 'COMPLETED', 5, 'PAYMENT');
INSERT INTO banking_payments.payments (id, amount, created_at, description, from_account_id, status, to_account_id, category) VALUES (12, 75.00, '2026-03-04 14:16:31.910476+01', 'Clickable transaction', 5, 'COMPLETED', 17, 'SHOPPING');
INSERT INTO banking_payments.payments (id, amount, created_at, description, from_account_id, status, to_account_id, category) VALUES (13, 100000000.00, '2026-03-04 15:43:47.965925+01', 'First bank transfer', 15, 'COMPLETED', 11, 'TRANSFER');
INSERT INTO banking_payments.payments (id, amount, created_at, description, from_account_id, status, to_account_id, category) VALUES (14, 777.00, '2026-03-04 16:19:37.604443+01', 'Move money', 5, 'COMPLETED', 15, 'TRANSFER');
INSERT INTO banking_payments.payments (id, amount, created_at, description, from_account_id, status, to_account_id, category) VALUES (15, 200.00, '2026-03-05 09:27:36.816017+01', 'February rent', 5, 'COMPLETED', 6, 'TRANSFER');
INSERT INTO banking_payments.payments (id, amount, created_at, description, from_account_id, status, to_account_id, category) VALUES (16, 200.00, '2026-03-05 09:27:54.102243+01', 'February rent', 5, 'COMPLETED', 6, 'TRANSFER');
INSERT INTO banking_payments.payments (id, amount, created_at, description, from_account_id, status, to_account_id, category) VALUES (17, 200.00, '2026-03-05 09:28:07.243085+01', 'February rent', 5, 'COMPLETED', 6, 'TRANSFER');
INSERT INTO banking_payments.payments (id, amount, created_at, description, from_account_id, status, to_account_id, category) VALUES (18, 200.00, '2026-03-05 09:30:56.74789+01', 'February rent', 5, 'COMPLETED', 6, 'TRANSFER');
INSERT INTO banking_payments.payments (id, amount, created_at, description, from_account_id, status, to_account_id, category) VALUES (19, 200.00, '2026-03-05 09:33:32.614104+01', 'February rent', 5, 'COMPLETED', 6, 'TRANSFER');
INSERT INTO banking_payments.payments (id, amount, created_at, description, from_account_id, status, to_account_id, category) VALUES (20, 200.00, '2026-03-05 09:51:53.110313+01', 'February rent', 5, 'COMPLETED', 6, 'TRANSFER');
INSERT INTO banking_payments.payments (id, amount, created_at, description, from_account_id, status, to_account_id, category) VALUES (21, 200.00, '2026-03-05 09:55:55.202767+01', 'February rent', 5, 'COMPLETED', 6, 'TRANSFER');
INSERT INTO banking_payments.payments (id, amount, created_at, description, from_account_id, status, to_account_id, category) VALUES (22, 52222.00, '2026-03-05 10:02:12.523755+01', 'Test internal fav opertion', 15, 'COMPLETED', 5, 'TRANSFER');
INSERT INTO banking_payments.payments (id, amount, created_at, description, from_account_id, status, to_account_id, category) VALUES (23, 52222.00, '2026-03-05 14:43:40.929032+01', 'Test internal fav opertion', 5, 'COMPLETED', 15, 'TRANSFER');
INSERT INTO banking_payments.payments (id, amount, created_at, description, from_account_id, status, to_account_id, category) VALUES (24, 28.00, '2026-03-05 14:44:06.550757+01', 'Test internal fav opertion', 5, 'COMPLETED', 14, 'TRANSFER');
INSERT INTO banking_payments.payments (id, amount, created_at, description, from_account_id, status, to_account_id, category) VALUES (25, 10.00, '2026-03-05 16:07:40.663129+01', 'Internal Transfer to UserAccount 2', 6, 'COMPLETED', 16, 'SALARY');
INSERT INTO banking_payments.payments (id, amount, created_at, description, from_account_id, status, to_account_id, category) VALUES (26, 7.00, '2026-03-05 16:09:14.242022+01', 'Train ticket', 16, 'COMPLETED', 11, 'TRANSPORT');
INSERT INTO banking_payments.payments (id, amount, created_at, description, from_account_id, status, to_account_id, category) VALUES (27, 815.00, '2026-03-05 16:23:49.322343+01', 'New favOps list layout', 6, 'COMPLETED', 16, 'TRANSFER');
INSERT INTO banking_payments.payments (id, amount, created_at, description, from_account_id, status, to_account_id, category) VALUES (28, 2.00, '2026-03-05 16:24:10.664885+01', 'Internal Transfer to UserAccount 2', 6, 'COMPLETED', 16, 'TRANSFER');
INSERT INTO banking_payments.payments (id, amount, created_at, description, from_account_id, status, to_account_id, category) VALUES (29, 95.00, '2026-03-05 21:09:57.331699+01', 'Test payment', 5, 'COMPLETED', 14, 'TRANSFER');
INSERT INTO banking_payments.payments (id, amount, created_at, description, from_account_id, status, to_account_id, category) VALUES (30, 95.00, '2026-03-05 21:10:12.643021+01', 'Test payment', 5, 'COMPLETED', 14, 'TRANSFER');
INSERT INTO banking_payments.payments (id, amount, created_at, description, from_account_id, status, to_account_id, category) VALUES (31, 95.00, '2026-03-05 21:12:33.908294+01', 'Test payment', 5, 'COMPLETED', 14, 'TRANSFER');
INSERT INTO banking_payments.payments (id, amount, created_at, description, from_account_id, status, to_account_id, category) VALUES (32, 10.00, '2026-03-05 21:18:01.645556+01', 'Test payment', 5, 'COMPLETED', 14, 'TRANSFER');
INSERT INTO banking_payments.payments (id, amount, created_at, description, from_account_id, status, to_account_id, category) VALUES (33, 500.00, '2026-03-05 21:18:51.748252+01', 'Doctor', 5, 'COMPLETED', 16, 'HEALTHCARE');
INSERT INTO banking_payments.payments (id, amount, created_at, description, from_account_id, status, to_account_id, category) VALUES (34, 10000.00, '2026-03-07 12:31:31.042643+01', 'Internal Transfer to MicAccount 2', 15, 'COMPLETED', 14, 'UTILITIES');
INSERT INTO banking_payments.payments (id, amount, created_at, description, from_account_id, status, to_account_id, category) VALUES (35, 10000.00, '2026-03-07 12:32:21.682384+01', 'Gift', 15, 'COMPLETED', 14, 'UTILITIES');
INSERT INTO banking_payments.payments (id, amount, created_at, description, from_account_id, status, to_account_id, category) VALUES (36, 5000.00, '2026-03-07 12:34:56.295476+01', 'Payment to IT11NEXS632111781360241549', 15, 'COMPLETED', 18, 'TRANSFER');
INSERT INTO banking_payments.payments (id, amount, created_at, description, from_account_id, status, to_account_id, category) VALUES (37, 623.00, '2026-03-09 13:58:01.368809+01', 'First payment', 14, 'COMPLETED', 19, 'TRANSFER');
INSERT INTO banking_payments.payments (id, amount, created_at, description, from_account_id, status, to_account_id, category) VALUES (38, 23.00, '2026-03-09 13:58:58.959017+01', 'Payment to IT65NEXS934350224722890982', 19, 'COMPLETED', 17, 'UTILITIES');
INSERT INTO banking_payments.payments (id, amount, created_at, description, from_account_id, status, to_account_id, category) VALUES (39, 23.00, '2026-03-09 13:59:19.97428+01', 'Payment to IT65NEXS934350224722890982', 19, 'COMPLETED', 17, 'UTILITIES');
INSERT INTO banking_payments.payments (id, amount, created_at, description, from_account_id, status, to_account_id, category) VALUES (40, 77.00, '2026-03-09 14:05:40.368945+01', 'February rent', 19, 'COMPLETED', 14, 'TRANSFER');
INSERT INTO banking_payments.payments (id, amount, created_at, description, from_account_id, status, to_account_id, category) VALUES (41, 100.00, '2026-03-10 16:10:17.330381+01', 'Internal Transfer to MicAccount 3', 5, 'COMPLETED', 19, 'TRANSFER');
INSERT INTO banking_payments.payments (id, amount, created_at, description, from_account_id, status, to_account_id, category) VALUES (42, 998.00, '2026-03-10 16:11:53.098787+01', 'RENT', 6, 'COMPLETED', 20, 'UTILITIES');
INSERT INTO banking_payments.payments (id, amount, created_at, description, from_account_id, status, to_account_id, category) VALUES (43, 98.00, '2026-03-10 16:17:00.145104+01', 'Internal Transfer to MicAccount5', 20, 'COMPLETED', 21, 'TRANSFER');
INSERT INTO banking_payments.payments (id, amount, created_at, description, from_account_id, status, to_account_id, category) VALUES (44, 100.00, '2026-03-11 13:37:14.828256+01', 'Internal Transfer to MicAccount 3', 5, 'COMPLETED', 19, 'TRANSFER');
INSERT INTO banking_payments.payments (id, amount, created_at, description, from_account_id, status, to_account_id, category) VALUES (45, 500.00, '2026-03-11 13:37:30.686017+01', 'Doctor', 5, 'COMPLETED', 16, 'HEALTHCARE');
INSERT INTO banking_payments.payments (id, amount, created_at, description, from_account_id, status, to_account_id, category) VALUES (46, 522.00, '2026-03-11 14:07:24.856554+01', 'Payment to IT73NEXS892582929170888462', 5, 'COMPLETED', 6, 'ENTERTAINMENT');
INSERT INTO banking_payments.payments (id, amount, created_at, description, from_account_id, status, to_account_id, category) VALUES (47, 33.00, '2026-03-11 14:55:51.684985+01', 'Payment to IT73NEXS892582929170888462', 5, 'COMPLETED', 18, 'TRANSFER');
INSERT INTO banking_payments.payments (id, amount, created_at, description, from_account_id, status, to_account_id, category) VALUES (48, 300.00, '2026-03-11 15:27:50.063997+01', 'Internal Transfer to MicAccount 3', 5, 'COMPLETED', 19, 'TRANSFER');
INSERT INTO banking_payments.payments (id, amount, created_at, description, from_account_id, status, to_account_id, category) VALUES (49, 23.00, '2026-03-13 11:26:25.443234+01', 'Payment to IT65NEXS934350224722890982', 19, 'COMPLETED', 17, 'UTILITIES');
INSERT INTO banking_payments.payments (id, amount, created_at, description, from_account_id, status, to_account_id, category) VALUES (50, 23.00, '2026-03-13 11:26:51.793318+01', 'Payment to IT65NEXS934350224722890982', 19, 'COMPLETED', 17, 'UTILITIES');
INSERT INTO banking_payments.payments (id, amount, created_at, description, from_account_id, status, to_account_id, category) VALUES (51, 23.00, '2026-03-13 11:27:14.929709+01', 'Internal Transfer to MicAccount4', 19, 'COMPLETED', 20, 'UTILITIES');
INSERT INTO banking_payments.payments (id, amount, created_at, description, from_account_id, status, to_account_id, category) VALUES (52, 77.00, '2026-03-13 11:27:50.133582+01', 'February rent', 19, 'COMPLETED', 21, 'TRANSFER');
INSERT INTO banking_payments.payments (id, amount, created_at, description, from_account_id, status, to_account_id, category) VALUES (53, 88.00, '2026-03-13 11:28:19.560538+01', 'Internal Transfer to MicAccount5', 19, 'COMPLETED', 21, 'TRANSFER');
INSERT INTO banking_payments.payments (id, amount, created_at, description, from_account_id, status, to_account_id, category) VALUES (54, 154.00, '2026-03-13 11:29:03.909659+01', 'Internal Transfer to MicAccount6', 5, 'COMPLETED', 22, 'TRANSFER');
INSERT INTO banking_payments.payments (id, amount, created_at, description, from_account_id, status, to_account_id, category) VALUES (55, 62.99, '2026-03-13 11:29:46.695335+01', 'Internal Transfer to MicAccount6', 21, 'COMPLETED', 22, 'TRANSFER');
INSERT INTO banking_payments.payments (id, amount, created_at, description, from_account_id, status, to_account_id, category) VALUES (56, 522.00, '2026-03-13 11:31:08.432082+01', 'RENT', 6, 'COMPLETED', 20, 'UTILITIES');
INSERT INTO banking_payments.payments (id, amount, created_at, description, from_account_id, status, to_account_id, category) VALUES (57, 66.00, '2026-03-13 11:37:24.031831+01', 'Internal Transfer to MicAccount5', 19, 'COMPLETED', 21, 'TRANSFER');
INSERT INTO banking_payments.payments (id, amount, created_at, description, from_account_id, status, to_account_id, category) VALUES (58, 16.99, '2026-03-13 11:45:46.330466+01', 'Internal Transfer to MicAccount 1', 22, 'COMPLETED', 5, 'TRANSFER');
INSERT INTO banking_payments.payments (id, amount, created_at, description, from_account_id, status, to_account_id, category) VALUES (59, 16.99, '2026-03-13 11:46:37.540953+01', 'Test new db', 5, 'COMPLETED', 17, 'TRANSFER');


--
-- Data for Name: transactions; Type: TABLE DATA; Schema: banking_transactions; Owner: -
--

INSERT INTO banking_transactions.transactions (id, amount, category, created_at, description, reference_id, type, user_id, account_id, counterparty_iban) VALUES (5, 100.00, 'TRANSFER', '2026-03-03 10:11:54.802632+01', 'Payment to account #5', '43fb9ede-0671-4e29-af15-8af7efd9c19a', 'DEBIT', 6, 6, '\N');
INSERT INTO banking_transactions.transactions (id, amount, category, created_at, description, reference_id, type, user_id, account_id, counterparty_iban) VALUES (6, 100.00, 'TRANSFER', '2026-03-03 10:11:54.830755+01', 'Payment from account #6', '43fb9ede-0671-4e29-af15-8af7efd9c19a', 'CREDIT', 5, 5, '\N');
INSERT INTO banking_transactions.transactions (id, amount, category, created_at, description, reference_id, type, user_id, account_id, counterparty_iban) VALUES (7, 500.00, 'SALARY', '2026-03-03 10:16:33.011212+01', 'Payment to account #6', '26e19440-2f76-4b54-97fb-6cc3ab0f41af', 'DEBIT', 5, 5, '\N');
INSERT INTO banking_transactions.transactions (id, amount, category, created_at, description, reference_id, type, user_id, account_id, counterparty_iban) VALUES (8, 500.00, 'SALARY', '2026-03-03 10:16:33.033607+01', 'Payment from account #5', '26e19440-2f76-4b54-97fb-6cc3ab0f41af', 'CREDIT', 6, 6, '\N');
INSERT INTO banking_transactions.transactions (id, amount, category, created_at, description, reference_id, type, user_id, account_id, counterparty_iban) VALUES (9, 50.00, 'FOOD', '2026-03-03 10:26:32.965599+01', 'Payment to account #6', '215b21cb-c26d-4d05-b04d-a81a2de6d51f', 'DEBIT', 5, 5, '\N');
INSERT INTO banking_transactions.transactions (id, amount, category, created_at, description, reference_id, type, user_id, account_id, counterparty_iban) VALUES (10, 50.00, 'FOOD', '2026-03-03 10:26:32.97935+01', 'Payment from account #5', '215b21cb-c26d-4d05-b04d-a81a2de6d51f', 'CREDIT', 6, 6, '\N');
INSERT INTO banking_transactions.transactions (id, amount, category, created_at, description, reference_id, type, user_id, account_id, counterparty_iban) VALUES (11, 10.00, 'TRANSPORT', '2026-03-03 10:27:38.183905+01', 'Bus ticket', '637d4475-6582-4ae3-98f2-c0a8593ace98', 'DEBIT', 5, 5, '\N');
INSERT INTO banking_transactions.transactions (id, amount, category, created_at, description, reference_id, type, user_id, account_id, counterparty_iban) VALUES (12, 10.00, 'TRANSPORT', '2026-03-03 10:27:38.202726+01', 'Bus ticket', '637d4475-6582-4ae3-98f2-c0a8593ace98', 'CREDIT', 6, 6, '\N');
INSERT INTO banking_transactions.transactions (id, amount, category, created_at, description, reference_id, type, user_id, account_id, counterparty_iban) VALUES (13, 60.00, 'UTILITIES', '2026-03-03 13:19:19.283917+01', 'Test Transfer', '688e6ab8-c030-414d-ba77-d7fc8c1e4137', 'DEBIT', 6, 6, 'IT73NEXS892582929170888462');
INSERT INTO banking_transactions.transactions (id, amount, category, created_at, description, reference_id, type, user_id, account_id, counterparty_iban) VALUES (14, 60.00, 'UTILITIES', '2026-03-03 13:19:19.341468+01', 'Test Transfer', '688e6ab8-c030-414d-ba77-d7fc8c1e4137', 'CREDIT', 5, 5, 'IT57NEXS704043974399150956');
INSERT INTO banking_transactions.transactions (id, amount, category, created_at, description, reference_id, type, user_id, account_id, counterparty_iban) VALUES (15, 200.00, 'ENTERTAINMENT', '2026-03-03 14:31:11.662027+01', 'test account 2', '8fbca049-0342-497d-ad01-65efa6a68f7e', 'DEBIT', 5, 14, 'IT78NEXS709563321058228999');
INSERT INTO banking_transactions.transactions (id, amount, category, created_at, description, reference_id, type, user_id, account_id, counterparty_iban) VALUES (16, 200.00, 'ENTERTAINMENT', '2026-03-03 14:31:11.683201+01', 'test account 2', '8fbca049-0342-497d-ad01-65efa6a68f7e', 'CREDIT', 11, 11, 'IT79NEXS695999735361278337');
INSERT INTO banking_transactions.transactions (id, amount, category, created_at, description, reference_id, type, user_id, account_id, counterparty_iban) VALUES (17, 100.00, 'DEPOSIT', '2026-03-03 15:00:44.454209+01', 'Deposit to account', '53d04f5c-bdbe-402f-9722-994918be09db', 'CREDIT', 6, 6, '\N');
INSERT INTO banking_transactions.transactions (id, amount, category, created_at, description, reference_id, type, user_id, account_id, counterparty_iban) VALUES (18, 10000000000000000.00, 'DEPOSIT', '2026-03-03 15:04:02.053086+01', 'Deposit to account', 'f90f1ce6-8415-44c0-9896-1d106ba7279b', 'CREDIT', 5, 15, '\N');
INSERT INTO banking_transactions.transactions (id, amount, category, created_at, description, reference_id, type, user_id, account_id, counterparty_iban) VALUES (19, 60.00, 'PAYMENT', '2026-03-04 13:22:02.501178+01', 'Test Transfer', 'f247246d-525f-4e7b-9399-29e2de2e8339', 'DEBIT', 6, 6, 'IT73NEXS892582929170888462');
INSERT INTO banking_transactions.transactions (id, amount, category, created_at, description, reference_id, type, user_id, account_id, counterparty_iban) VALUES (20, 60.00, 'PAYMENT', '2026-03-04 13:22:02.536111+01', 'Test Transfer', 'f247246d-525f-4e7b-9399-29e2de2e8339', 'CREDIT', 5, 5, 'IT57NEXS704043974399150956');
INSERT INTO banking_transactions.transactions (id, amount, category, created_at, description, reference_id, type, user_id, account_id, counterparty_iban) VALUES (21, 15.00, 'PAYMENT', '2026-03-04 14:09:07.532275+01', 'Iban-to-Iban test', '191ebc6e-ce50-452a-8fec-10cd20137bc9', 'DEBIT', 6, 6, 'IT73NEXS892582929170888462');
INSERT INTO banking_transactions.transactions (id, amount, category, created_at, description, reference_id, type, user_id, account_id, counterparty_iban) VALUES (22, 15.00, 'PAYMENT', '2026-03-04 14:09:07.555681+01', 'Iban-to-Iban test', '191ebc6e-ce50-452a-8fec-10cd20137bc9', 'CREDIT', 5, 5, 'IT57NEXS704043974399150956');
INSERT INTO banking_transactions.transactions (id, amount, category, created_at, description, reference_id, type, user_id, account_id, counterparty_iban) VALUES (23, 75.00, 'SHOPPING', '2026-03-04 14:16:31.916519+01', 'Clickable transaction', 'fdc560df-be21-4d1d-b788-85a2d9d9f5f6', 'DEBIT', 5, 5, 'IT55NEXS756603083054917901');
INSERT INTO banking_transactions.transactions (id, amount, category, created_at, description, reference_id, type, user_id, account_id, counterparty_iban) VALUES (24, 75.00, 'SHOPPING', '2026-03-04 14:16:31.935154+01', 'Clickable transaction', 'fdc560df-be21-4d1d-b788-85a2d9d9f5f6', 'CREDIT', 6, 17, 'IT73NEXS892582929170888462');
INSERT INTO banking_transactions.transactions (id, amount, category, created_at, description, reference_id, type, user_id, account_id, counterparty_iban) VALUES (25, 100000000.00, 'TRANSFER', '2026-03-04 15:43:48.035417+01', 'First bank transfer', '4f1a149f-2a81-460b-926c-d8b29f3637c1', 'DEBIT', 5, 15, 'IT78NEXS709563321058228999');
INSERT INTO banking_transactions.transactions (id, amount, category, created_at, description, reference_id, type, user_id, account_id, counterparty_iban) VALUES (26, 100000000.00, 'TRANSFER', '2026-03-04 15:43:48.075953+01', 'First bank transfer', '4f1a149f-2a81-460b-926c-d8b29f3637c1', 'CREDIT', 11, 11, 'IT11NEXS632111781360241549');
INSERT INTO banking_transactions.transactions (id, amount, category, created_at, description, reference_id, type, user_id, account_id, counterparty_iban) VALUES (27, 777.00, 'TRANSFER', '2026-03-04 16:19:37.619314+01', 'Move money', 'dbb476b6-d784-4d45-be51-43d6b38c7aea', 'DEBIT', 5, 5, 'IT11NEXS632111781360241549');
INSERT INTO banking_transactions.transactions (id, amount, category, created_at, description, reference_id, type, user_id, account_id, counterparty_iban) VALUES (28, 777.00, 'TRANSFER', '2026-03-04 16:19:37.637038+01', 'Move money', 'dbb476b6-d784-4d45-be51-43d6b38c7aea', 'CREDIT', 5, 15, 'IT73NEXS892582929170888462');
INSERT INTO banking_transactions.transactions (id, amount, category, created_at, description, reference_id, type, user_id, account_id, counterparty_iban) VALUES (29, 200.00, 'TRANSFER', '2026-03-05 09:27:36.924366+01', 'February rent', 'a94cab3b-9a22-4c6a-bd2e-b5d064277c31', 'DEBIT', 5, 5, 'IT57NEXS704043974399150956');
INSERT INTO banking_transactions.transactions (id, amount, category, created_at, description, reference_id, type, user_id, account_id, counterparty_iban) VALUES (30, 200.00, 'TRANSFER', '2026-03-05 09:27:36.993322+01', 'February rent', 'a94cab3b-9a22-4c6a-bd2e-b5d064277c31', 'CREDIT', 6, 6, 'IT73NEXS892582929170888462');
INSERT INTO banking_transactions.transactions (id, amount, category, created_at, description, reference_id, type, user_id, account_id, counterparty_iban) VALUES (31, 200.00, 'TRANSFER', '2026-03-05 09:27:54.110962+01', 'February rent', 'b66186d3-6c54-49bf-bad9-af53777644e3', 'DEBIT', 5, 5, 'IT57NEXS704043974399150956');
INSERT INTO banking_transactions.transactions (id, amount, category, created_at, description, reference_id, type, user_id, account_id, counterparty_iban) VALUES (32, 200.00, 'TRANSFER', '2026-03-05 09:27:54.139505+01', 'February rent', 'b66186d3-6c54-49bf-bad9-af53777644e3', 'CREDIT', 6, 6, 'IT73NEXS892582929170888462');
INSERT INTO banking_transactions.transactions (id, amount, category, created_at, description, reference_id, type, user_id, account_id, counterparty_iban) VALUES (33, 200.00, 'TRANSFER', '2026-03-05 09:28:07.250931+01', 'February rent', '63606572-1d80-4674-9036-681d8416bb5c', 'DEBIT', 5, 5, 'IT57NEXS704043974399150956');
INSERT INTO banking_transactions.transactions (id, amount, category, created_at, description, reference_id, type, user_id, account_id, counterparty_iban) VALUES (34, 200.00, 'TRANSFER', '2026-03-05 09:28:07.274918+01', 'February rent', '63606572-1d80-4674-9036-681d8416bb5c', 'CREDIT', 6, 6, 'IT73NEXS892582929170888462');
INSERT INTO banking_transactions.transactions (id, amount, category, created_at, description, reference_id, type, user_id, account_id, counterparty_iban) VALUES (35, 200.00, 'TRANSFER', '2026-03-05 09:30:56.755468+01', 'February rent', '367f7108-31f3-42e5-8a7f-80a3a7d75397', 'DEBIT', 5, 5, 'IT57NEXS704043974399150956');
INSERT INTO banking_transactions.transactions (id, amount, category, created_at, description, reference_id, type, user_id, account_id, counterparty_iban) VALUES (36, 200.00, 'TRANSFER', '2026-03-05 09:30:56.781272+01', 'February rent', '367f7108-31f3-42e5-8a7f-80a3a7d75397', 'CREDIT', 6, 6, 'IT73NEXS892582929170888462');
INSERT INTO banking_transactions.transactions (id, amount, category, created_at, description, reference_id, type, user_id, account_id, counterparty_iban) VALUES (37, 200.00, 'TRANSFER', '2026-03-05 09:33:32.663229+01', 'February rent', '103767c3-6dc6-44e6-97e6-b58ea4f99129', 'DEBIT', 5, 5, 'IT57NEXS704043974399150956');
INSERT INTO banking_transactions.transactions (id, amount, category, created_at, description, reference_id, type, user_id, account_id, counterparty_iban) VALUES (38, 200.00, 'TRANSFER', '2026-03-05 09:33:32.693675+01', 'February rent', '103767c3-6dc6-44e6-97e6-b58ea4f99129', 'CREDIT', 6, 6, 'IT73NEXS892582929170888462');
INSERT INTO banking_transactions.transactions (id, amount, category, created_at, description, reference_id, type, user_id, account_id, counterparty_iban) VALUES (39, 200.00, 'TRANSFER', '2026-03-05 09:51:53.120403+01', 'February rent', 'f108f196-92a4-4a40-81ea-5a7ea78f9713', 'DEBIT', 5, 5, 'IT57NEXS704043974399150956');
INSERT INTO banking_transactions.transactions (id, amount, category, created_at, description, reference_id, type, user_id, account_id, counterparty_iban) VALUES (40, 200.00, 'TRANSFER', '2026-03-05 09:51:53.142987+01', 'February rent', 'f108f196-92a4-4a40-81ea-5a7ea78f9713', 'CREDIT', 6, 6, 'IT73NEXS892582929170888462');
INSERT INTO banking_transactions.transactions (id, amount, category, created_at, description, reference_id, type, user_id, account_id, counterparty_iban) VALUES (41, 200.00, 'TRANSFER', '2026-03-05 09:55:55.245417+01', 'February rent', 'a19cd122-f34e-41c3-b302-ade6f97c18a2', 'DEBIT', 5, 5, 'IT57NEXS704043974399150956');
INSERT INTO banking_transactions.transactions (id, amount, category, created_at, description, reference_id, type, user_id, account_id, counterparty_iban) VALUES (42, 200.00, 'TRANSFER', '2026-03-05 09:55:55.287492+01', 'February rent', 'a19cd122-f34e-41c3-b302-ade6f97c18a2', 'CREDIT', 6, 6, 'IT73NEXS892582929170888462');
INSERT INTO banking_transactions.transactions (id, amount, category, created_at, description, reference_id, type, user_id, account_id, counterparty_iban) VALUES (43, 52222.00, 'TRANSFER', '2026-03-05 10:02:12.535597+01', 'Test internal fav opertion', 'd2a26e94-100b-4c4e-805c-0e02df2eca77', 'DEBIT', 5, 15, 'IT73NEXS892582929170888462');
INSERT INTO banking_transactions.transactions (id, amount, category, created_at, description, reference_id, type, user_id, account_id, counterparty_iban) VALUES (44, 52222.00, 'TRANSFER', '2026-03-05 10:02:12.557769+01', 'Test internal fav opertion', 'd2a26e94-100b-4c4e-805c-0e02df2eca77', 'CREDIT', 5, 5, 'IT11NEXS632111781360241549');
INSERT INTO banking_transactions.transactions (id, amount, category, created_at, description, reference_id, type, user_id, account_id, counterparty_iban) VALUES (45, 52222.00, 'TRANSFER', '2026-03-05 14:43:41.2062+01', 'Test internal fav opertion', '24799142-9b6a-403a-be3f-dcefd910d7c3', 'DEBIT', 5, 5, 'IT11NEXS632111781360241549');
INSERT INTO banking_transactions.transactions (id, amount, category, created_at, description, reference_id, type, user_id, account_id, counterparty_iban) VALUES (46, 52222.00, 'TRANSFER', '2026-03-05 14:43:41.317522+01', 'Test internal fav opertion', '24799142-9b6a-403a-be3f-dcefd910d7c3', 'CREDIT', 5, 15, 'IT73NEXS892582929170888462');
INSERT INTO banking_transactions.transactions (id, amount, category, created_at, description, reference_id, type, user_id, account_id, counterparty_iban) VALUES (47, 28.00, 'TRANSFER', '2026-03-05 14:44:06.555663+01', 'Test internal fav opertion', '69041688-f221-4c2f-b220-b4224120de41', 'DEBIT', 5, 5, 'IT79NEXS695999735361278337');
INSERT INTO banking_transactions.transactions (id, amount, category, created_at, description, reference_id, type, user_id, account_id, counterparty_iban) VALUES (48, 28.00, 'TRANSFER', '2026-03-05 14:44:06.57211+01', 'Test internal fav opertion', '69041688-f221-4c2f-b220-b4224120de41', 'CREDIT', 5, 14, 'IT73NEXS892582929170888462');
INSERT INTO banking_transactions.transactions (id, amount, category, created_at, description, reference_id, type, user_id, account_id, counterparty_iban) VALUES (49, 10.00, 'SALARY', '2026-03-05 16:07:40.698088+01', 'Internal Transfer to UserAccount 2', 'b6229044-9720-4848-b6f3-01b83cd47bf1', 'DEBIT', 6, 6, 'IT84NEXS020386725308339380');
INSERT INTO banking_transactions.transactions (id, amount, category, created_at, description, reference_id, type, user_id, account_id, counterparty_iban) VALUES (50, 10.00, 'SALARY', '2026-03-05 16:07:40.733093+01', 'Internal Transfer to UserAccount 2', 'b6229044-9720-4848-b6f3-01b83cd47bf1', 'CREDIT', 6, 16, 'IT57NEXS704043974399150956');
INSERT INTO banking_transactions.transactions (id, amount, category, created_at, description, reference_id, type, user_id, account_id, counterparty_iban) VALUES (51, 7.00, 'TRANSPORT', '2026-03-05 16:09:14.249012+01', 'Train ticket', '5683cf86-4607-4da6-95ec-b3d44bbf93dd', 'DEBIT', 6, 16, 'IT78NEXS709563321058228999');
INSERT INTO banking_transactions.transactions (id, amount, category, created_at, description, reference_id, type, user_id, account_id, counterparty_iban) VALUES (52, 7.00, 'TRANSPORT', '2026-03-05 16:09:14.269032+01', 'Train ticket', '5683cf86-4607-4da6-95ec-b3d44bbf93dd', 'CREDIT', 11, 11, 'IT84NEXS020386725308339380');
INSERT INTO banking_transactions.transactions (id, amount, category, created_at, description, reference_id, type, user_id, account_id, counterparty_iban) VALUES (53, 815.00, 'TRANSFER', '2026-03-05 16:23:49.334454+01', 'New favOps list layout', '51fde97e-237f-4b59-b2eb-ec100028dc7b', 'DEBIT', 6, 6, 'IT84NEXS020386725308339380');
INSERT INTO banking_transactions.transactions (id, amount, category, created_at, description, reference_id, type, user_id, account_id, counterparty_iban) VALUES (54, 815.00, 'TRANSFER', '2026-03-05 16:23:49.357855+01', 'New favOps list layout', '51fde97e-237f-4b59-b2eb-ec100028dc7b', 'CREDIT', 6, 16, 'IT57NEXS704043974399150956');
INSERT INTO banking_transactions.transactions (id, amount, category, created_at, description, reference_id, type, user_id, account_id, counterparty_iban) VALUES (55, 2.00, 'TRANSFER', '2026-03-05 16:24:10.673163+01', 'Internal Transfer to UserAccount 2', '3e5cdcfd-2425-4f8b-ac98-0a48d3e3c5e7', 'DEBIT', 6, 6, 'IT84NEXS020386725308339380');
INSERT INTO banking_transactions.transactions (id, amount, category, created_at, description, reference_id, type, user_id, account_id, counterparty_iban) VALUES (56, 2.00, 'TRANSFER', '2026-03-05 16:24:10.69252+01', 'Internal Transfer to UserAccount 2', '3e5cdcfd-2425-4f8b-ac98-0a48d3e3c5e7', 'CREDIT', 6, 16, 'IT57NEXS704043974399150956');
INSERT INTO banking_transactions.transactions (id, amount, category, created_at, description, reference_id, type, user_id, account_id, counterparty_iban) VALUES (57, 95.00, 'TRANSFER', '2026-03-05 21:09:57.513682+01', 'Test payment', 'b2c9deb3-aba6-40d1-bb93-a73f94d50fbf', 'DEBIT', 5, 5, 'IT79NEXS695999735361278337');
INSERT INTO banking_transactions.transactions (id, amount, category, created_at, description, reference_id, type, user_id, account_id, counterparty_iban) VALUES (58, 95.00, 'TRANSFER', '2026-03-05 21:09:57.608723+01', 'Test payment', 'b2c9deb3-aba6-40d1-bb93-a73f94d50fbf', 'CREDIT', 5, 14, 'IT73NEXS892582929170888462');
INSERT INTO banking_transactions.transactions (id, amount, category, created_at, description, reference_id, type, user_id, account_id, counterparty_iban) VALUES (59, 95.00, 'TRANSFER', '2026-03-05 21:10:12.651185+01', 'Test payment', '7e19eb75-a9f2-4cdf-8c4f-403a09e5faae', 'DEBIT', 5, 5, 'IT79NEXS695999735361278337');
INSERT INTO banking_transactions.transactions (id, amount, category, created_at, description, reference_id, type, user_id, account_id, counterparty_iban) VALUES (60, 95.00, 'TRANSFER', '2026-03-05 21:10:12.675873+01', 'Test payment', '7e19eb75-a9f2-4cdf-8c4f-403a09e5faae', 'CREDIT', 5, 14, 'IT73NEXS892582929170888462');
INSERT INTO banking_transactions.transactions (id, amount, category, created_at, description, reference_id, type, user_id, account_id, counterparty_iban) VALUES (61, 95.00, 'TRANSFER', '2026-03-05 21:12:33.922577+01', 'Test payment', '2adadd76-e8ce-48fe-aae0-4d2b0ba1543c', 'DEBIT', 5, 5, 'IT79NEXS695999735361278337');
INSERT INTO banking_transactions.transactions (id, amount, category, created_at, description, reference_id, type, user_id, account_id, counterparty_iban) VALUES (62, 95.00, 'TRANSFER', '2026-03-05 21:12:33.954974+01', 'Test payment', '2adadd76-e8ce-48fe-aae0-4d2b0ba1543c', 'CREDIT', 5, 14, 'IT73NEXS892582929170888462');
INSERT INTO banking_transactions.transactions (id, amount, category, created_at, description, reference_id, type, user_id, account_id, counterparty_iban) VALUES (63, 10.00, 'TRANSFER', '2026-03-05 21:18:01.657599+01', 'Test payment', 'a96cdbf7-9ab6-4bf7-ac09-2a7a851f0134', 'DEBIT', 5, 5, 'IT79NEXS695999735361278337');
INSERT INTO banking_transactions.transactions (id, amount, category, created_at, description, reference_id, type, user_id, account_id, counterparty_iban) VALUES (64, 10.00, 'TRANSFER', '2026-03-05 21:18:01.679548+01', 'Test payment', 'a96cdbf7-9ab6-4bf7-ac09-2a7a851f0134', 'CREDIT', 5, 14, 'IT73NEXS892582929170888462');
INSERT INTO banking_transactions.transactions (id, amount, category, created_at, description, reference_id, type, user_id, account_id, counterparty_iban) VALUES (65, 500.00, 'HEALTHCARE', '2026-03-05 21:18:51.758729+01', 'Doctor', '40ed569e-e854-4648-b5b6-997d952078e6', 'DEBIT', 5, 5, 'IT84NEXS020386725308339380');
INSERT INTO banking_transactions.transactions (id, amount, category, created_at, description, reference_id, type, user_id, account_id, counterparty_iban) VALUES (66, 500.00, 'HEALTHCARE', '2026-03-05 21:18:51.782068+01', 'Doctor', '40ed569e-e854-4648-b5b6-997d952078e6', 'CREDIT', 6, 16, 'IT73NEXS892582929170888462');
INSERT INTO banking_transactions.transactions (id, amount, category, created_at, description, reference_id, type, user_id, account_id, counterparty_iban) VALUES (67, 100.00, 'DEPOSIT', '2026-03-06 22:39:15.418123+01', 'Deposit to account', 'af75af25-e80a-48ae-9dd2-3cc33f7a6260', 'CREDIT', 5, 1, '\N');
INSERT INTO banking_transactions.transactions (id, amount, category, created_at, description, reference_id, type, user_id, account_id, counterparty_iban) VALUES (68, 10000.00, 'UTILITIES', '2026-03-07 12:31:31.101967+01', 'Internal Transfer to MicAccount 2', '31a28de4-9b0a-47d2-a04e-3141c89559e9', 'DEBIT', 5, 15, 'IT79NEXS695999735361278337');
INSERT INTO banking_transactions.transactions (id, amount, category, created_at, description, reference_id, type, user_id, account_id, counterparty_iban) VALUES (69, 10000.00, 'UTILITIES', '2026-03-07 12:31:31.15354+01', 'Internal Transfer to MicAccount 2', '31a28de4-9b0a-47d2-a04e-3141c89559e9', 'CREDIT', 5, 14, 'IT11NEXS632111781360241549');
INSERT INTO banking_transactions.transactions (id, amount, category, created_at, description, reference_id, type, user_id, account_id, counterparty_iban) VALUES (70, 10000.00, 'UTILITIES', '2026-03-07 12:32:21.687843+01', 'Gift', 'd0b5494f-3a8b-492a-abf7-015b3f44a895', 'DEBIT', 5, 15, 'IT79NEXS695999735361278337');
INSERT INTO banking_transactions.transactions (id, amount, category, created_at, description, reference_id, type, user_id, account_id, counterparty_iban) VALUES (71, 10000.00, 'UTILITIES', '2026-03-07 12:32:21.70057+01', 'Gift', 'd0b5494f-3a8b-492a-abf7-015b3f44a895', 'CREDIT', 5, 14, 'IT11NEXS632111781360241549');
INSERT INTO banking_transactions.transactions (id, amount, category, created_at, description, reference_id, type, user_id, account_id, counterparty_iban) VALUES (72, 5000.00, 'TRANSFER', '2026-03-07 12:34:56.30135+01', 'Payment to IT35NEXS568133426071117329', '765ed1e4-2341-4469-aba6-a8ea6438c024', 'DEBIT', 5, 15, 'IT35NEXS568133426071117329');
INSERT INTO banking_transactions.transactions (id, amount, category, created_at, description, reference_id, type, user_id, account_id, counterparty_iban) VALUES (73, 5000.00, 'TRANSFER', '2026-03-07 12:34:56.312638+01', 'Payment from IT11NEXS632111781360241549', '765ed1e4-2341-4469-aba6-a8ea6438c024', 'CREDIT', 11, 18, 'IT11NEXS632111781360241549');
INSERT INTO banking_transactions.transactions (id, amount, category, created_at, description, reference_id, type, user_id, account_id, counterparty_iban) VALUES (74, 623.00, 'TRANSFER', '2026-03-09 13:58:01.424402+01', 'First payment', '795dbbb2-7dac-485e-881a-c343ee88b261', 'DEBIT', 5, 14, 'IT65NEXS934350224722890982');
INSERT INTO banking_transactions.transactions (id, amount, category, created_at, description, reference_id, type, user_id, account_id, counterparty_iban) VALUES (75, 623.00, 'TRANSFER', '2026-03-09 13:58:01.457452+01', 'First payment', '795dbbb2-7dac-485e-881a-c343ee88b261', 'CREDIT', 5, 19, 'IT79NEXS695999735361278337');
INSERT INTO banking_transactions.transactions (id, amount, category, created_at, description, reference_id, type, user_id, account_id, counterparty_iban) VALUES (76, 23.00, 'UTILITIES', '2026-03-09 13:58:58.963621+01', 'Payment to IT55NEXS756603083054917901', '996e6113-f768-45ed-9a0e-e9ab56c8178c', 'DEBIT', 5, 19, 'IT55NEXS756603083054917901');
INSERT INTO banking_transactions.transactions (id, amount, category, created_at, description, reference_id, type, user_id, account_id, counterparty_iban) VALUES (77, 23.00, 'UTILITIES', '2026-03-09 13:58:58.973666+01', 'Payment from IT65NEXS934350224722890982', '996e6113-f768-45ed-9a0e-e9ab56c8178c', 'CREDIT', 6, 17, 'IT65NEXS934350224722890982');
INSERT INTO banking_transactions.transactions (id, amount, category, created_at, description, reference_id, type, user_id, account_id, counterparty_iban) VALUES (78, 23.00, 'UTILITIES', '2026-03-09 13:59:19.985625+01', 'Payment to IT55NEXS756603083054917901', 'eecc2aa5-cf20-4be0-94ce-c276cfe95965', 'DEBIT', 5, 19, 'IT55NEXS756603083054917901');
INSERT INTO banking_transactions.transactions (id, amount, category, created_at, description, reference_id, type, user_id, account_id, counterparty_iban) VALUES (79, 23.00, 'UTILITIES', '2026-03-09 13:59:19.999924+01', 'Payment from IT65NEXS934350224722890982', 'eecc2aa5-cf20-4be0-94ce-c276cfe95965', 'CREDIT', 6, 17, 'IT65NEXS934350224722890982');
INSERT INTO banking_transactions.transactions (id, amount, category, created_at, description, reference_id, type, user_id, account_id, counterparty_iban) VALUES (80, 77.00, 'TRANSFER', '2026-03-09 14:05:40.374853+01', 'February rent', 'cc51ff67-807c-47c1-bac5-a738fdeb3479', 'DEBIT', 5, 19, 'IT79NEXS695999735361278337');
INSERT INTO banking_transactions.transactions (id, amount, category, created_at, description, reference_id, type, user_id, account_id, counterparty_iban) VALUES (81, 77.00, 'TRANSFER', '2026-03-09 14:05:40.385024+01', 'February rent', 'cc51ff67-807c-47c1-bac5-a738fdeb3479', 'CREDIT', 5, 14, 'IT65NEXS934350224722890982');
INSERT INTO banking_transactions.transactions (id, amount, category, created_at, description, reference_id, type, user_id, account_id, counterparty_iban) VALUES (82, 100.00, 'TRANSFER', '2026-03-10 16:10:17.414395+01', 'Internal Transfer to MicAccount 3', '85826f25-6a72-4b3a-8713-d1543e5b67fb', 'DEBIT', 5, 5, 'IT65NEXS934350224722890982');
INSERT INTO banking_transactions.transactions (id, amount, category, created_at, description, reference_id, type, user_id, account_id, counterparty_iban) VALUES (83, 100.00, 'TRANSFER', '2026-03-10 16:10:17.48015+01', 'Internal Transfer to MicAccount 3', '85826f25-6a72-4b3a-8713-d1543e5b67fb', 'CREDIT', 5, 19, 'IT73NEXS892582929170888462');
INSERT INTO banking_transactions.transactions (id, amount, category, created_at, description, reference_id, type, user_id, account_id, counterparty_iban) VALUES (84, 998.00, 'UTILITIES', '2026-03-10 16:11:53.106945+01', 'RENT', 'c32b3634-8149-4625-8fe1-553801184100', 'DEBIT', 6, 6, 'IT87NEXS749563253715271001');
INSERT INTO banking_transactions.transactions (id, amount, category, created_at, description, reference_id, type, user_id, account_id, counterparty_iban) VALUES (85, 998.00, 'UTILITIES', '2026-03-10 16:11:53.126433+01', 'RENT', 'c32b3634-8149-4625-8fe1-553801184100', 'CREDIT', 5, 20, 'IT57NEXS704043974399150956');
INSERT INTO banking_transactions.transactions (id, amount, category, created_at, description, reference_id, type, user_id, account_id, counterparty_iban) VALUES (86, 98.00, 'TRANSFER', '2026-03-10 16:17:00.154947+01', 'Internal Transfer to MicAccount5', '0fc44139-37d0-4098-8877-7f2697ac1d95', 'DEBIT', 5, 20, 'IT68NEXS365639249649076887');
INSERT INTO banking_transactions.transactions (id, amount, category, created_at, description, reference_id, type, user_id, account_id, counterparty_iban) VALUES (87, 98.00, 'TRANSFER', '2026-03-10 16:17:00.175273+01', 'Internal Transfer to MicAccount5', '0fc44139-37d0-4098-8877-7f2697ac1d95', 'CREDIT', 5, 21, 'IT87NEXS749563253715271001');
INSERT INTO banking_transactions.transactions (id, amount, category, created_at, description, reference_id, type, user_id, account_id, counterparty_iban) VALUES (88, 81650.00, 'DEPOSIT', '2026-03-10 17:14:17.375935+01', 'Deposit to account', '377a9482-a191-4d8d-9bc6-37107df38691', 'CREDIT', 5, 5, '\N');
INSERT INTO banking_transactions.transactions (id, amount, category, created_at, description, reference_id, type, user_id, account_id, counterparty_iban) VALUES (89, 50.00, 'DEPOSIT', '2026-03-11 13:37:08.958098+01', 'Deposit to account', '17cbe234-e4c4-4a30-859b-ba086a76f073', 'CREDIT', 5, 5, '\N');
INSERT INTO banking_transactions.transactions (id, amount, category, created_at, description, reference_id, type, user_id, account_id, counterparty_iban) VALUES (90, 100.00, 'TRANSFER', '2026-03-11 13:37:14.854244+01', 'Internal Transfer to MicAccount 3', '8c5a39bb-e47c-4d17-9585-65164814df36', 'DEBIT', 5, 5, 'IT65NEXS934350224722890982');
INSERT INTO banking_transactions.transactions (id, amount, category, created_at, description, reference_id, type, user_id, account_id, counterparty_iban) VALUES (91, 100.00, 'TRANSFER', '2026-03-11 13:37:14.868634+01', 'Internal Transfer to MicAccount 3', '8c5a39bb-e47c-4d17-9585-65164814df36', 'CREDIT', 5, 19, 'IT73NEXS892582929170888462');
INSERT INTO banking_transactions.transactions (id, amount, category, created_at, description, reference_id, type, user_id, account_id, counterparty_iban) VALUES (92, 500.00, 'HEALTHCARE', '2026-03-11 13:37:30.691307+01', 'Doctor', 'e7fc1da7-5c32-4c46-ae9d-be8de1e74b4c', 'DEBIT', 5, 5, 'IT84NEXS020386725308339380');
INSERT INTO banking_transactions.transactions (id, amount, category, created_at, description, reference_id, type, user_id, account_id, counterparty_iban) VALUES (93, 500.00, 'HEALTHCARE', '2026-03-11 13:37:30.70317+01', 'Doctor', 'e7fc1da7-5c32-4c46-ae9d-be8de1e74b4c', 'CREDIT', 6, 16, 'IT73NEXS892582929170888462');
INSERT INTO banking_transactions.transactions (id, amount, category, created_at, description, reference_id, type, user_id, account_id, counterparty_iban) VALUES (94, 855.00, 'DEPOSIT', '2026-03-11 14:06:07.345079+01', 'Deposit to account', 'f4eae0ba-a3a6-4211-bc06-e43cfe13e2be', 'CREDIT', 5, 5, '\N');
INSERT INTO banking_transactions.transactions (id, amount, category, created_at, description, reference_id, type, user_id, account_id, counterparty_iban) VALUES (95, 522.00, 'ENTERTAINMENT', '2026-03-11 14:07:24.881007+01', 'Payment to IT57NEXS704043974399150956', 'e55008f0-df2d-4cc8-a65c-787671099a81', 'DEBIT', 5, 5, 'IT57NEXS704043974399150956');
INSERT INTO banking_transactions.transactions (id, amount, category, created_at, description, reference_id, type, user_id, account_id, counterparty_iban) VALUES (96, 522.00, 'ENTERTAINMENT', '2026-03-11 14:07:24.895724+01', 'Payment from IT73NEXS892582929170888462', 'e55008f0-df2d-4cc8-a65c-787671099a81', 'CREDIT', 6, 6, 'IT73NEXS892582929170888462');
INSERT INTO banking_transactions.transactions (id, amount, category, created_at, description, reference_id, type, user_id, account_id, counterparty_iban) VALUES (97, 33.00, 'TRANSFER', '2026-03-11 14:55:51.74234+01', 'Payment to IT35NEXS568133426071117329', '1bd97c57-e6a8-4f4d-b6ad-825d50150eae', 'DEBIT', 5, 5, 'IT35NEXS568133426071117329');
INSERT INTO banking_transactions.transactions (id, amount, category, created_at, description, reference_id, type, user_id, account_id, counterparty_iban) VALUES (98, 33.00, 'TRANSFER', '2026-03-11 14:55:51.77562+01', 'Payment from IT73NEXS892582929170888462', '1bd97c57-e6a8-4f4d-b6ad-825d50150eae', 'CREDIT', 11, 18, 'IT73NEXS892582929170888462');
INSERT INTO banking_transactions.transactions (id, amount, category, created_at, description, reference_id, type, user_id, account_id, counterparty_iban) VALUES (99, 300.00, 'TRANSFER', '2026-03-11 15:27:50.12259+01', 'Internal Transfer to MicAccount 3', '6abf9de3-be70-4dd3-8a8d-baed4b7d53b1', 'DEBIT', 5, 5, 'IT65NEXS934350224722890982');
INSERT INTO banking_transactions.transactions (id, amount, category, created_at, description, reference_id, type, user_id, account_id, counterparty_iban) VALUES (100, 300.00, 'TRANSFER', '2026-03-11 15:27:50.155693+01', 'Internal Transfer to MicAccount 3', '6abf9de3-be70-4dd3-8a8d-baed4b7d53b1', 'CREDIT', 5, 19, 'IT73NEXS892582929170888462');
INSERT INTO banking_transactions.transactions (id, amount, category, created_at, description, reference_id, type, user_id, account_id, counterparty_iban) VALUES (101, 16.99, 'TRANSFER', '2026-03-13 11:45:46.392224+01', 'Internal Transfer to MicAccount 1', 'dcf12a4a-634d-473a-bb18-4b97c1da718c', 'DEBIT', 5, 22, 'IT73NEXS892582929170888462');
INSERT INTO banking_transactions.transactions (id, amount, category, created_at, description, reference_id, type, user_id, account_id, counterparty_iban) VALUES (102, 16.99, 'TRANSFER', '2026-03-13 11:45:46.423618+01', 'Internal Transfer to MicAccount 1', 'dcf12a4a-634d-473a-bb18-4b97c1da718c', 'CREDIT', 5, 5, 'IT50NEXS419210706416462172');
INSERT INTO banking_transactions.transactions (id, amount, category, created_at, description, reference_id, type, user_id, account_id, counterparty_iban) VALUES (103, 16.99, 'TRANSFER', '2026-03-13 11:46:37.546593+01', 'Test new db', 'cd7426c2-66e0-4666-a9a3-8b19a56c535b', 'DEBIT', 5, 5, 'IT55NEXS756603083054917901');
INSERT INTO banking_transactions.transactions (id, amount, category, created_at, description, reference_id, type, user_id, account_id, counterparty_iban) VALUES (104, 16.99, 'TRANSFER', '2026-03-13 11:46:37.553202+01', 'Test new db', 'cd7426c2-66e0-4666-a9a3-8b19a56c535b', 'CREDIT', 6, 17, 'IT73NEXS892582929170888462');


--
-- Name: accounts_id_seq; Type: SEQUENCE SET; Schema: banking_accounts; Owner: -
--

SELECT pg_catalog.setval('banking_accounts.accounts_id_seq', 23, true);


--
-- Name: cards_id_seq; Type: SEQUENCE SET; Schema: banking_accounts; Owner: -
--

SELECT pg_catalog.setval('banking_accounts.cards_id_seq', 2, true);


--
-- Name: role_permissions_id_seq; Type: SEQUENCE SET; Schema: banking_auth; Owner: -
--

SELECT pg_catalog.setval('banking_auth.role_permissions_id_seq', 5, true);


--
-- Name: users_id_seq; Type: SEQUENCE SET; Schema: banking_auth; Owner: -
--

SELECT pg_catalog.setval('banking_auth.users_id_seq', 11, true);


--
-- Name: favorite_operations_id_seq; Type: SEQUENCE SET; Schema: banking_payments; Owner: -
--

SELECT pg_catalog.setval('banking_payments.favorite_operations_id_seq', 17, true);


--
-- Name: payments_id_seq; Type: SEQUENCE SET; Schema: banking_payments; Owner: -
--

SELECT pg_catalog.setval('banking_payments.payments_id_seq', 59, true);


--
-- Name: transactions_id_seq; Type: SEQUENCE SET; Schema: banking_transactions; Owner: -
--

SELECT pg_catalog.setval('banking_transactions.transactions_id_seq', 104, true);


--
-- Name: accounts accounts_iban_key; Type: CONSTRAINT; Schema: banking_accounts; Owner: -
--

ALTER TABLE ONLY banking_accounts.accounts
    ADD CONSTRAINT accounts_iban_key UNIQUE (iban);


--
-- Name: accounts accounts_pkey; Type: CONSTRAINT; Schema: banking_accounts; Owner: -
--

ALTER TABLE ONLY banking_accounts.accounts
    ADD CONSTRAINT accounts_pkey PRIMARY KEY (id);


--
-- Name: cards cards_card_number_key; Type: CONSTRAINT; Schema: banking_accounts; Owner: -
--

ALTER TABLE ONLY banking_accounts.cards
    ADD CONSTRAINT cards_card_number_key UNIQUE (card_number);


--
-- Name: cards cards_pkey; Type: CONSTRAINT; Schema: banking_accounts; Owner: -
--

ALTER TABLE ONLY banking_accounts.cards
    ADD CONSTRAINT cards_pkey PRIMARY KEY (id);


--
-- Name: role_permissions role_permissions_pkey; Type: CONSTRAINT; Schema: banking_auth; Owner: -
--

ALTER TABLE ONLY banking_auth.role_permissions
    ADD CONSTRAINT role_permissions_pkey PRIMARY KEY (id);


--
-- Name: users users_email_key; Type: CONSTRAINT; Schema: banking_auth; Owner: -
--

ALTER TABLE ONLY banking_auth.users
    ADD CONSTRAINT users_email_key UNIQUE (email);


--
-- Name: users users_pkey; Type: CONSTRAINT; Schema: banking_auth; Owner: -
--

ALTER TABLE ONLY banking_auth.users
    ADD CONSTRAINT users_pkey PRIMARY KEY (id);


--
-- Name: favorite_operations favorite_operations_pkey; Type: CONSTRAINT; Schema: banking_payments; Owner: -
--

ALTER TABLE ONLY banking_payments.favorite_operations
    ADD CONSTRAINT favorite_operations_pkey PRIMARY KEY (id);


--
-- Name: payments payments_pkey; Type: CONSTRAINT; Schema: banking_payments; Owner: -
--

ALTER TABLE ONLY banking_payments.payments
    ADD CONSTRAINT payments_pkey PRIMARY KEY (id);


--
-- Name: transactions transactions_pkey; Type: CONSTRAINT; Schema: banking_transactions; Owner: -
--

ALTER TABLE ONLY banking_transactions.transactions
    ADD CONSTRAINT transactions_pkey PRIMARY KEY (id);


--
-- PostgreSQL database dump complete
--

\unrestrict VaLPI5VeKbrknJv1T57YckJWMo4kyUAn9Tc1vMTYT4SbTfpDJutSoJv4scdbBeh

