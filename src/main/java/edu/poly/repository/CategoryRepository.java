package edu.poly.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import edu.poly.model.Category;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Integer> {
	
	Page<Category> findByNameLike(String keyword, Pageable pageable);
	
}
