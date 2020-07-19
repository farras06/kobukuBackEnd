package com.project.kobuku.controller;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
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

import com.project.kobuku.dao.UserRepo;
import com.project.kobuku.entity.Category;
import com.project.kobuku.entity.Product;
import com.project.kobuku.entity.User;
import com.project.kobuku.util.EmailUtil;


@RestController
@RequestMapping("/users")
@CrossOrigin

public class UserController {
	
	@Autowired
	private UserRepo userRepo;
	
	@Autowired
	private EmailUtil emailUtil;
	
	private PasswordEncoder pwEncoder = new BCryptPasswordEncoder();
	
	@GetMapping("/getUser")
	public Iterable <User> getUser() {
		return userRepo.findAll();
	}
	
	@PostMapping
	public User registerUser(@RequestBody User user) {
		
		Optional<User> findUser = userRepo.findByUsername(user.getUsername());
		Optional<User> findEmail = userRepo.findByEmail(user.getEmail());
		
		
		if (findUser.toString() != "Optional.empty") {
			throw new RuntimeException("Username Already exists!");
		}
		
		if (findEmail.toString() != "Optional.empty") {
			throw new RuntimeException("Email Already exists!");
		}
		
		String verifyToken = pwEncoder.encode(user.getUsername() + user.getEmail());
		
		String encodedPassword = pwEncoder.encode(user.getPassword());
		
		user.setPassword(encodedPassword);
		user.setVerified(false);
		user.setVerifyToken(verifyToken);
		
		
		User savedUser = userRepo.save(user);
		savedUser.setPassword(null);
		
		String linkToVerify = "http://localhost:8080/users/verify/" + user.getUsername() + "?token=" + verifyToken;
		
		String message = "<h3>Congrats! Registrasi Berhasil</h3>\n";
		message += "an Account With Username : " + user.getUsername() + " Been Registered!\n";
		message += "Click <a href=\"" + linkToVerify + "\">This Link</a> to Verify Your Account.";
		
		
		emailUtil.sendEmail(user.getEmail(), "Account Registration", message);
		
		return savedUser;
	}
	
	
	@GetMapping("/login")
	public User getLoginUser(@RequestParam String username, @RequestParam String password) {
		User findUser = userRepo.findByUsername(username).get();

		if (pwEncoder.matches(password, findUser.getPassword())) {	
			findUser.setPassword(null);
			return findUser;
		} 

		throw new RuntimeException("Wrong password!");
		
	}
	
	@GetMapping("/keeplogin/{id}")
	public User keepLogin( @PathVariable int id ) {
		User findUser = userRepo.findById(id).get();
		
		if (findUser.toString() != "Optinal.empty") {
			return findUser;
		}
		throw new RuntimeException("username doesn't exist");
		
	}
	
	@GetMapping("/verify/{username}")
	public String verifyUserEmail (@PathVariable String username, @RequestParam String token) {
		User findUser = userRepo.findByUsername(username).get();
		
		if (findUser.getVerifyToken().equals(token)) {
			findUser.setVerified(true);
		} else {
			throw new RuntimeException("Token is invalid");
		}
		
		userRepo.save(findUser);
		
		return "Sukses!";
	}
	
	@GetMapping("/verifyAgain/{username}")
		public String verifyAgain (@PathVariable String username ) {
		
		User findUsername = userRepo.findByUsername(username).get();
		
		String linkToVerify = "http://localhost:8080/users/verify/" + findUsername.getUsername() + "?token=" + findUsername.getVerifyToken();
		
		String message = "<h3> Your Email Verification has been Resend</h3>\n";
		message += "Click <a href=\"" + linkToVerify + "\">This Link</a> to Verify Your Account.";
		
		emailUtil.sendEmail(findUsername.getEmail(), "Resend Email Verification", message);
		
		return "sukses";
		
		
	}
	
	@PutMapping("/editProfile/{oldPassword}/{newPassword}")
	public User editUser(@RequestBody User user , @PathVariable String newPassword, @PathVariable String oldPassword  ) {
		System.out.println(oldPassword + " " + newPassword);
		User findPassword = userRepo.findById(user.getId()).get();
		
		if (pwEncoder.matches(oldPassword, findPassword.getPassword() )) {
			String encodedPassword = pwEncoder.encode(newPassword);
			user.setPassword(encodedPassword);
			String verifyToken = pwEncoder.encode(user.getUsername() + user.getEmail());
			user.setVerifyToken(verifyToken);
			user.setRole(findPassword.getRole());
			user.setVerified(findPassword.isVerified());
			return userRepo.save(user);
		}
		throw new RuntimeException("Old Password is Wrong!");
	}

	@GetMapping("/forgetPassword/{username}")
	public User forgetPassword (@PathVariable String username) {
		
		Optional<User> findUser = userRepo.findByUsername(username);
		
		if (findUser.toString() == "Optional.empty") {
			throw new RuntimeException("Username dosen't exists!");
		}
		
		String verifyToken = findUser.get().getVerifyToken();
		
		String linkToVerify = "http://localhost:3000/users/forgetPassword/" + findUser.get().getUsername() + "/" + verifyToken;
		
		String message = "<h1>Please Change Your Password</h1>\n";
		message += "Klik <a href=\"" + linkToVerify + "\">Click This Link</a> to Change Your Password.";
		
		emailUtil.sendEmail(findUser.get().getEmail(), "Forgot Password", message);
		
		return findUser.get();
		
	}
	
	@PutMapping("/saveForgetPassword/{username}/{newPassword}")
	public User saveForgetPassword (@PathVariable String username, @PathVariable String newPassword ) {
		
		User findUser = userRepo.findByUsername(username).get();
		String encodedPassword = pwEncoder.encode(newPassword);
		findUser.setPassword(encodedPassword);
		userRepo.save(findUser);
		return findUser;
		
	}
	
	@PutMapping("/EditUserByAdmin/{id}")
	private User addCategory(@RequestBody User user, @PathVariable Integer id) {
		User findUser = userRepo.findById(id).get();
		return userRepo.save(user);
	}
	
	@DeleteMapping("/deleteUser/{id}")
		public void deleteUsery(@PathVariable int id ) {
		userRepo.deleteById(id);
	}
	
}
