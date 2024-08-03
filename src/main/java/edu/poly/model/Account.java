package edu.poly.model;

import java.util.Date;
import java.util.Set;

import org.hibernate.validator.constraints.Length;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name ="Accounts" , uniqueConstraints = {
		@UniqueConstraint(columnNames = {"email"})
})
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Account {
	
	@Id
	@Column(length = 30)
	@NotEmpty(message = "User name must be entered")
	@Length(min = 5, message = "Length of user namee must be greated than 5 chareters")
	private String username;
	
	@Column(length = 20)
	@NotEmpty(message = "Password must be entered")
	@Length(min = 6, message = "Length of password must be greated than 6 chareters")
	private String password;
	
	@Column(columnDefinition = "nvarchar(100)")
	@NotEmpty(message = "Fullname must be entered")
	private String fullname;
	
	@Column(length = 100, nullable = false, unique = true)
	@NotEmpty(message = "Email must be entered")
	private String email;
	
	@Column(length = 255, nullable = true)
	private String photo;
	
	@Temporal(TemporalType.DATE)
	private Date createdDate;
	
	@Temporal(TemporalType.DATE)
	private Date updatedDate;
	
	private AccountStatus status;
	
	private AccountRole role;
	
	@OneToMany(mappedBy = "account")
	private Set<Order> orders;
	
	@PrePersist
	public void preCreate() {
		createdDate = new Date();
	}
	
	@PreUpdate
	public void preUpdate() {
		updatedDate = new Date();
	}
	
	
}
