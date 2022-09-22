DELETE FROM payment;

INSERT INTO `payment` (`id`, `user_id`, `amount`, `status`) VALUES
(1, 'user-id-1', 1, 'NEW'),
(2, 'user-id-2', 1, 'PROCESSED'),
(3, 'user-id-3', 1, 'PROCESSED'),
(4, 'user-id-1', 1, 'PROCESSED');