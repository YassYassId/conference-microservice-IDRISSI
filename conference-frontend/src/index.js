// src/index.js
import React from 'react';
import ReactDOM from 'react-dom/client';
import App from './App';
import keycloak from './keycloak';
import { ReactKeycloakProvider } from '@react-keycloak/web';

const root = ReactDOM.createRoot(document.getElementById('root'));

root.render(
    <ReactKeycloakProvider
        authClient={keycloak}
        initOptions={{
            onLoad: 'login-required',   // Forces login on app start
            pkceMethod: 'S256',
            checkLoginIframe: false,
        }}
    >
        <React.StrictMode>
            <App />
        </React.StrictMode>
    </ReactKeycloakProvider>
);