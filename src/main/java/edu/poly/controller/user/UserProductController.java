package edu.poly.controller.user;

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
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import edu.poly.model.Category;
import edu.poly.model.Product;
import edu.poly.repository.CategoryRepository;
import edu.poly.repository.ProductRepository;

@Controller
@RequestMapping("products")
public class UserProductController {
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

		List<Long> discountedPrices = pages.stream()
				.map(product -> Math.round( product.getPrice() * (1 - product.getDiscount() / 100.0f))).collect(Collectors.toList());

		model.addAttribute("discountedPrices", discountedPrices);
		model.addAttribute("pages", pages);
		return "user/product";
	}
	
	@GetMapping(value = {"productDetail/{id}"})
	public String productDetail(Model model,@PathVariable(name = "id", required = false) Long id) {
		Optional<Product> productExsisted = productRepository.findById(id);
		
		if (!productExsisted.isPresent()) {
			return "user/product";
		}
		
		Product product = productExsisted.get();
		
		Long discountedPrices = Math.round( product.getPrice() * (1 - product.getDiscount() / 100.0f));
		
		model.addAttribute("discountedPrices", discountedPrices);
		model.addAttribute("product", product);
		return "user/productdetail";
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

		List<Long> discountedPrices = pages.stream()
				.map(product -> Math.round( product.getPrice() * (1 - product.getDiscount() / 100.0f))).collect(Collectors.toList());

		model.addAttribute("discountedPrices", discountedPrices);
		model.addAttribute("pages", pages);
		return "user/product";

	}

}
