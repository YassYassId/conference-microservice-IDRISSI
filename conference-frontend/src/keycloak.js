// src/keycloak.js
import Keycloak from 'keycloak-js';

const keycloak = new Keycloak({
    url: 'http://localhost:8080',           // CHANGE IF YOUR KEYCLOAK IS ON DIFFERENT PORT/HOST
    realm: 'conference-realm',              // CHANGE TO YOUR ACTUAL REALM NAME
    clientId: 'react-frontend',
});

export default keycloak;