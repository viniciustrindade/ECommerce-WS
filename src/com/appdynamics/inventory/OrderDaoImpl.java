package com.appdynamics.inventory;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Query;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import com.appdynamicspilot.exception.InventoryServerException;



public class OrderDaoImpl implements OrderDao {

	private Logger logger = LoggerFactory.getLogger(OrderDaoImpl.class);
	
	Map<Integer, Order> orderMap;
	InventoryItem item = null;
	private static Integer i = 1;
	public static final int SLOW_BOOK = 3;
	private EntityManagerFactory entityManagerFactory;
	private EntityManager entityManager;

    private String selectQuery = null;
	
	/*public Long createOrder(OrderRequest orderRequest) {

		System.out.println("in create Order");
		logger.info("in create Order");
		item.setId(orderRequest.getItemId());
		item.setQuantity(orderRequest.getQuantity());
		
		Order order = new Order(orderRequest, item);
		Date date = new Date(System.currentTimeMillis());
		order.setCreatedOn(date);
		
		orderMap.put(i, order);
		System.out.println("created order and stored in map" + orderMap.get(i).getId());
		logger.info("created order and stored in map" + orderMap.get(i).getId());
		i++;
		return order.getId();
	}*/
	
	
	

    public EntityManagerFactory getEntityManagerFactory() {
        return entityManagerFactory;
    }

    public void setEntityManagerFactory(EntityManagerFactory entityManagerFactory) {
        this.entityManagerFactory = entityManagerFactory;
    }

    

    public synchronized EntityManager getEntityManager() {
       if (entityManager ==null) {
          entityManager = getEntityManagerFactory().createEntityManager();
       }
        return entityManager;
    }

    public void setEntityManager(EntityManager entityManager) {
        this.entityManager = entityManager;
    }
    	
    
        public Long createOrder(OrderRequest orderRequest) throws InventoryServerException {
        InventoryItem item = getEntityManager().find(InventoryItem.class,orderRequest.getItemId());
        /**
         * Throws an error if the item ID is 5
         */
        
        if (orderRequest.getItemId() == 5) {
            throw new InventoryServerException("Error in creating order for " + item.getId(), null);
        }


        try {
            Query q = getEntityManager().createNativeQuery(this.selectQuery);
            q.getResultList();

        } catch (Exception e) {
            e.printStackTrace();
        }
        

        /**
         *Creates a slow query if the minute of the current hour is 0 to 20. 
         */
        Date date = new Date();
        Calendar calendar = GregorianCalendar.getInstance();
        calendar.setTime(date);
        int minutes = calendar.get(Calendar.MINUTE);
        boolean triggerSlow = false;
        if ((minutes >= 0) && (minutes <= 20)) {
            triggerSlow = true;
        }

        QueryExecutor qe = new QueryExecutor();
        if (triggerSlow) {
            qe.executeSimplePS(10000);
        } else {
            qe.executeSimplePS(10);
        }
        return storeOrder(orderRequest);
    }

    private Long storeOrder(OrderRequest orderRequest) {
        InventoryItem item = entityManager.find(InventoryItem.class,orderRequest.getItemId());
        Order order = new Order(orderRequest,item);

        order.setQuantity(orderRequest.getQuantity());
        persistOrder(order);
        //deleting the order to reduce size of data
        removeOrder(order);
        return order.getId();
    }

    private void persistOrder(Order order) {
        EntityTransaction txn = getEntityManager().getTransaction();
        try {
            txn.begin();
            entityManager.persist(order);
        } catch (Exception ex) {
             logger.error(ex.toString());
             txn.rollback();
        } finally {
            if(!txn.getRollbackOnly()) {
               txn.commit();
            }
        }
    }

    private void removeOrder(Order order) {
        EntityTransaction txn = getEntityManager().getTransaction();
        try {
            txn.begin();
            entityManager.remove(order);
        } catch (Exception ex) {
            logger.error(ex.toString());
            txn.rollback();
        } finally {
            if(!txn.getRollbackOnly()) {
                txn.commit();
            }
        }
    }

    /**
     * @param selectQuery the selectQuery to set
     */
    public void setSelectQuery(String selectQuery) {
        this.selectQuery = selectQuery;
    }

        
	
}