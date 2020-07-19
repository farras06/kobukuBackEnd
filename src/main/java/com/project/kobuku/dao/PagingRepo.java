package com.project.kobuku.dao;

import org.springframework.data.repository.PagingAndSortingRepository;

import com.project.kobuku.entity.Product;

public interface PagingRepo extends PagingAndSortingRepository <Product, Integer> {

}
