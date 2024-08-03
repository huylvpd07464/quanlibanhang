package edu.poly.controller.user;

import java.text.DecimalFormat;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import edu.poly.model.Account;
import edu.poly.model.Order;
import edu.poly.model.OrderDetail;
import edu.poly.model.OrderStatus;
import edu.poly.model.Product;
import edu.poly.repository.AccountRepository;
import edu.poly.repository.OrderDetailRepository;
import edu.poly.repository.OrderRepository;
import edu.poly.repository.ProductRepository;
import edu.poly.service.SessionService;

@Controller
@RequestMapping("shoppingcart")
public class ShoppingCartController {

	@Autowired
	ProductRepository productRepository;

	@Autowired
	OrderRepository orderRepository;

	@Autowired
	OrderDetailRepository orderDetailRepository;

	@Autowired
	AccountRepository accountRepository;

	@Autowired
	SessionService sessionService;

	@GetMapping("listorderdetails")
	public String list(ModelMap model) {
		Account account = sessionService.get("user");
		List<Order> listOrderPending = orderRepository.findByAccountAndStatus(account, OrderStatus.Pending);
		
		if (listOrderPending.size() == 0) {
			Account ac = accountRepository.findByUsername(account.getUsername());
			model.addAttribute("account", ac);
			return "user/shoppingcartisempty";
		}
		
		Order order = listOrderPending.get(0);

		List<OrderDetail> listOrderDetails = orderDetailRepository.findByOrder(order);

		Account ac = accountRepository.findByUsername(account.getUsername());

		double totalAmount = 0.0;
		double totalDiscountAmount = 0.0;

		for (OrderDetail orderDetail : listOrderDetails) {
			double itemTotal = orderDetail.getQuantity() * orderDetail.getPrice();
			double itemDiscount = itemTotal * (orderDetail.getDiscount() / 100);

			totalAmount += itemTotal;
			totalDiscountAmount += itemDiscount;
		}

		double total = totalAmount - totalDiscountAmount;

		DecimalFormat df = new DecimalFormat("#.0");
		String formattedTotalAmount = df.format(totalAmount);
		String formattedTotalDiscountAmount = df.format(totalDiscountAmount);
		String formattedTotal = df.format(total);

		model.addAttribute("totalAmount", formattedTotalAmount);
		model.addAttribute("totalDiscountAmount", formattedTotalDiscountAmount);
		model.addAttribute("total", formattedTotal);
		model.addAttribute("order", order);
		model.addAttribute("account", ac);
		model.addAttribute("listOrderDetails", listOrderDetails);
		return "user/shoppingcart";
	}
	
	@GetMapping("checkout")
	public String checkout(RedirectAttributes attributes) {
		Account account = sessionService.get("user");
		List<Order> listOrderPending = orderRepository.findByAccountAndStatus(account, OrderStatus.Pending);
		Order order = listOrderPending.get(0);
		List<OrderDetail> listOrderDetails = orderDetailRepository.findByOrder(order);
		
		for (OrderDetail orderDetail : listOrderDetails) {
			Product product = orderDetail.getProduct();
			if (product.getQuantity() < orderDetail.getQuantity()) {
				attributes.addFlashAttribute("message", product.getName() + " is out of stock");
				return "redirect:/shoppingcart/listorderdetails";
			}
			
			Integer quantityProduct = product.getQuantity() - orderDetail.getQuantity();
			product.setQuantity(quantityProduct);
		}
		
		order.setStatus(OrderStatus.Completed);
		
		orderRepository.save(order);
	
		return "redirect:/shoppingcart/listorderdetails";
	}

	@GetMapping("delete/{id}")
	public String delete(RedirectAttributes attributes, @PathVariable("id") Long id) {
		orderDetailRepository.deleteById(id);
		attributes.addAttribute("message", "This product has been removed!");
		return "redirect:/shoppingcart/listorderdetails";
	}
	
	@PostMapping("update")
	public String update(@RequestParam("id") Long id, @RequestParam("quantity") Integer quantity) {
		Optional<OrderDetail> orderDetailOtn = orderDetailRepository.findById(id);
		OrderDetail orderDetailExsisted = orderDetailOtn.get();
		
		if (quantity <= 0) {
			orderDetailRepository.deleteById(id);
		}else {
			orderDetailExsisted.setQuantity(quantity);
			orderDetailRepository.save(orderDetailExsisted);
		}
		
		return "redirect:/shoppingcart/listorderdetails";
	}

	@GetMapping(value = { "order/{id}" })
	public String order(RedirectAttributes attributes, @PathVariable(name = "id", required = false) Long id) {
		Optional<Product> productExsisted = productRepository.findById(id);
		Account account = sessionService.get("user");
		if (productExsisted.isPresent()) {
			List<Order> listOrderPending = orderRepository.findByAccountAndStatus(account, OrderStatus.Pending);
			if (listOrderPending.size() == 0) {
				Order order = new Order();
				order.setDescription("Welcom to Elite Furnishings");
				order.setStatus(OrderStatus.Pending);
				order.setAccount(account);
				orderRepository.save(order);

				// Thêm product vào order vừa mới tạo
				Product product = productExsisted.get();

				OrderDetail orderDetail = new OrderDetail();
				orderDetail.setDiscount(product.getDiscount());
				orderDetail.setPrice(product.getPrice());
//                orderDetail.setNote("abc");
				orderDetail.setQuantity(1);
				orderDetail.setOrder(order);
				orderDetail.setProduct(product);

				orderDetailRepository.save(orderDetail);
			} else if (listOrderPending.size() > 0) {
				Order order = listOrderPending.get(0);
				Product product = productExsisted.get();

				Optional<OrderDetail> orderDetailExsisted = orderDetailRepository.findByProductAndOrder(product, order);
				if (orderDetailExsisted.isPresent()) {
					OrderDetail orderDE = orderDetailExsisted.get();
					int quantityUpdate = orderDE.getQuantity() + 1;
					orderDE.setQuantity(quantityUpdate);
					orderDetailRepository.save(orderDE);
				} else {
					OrderDetail orderDetail = new OrderDetail();
					orderDetail.setDiscount(product.getDiscount());
					orderDetail.setPrice(product.getPrice());
//					orderDetail.setNote("abc");
					orderDetail.setQuantity(1);
					orderDetail.setOrder(order);
					orderDetail.setProduct(product);

					orderDetailRepository.save(orderDetail);
				}

			} else {
				attributes.addAttribute("message", "Order Error");

			}

		} else {
			attributes.addAttribute("message", "The product not exsisted");
			return "products";
		}

		return "redirect:/shoppingcart/listorderdetails";
	}

}
