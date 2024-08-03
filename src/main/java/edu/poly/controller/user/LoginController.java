package edu.poly.controller.user;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import edu.poly.model.Account;
import edu.poly.model.AccountRole;
import edu.poly.repository.AccountRepository;
import edu.poly.service.SessionService;

@Controller
public class LoginController {
	
	@Autowired
	AccountRepository accountRepository;
	
	@Autowired
	SessionService sessionService;
	
	@GetMapping("login")
	public String login() {
		return "user/login";
	}

	@PostMapping("login")
	public String login(Model model, @RequestParam("username") String username,
			@RequestParam("password") String password) {
		try {
			Optional<Account> account = accountRepository.findById(username);
			Account user = account.get();
			if (!user.getPassword().equals(password)) {
				model.addAttribute("message", "Invalid password");
			} else {
				sessionService.set("user", user);
				String uri = sessionService.get("security-uri");
				if (uri != null) {
					return "redirect:" + uri;
				} else {
					if (user.getRole().equals(AccountRole.Admin)) {
						model.addAttribute("message", "Login succeed");
						return "redirect:/admin/accounts";
					}else {
						model.addAttribute("message", "Login succeed");
						model.addAttribute("account", user);
						return "redirect:/account/homepage";
					}
				}
			}
		} catch (Exception e) {
			model.addAttribute("message", "Invalid username");
		}
		return "user/login";
	}
	
	@GetMapping("logout")
	public String logout() {
		sessionService.remove("security-uri");
		sessionService.remove("user");
		return "user/login";
	}
}
