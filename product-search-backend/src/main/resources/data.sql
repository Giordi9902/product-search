INSERT INTO products(id, name, category, price, description, image_path) VALUES
(1, 'Wireless Mouse', 'ELECTRONICS', 25.99, 'A high-precision wireless mouse with ergonomic design.',
 '/uploads/products/wireless_mouse.jpg'),
(2, 'Bluetooth Headphones', 'ELECTRONICS', 59.99, 'Noise-cancelling over-ear headphones with Bluetooth connectivity.',
 '/uploads/products/bluetooth_headphones.jpg'),
(3, 'Smartphone Stand', 'ACCESSORIES', 12.49, 'Adjustable stand for smartphones and tablets.',
 '/uploads/products/smartphone_stand.jpg'),
(4, 'USB-C Charger', 'ELECTRONICS', 19.99, 'Fast-charging USB-C wall charger compatible with various devices.',
 '/uploads/products/usb_c_charger.jpg'),
(5, 'Gaming Keyboard', 'ELECTRONICS', 89.99, 'Mechanical keyboard with customizable RGB lighting and programmable keys.',
 '/uploads/products/gaming_keyboard.jpg'),
(6, 'Fitness Tracker', 'ACCESSORIES', 49.99, 'Water-resistant fitness tracker with heart rate monitor and sleep tracking.',
 '/uploads/products/fitness_tracker.jpg'),
(7, '4K Action Camera', 'ELECTRONICS', 129.99, 'Compact action camera with 4K video recording and waterproof casing.',
 '/uploads/products/4k_action_camera.jpg'),
(8, 'Portable Bluetooth Speaker', 'ELECTRONICS', 39.99, 'Compact speaker with powerful sound and long battery life.',
 '/uploads/products/portable_bluetooth_speaker.jpg'),
(9, 'E-Reader', 'ELECTRONICS', 79.99, 'Lightweight e-reader with adjustable backlight and high-resolution display.',
 '/uploads/products/e_reader.jpg'),
(10, 'Smart Home Hub', 'HOME_APPLIANCES', 99.99, 'Central hub for controlling smart home devices with voice commands.',
 '/uploads/products/smart_home_hub.jpg'),
(11, 'Wireless Charging Pad', 'ACCESSORIES', 29.99, 'Fast wireless charging pad compatible with Qi-enabled devices.',
 '/uploads/products/wireless_charging_pad.jpg'),
(12, 'Laptop Sleeve', 'ACCESSORIES', 15.99, 'Protective sleeve for laptops up to 15 inches.',
 '/uploads/products/laptop_sleeve.jpg'),
(13, 'Digital Photo Frame', 'ELECTRONICS', 59.99, 'Wi-Fi enabled digital photo frame with slideshow feature.',
 '/uploads/products/digital_photo_frame.jpg'),
(14, 'Smartwatch', 'ACCESSORIES', 199.99, 'Feature-rich smartwatch with fitness tracking and notification support.',
 '/uploads/products/smartwatch.jpg'),
(15, 'Noise-Cancelling Earbuds', 'ELECTRONICS', 79.99, 'In-ear earbuds with active noise cancellation and superior sound quality.',
 '/uploads/products/noise_cancelling_earbuds.jpg')
ON CONFLICT (id) DO NOTHING;

-- Hibernate creates products.id as an IDENTITY column, which owns its sequence.
-- After inserting explicit ids, move the sequence forward so the next product gets MAX(id) + 1.
SELECT setval(pg_get_serial_sequence('products', 'id'), (SELECT COALESCE(MAX(id), 1) FROM products));