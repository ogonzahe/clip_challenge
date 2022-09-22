DELETE FROM payment;

INSERT INTO `payment` (`id`, `user_id`, `amount`, `status`) VALUES
(1, 'user-id-1', 5.50, 'NEW'),
(2, 'user-id-1', 7.30, 'NEW'),
(3, 'user-id-2', 3.20, 'PROCESSED'),
(4, 'user-id-2', 1.25, 'NEW'),
(5, 'user-id-2', 25.99, 'NEW'),
(6, 'user-id-1', 10.88, 'PROCESSED');