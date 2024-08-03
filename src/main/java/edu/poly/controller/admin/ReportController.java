package edu.poly.controller.admin;

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
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import edu.poly.model.Order;
import edu.poly.model.OrderDetail;
import edu.poly.model.OrderStatus;
import edu.poly.model.Product;
import edu.poly.model.ReportCategory;
import edu.poly.repository.OrderDetailRepository;
import edu.poly.repository.OrderRepository;
import edu.poly.repository.ProductRepository;

@Controller
@RequestMapping("admin/report")
public class ReportController {

	@Autowired
	ProductRepository productRepository;
	
	@Autowired
	OrderDetailRepository orderDetailRepository;
	
	@Autowired
	OrderRepository orderRepository;
	
	@GetMapping("category")
	public String findTopSellingProducts(Model model) {
		List<ReportCategory> items = productRepository.getInventoryByCategory();
		model.addAttribute("items", items);
		return "admin/report/category";
	}
	
	@GetMapping("integratedStatistics")
	public String integratedStatictis(Model model) {
		Integer totalProductsSold = orderRepository.getTotalProductsSoldByStatus(OrderStatus.Completed);
		
		Double 	totalRevenue = orderRepository.getTotalRevenueByStatus(OrderStatus.Completed);
		
		Integer totalCustomers = orderRepository.getTotalCustomer(OrderStatus.Completed);
		
		List<Order> listOrder = orderRepository.findAll();
		
		
		model.addAttribute("listOrder", listOrder);
		model.addAttribute("totalCustomers", totalCustomers);
		model.addAttribute("totalRevenue", Math.round(totalRevenue));
		model.addAttribute("totalProductsSold", totalProductsSold);
		return "admin/report/integratedStatistics";
	}
	
//	@GetMapping("integratedStatistics")
//	public String list(ModelMap model,
//			@PageableDefault(size = 5, sort = "id", direction = Direction.ASC) Pageable pageable) {
//
//		Page<Order> pages = orderRepository.findAll(pageable);
//
//		List<Sort.Order> sortOrders = pages.getSort().stream().collect(Collectors.toList());
//
//		if (sortOrders.size() > 0) {
//			Sort.Order order = sortOrders.get(0);
//
//			model.addAttribute("sort",
//					order.getProperty() + "," + (order.getDirection() == Sort.Direction.ASC ? "asc" : "desc"));
//		} else {
//			model.addAttribute("sort", "id,asc");
//		}
//
//		model.addAttribute("pages", pages);
//		return "admin/products/listProduct";
//	}

}
