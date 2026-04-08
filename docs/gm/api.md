# GM API

## Base URL

```
/api/v1
```

---

## POST /auth/login

Authenticate user via SWS.

---

## Request

```json
{
  "username": "string",
  "password": "string"
}
```

---

## Response (200 OK)

```json
{
  "token": "string",
  "expiresAt": "string"
}
```

---

## Errors

### 401 Unauthorized

```json
{
  "error": "Unauthorized"
}
```

### 502 Bad Gateway

```json
{
  "error": "Invalid response from SWS"
}
```

### 500 Internal Server Error

```json
{
  "error": "Internal server error"
}
```
