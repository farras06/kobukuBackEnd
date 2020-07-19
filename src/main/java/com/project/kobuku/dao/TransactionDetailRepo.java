package com.project.kobuku.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.project.kobuku.entity.TransactionDetail;

public interface TransactionDetailRepo extends JpaRepository <TransactionDetail, Integer> {

}
