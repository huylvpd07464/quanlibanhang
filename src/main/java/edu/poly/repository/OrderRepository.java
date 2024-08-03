package edu.poly.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import edu.poly.model.Account;
import edu.poly.model.Order;
import edu.poly.model.OrderStatus;

import java.util.List;


@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
	
	@Query("SELECT o FROM Order o WHERE o.account = ?1 AND o.status = ?2")
    List<Order> findByAccountAndStatus(Account account, OrderStatus status);
	
	@Query("SELECT SUM(od.quantity) FROM OrderDetail od JOIN od.order o WHERE o.status = :status") 
	Integer getTotalProductsSoldByStatus(@Param("status") OrderStatus status);
	
	@Query("SELECT SUM(od.quantity * od.product.price * (1 - od.product.discount / 100)) FROM OrderDetail od JOIN od.order o WHERE o.status = :status") 
	Double getTotalRevenueByStatus(@Param("status") OrderStatus status);
	
	@Query("SELECT COUNT(DISTINCT o.account.username) FROM Order o WHERE o.status = ?1") 
	Integer getTotalCustomer(OrderStatus status);
	
}	
