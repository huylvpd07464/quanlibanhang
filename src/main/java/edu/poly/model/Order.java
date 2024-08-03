package edu.poly.model;

import java.util.Date;
import java.util.Set;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name ="oders")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Order {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Temporal(TemporalType.DATE)
	private Date orderedDate;
	
	@Column(columnDefinition = "nvarchar(200)")
	private String description;
	
	@Column(nullable = false)
	private OrderStatus status;
	
	@ManyToOne
	@JoinColumn(name = "accountId")
	private Account account;
	
	@OneToMany(mappedBy = "order")
	Set<OrderDetail> orderDetails;
	
	@PrePersist
	public void preCreate() {
		orderedDate = new Date();
	}
}
