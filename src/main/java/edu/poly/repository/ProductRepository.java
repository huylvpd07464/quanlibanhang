package edu.poly.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import edu.poly.model.Product;
import edu.poly.model.ReportCategory;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
	
	Optional<Product> findById(Long id);

	public Page<Product> findByNameLike(String string, Pageable pageable);
	
	@Query("SELECT new edu.poly.model.ReportCategory(o.category, sum(o.price), count(o)) "
			+ " FROM Product o "
			+ " GROUP BY o.category"
			+ " ORDER BY sum(o.price) DESC")
	List<ReportCategory> getInventoryByCategory();


}
