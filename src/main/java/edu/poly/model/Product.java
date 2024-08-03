package edu.poly.model;

import java.util.Date;
import java.util.Set;

import org.hibernate.validator.constraints.Length;

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
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
@Entity
@Table(name ="products")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Product {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@NotEmpty(message = "Name must be entered")
	@Length(min = 5, message = "Length of name must be greated than 5 chareters")
	@Column(columnDefinition = "nvarchar(100)")
	private String name;
	
	@Temporal(TemporalType.DATE)
	private Date createdDate;
	
	@Column(nullable = false)
	@Min(value = 0, message = "Price must be greater than or equals 0")
	private Double price;
	
	@Column(length = 100)
	private String imageUrl;
	
	@Column(nullable = false)
	@Min(value = 0, message = "Quantity must be greater than or equals 0")
	private Integer quantity;
	
	@Column(nullable = false)
	@Min(value = 0, message = "Discount must be greater than or equals 0")
	@Max(value = 100, message = "Discount must be less than 100")
	private Float discount;
	
	private ProductStatus status;
	
	@ManyToOne()
	@JoinColumn(name = "categoryId")
	Category category;
	
	@OneToMany(mappedBy = "product")
	Set<OrderDetail> orderDetails;
	
	@PrePersist
	public void prePersist() {
		createdDate = new Date();
	}
}
