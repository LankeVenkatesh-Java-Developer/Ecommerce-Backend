-- Insert/Update Admin User
-- Password: password (BCrypt hash - known working)
MERGE INTO users u
USING (SELECT 'admin@example.com' as email FROM dual) d
ON (u.email = d.email)
WHEN MATCHED THEN
    UPDATE SET password='$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi',
                role='ADMIN',
                status='ACTIVE',
                updated_at=SYSDATE
WHEN NOT MATCHED THEN
    INSERT (first_name, last_name, email, mobile_number, password, role, status, created_at, updated_at)
    VALUES ('Admin', 'User', 'admin@example.com', '9999999999', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'ADMIN', 'ACTIVE', SYSDATE, SYSDATE);
