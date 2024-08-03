package edu.poly.controller.user;

import java.io.IOException;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import edu.poly.model.Account;
import edu.poly.model.AccountRole;
import edu.poly.model.AccountStatus;
import edu.poly.repository.AccountRepository;
import edu.poly.service.SessionService;
import edu.poly.ultils.UploadUtils;
import jakarta.servlet.ServletContext;
import jakarta.validation.Valid;

@Controller
@RequestMapping("account")
public class ProfileController {
	
	@Autowired
	ServletContext application;

	@Autowired
	private AccountRepository accountRepository;
	
	@Autowired
	SessionService sessionService;

	@GetMapping("profile")
	public String profile(Model model) {
			Account ac = sessionService.get("user");

			if (ac == null) {
				return "user/login";
			}else {
				Account account = accountRepository.findByUsername(ac.getUsername());
				model.addAttribute("account", account);
			}

			return "user/profile";
	}
	
	@PostMapping("update")
	public String saveOrUpdate(RedirectAttributes attributes, ModelMap model, @Valid Account account,
			BindingResult result, @RequestParam("image") MultipartFile imageFile)
			throws IllegalStateException, IOException {
		if (result.hasErrors()) {
			model.addAttribute("account", account);
			return "user/profile";
		}

		// Xử lý tải lên tệp
		if (!imageFile.isEmpty()) {
			try {
				String uploadDir = "src/main/resources/static/uploads/accountimage/";
				String originalFilename = imageFile.getOriginalFilename();
				String newFilename = UploadUtils.saveFile(uploadDir, originalFilename, imageFile);
				System.out.println("file: " + newFilename);
				account.setPhoto(newFilename);
			} catch (IOException e) {
				e.printStackTrace();
				model.addAttribute("message", "Lỗi khi tải tệp: " + e.getMessage());
				return "user/profile";
			}
		} else {
			// Giữ lại tên ảnh hiện tại
			Optional<Account> existingAccountOpt = accountRepository.findById(account.getUsername());
			if (existingAccountOpt.isPresent()) {
				account.setPhoto(existingAccountOpt.get().getPhoto());
			}
		}
		
		account.setStatus(AccountStatus.Activated);
		account.setRole(AccountRole.User);

		accountRepository.save(account);
		attributes.addAttribute("message", "This account has been saved!");
		return "redirect:/products";
	}

}
