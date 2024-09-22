
USE hospitality;

-- Create Hotels table
CREATE TABLE IF NOT EXISTS Hotels (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    location VARCHAR(255),
    amenities TEXT
);

-- Create Rooms table
CREATE TABLE IF NOT EXISTS Rooms (
    id INT AUTO_INCREMENT PRIMARY KEY,
    hotel_id INT NOT NULL,
    room_number VARCHAR(50) NOT NULL,
    type VARCHAR(50),
    price DECIMAL(10, 2),
    status ENUM('Available', 'Occupied') DEFAULT 'Available',
    FOREIGN KEY (hotel_id) REFERENCES Hotels(id) ON DELETE CASCADE
);

-- Create Guests table
CREATE TABLE IF NOT EXISTS Guests (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) UNIQUE,
    phone_number VARCHAR(20)
);

-- Create Reservations table
CREATE TABLE IF NOT EXISTS Reservations (
    id INT AUTO_INCREMENT PRIMARY KEY,
    guest_id INT NOT NULL,
    room_id INT NOT NULL,
    check_in_date DATE NOT NULL,
    check_out_date DATE NOT NULL,
    FOREIGN KEY (guest_id) REFERENCES Guests(id) ON DELETE CASCADE,
    FOREIGN KEY (room_id) REFERENCES Rooms(id) ON DELETE CASCADE
);

-- Insert sample data into Hotels table
INSERT INTO Hotels (name, location, amenities) VALUES
('Grand Hotel', 'New York', 'Free WiFi, Pool, Spa'),
('Ocean View Resort', 'Miami', 'Beachfront, Free Breakfast');

-- Insert sample data into Rooms table
INSERT INTO Rooms (hotel_id, room_number, type, price, status) VALUES
(1, '101', 'Single', 100.00, 'Available'),
(1, '102', 'Double', 150.00, 'Available'),
(2, '201', 'Suite', 250.00, 'Occupied');

-- Insert sample data into Guests table
INSERT INTO Guests (name, email, phone_number) VALUES
('John Doe', 'john.doe@example.com', '123-456-7890'),
('Jane Smith', 'jane.smith@example.com', '098-765-4321');

-- Insert sample data into Reservations table
INSERT INTO Reservations (guest_id, room_id, check_in_date, check_out_date) VALUES
(1, 1, '2024-10-01', '2024-10-07'),
(2, 3, '2024-09-20', '2024-09-25');
