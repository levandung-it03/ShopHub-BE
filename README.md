# Biz
- One `Admin`
- Multiple `User`
- Small **_Ecommerce_**
- Notification + Billing base on Email.

# Environment
## 1. Repository usage:
- Injecting directly to avoid Circle-Injecting Components in Spring context.

# Endpoints
## 1. Structures:
```md
/api: rest-api prefix
/private: scope [private/public]
/admin: role [admin/user/auth/None]
/order: entity or service [auth/entity]
/search: detailed action [detailed-action/None]
?parm=value: parameters/value for body based URL.
```

## 2. Cases:
| Details                      | Endpoint Example                          |
|:-----------------------------|:------------------------------------------|
| Public                       | `POST` `/api/public/sign-in`              |
| All private (auth=authzed)   | `PUT` `/api/private/auth/refresh-token`   |
| Following RestAPI convention | `GET` `/api/private/admin/user-profile/1` |

