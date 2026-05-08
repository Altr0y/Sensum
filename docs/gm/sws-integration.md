# SWS Integration

## Overview

The system integrates with SWS (Smart Web Service) using SOAP.

---

## Endpoint

```
https://smart.eltratec.com/SmartWebDev/SmartWebService.asmx
```

---

## Login

### SOAP Action

```
http://www.eltratec.com/Login
```

---

## Request

```xml
<Login>
  <UserName>username</UserName>
  <Password>password</Password>
</Login>
```

Wrapped inside SOAP envelope.

---

## Response

Contains:

- LoginResult: true or false  
- Set-Cookie header: session identifier

---

## Session Handling

The session is extracted from the Set-Cookie header:

```
cookieName=cookieValue
```

Stored and reused for future requests.

---

## Error Handling

- HTTP 401 → invalid credentials
- Invalid SOAP response → parsing error
- Missing cookie → invalid response  
