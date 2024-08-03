package edu.poly.controller.user;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import edu.poly.mail.MailerServiceImpl;
import edu.poly.model.Account;
import edu.poly.repository.AccountRepository;
import jakarta.mail.MessagingException;

@Controller
public class ForgotPasswordController {
	@Autowired
	AccountRepository accountRepository;
	
	@Autowired
	MailerServiceImpl mailer;
	
	@GetMapping("forgotpass")
	public String forgotPassword() {
		return "user/forgotpassword";
	}
	
	@PostMapping("forgotpass")
	public String forgotPassword(Model model,@RequestParam("email") String email) {
		Account account = accountRepository.findByEmail(email);
		
		if (account == null) {
			model.addAttribute("message", "This account does not exist");
		}else {
			try {
				mailer.send(account.getEmail(), "Your password", account.getPassword());
				model.addAttribute("message", "Email sent successfully");
			} catch (MessagingException e) {
				model.addAttribute("message", "Email sent failed");
				return "user/forgotpassword";
			}
		}
		
		return "user/forgotpassword";
	}
}
