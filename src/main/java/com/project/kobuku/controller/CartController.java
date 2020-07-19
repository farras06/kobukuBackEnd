package com.project.kobuku.controller;

import java.util.Optional;

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

import com.project.kobuku.dao.CartRepo;
import com.project.kobuku.dao.ProductRepo;
import com.project.kobuku.dao.UserRepo;
import com.project.kobuku.entity.Cart;
import com.project.kobuku.entity.Product;
import com.project.kobuku.entity.User;

@RestController
@RequestMapping("/cart")
@CrossOrigin
public class CartController {

    @Autowired
    private ProductRepo productRepo;

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private CartRepo cartRepo;

    @GetMapping
    public Iterable<Cart> getAllCart(){
        return cartRepo.findAll();
    }

    @GetMapping("/userCart/{userId}")
    public Iterable<Cart> getUserCarts(@PathVariable int userId){	
        return cartRepo.findByUserId(userId);
    }
    
    
    @DeleteMapping("/deleteCart/{cartId}")
    public void deleteCartById(@PathVariable int cartId) {
    	Cart findCart = cartRepo.findById(cartId).get();
    	
    	findCart.getProduct().setStockUser(findCart.getQuantity() + findCart.getProduct().getStockUser());
    	findCart.getProduct().setCart(null);
    	productRepo.save(findCart.getProduct());
    	findCart.getUser().setCart(null);
    	userRepo.save(findCart.getUser());
    	cartRepo.delete(findCart);
    }
    
    @DeleteMapping("/deleteCartUserTransaction/{cartId}")
    public void deleteCartUserTransaction(@PathVariable int cartId) {
    	Cart findCart = cartRepo.findById(cartId).get();
    	
    	findCart.getProduct().setCart(null);
    	productRepo.save(findCart.getProduct());
    	findCart.getUser().setCart(null);
    	userRepo.save(findCart.getUser());
    	cartRepo.delete(findCart);
    }

    @PostMapping("/addToCart/{userId}/{productId}")
    public Cart addToCart(@RequestBody Cart cart ,@PathVariable int userId, @PathVariable int productId){
        Product findProduct = productRepo.findById(productId).get();
        
        User findUser = userRepo.findById(userId).get();
        
        findProduct.setStockUser(findProduct.getStockUser() - cart.getQuantity());
        cart.setProduct(findProduct);
        cart.setUser(findUser);

        return cartRepo.save(cart);
    }

    @PutMapping("/addQuantity/{cartId}/{productId}")
    public Cart updateCartQty (@PathVariable int cartId, @PathVariable int productId ){
        Cart findCartData = cartRepo.findById(cartId).get();
        Product findProduct = productRepo.findById(productId).get();
        
        findCartData.setQuantity(findCartData.getQuantity() + 1);
        findProduct.setStockUser(findProduct.getStockUser()-1);
      
        return cartRepo.save(findCartData);
    }
    
    @PutMapping("/reduceQuantity/{cartId}/{productId}")
    public Cart reduceCartQty (@PathVariable int cartId, @PathVariable int productId ){
        Cart findCartData = cartRepo.findById(cartId).get();
        Product findProduct = productRepo.findById(productId).get();
        
        findCartData.setQuantity(findCartData.getQuantity() - 1);
        findProduct.setStockUser(findProduct.getStockUser()+1);
      
        return cartRepo.save(findCartData);
    }
}