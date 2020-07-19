package com.project.kobuku.controller;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Date;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.project.kobuku.dao.ProductRepo;
import com.project.kobuku.dao.TransactionDetailRepo;
import com.project.kobuku.dao.TransactionRepo;
import com.project.kobuku.dao.UserRepo;
import com.project.kobuku.entity.Transaction;
import com.project.kobuku.entity.User;
import com.project.kobuku.util.EmailUtil;

@RestController
@RequestMapping("/transaction")
@CrossOrigin
public class TransactionController {

	private String uploadPath = System.getProperty("user.dir") + "\\src\\main\\resources\\static\\transfer\\";
	
	@Autowired
	private UserRepo userRepo;

	@Autowired
    private TransactionDetailRepo transactionDetailRepo;

    @Autowired
    private TransactionRepo transactionRepo;
    
    @Autowired
    private ProductRepo productRepo;
    
    @Autowired
	private EmailUtil emailUtil;
    
    
    @GetMapping
    public Iterable<Transaction> getAllTransaction(@RequestParam String status){
        return transactionRepo.findTransactionByStatus(status);
    }
    
    @GetMapping("/userTransaction/{userId}")
    public Iterable<Transaction> getUserTransaction(@PathVariable int userId, @RequestParam String status){
        User findUser = userRepo.findById(userId).get();
        return transactionRepo.findTransactionByStatusAndUserId(userId, status);
    }
    
    String message = "";
    @PutMapping("admin/accept/{transactionId}")
    public Transaction adminAccept (@PathVariable int transactionId, @RequestBody Transaction transaction) {
    	
    	Transaction findTransaction = transactionRepo.findById(transactionId).get();
    	String findEmail = transactionRepo.findById(transactionId).get().getUser().getEmail();
    	int findTransactionId = transactionRepo.findById(transactionId).get().getId();
    	String findUsername = transactionRepo.findById(transactionId).get().getUser().getUsername();
    	
    	
    	findTransaction.setStatus("accepted");
    	findTransaction.setConfirmDate(transaction.getConfirmDate());
    	transactionRepo.save(findTransaction);
    	
    	findTransaction.getTransactionDetail().forEach(val -> {
    		val.getProduct().setStockStorage(val.getProduct().getStockStorage() - val.getQuantity());
    		val.getProduct().setSold(val.getProduct().getSold() + val.getQuantity());
    		
    		productRepo.save(val.getProduct());
    	});
    	
    	message= "";
    	message += "<h4>Congrats! " + findUsername + "</h4>";
    	message += "<h6> Your Transaction with Transaction Number : " + findTransactionId + "<br>" ;
		message += "Has been Accepted <br>";
		message += "Total Price : " + findTransaction.getTotalPrice() + "<br>" ;
		message += "Purchase Date : " + findTransaction.getPurchaseDate() + "<br>" ;
		message += "Confirm Date : " + findTransaction.getConfirmDate() + "</h6>" ;
		
		
		findTransaction.getTransactionDetail().forEach(val -> {
			message += "-------------------------------";	
			message += "<h6> Product Name : " + val.getProduct().getProductName() + "<br>";
			message += "Product Price : " + val.getProduct().getPrice() + "<br>";
			message += "Products Price  : " + val.getTotalPriceProduct() + "</h6>";
			message += "<br>";
			message += "-------------------------------";	
		});
		
		emailUtil.sendEmail(findEmail, "Transaction Accepted", message);
		
		return transactionRepo.save(findTransaction);
 
    }
    
    @PutMapping("admin/reject/{transactionId}")
    public Transaction adminReject (@PathVariable int transactionId, @RequestBody Transaction transaction) {
    	
    	Transaction findTransaction = transactionRepo.findById(transactionId).get();
    	String findEmail = transactionRepo.findById(transactionId).get().getUser().getEmail();
    	int findTransactionId = transactionRepo.findById(transactionId).get().getId();
    	String findUsername = transactionRepo.findById(transactionId).get().getUser().getUsername();
    	
    	
    	findTransaction.setStatus("rejected");
    	findTransaction.setRejectedDate(transaction.getRejectedDate());
    	findTransaction.setConfirmDate(null);
    	findTransaction.setTransfer(null);
    	
    	findTransaction.getTransactionDetail().forEach(val -> {
    		val.getProduct().setStockUser(val.getProduct().getStockUser() + val.getQuantity());
    		val.getProduct().setSold(val.getProduct().getSold() + val.getQuantity());
    		
    		productRepo.save(val.getProduct());
    	});
    	
    	
    	String message = "Sad News :( " + findUsername + "\n";
    	message += " Your Transaction with Transaction Number : " + findTransactionId + "\n" ;
		message += " Has been Rejected, Please Re-Upload Your transfer Receipt \n";
		
		emailUtil.sendEmail(findEmail, "Transaction Rejected", message);
		
		return transactionRepo.save(findTransaction);
 
    }
    
    
    @PostMapping("/addTransaction/{userId}")
	public Transaction addTransaction(@RequestBody Transaction transaction,@PathVariable int userId) {
        User findUser = userRepo.findById(userId).get();
        transaction.setUser(findUser);
		return transactionRepo.save(transaction);
	}
    
    @PutMapping("/transfer/{transactionId}")
   	public Transaction  uploadBuktiTransfer(@RequestParam ("file") Optional<MultipartFile> file, @PathVariable int transactionId ) {
   		
   		Transaction findTransaction = transactionRepo.findById(transactionId).get();
   		Date date = new Date();
   		
   		String fileDownloadUri = "no image";
   		
   		if (file.toString()!="Optional.empty") {
   			String fileExtension = file.get().getContentType().split("/")[1];
   			String newFileName = "TRF-"+ date.getTime()+ "." + fileExtension;
   			
   			String fileName = StringUtils.cleanPath(newFileName);
   			
   			Path path = Paths.get(StringUtils.cleanPath(uploadPath) + fileName);
   			
   			try {
   				Files.copy(file.get().getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
   			} catch (IOException e) {
   				e.printStackTrace();
   			}
   			

   			fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath().path("/transaction/download/")
   					.path(fileName).toUriString();
   		}
   		findTransaction.setId(findTransaction.getId());
   		findTransaction.setStatus("pending");
   		findTransaction.setTransfer(fileDownloadUri);
   		findTransaction.setTransactionDetail(findTransaction.getTransactionDetail());
   		transactionRepo.save(findTransaction);
   		return transactionRepo.save(findTransaction);
   	}
     
    
    @GetMapping("/download/{fileName:.+}")
    public ResponseEntity<Object> downloadFile(@PathVariable String fileName){
   		Path path = Paths.get(uploadPath, fileName);
   		Resource resource = null;
   		
   		try {
   			resource = new UrlResource(path.toUri());
   		}catch (MalformedURLException e) {
   			e.printStackTrace();
   		}
   			return ResponseEntity.ok().contentType(MediaType.parseMediaType("application/octet-stream"))
   					.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
   					.body(resource);
   		
   	}
    
    
    
}