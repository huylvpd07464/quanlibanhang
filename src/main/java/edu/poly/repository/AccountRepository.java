package edu.poly.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import edu.poly.model.Account;
import java.util.List;


@Repository
public interface AccountRepository extends JpaRepository<Account, String> {
	Page<Account> findByUsernameLike(String keyword, Pageable pageable);
	
	Account findByEmail(String email);
	
	Account findByUsername(String username);
}
