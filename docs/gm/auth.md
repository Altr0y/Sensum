# Authentication

## Overview

Authentication is delegated to the external SWS system.

GM does not validate credentials directly.

---

## Login Flow

1. Client sends username and password to GM
2. GM sends SOAP request to SWS
3. SWS returns:
    - login result
    - session cookie

4. GM:
    - generates token
    - stores session
    - returns token to client

---

## Token

- Generated using secure random bytes
- Encoded using Base64
- Has expiration time

---

## Session Storage

Sessions are stored in memory.

Each session contains:

- GM token
- SWS cookie (name + value)
- username
- creation time
- expiration time

---

## Expiration

- Sessions expire after defined TTL
- Expired sessions are removed on access  
