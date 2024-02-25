CREATE TABLE User (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(255) NOT NULL,
    age INT
);

INSERT INTO User (name, age) VALUES
('John Doe', 25),
('Jane Smith', 30),
('Bob Johnson', 22),
('Alice Williams', 28);