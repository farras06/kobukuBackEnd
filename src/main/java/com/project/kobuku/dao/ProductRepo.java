package com.project.kobuku.dao;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import com.project.kobuku.entity.Product;


public interface ProductRepo extends JpaRepository<Product, Integer>,PagingAndSortingRepository<Product, Integer> {
	public Product findByProductName(String productName);
	
	//public Product findByCategory(String category);
	
		@Query(value = "SELECT * FROM Product WHERE category = ?", nativeQuery = true)
		public Iterable<Product> findByCategory(String category);
		
		@Query(value = "SELECT * FROM Product WHERE price <= ?", nativeQuery = true)
		public Iterable<Product> findProductByMaxPrice(double maxPrice);
		
		@Query(value = "select * from product where price >=?1 and price <= ?2 and product_name like %?3% order by product_name asc",nativeQuery = true)
		public Page<Product> findProductByPriceOrderByProductNameAsc(double minPrice, double maxPrice, String productName, Pageable pageable);

		@Query(value = "select * from product where price >=?1 and price <= ?2 and product_name like %?3% order by product_name desc",nativeQuery = true)
		public Page<Product> findProductByPriceOrderByProductNameDesc(double minPrice, double maxPrice, String productName,Pageable pageable );

		@Query(value = "select * from product where price >=?1 and price <= ?2 and product_name like %?3% order by price asc",nativeQuery = true)
		public Page<Product> findProductByPriceOrderByPriceAsc(double minPrice, double maxPrice, String productName, Pageable pageable);
		
		@Query(value = "select * from product where price >=?1 and price <= ?2 and product_name like %?3% order by price desc",nativeQuery = true)
		public Page<Product> findProductByPriceOrderByPriceDesc(double minPrice, double maxPrice, String productName, Pageable pageable);

		@Query(value = "select * from product where price >=?1 and price <= ?2 and product_name like %?3% order by sold asc",nativeQuery = true)
		public Page<Product> findProductByPriceOrderBySoldAsc(double minPrice, double maxPrice, String productName, Pageable pageable);
		
		@Query(value = "select * from product where price >=?1 and price <= ?2 and product_name like %?3% order by sold desc",nativeQuery = true)
		public Page<Product> findProductByPriceOrderBySoldDesc(double minPrice, double maxPrice, String productName, Pageable pageable);
		
		@Query(value = "select * from product order by sold desc limit 4",nativeQuery = true)
		public Iterable<Product> findProductCarousel();


		//Product with Categories FILTER AND SORT

		@Query(value = "select * from product_category pc join product p on p.id = pc.product_id join category c on c.id = pc.category_id where p.price>=?1 and p.price<= ?2 and p.product_name like %?3% and c.category_name=?4 order by product_name asc", nativeQuery = true)
		public Page<Product> findProductCategoryByPriceOrderByProductNameAsc(double minPrice, double maxPrice, String productName, String categoryName, Pageable pageable);

		@Query(value = "select * from product_category pc join product p on p.id = pc.product_id join category c on c.id = pc.category_id where p.price>=?1 and p.price<= ?2 and p.product_name like %?3% and c.category_name=?4 order by product_name desc", nativeQuery = true)
		public Page<Product> findProductCategoryByPriceOrderByProductNameDesc(double minPrice, double maxPrice, String productName, String categoryName, Pageable pageable);

		@Query(value = "select * from product_category pc join product p on p.id = pc.product_id join category c on c.id = pc.category_id where p.price>=?1 and p.price<= ?2 and p.product_name like %?3% and c.category_name=?4 order by price asc", nativeQuery = true)
		public Page<Product> findProductCategoryByPriceOrderByPriceAsc(double minPrice, double maxPrice, String productName, String categoryName, Pageable pageable);

		@Query(value = "select * from product_category pc join product p on p.id = pc.product_id join category c on c.id = pc.category_id where p.price>=?1 and p.price<= ?2 and p.product_name like %?3% and c.category_name=?4 order by price desc", nativeQuery = true)
		public Page<Product> findProductCategoryByPriceOrderByPriceDesc(double minPrice, double maxPrice, String productName, String categoryName, Pageable pageable);

		@Query(value = "select * from product_category pc join product p on p.id = pc.product_id join category c on c.id = pc.category_id where p.price>=?1 and p.price<= ?2 and p.product_name like %?3% and c.category_name=?4 order by sold asc", nativeQuery = true)
		public Page<Product> findProductCategoryByPriceOrderBySoldAsc(double minPrice, double maxPrice, String productName, String categoryName, Pageable pageable);

		@Query(value = "select * from product_category pc join product p on p.id = pc.product_id join category c on c.id = pc.category_id where p.price>=?1 and p.price<= ?2 and p.product_name like %?3% and c.category_name=?4 order by sold desc", nativeQuery = true)
		public Page<Product> findProductCategoryByPriceOrderBySoldDesc(double minPrice, double maxPrice, String productName, String categoryName, Pageable pageable);

}