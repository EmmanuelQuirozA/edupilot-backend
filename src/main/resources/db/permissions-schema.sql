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
    name_en VARCHAR(100),
    name_es VARCHAR(100)
);

-- Permissions table
CREATE TABLE permissions (
    permission_id INT AUTO_INCREMENT PRIMARY KEY,
    role_id INT NOT NULL,
    module_id INT NOT NULL,
    c BOOL DEFAULT 0,
    r BOOL DEFAULT 0,
    u BOOL DEFAULT 0,
    d BOOL DEFAULT 0,
    FOREIGN KEY (role_id) REFERENCES roles(role_id),
    FOREIGN KEY (module_id) REFERENCES modules(module_id)
);
