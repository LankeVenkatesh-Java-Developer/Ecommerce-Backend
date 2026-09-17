-- Insert sample categories
INSERT IGNORE INTO categories (name, description, active, status, created_at, updated_at) VALUES
('Electronics', 'Electronic devices and accessories', true, 'ACTIVE', NOW(), NOW()),
('Clothing', 'Fashion and apparel', true, 'ACTIVE', NOW(), NOW()),
('Home & Kitchen', 'Home appliances and kitchen items', true, 'ACTIVE', NOW(), NOW()),
('Sports', 'Sports equipment and accessories', true, 'ACTIVE', NOW(), NOW()),
('Books', 'Books and educational materials', true, 'ACTIVE', NOW(), NOW());

-- Insert sample products
INSERT IGNORE INTO products (name, description, price, stock_quantity, sku, brand, category_id, status, image_url, created_at, updated_at) VALUES
('iPhone 15 Pro', 'Latest iPhone with A17 chip', 99999.00, 50, 'IPH-1234', 'Apple', 1, 'ACTIVE', 'https://images.unsplash.com/photo-1496181133206-80ce9b88a853?w=500', NOW(), NOW()),
('Samsung Galaxy S24', 'Premium Android smartphone', 89999.00, 30, 'SAM-5678', 'Samsung', 1, 'ACTIVE', 'https://images.unsplash.com/photo-1527864550417-7fd91fc51a46?w=500', NOW(), NOW()),
('MacBook Pro', 'Powerful laptop for professionals', 199999.00, 20, 'MAC-9012', 'Apple', 1, 'ACTIVE', 'https://images.unsplash.com/photo-1521572163474-6864f9cf17ab?w=500', NOW(), NOW()),
('Nike Air Max', 'Comfortable running shoes', 8999.00, 100, 'NIK-3456', 'Nike', 2, 'ACTIVE', 'https://images.unsplash.com/photo-1542272604-787c3835535d?w=500', NOW(), NOW()),
('Adidas T-Shirt', 'Cotton casual t-shirt', 1999.00, 150, 'ADI-7890', 'Adidas', 2, 'ACTIVE', 'https://images.unsplash.com/photo-1532012197267-da84d127e765?w=500', NOW(), NOW()),
('Coffee Maker', 'Automatic coffee machine', 5999.00, 40, 'KIT-2345', 'Philips', 3, 'ACTIVE', 'https://images.unsplash.com/photo-1517668808822-9ebb02f2a0e6?w=500', NOW(), NOW()),
('Blender', 'High-speed blender', 3999.00, 60, 'KIT-6789', 'Philips', 3, 'ACTIVE', 'https://images.unsplash.com/photo-1601925260368-ae2f83cf8b7f?w=500', NOW(), NOW()),
('Yoga Mat', 'Non-slip yoga mat', 1499.00, 200, 'SPT-0123', 'Nike', 4, 'ACTIVE', 'https://images.unsplash.com/photo-1511707171634-5f897ff02aa9?w=500', NOW(), NOW()),
('Dumbbells Set', 'Adjustable dumbbells', 4999.00, 50, 'SPT-4567', 'Reebok', 4, 'ACTIVE', 'https://images.unsplash.com/photo-1542291026-7eec264c27ff?w=500', NOW(), NOW()),
('Java Programming Book', 'Learn Java from scratch', 999.00, 80, 'BK-8901', 'OReilly', 5, 'ACTIVE', 'https://images.unsplash.com/photo-1570222094114-d054a817e56b?w=500', NOW(), NOW());

-- Update category image URLs
UPDATE categories SET image_url = 'https://images.unsplash.com/photo-1498049794561-7780e7231661?w=500', updated_at = NOW() WHERE id = 1;
UPDATE categories SET image_url = 'https://images.unsplash.com/photo-1445205170230-053b83016050?w=500', updated_at = NOW() WHERE id = 2;
UPDATE categories SET image_url = 'https://images.unsplash.com/photo-1512820790803-83ca734da794?w=500', updated_at = NOW() WHERE id = 3;
UPDATE categories SET image_url = 'https://images.unsplash.com/photo-1556909114-f6e7ad7d3136?w=500', updated_at = NOW() WHERE id = 4;
UPDATE categories SET image_url = 'https://images.unsplash.com/photo-1461896836934-ffe607ba8211?w=500', updated_at = NOW() WHERE id = 5;
