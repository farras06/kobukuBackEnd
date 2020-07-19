package com.project.kobuku.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.project.kobuku.entity.Transaction;

public interface TransactionRepo extends JpaRepository <Transaction, Integer> {
	
	@Query(value = "select * from transaction where users_id = ?1 and status = ?2",nativeQuery = true)
	public Iterable<Transaction> findTransactionByStatusAndUserId(int id, String status);
	
	@Query(value = "select * from transaction where status = ?1",nativeQuery = true)
	public Iterable<Transaction> findTransactionByStatus(String status);

}
