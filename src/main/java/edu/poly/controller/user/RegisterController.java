package edu.poly.controller.user;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import edu.poly.model.Account;
import edu.poly.model.AccountRole;
import edu.poly.model.AccountStatus;
import edu.poly.repository.AccountRepository;
import jakarta.validation.Valid;

@Controller
public class RegisterController {

	@Autowired
	AccountRepository accountRepository;

	@GetMapping("register")
	public String register(ModelMap model) {
		Account account;
		account = new Account();
		model.addAttribute("account", account);
		return "user/register";
	}

	@PostMapping("register")
	public String saveOrUpdate(RedirectAttributes attributes, ModelMap model, @Valid Account account,
			BindingResult result){
		if (result.hasErrors()) {
			model.addAttribute("account", account);
			return "user/register";
		}
		Optional<Account> existedAc = accountRepository.findById(account.getUsername());
		if (existedAc.isPresent()) {
			model.addAttribute("message", "Account already exists");
			model.addAttribute("account", account);
			return "user/register";
		}
		
		account.setStatus(AccountStatus.Activated);
		account.setRole(AccountRole.User);
		account.setPhoto("person.jpg");

		accountRepository.save(account);
		attributes.addAttribute("message", "This account has been saved!");
		return "user/login";
	}
}
