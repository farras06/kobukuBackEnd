package com.project.kobuku.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.project.kobuku.dao.CategoryRepo;
import com.project.kobuku.entity.Category;

@RestController
@RequestMapping("/category")
@CrossOrigin


public class CategoryController {
	
	@Autowired 
	private CategoryRepo categoryRepo;
	
	@GetMapping("/getCategory")
	private Iterable<Category> getCategory() {
		return categoryRepo.findAll();
	}
	
	@PostMapping("/addCategory")
	private Category addCategory(@RequestBody Category category) {
		category.setId(0);
		return categoryRepo.save(category);
	}
	
	@PutMapping("/editCategory/{id}")
	private Category editCategory (@RequestBody Category category, @PathVariable int id ) {
		Category findCategory = categoryRepo.findById(id).get();
		return categoryRepo.save(category);
	}
	
	@DeleteMapping ("/{id}") 
		public void deleteCategory(@PathVariable int id ) {
		Category findCategory = categoryRepo.findById(id).get();
		categoryRepo.deleteById(id);
		}
	
}
