package com.project.kobuku.entity;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
public class TransactionDetail {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;
	private int price;
	private int quantity;
	private int totalPriceProduct;
	
	@ManyToOne(cascade = { CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH })
    @JoinColumn(name = "transaction_id")
	@JsonIgnore
	private Transaction transaction;
	
	@ManyToOne(cascade = { CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH })
    @JoinColumn(name = "products_id")
	private Product product;

	public int getId() {
		return id;
	}

	public int getPrice() {
		return price;
	}

	public int getQuantity() {
		return quantity;
	}

	public int getTotalPriceProduct() {
		return totalPriceProduct;
	}

	public Transaction getTransaction() {
		return transaction;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setPrice(int price) {
		this.price = price;
	}

	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}

	public void setTotalPriceProduct(int totalPriceProduct) {
		this.totalPriceProduct = totalPriceProduct;
	}

	public void setTransaction(Transaction transaction) {
		this.transaction = transaction;
	}

	public Product getProduct() {
		return product;
	}

	public void setProduct(Product product) {
		this.product = product;
	}	
	
	
	
}