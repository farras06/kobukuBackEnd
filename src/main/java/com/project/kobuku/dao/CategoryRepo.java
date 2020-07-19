package com.project.kobuku.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.project.kobuku.entity.Category;

public interface CategoryRepo extends JpaRepository <Category, Integer> {

}
