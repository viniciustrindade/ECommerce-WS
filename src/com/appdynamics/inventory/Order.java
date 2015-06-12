package com.appdynamics.inventory;

import java.util.Date;


public class Order {
	
	private Long id;
	private Integer quantity;
	private Date createdOn;
	
	protected InventoryItem item;
	
	public Order(){
		
	}
	
	public Order(OrderRequest orderRequest, InventoryItem item){
		this.quantity = orderRequest.getQuantity();
        this.createdOn = new Date();
        this.item = item;
	}
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Integer getQuantity() {
		return quantity;
	}
	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}
	public Date getCreatedOn() {
		return createdOn;
	}
	public void setCreatedOn(Date createdOn) {
		this.createdOn = createdOn;
	}
}