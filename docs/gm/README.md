# Eltratec GM

Eltratec GM is the middleware component responsible for communication with the external SWS (Smart Web Service).

It acts as a bridge between client applications and the SWS system, handling authentication, session management, and data exchange.

---

## Responsibilities

- Authenticate users via SWS
- Manage internal sessions and tokens
- Communicate with external SOAP service
- Expose REST API endpoints
- Handle logging and error responses

---

## Structure

- Routes – HTTP endpoints (Ktor)
- Services – business logic
- Clients – SWS communication (SOAP)
- Shared modules – authentication, models, logging

---

## API

### Base path

```
/api/v1
```

### Endpoint

```
POST /api/v1/auth/login
```

Authenticates user via SWS and returns a GM token.

---

## Authentication

Authentication is delegated to the external SWS system.

### Flow

1. Client sends credentials (username, password) to GM
2. GM sends SOAP request to SWS
3. SWS validates credentials
4. SWS returns:
    - login result
    - session cookie

5. GM:
    - generates internal token
    - stores session (token + SWS cookie)
    - returns token to client

### Token

- Generated using secure random bytes
- Encoded using Base64 (URL-safe)
- Has expiration time (TTL)

### Session Storage

Sessions are stored in memory.

Each session contains:

- GM token
- SWS cookie (name + value)
- username
- creation time
- expiration time

---

## SWS Integration

The GM communicates with the external Smart Web Service (SWS) using SOAP.

### Endpoint

```
https://smart.eltratec.com/SmartWebDev/SmartWebService.asmx
```

### Login Operation

SOAP Action:

```
http://www.eltratec.com/Login
```

### Request

```xml
<Login>
  <UserName>username</UserName>
  <Password>password</Password>
</Login>
```

Wrapped inside a SOAP envelope.

---

## Request Flow

1. Client sends HTTP request to GM
2. GM processes request
3. GM calls SWS (SOAP)
4. GM processes response
5. GM returns response to client