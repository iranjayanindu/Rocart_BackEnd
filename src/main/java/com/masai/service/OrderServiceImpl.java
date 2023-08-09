package com.masai.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.masai.exception.CustomerNotFoundException;
import com.masai.models.*;
import com.masai.repository.CustomerDao;
import com.masai.repository.SessionDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.masai.exception.LoginException;
import com.masai.exception.OrderException;
import com.masai.repository.OrderDao;

@Service
public class OrderServiceImpl implements OrderService {
	@Autowired
	private OrderDao oDao;
	
	@Autowired
	private CustomerService cs;

	@Autowired
	private CustomerDao customerDao;
	
	@Autowired
	private CartServiceImpl cartservicei;

	@Autowired
	private LoginLogoutService loginService;

	@Autowired
	private SessionDao sessionDao;
	
	
	@Override
	public Order saveOrder(OrderDTO odto,String token) throws LoginException, OrderException {
		
		Order newOrder= new Order();
		
		Customer loggedInCustomer= cs.getLoggedInCustomerDetails(token);
		
		if(loggedInCustomer != null) {
			//Customer loggedInCustomer= cs.getLoggedInCustomerDetails(token);
			newOrder.setCustomer(loggedInCustomer);
			String usersCardNumber= loggedInCustomer.getCreditCard().getCardNumber();
			String userGivenCardNumber= odto.getCardNumber().getCardNumber();
			List<CartItem> productsInCart= loggedInCustomer.getCustomerCart().getCartItems();
			Map<String, Address> address = loggedInCustomer.getAddress();
			List<CartItem> productsInOrder = new ArrayList<>(productsInCart);
			
			newOrder.setOrdercartItems(productsInOrder);
			newOrder.setTotal(loggedInCustomer.getCustomerCart().getCartTotal());
			
			
			//check cart has items
			if(productsInCart.size()!=0) {
				//check the address
				if(!address.containsKey(odto.getAddressType())){
					throw new OrderException("Address does not exist");
				}

				//check user card
				if((usersCardNumber.equals(userGivenCardNumber)) 
						&& (odto.getCardNumber().getCardValidity().equals(loggedInCustomer.getCreditCard().getCardValidity())
								&& (odto.getCardNumber().getCardCVV().equals(loggedInCustomer.getCreditCard().getCardCVV())))) {
					
					//System.out.println(usersCardNumber);
					newOrder.setCardNumber(odto.getCardNumber().getCardNumber());
					newOrder.setAddress(loggedInCustomer.getAddress().get(odto.getAddressType()));
					newOrder.setDate(LocalDate.now());
					newOrder.setOrderStatus(OrderStatusValues.SUCCESS);
					System.out.println(usersCardNumber);
					//get item of the cart by user
					List<CartItem> cartItemsList= loggedInCustomer.getCustomerCart().getCartItems();
					
					for(CartItem cartItem : cartItemsList ) {
						int sellingCount = cartItem.getCartProduct().getSellingCount();
						Integer sellerId = cartItem.getCartProduct().getSeller().getSellerId();
						Integer remainingQuantity = cartItem.getCartProduct().getQuantity()-cartItem.getCartItemQuantity();
						if(remainingQuantity < 0 || cartItem.getCartProduct().getStatus() == ProductStatus.OUTOFSTOCK) {
							CartDTO cartdto = new CartDTO();
							cartdto.setProductId(cartItem.getCartProduct().getProductId());
							cartservicei.removeProductFromCart(cartdto, token);
							throw new OrderException("Product "+ cartItem.getCartProduct().getProductName() + " OUT OF STOCK");
						}
						cartItem.getCartProduct().setQuantity(remainingQuantity);
						cartItem.getCartProduct().setSellingCount(sellingCount+cartItem.getCartItemQuantity());
						if(cartItem.getCartProduct().getQuantity()==0) {
							cartItem.getCartProduct().setStatus(ProductStatus.OUTOFSTOCK);
						}
					}
					cartservicei.clearCart(token);
					//System.out.println(newOrder);
					return oDao.save(newOrder);
				}
				else {
					System.out.println("Not same");
					newOrder.setCardNumber(null);
					newOrder.setAddress(loggedInCustomer.getAddress().get(odto.getAddressType()));
					newOrder.setDate(LocalDate.now());
					newOrder.setOrderStatus(OrderStatusValues.PENDING);
					cartservicei.clearCart(token);
					return oDao.save(newOrder);
					
				}
			}
			else {
				throw new OrderException("No products in Cart");
			}
			
		}
		else {
			throw new LoginException("Invalid session token for customer"+"Kindly Login");
		}
	}

	@Override
	public Order getOrderByOrderId(Integer OrderId) throws OrderException {
		return oDao.findById(OrderId).orElseThrow(()-> new OrderException("No order exists with given OrderId "+ OrderId));
		
	}

	@Override
	public List<Order> getAllOrders() throws OrderException {
		// TODO Auto-generated method stub
		List<Order> orders = oDao.findAll();
		if(orders.size()>0)
			return orders;
		else
			throw new OrderException("No Orders exists on your account");
	}

	@Override
	public List<Order> getAllOrdersByCustomer(String token) throws OrderException {
		loginService.checkTokenStatus(token);
		UserSession user = sessionDao.findByToken(token).get();

		Optional<Customer> opt = customerDao.findById(user.getUserId());

		if(opt.isEmpty())
			throw new CustomerNotFoundException("Customer does not exist");

		List<Order> ordersByCustomerCustomerId = oDao.findOrdersByCustomerCustomerId(opt.get().getCustomerId());
		return ordersByCustomerCustomerId;
	}

	@Override
	public Order cancelOrderByOrderId(Integer OrderId,String token) throws OrderException {
		Order order= oDao.findById(OrderId).orElseThrow(()->new OrderException("No order exists with given OrderId "+ OrderId));
		if(order.getCustomer().getCustomerId()==cs.getLoggedInCustomerDetails(token).getCustomerId()) {
			if(order.getOrderStatus()==OrderStatusValues.PENDING) {
				order.setOrderStatus(OrderStatusValues.CANCELLED);
				oDao.save(order);
				return order;
			}
			else if(order.getOrderStatus()==OrderStatusValues.SUCCESS) {
				order.setOrderStatus(OrderStatusValues.CANCELLED);
				List<CartItem> cartItemsList= order.getOrdercartItems();
				
				for(CartItem cartItem : cartItemsList ) {
					Integer addedQuantity = cartItem.getCartProduct().getQuantity()+cartItem.getCartItemQuantity();
					cartItem.getCartProduct().setQuantity(addedQuantity);
					if(cartItem.getCartProduct().getStatus() == ProductStatus.OUTOFSTOCK) {
						cartItem.getCartProduct().setStatus(ProductStatus.AVAILABLE);
					}
				}
				
				oDao.save(order);
				return order;
			}
			else {
				throw new OrderException("Order was already cancelled");
			}
		}
		else {
			throw new LoginException("Invalid session token for customer"+"Kindly Login");
		}

		
	}

	@Override
	public Order updateOrderByOrder(OrderDTO orderdto, Integer OrderId,String token) throws OrderException,LoginException {
		Order existingOrder= oDao.findById(OrderId).orElseThrow(()->new OrderException("No order exists with given OrderId "+ OrderId));
		
		if(existingOrder.getCustomer().getCustomerId()==cs.getLoggedInCustomerDetails(token).getCustomerId()) {
			//existingOrder.setCardNumber(orderdto.getCardNumber().getCardNumber());
			//existingOrder.setAddress(existingOrder.getCustomer().getAddress().get(orderdto.getAddressType()));
			Customer loggedInCustomer = cs.getLoggedInCustomerDetails(token);
			String usersCardNumber= loggedInCustomer.getCreditCard().getCardNumber();
			String userGivenCardNumber= orderdto.getCardNumber().getCardNumber();
//			System.out.println(loggedInCustomer);
			if((usersCardNumber.equals(userGivenCardNumber)) 
					&& (orderdto.getCardNumber().getCardValidity().equals(loggedInCustomer.getCreditCard().getCardValidity())
							&& (orderdto.getCardNumber().getCardCVV().equals(loggedInCustomer.getCreditCard().getCardCVV())))) {
				existingOrder.setCardNumber(orderdto.getCardNumber().getCardNumber());
				existingOrder.setAddress(existingOrder.getCustomer().getAddress().get(orderdto.getAddressType()));
				existingOrder.setOrderStatus(OrderStatusValues.SUCCESS);
				List<CartItem> cartItemsList= existingOrder.getOrdercartItems();
				for(CartItem cartItem : cartItemsList ) {
					Integer remainingQuantity = cartItem.getCartProduct().getQuantity()-cartItem.getCartItemQuantity();
					if(remainingQuantity < 0 || cartItem.getCartProduct().getStatus() == ProductStatus.OUTOFSTOCK) {
						CartDTO cartdto = new CartDTO();
						cartdto.setProductId(cartItem.getCartProduct().getProductId());
						cartservicei.removeProductFromCart(cartdto, token);
						throw new OrderException("Product "+ cartItem.getCartProduct().getProductName() + " OUT OF STOCK");
					}
					cartItem.getCartProduct().setQuantity(remainingQuantity);
					if(cartItem.getCartProduct().getQuantity()==0) {
						cartItem.getCartProduct().setStatus(ProductStatus.OUTOFSTOCK);
					}
				}
				return oDao.save(existingOrder);
			}
			else {
				throw new OrderException("Incorrect Card Number Again" + usersCardNumber + userGivenCardNumber);
			}
			
			
		}
		else {
			throw new LoginException("Invalid session token for customer"+"Kindly Login");
		}
		
	}

	@Override
	public List<Order> getAllOrdersByDate(LocalDate date) throws OrderException {
		
		List<Order> listOfOrdersOntheDay= oDao.findByDate(date);
		return listOfOrdersOntheDay;
	}

	@Override
	public Customer getCustomerByOrderid(Integer orderId) throws OrderException {
		Optional<Order> opt= oDao.findById(orderId);
		if(opt.isPresent()) {
			Order existingorder= opt.get();
			
			return oDao.getCustomerByOrderid(existingorder.getCustomer().getCustomerId());
		}
		else
			throw new OrderException("No Order exists with orderId "+orderId);
	}

}
