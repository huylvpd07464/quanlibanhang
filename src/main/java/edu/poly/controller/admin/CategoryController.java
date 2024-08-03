package edu.poly.controller.admin;

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
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import edu.poly.model.Category;
import edu.poly.model.Product;
import edu.poly.repository.CategoryRepository;
import jakarta.validation.Valid;

@Controller
@RequestMapping("admin/categories")
public class CategoryController {

	@Autowired
	CategoryRepository categoryRepository;

	@GetMapping
	public String list(ModelMap model, @RequestParam Optional<String> message,
			@PageableDefault(size = 5, sort = "name", direction = Direction.ASC) Pageable pageable) {

		Page<Category> pages = categoryRepository.findAll(pageable);

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
		return "admin/categories/listCategories";

	}

	@GetMapping(value = { "newOrEdit", "newOrEdit/{id}" })
	public String newOrEdit(ModelMap model, @PathVariable(name = "id", required = false) Optional<Integer> id) {
		Category category;

		if (id.isPresent()) {
			Optional<Category> existed = categoryRepository.findById(id.get());
			category = existed.isPresent() ? existed.get() : new Category();
		} else {
			category = new Category();
		}

		model.addAttribute("category", category);
		return "admin/categories/newOrEdit";
	}

	@PostMapping("saveOrUpdate")
	public String saveOrUpdate(RedirectAttributes attributes, ModelMap model, @Valid Category category,
			BindingResult result) {
		if (result.hasErrors()) {
			model.addAttribute("category", category);
			return "admin/categories/newOrEdit";
		}

		categoryRepository.save(category);

		attributes.addAttribute("message", "This category has been saved!");

		return "redirect:/admin/categories";
	}
	
	@GetMapping("delete/{id}")
	public String delete(RedirectAttributes attributes, @PathVariable("id") Integer id) {
		categoryRepository.deleteById(id);
		attributes.addAttribute("message", "This category has been removed!");
		return "redirect:/admin/categories";
	}
	
	@GetMapping("findByKeyword")
	public String findByKeyword(ModelMap model, @RequestParam Optional<String> message,
			@RequestParam Optional<String> keyword,
			@PageableDefault(size = 5,sort = "name", direction = Direction.ASC) Pageable pageable) {

		String searchKeyword = keyword.map(kw -> "%" + kw + "%").orElse("%");
		
		Page<Category> pages = categoryRepository.findByNameLike(searchKeyword, pageable);

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
		return "admin/categories/listCategories";
	}
}
