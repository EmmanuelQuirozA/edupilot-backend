package com.monarchsolutions.sms.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Declarative permission guard that maps an endpoint to a module/action pair
 * stored in the database. Example usage:
 *
 * <pre>
 *     @RequirePermission(module = "students", action = "r")
 *     @GetMapping("/students/list")
 *     public List<StudentDTO> getStudents() { ... }
 * </pre>
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface RequirePermission {

    /**
     * Module key, e.g. "students", "payments", "finance".
     */
    String module();

    /**
     * CRUD action: c (create), r (read), u (update), d (delete).
     */
    String action();
}
