package edu.poly.controller.user;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import edu.poly.model.Product;
import edu.poly.repository.ProductRepository;

@Controller
@RequestMapping("account")
public class HomePageController {
	
	@Autowired
	ProductRepository productRepository;
	
	@GetMapping("homepage")
	public String list(ModelMap model, @RequestParam Optional<String> message,
			@PageableDefault(size = 8, sort = "price", direction = Direction.DESC) Pageable pageable) {

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
		return "user/homepage";
	}
	
	@GetMapping("contact")
	public String contact() {
		return "user/contact";
	}
	
	@GetMapping("about")
	public String about() {
		return "user/about";
	}
	

}
