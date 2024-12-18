-- Create User Table
CREATE TABLE User (
    user_id INT AUTO_INCREMENT PRIMARY KEY,
    first_name VARCHAR(255) NOT NULL,
    last_name VARCHAR(255) NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    active BOOLEAN DEFAULT TRUE,
    type ENUM('VISITOR', 'NORMAL', 'ADMIN', 'SUPERADMIN') NOT NULL,
    profile_image_url VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Insert Admin User
INSERT INTO User (first_name, last_name, email, active, type, profile_image_url)
VALUES 
('Admin', 'User', 'admin@admin.com', TRUE, 'ADMIN', 'https://pbcdn1.podbean.com/imglogo/image-logo/4389756/picture-1-1505503146.png');

-- Insert SuperAdmin User
INSERT INTO User (first_name, last_name, email, active, type, profile_image_url)
VALUES 
('SuperAdmin', 'User', 'superadmin@superadmin.com', TRUE, 'SUPERADMIN', 'https://pbcdn1.podbean.com/imglogo/image-logo/4389756/picture-1-1505503146.png');
