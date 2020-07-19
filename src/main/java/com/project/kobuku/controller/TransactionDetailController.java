package com.project.kobuku.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.project.kobuku.dao.ProductRepo;
import com.project.kobuku.dao.TransactionDetailRepo;
import com.project.kobuku.dao.TransactionRepo;
import com.project.kobuku.entity.Product;
import com.project.kobuku.entity.Transaction;
import com.project.kobuku.entity.TransactionDetail;

@RestController
@RequestMapping("/transactionDetail")
@CrossOrigin
public class TransactionDetailController {
	
	@Autowired
	private ProductRepo productRepo;
	
	@Autowired
    private TransactionDetailRepo transactionDetailRepo;

    @Autowired
    private TransactionRepo transactionRepo;
    
    @GetMapping
    public Iterable<TransactionDetail> getAllTransactionDetail(){
        return transactionDetailRepo.findAll();
    }
    
    @PostMapping("/addTransactionDetail/{transactionId}/{productId}")
	public TransactionDetail addTransactionDetail(@RequestBody TransactionDetail transactionDetail, @PathVariable int transactionId, @PathVariable int productId) {
        Transaction findTransaction = transactionRepo.findById(transactionId).get();
        Product findProduct = productRepo.findById(productId).get();
        transactionDetail.setTransaction(findTransaction);
        transactionDetail.setProduct(findProduct);
        
        return transactionDetailRepo.save(transactionDetail);
    }
    
  
}