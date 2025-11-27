# Dynamic permission authorization (JWT + DB)

## SQL bootstrap
The schema for roles/modules/permissions is stored in `db/permissions-schema.sql` and matches:

```sql
roles(role_id, name, description)
modules(module_id, key, name, description)
permissions(permission_id, role_id, module_id, can_create, can_read, can_update, can_delete)
```

## JWT payload example
The backend expects `roleId` in the token claims (in addition to other fields). A sample payload looks like:

```json
{
  "sub": "user@example.com",
  "role": "FINANCE",
  "roleId": 3,
  "userId": 42,
  "schoolId": 7,
  "iat": 1710000000,
  "exp": 1710604800
}
```

## Custom annotation
Annotate controllers or methods with `@RequirePermission(module = "students", action = "r")`. The `action` field accepts `c`, `r`, `u`, `d` and maps to the CRUD flags in the `permissions` table.

## Interceptor behavior
1. Extracts the Bearer token from the `Authorization` header.
2. Uses `jwtUtil.extractRoleId(token)` to obtain `role_id`.
3. Loads the module by `key` and validates the matching permission row.
4. Returns HTTP **403 Forbidden** with a JSON body when the role does not have the required action for the module. Missing/expired tokens return **401 Unauthorized**.

Example 403 response:

```json
{
  "timestamp": "2024-01-01T12:00:00Z",
  "status": 403,
  "error": "Forbidden",
  "message": "Role 3 lacks 'u' permission on module 'payments'"
}
```

## End-to-end flow demo
* Endpoint: `@RequirePermission(module = "payments", action = "u")`
* Token: contains `"roleId": 3`
* Database: permissions row for role **3** and module **payments** has `can_update = 0`

Result: interceptor answers with **403 Forbidden** and the JSON body shown above.

## Extending to record-level permissions (optional)
You can extend `PermissionService` to accept an additional resource identifier and look it up in a new table that stores `role_id`, `module_id`, `resource_id`, and CRUD flags. The interceptor would pass the resource id (from path variable or request attribute) to the service and deny access if the specific row is missing.
