package com.titan.Transformerslab.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;

import com.mongodb.WriteResult;
import com.titan.Transformerslab.domain.Order;

@Component
public class OrderRepositoryCustomImpl implements OrderRepositoryCustom {

	@Autowired
    MongoTemplate mongoTemplate;
	
	@Override
	public int updateOrder(String order, String customerName) {
		Query query = new Query(Criteria.where("orderNumber").is(order));
        Update update = new Update();
        update.set("customerName", customerName);

        WriteResult result = mongoTemplate.updateFirst(query, update, Order.class);

        if(result!=null)
            return result.getN();
        else
            return 0;
	}
	
	public void saveOrder(Order order) {
		mongoTemplate.save(order);
		System.out.println("Saved");
	}
	
	public void updateOrder(Order order) {
		
	}

}
