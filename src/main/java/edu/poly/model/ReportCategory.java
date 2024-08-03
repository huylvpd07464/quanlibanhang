package edu.poly.model;

import java.io.Serializable;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ReportCategory {
	@Id
	Category group;
	Double sum;
	Long count;
	
	public ReportCategory(Category group, Double sum, Long count) {
		super();
		this.group = group;
		this.sum = sum;
		this.count = count;
	}

	
}
