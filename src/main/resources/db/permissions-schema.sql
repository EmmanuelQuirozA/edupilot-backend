-- Roles table
CREATE TABLE roles (
    role_id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100),
    description VARCHAR(255)
);

-- Modules table
CREATE TABLE modules (
    module_id INT AUTO_INCREMENT PRIMARY KEY,
    `key` VARCHAR(100) UNIQUE,
    name VARCHAR(100),
    description VARCHAR(255)
);

-- Permissions table
CREATE TABLE permissions (
    permission_id INT AUTO_INCREMENT PRIMARY KEY,
    role_id INT NOT NULL,
    module_id INT NOT NULL,
    can_create BOOL DEFAULT 0,
    can_read   BOOL DEFAULT 0,
    can_update BOOL DEFAULT 0,
    can_delete BOOL DEFAULT 0,
    FOREIGN KEY (role_id) REFERENCES roles(role_id),
    FOREIGN KEY (module_id) REFERENCES modules(module_id)
);
