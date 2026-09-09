-- Delete existing admin user to avoid conflicts
DELETE FROM users WHERE email = 'admin@example.com';

-- Insert Admin User
-- Password: password (BCrypt encoded - known working hash)
INSERT INTO users (first_name, last_name, email, mobile_number, password, role, status, created_at, updated_at)
VALUES ('Admin', 'User', 'admin@example.com', '9999999999', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'ADMIN', 'ACTIVE', NOW(), NOW());
