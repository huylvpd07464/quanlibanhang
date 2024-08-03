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

import edu.poly.model.Category;
import edu.poly.model.Product;
import edu.poly.repository.CategoryRepository;
import edu.poly.repository.ProductRepository;
import edu.poly.ultils.UploadUtils;
import jakarta.servlet.ServletContext;
import jakarta.validation.Valid;

@Controller
@RequestMapping("admin/products")
public class ProductController {

	@Autowired
	ServletContext application;

	@Autowired
	private ProductRepository productRepository;

	@Autowired
	private CategoryRepository categoryRepository;

	@ModelAttribute("categories")
	public List<Category> getCategories() {
		return categoryRepository.findAll();
	}

	@GetMapping
	public String list(ModelMap model, @RequestParam Optional<String> message,
			@PageableDefault(size = 5, sort = "name", direction = Direction.ASC) Pageable pageable) {

		Page<Product> pages = productRepository.findAll(pageable);

		if (message.isPresent()) {
			model.addAttribute("message", message.get());
		}

		List<Sort.Order> sortOrders = pages.getSort().stream().collect(Collectors.toList());

		if (sortOrders.size() > 0) {
			Sort.Order order = sortOrders.get(0);

			model.addAttribute("sort",
					order.getProperty() + "," + (order.getDirection() == Sort.Direction.ASC ? "asc" : "desc"));
		} else {
			model.addAttribute("sort", "name,asc");
		}

		model.addAttribute("pages", pages);
		return "admin/products/listProduct";
	}

	@GetMapping("findByKeyword")
	public String findByKeyword(ModelMap model, @RequestParam Optional<String> message,
			@RequestParam Optional<String> keyword,
			@PageableDefault(size = 5, sort = "name", direction = Direction.ASC) Pageable pageable) {

		String searchKeyword = keyword.map(kw -> "%" + kw + "%").orElse("%");

		Page<Product> pages = productRepository.findByNameLike(searchKeyword, pageable);

		if (message.isPresent()) {
			model.addAttribute("message", message.get());
		}

		List<Sort.Order> sortOrders = pages.getSort().stream().collect(Collectors.toList());

		if (sortOrders.size() > 0) {
			Sort.Order order = sortOrders.get(0);

			model.addAttribute("sort",
					order.getProperty() + "," + (order.getDirection() == Sort.Direction.ASC ? "asc" : "desc"));
		} else {
			model.addAttribute("sort", "name,asc");
		}

		model.addAttribute("pages", pages);
		return "admin/products/listProduct";

	}

	@GetMapping("delete/{id}")
	public String delete(RedirectAttributes attributes, @PathVariable("id") Long id) {
		productRepository.deleteById(id);
		attributes.addAttribute("message", "This product has been removed!");
		return "redirect:/admin/products";
	}

	@GetMapping(value = { "newOrEdit", "newOrEdit/{id}" })
	public String newOrEdit(ModelMap model, @PathVariable(name = "id", required = false) Optional<Long> id) {
		Product product;

		if (id.isPresent()) {
			Optional<Product> existed = productRepository.findById(id.get());
			product = existed.isPresent() ? existed.get() : new Product();
		} else {
			product = new Product();
		}

		model.addAttribute("product", product);
		return "admin/products/newOrEdit";
	}

	@PostMapping("saveOrUpdate")
	public String saveOrUpdate(RedirectAttributes attributes, ModelMap model, @Valid Product product,
			BindingResult result, @RequestParam("image") MultipartFile imageFile) throws IllegalStateException, IOException {
		if (result.hasErrors()) {
			model.addAttribute("product", product);
			return "admin/products/newOrEdit";
		}

		// Xử lý tải lên tệp
		if (!imageFile.isEmpty()) {
			try {
				String uploadDir = "src/main/resources/static/uploads/";
				String originalFilename = imageFile.getOriginalFilename();
				String newFilename = UploadUtils.saveFile(uploadDir, originalFilename, imageFile);
				System.out.println("file: "+ newFilename);
				product.setImageUrl(newFilename);
			} catch (IOException e) {
				e.printStackTrace();
				model.addAttribute("message", "Lỗi khi tải tệp: " + e.getMessage());
				return "admin/products/newOrEdit";
			}
		} else {
			// Giữ lại tên ảnh hiện tại
			Optional<Product> existingProductOpt = productRepository.findById(product.getId());
			if (existingProductOpt.isPresent()) {
				product.setImageUrl(existingProductOpt.get().getImageUrl());
			}
		}
		
		
		productRepository.save(product);
		attributes.addAttribute("message", "This product has been saved!");
		return "redirect:/admin/products";
	}

}
