insert into client (id, username, password, email, role)
values (1, 'admin', '$2a$10$bkUC.4wJKBxvy6/3uK.5ceoZEyNlesxsqNBFKCJOhg9a/pisJnUVy', 'admin@example.com', 'ADMIN'), --  admin
       (2, 'user1', '$2a$10$TvCQ7wnkWJA2wb8ga83H2u52RS6qPFUn9KKhbDQVayeR3W9rZP/Re', 'user1@example.com', 'USER'), --  password
       (3, 'user2', '$2a$10$TvCQ7wnkWJA2wb8ga83H2u52RS6qPFUn9KKhbDQVayeR3W9rZP/Re', 'user2@example.com', 'USER'), --  password
       (4, 'user3', '$2a$10$TvCQ7wnkWJA2wb8ga83H2u52RS6qPFUn9KKhbDQVayeR3W9rZP/Re', 'user3@example.com', 'USER'), --  password
       (5, 'user4', '$2a$10$TvCQ7wnkWJA2wb8ga83H2u52RS6qPFUn9KKhbDQVayeR3W9rZP/Re', 'user4@example.com', 'USER'), --  password
       (6, 'user5', '$2a$10$TvCQ7wnkWJA2wb8ga83H2u52RS6qPFUn9KKhbDQVayeR3W9rZP/Re', 'user5@example.com', 'USER'); --  password

insert into card (id, number, owner_id, validity_period, status, balance)
values (1, '0izUPjnwxe9RC5tZbS4ERDPL6peTvCOw6QygCWpZf4rj7HPqgdjK+w==', 2, '12/29', 'ACTIVE', 150000), -- 410000000001 user1
       (2, 'dHGgzVVbtrd0gVjQOHcJvrx26BqRQYq+KH4gMYkDGuTp83ZYpF8igQ==', 3, '11/28', 'ACTIVE', 50000), -- 410000000002 user2
       (3, '7tF/N/ol+knUlzXqdfXmCiaKWfO/dOFGHPlHQiSlxKHO5iHYODVFfQ==', 3, '10/30', 'ACTIVE', 2500000), -- 410000000003 user2
       (4, 'zYMnqMMay6Dr/LW3P/MK1uAh9/MkiszXTYE2Zfr5FW9PcGFPIbDy+g==', 4, '09/27', 'ACTIVE', 0), -- 410000000004 user3
       (5, 'T6SdHI6sWMgtwpfa3TIg0NW3yLKaTkHGz0JCRnvA7/s6t44N4A0hXA==', 5, '08/31', 'ACTIVE', 99999), -- 410000000005 user4
       (6, 'oFi7yVG5Or0QpJ+sJojT/946wBtkgUU8hGykidAJq7BJxQddAECgJw==', 6, '07/26', 'ACTIVE', 10000), -- 410000000006 user5
       (7, 'VyhnhIf2Ii9yP5WeHC5bx0BR5f/XwN2vNQhXXIwuRTi8h4tHX6VMXA==', 6, '06/32', 'ACTIVE', 20000), -- 410000000007 user5
       (8, 'InsSeedUser2Extra01AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA==', 3, '05/30', 'ACTIVE', 75000), -- 410000000008 user2
       (9, 'InsSeedUser2Extra02BBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBB==', 3, '04/29', 'ACTIVE', 12000), -- 410000000009 user2
       (10, 'InsSeedUser4Extra01CCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCC==', 5, '03/28', 'ACTIVE', 5000); -- 410000000010 user4

select setval('client_seq', (select max(id) from client));
select setval('card_seq', (select max(id) from card));
