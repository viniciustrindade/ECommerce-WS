package com.appdynamics.inventory;

import javax.jws.WebMethod;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.jws.soap.SOAPBinding.Style;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.appdynamicspilot.exception.InventoryServerException;


@WebService
@SOAPBinding(style = Style.RPC)
public class OrderService {
	
	private OrderDao orderDao;
	private static final Logger log = LoggerFactory.getLogger(OrderService.class);
	
	@WebMethod(exclude=true)
	public void setOrderDao(OrderDao orderDao){
		this.orderDao = orderDao;
	}
	
	@WebMethod(operationName = "createOrder")
	
	public Long createOrder(OrderRequest orderRequest) {
		
		System.out.println("creating order with order request: " +orderRequest.toString());
		try {	
			return orderDao.createOrder(orderRequest);
		} catch (InventoryServerException e) {
			e.printStackTrace();
		}
		return (long) 0;
	}
	
	@WebMethod(operationName = "createPO")
	public Long createPO(Long itemId, Integer quantity){
		OrderRequest orderRequest = new OrderRequest();
		orderRequest.setItemId(itemId);
		orderRequest.setQuantity(quantity);
		log.info("creating order with request: " +orderRequest.toString());
		
		try{
			return orderDao.createOrder(orderRequest);
		}catch(Exception e){
			log.error("Error in creating order [" +orderRequest.toString() + "]");
		}
		
		return (long) 0;
	}
	
}