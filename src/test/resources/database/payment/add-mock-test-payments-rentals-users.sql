INSERT INTO users(id, email, password, first_name, last_name, role, is_deleted)
VALUES (999, 'testmanager@rental.com', '$2a$10$hLSU4qNrQO6lItn7H/cALuq0YvqLUmAVZ5qlKLsmigSV7FberE8v2', 'John', 'Doe',
        'MANAGER', 0);

INSERT INTO cars(id, model, brand, type, inventory, daily_fee, is_deleted)
VALUES (888, 'XM', 'BMW', 'SUV', 2, 400.00, 0);

INSERT INTO rentals(id, car_id, user_id, rental_date, return_date, actual_return_date, status, is_deleted)
VALUES (777, 888, 999, '2024-06-01', '2024-06-08', null, 'ACTIVE', 0);

INSERT INTO payments(id, rental_id, session_url, session_id, amount_to_pay, status, type, is_deleted)
VALUES (666, 777, 'url', 'id', 2800.00, 'PENDING', 'PAYMENT', 0);
