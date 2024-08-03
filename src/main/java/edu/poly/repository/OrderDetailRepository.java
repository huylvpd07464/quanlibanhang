package edu.poly.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import edu.poly.model.Order;
import edu.poly.model.OrderDetail;
import edu.poly.model.OrderStatus;
import edu.poly.model.Product;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderDetailRepository extends JpaRepository<OrderDetail, Long> {
	@Query("SELECT o FROM OrderDetail o WHERE o.product = :product AND o.order = :order")
	Optional<OrderDetail> findByProductAndOrder(@Param("product") Product product, @Param("order") Order order);

	List<OrderDetail> findByOrder(Order order);
	
}
