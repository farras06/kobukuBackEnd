package com.project.kobuku.controller;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.project.kobuku.dao.CategoryRepo;
import com.project.kobuku.dao.PagingRepo;
import com.project.kobuku.dao.ProductRepo;
import com.project.kobuku.entity.Category;
import com.project.kobuku.entity.Product;

@RestController
@RequestMapping("/products")
@CrossOrigin

public class ProductController {

	@Autowired
	private ProductRepo productRepo;
	
	@Autowired
	private CategoryRepo categoryRepo;
	
	@Autowired
    private PagingRepo pagingRepo;
	
	@GetMapping("/getProduct")
	public Iterable <Product> getProduct() {
		return productRepo.findAll();
	}
	
	@GetMapping("/getProduct/carousel")
	public Iterable <Product> getProductCarousel() {
		return productRepo.findProductCarousel();
	}
	
	
	@GetMapping("/getProductCard/{productId}")
	public Optional<Product> getProductCard(@PathVariable int productId) {
		return productRepo.findById(productId);
	}
	
	@PostMapping("/addProduct")
	public Product addProduct (@RequestBody Product product) {
		product.setId(0);
		product.setSold(0);
		product.setStockUser(product.getStockStorage());
		return productRepo.save(product);
	}
	
	@PutMapping("/editProduct/{productId}") 
	public Product editProduct (@RequestBody Product product, @PathVariable int productId) {
		Product findProduct = productRepo.findById(productId).get();
		product.setId(productId);
		product.setStockUser(product.getStockStorage());
		product.setCategory(findProduct.getCategory());
		return productRepo.save(product);
	}
	
	@DeleteMapping("/deleteProduct/{id}") 
		public void deleteProduct(@PathVariable int id) {
			Optional <Product> findProduct = productRepo.findById(id);
			productRepo.deleteById(id);
		}
	
	@PutMapping("/{productId}/category/{categoryId}")
		public Product addProductToCategories (@PathVariable int productId,@PathVariable int categoryId ) {
		Product findProduct = productRepo.findById(productId).get();
		Category findCategory = categoryRepo.findById(categoryId).get();
		
		if (findProduct.getCategory().contains(findCategory)) {
			throw new RuntimeException("Product already Added to Category");
		}
		findProduct.getCategory().add(findCategory);
		return productRepo.save(findProduct);
		
		
	}
	
	@DeleteMapping("/{productId}/deleteCategories/{categoryId}")
	public Product deleteCategoriesFromProduct (@PathVariable int productId, @PathVariable int categoryId) {
		Product findProduct = productRepo.findById(productId).get();
		Category findCategory = categoryRepo.findById(categoryId).get();
		
		findProduct.getCategory().remove(findCategory);
		return productRepo.save(findProduct);
	}
	
	@GetMapping("/{minPrice}/{maxPrice}/{orderBy}/{sortList}")
	public Iterable<Product> findProductByPrice
		(
			@PathVariable double minPrice, 
			@PathVariable double maxPrice, 
			@RequestParam String productName,
			@PathVariable String orderBy, 
			@PathVariable String sortList,
			Pageable pageable
		){
		if (maxPrice == 0) {
			maxPrice = 9999999;
		}
			if (orderBy.equals("productName") && sortList.equals("asc")) {
				return productRepo.findProductByPriceOrderByProductNameAsc(minPrice, maxPrice, productName, pageable);
			}else if (orderBy.equals("productName") && sortList.equals("desc")) {
				return productRepo.findProductByPriceOrderByProductNameDesc(minPrice, maxPrice, productName, pageable);
			}else if (orderBy.equals("price") && sortList.equals("asc")) {
				return productRepo.findProductByPriceOrderByPriceAsc(minPrice, maxPrice, productName, pageable);
			}else if (orderBy.equals("price") && sortList.equals("desc")){
				return productRepo.findProductByPriceOrderByPriceDesc(minPrice, maxPrice, productName, pageable);
			}else if (orderBy.equals("sold") && sortList.equals("asc")) {
				return productRepo.findProductByPriceOrderBySoldAsc(minPrice, maxPrice, productName, pageable);
			}else  {
				return productRepo.findProductByPriceOrderBySoldDesc(minPrice, maxPrice, productName, pageable);
			}
		}
	
	@GetMapping("/{minPrice}/category/{maxPrice}/{orderBy}/{sortList}")
	public Iterable<Product> findProductWithFilter
			(
			@PathVariable double minPrice, 
			@PathVariable double maxPrice, 
			@RequestParam String productName, 
			@RequestParam String categoryName, 
			@PathVariable String orderBy, 
			@PathVariable String sortList,
			Pageable pageable
			){
		
		if (maxPrice == 0) {
			maxPrice = 9999999;
		}
			if (orderBy.equals("productName") && sortList.equals("asc")) {
				return productRepo.findProductCategoryByPriceOrderByProductNameAsc(minPrice, maxPrice, productName, categoryName, pageable);
			}
			else if (orderBy.equals("productName") && sortList.equals("desc")) {
				return productRepo.findProductCategoryByPriceOrderByProductNameDesc(minPrice, maxPrice, productName, categoryName, pageable);
			}
			else if (orderBy.equals("price") && sortList.equals("asc")) {
				return productRepo.findProductCategoryByPriceOrderByPriceAsc(minPrice, maxPrice, productName, categoryName, pageable);
			}else if (orderBy.equals("price") && sortList.equals("desc")){
				return productRepo.findProductCategoryByPriceOrderByPriceDesc(minPrice, maxPrice, productName, categoryName, pageable);
			}else if (orderBy.equals("sold") && sortList.equals("asc")) {
				return productRepo.findProductCategoryByPriceOrderBySoldAsc(minPrice, maxPrice, productName, categoryName, pageable);
			}else  {
				return productRepo.findProductCategoryByPriceOrderBySoldDesc(minPrice, maxPrice, productName, categoryName, pageable);
			}
		}
	
	@GetMapping("/pages")
	 public Page<Product> getAllProduct(Pageable pageable) {
	    return pagingRepo.findAll(pageable);
	 }
	
	
}
