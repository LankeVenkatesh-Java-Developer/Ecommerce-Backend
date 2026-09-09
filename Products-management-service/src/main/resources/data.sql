-- Update category image URLs (will be applied after Hibernate adds the column)
UPDATE categories SET image_url = 'https://images.unsplash.com/photo-1498049794561-7780e7231661?w=500', updated_at = NOW() WHERE id = 1;
UPDATE categories SET image_url = 'https://images.unsplash.com/photo-1445205170230-053b83016050?w=500', updated_at = NOW() WHERE id = 2;
UPDATE categories SET image_url = 'https://images.unsplash.com/photo-1512820790803-83ca734da794?w=500', updated_at = NOW() WHERE id = 3;
UPDATE categories SET image_url = 'https://images.unsplash.com/photo-1556909114-f6e7ad7d3136?w=500', updated_at = NOW() WHERE id = 4;
UPDATE categories SET image_url = 'https://images.unsplash.com/photo-1461896836934-ffe607ba8211?w=500', updated_at = NOW() WHERE id = 5;

-- Update product image URLs
UPDATE products SET image_url = 'https://images.unsplash.com/photo-1496181133206-80ce9b88a853?w=500', updated_at = NOW() WHERE id = 1;
UPDATE products SET image_url = 'https://images.unsplash.com/photo-1527864550417-7fd91fc51a46?w=500', updated_at = NOW() WHERE id = 2;
UPDATE products SET image_url = 'https://images.unsplash.com/photo-1521572163474-6864f9cf17ab?w=500', updated_at = NOW() WHERE id = 3;
UPDATE products SET image_url = 'https://images.unsplash.com/photo-1542272604-787c3835535d?w=500', updated_at = NOW() WHERE id = 4;
UPDATE products SET image_url = 'https://images.unsplash.com/photo-1532012197267-da84d127e765?w=500', updated_at = NOW() WHERE id = 5;
UPDATE products SET image_url = 'https://images.unsplash.com/photo-1517668808822-9ebb02f2a0e6?w=500', updated_at = NOW() WHERE id = 6;
UPDATE products SET image_url = 'https://images.unsplash.com/photo-1601925260368-ae2f83cf8b7f?w=500', updated_at = NOW() WHERE id = 7;
UPDATE products SET image_url = 'https://images.unsplash.com/photo-1511707171634-5f897ff02aa9?w=500', updated_at = NOW() WHERE id = 8;
UPDATE products SET image_url = 'https://images.unsplash.com/photo-1542291026-7eec264c27ff?w=500', updated_at = NOW() WHERE id = 9;
UPDATE products SET image_url = 'https://images.unsplash.com/photo-1570222094114-d054a817e56b?w=500', updated_at = NOW() WHERE id = 10;
