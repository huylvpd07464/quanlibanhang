package edu.poly.controller.admin;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import edu.poly.model.Account;
import edu.poly.model.Category;
import edu.poly.model.Product;
import edu.poly.repository.AccountRepository;
import edu.poly.repository.CategoryRepository;
import edu.poly.repository.ProductRepository;
import edu.poly.ultils.UploadUtils;
import jakarta.servlet.ServletContext;
import jakarta.validation.Valid;

@Controller
@RequestMapping("admin/accounts")
public class AccountController {
	@Autowired
	ServletContext application;

	@Autowired
	private AccountRepository accountRepository;

	@GetMapping
	public String list(ModelMap model, @RequestParam Optional<String> message,
			@PageableDefault(size = 5, sort = "username", direction = Direction.ASC) Pageable pageable) {

		Page<Account> pages = accountRepository.findAll(pageable);

		if (message.isPresent()) {
			model.addAttribute("message", message.get());
		}

		List<Sort.Order> sortOrders = pages.getSort().stream().collect(Collectors.toList());

		if (sortOrders.size() > 0) {
			Sort.Order order = sortOrders.get(0);

			model.addAttribute("sort",
					order.getProperty() + "," + (order.getDirection() == Sort.Direction.ASC ? "asc" : "desc"));
		} else {
			model.addAttribute("sort", "username,asc");
		}

		model.addAttribute("pages", pages);
		return "admin/accounts/listAccount";

	}

	@GetMapping("findByKeyword")
	public String findByKeyword(ModelMap model, @RequestParam Optional<String> message,
			@RequestParam Optional<String> keyword, @RequestParam Optional<Integer> p,
			@PageableDefault(size = 5, sort = "username", direction = Direction.ASC) Pageable pageable) {
		pageable = PageRequest.of(p.orElse(0), 3);

		String searchKeyword = keyword.map(kw -> "%" + kw + "%").orElse("%");

		Page<Account> pages = accountRepository.findByUsernameLike(searchKeyword, pageable);

		if (message.isPresent()) {
			model.addAttribute("message", message.get());
		}

		List<Sort.Order> sortOrders = pages.getSort().stream().collect(Collectors.toList());

		if (sortOrders.size() > 0) {
			Sort.Order order = sortOrders.get(0);

			model.addAttribute("sort",
					order.getProperty() + "," + (order.getDirection() == Sort.Direction.ASC ? "asc" : "desc"));
		} else {
			model.addAttribute("sort", "username,asc");
		}

		model.addAttribute("pages", pages);
		return "admin/accounts/listAccount";

	}

	@GetMapping("delete/{username}")
	public String delete(RedirectAttributes attributes, @PathVariable("username") String username) {
		accountRepository.deleteById(username);
		attributes.addAttribute("message", "This account has been removed!");
		return "redirect:/admin/accounts";
	}

	@GetMapping(value = { "newOrEdit", "newOrEdit/{username}" })
	public String newOrEdit(ModelMap model,
			@PathVariable(name = "username", required = false) Optional<String> username) {
		Account account;

		if (username.isPresent()) {
			Optional<Account> existed = accountRepository.findById(username.get());
			account = existed.isPresent() ? existed.get() : new Account();
		} else {
			account = new Account();
		}

		model.addAttribute("account", account);
		return "admin/accounts/newOrEdit";
	}

	@PostMapping("saveOrUpdate")
	public String saveOrUpdate(RedirectAttributes attributes, ModelMap model, @Valid Account account,
			BindingResult result, @RequestParam("image") MultipartFile imageFile)
			throws IllegalStateException, IOException {
		if (result.hasErrors()) {
			model.addAttribute("account", account);
			return "admin/accounts/newOrEdit";
		}

		// Xử lý tải lên tệp
		if (!imageFile.isEmpty()) {
			try {
				String uploadDir = "src/main/resources/static/uploads/accountimage/";
				String originalFilename = imageFile.getOriginalFilename();
				String newFilename = UploadUtils.saveFile(uploadDir, originalFilename, imageFile);
				account.setPhoto(newFilename);
			} catch (IOException e) {
				e.printStackTrace();
				model.addAttribute("message", "Lỗi khi tải tệp: " + e.getMessage());
				return "admin/accounts/newOrEdit";
			}
		} else {
			// Giữ lại tên ảnh hiện tại
			Optional<Account> existingAccountOpt = accountRepository.findById(account.getUsername());
			if (existingAccountOpt.isPresent()) {
				account.setPhoto(existingAccountOpt.get().getPhoto());
			}
		}

		accountRepository.save(account);
		attributes.addAttribute("message", "This account has been saved!");
		return "redirect:/admin/accounts";
	}

}
