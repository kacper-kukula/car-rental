INSERT INTO users(id, email, password, first_name, last_name, role, is_deleted)
VALUES (888, 'testmanager@rental.com', '$2a$10$hLSU4qNrQO6lItn7H/cALuq0YvqLUmAVZ5qlKLsmigSV7FberE8v2', 'John', 'Doe',
        'MANAGER', 0),
       (999, 'testcustomer@rental.com', '$2a$10$hLSU4qNrQO6lItn7H/cALuq0YvqLUmAVZ5qlKLsmigSV7FberE8v2', 'Alice', 'Doe',
        'CUSTOMER', 0);