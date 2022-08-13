package edu.hu.ssbe.service;

import edu.hu.ssbe.bean.Order;
import edu.hu.ssbe.dao.OrderDao;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
public class OrderService {

    @Autowired
    private OrderDao orderDao;

    public List<Order> getOrders() {
        return orderDao.findAll();
    }

    public Order getOrder(String id, String foreignid, String productid) {
        if (StringUtils.isEmpty(id) || StringUtils.isEmpty(foreignid) || StringUtils.isEmpty(productid)) {
            return null;
        }
        return orderDao.findOne(id, foreignid, productid);
    }

    public Order getOrder(String id) {
        if (StringUtils.isEmpty(id)) {
            return null;
        }
        return orderDao.findOne(id);
    }

    public Order addOrder(Order order) {
        if (StringUtils.isEmpty(order.getForeignid()) || StringUtils.isEmpty(order.getProductid())) {
            return null;
        }
        order.setId(UUID.randomUUID().toString());
        Date now = new Date();
        order.setCreatedon(now);
        order.setLastupdate(now);
        if (StringUtils.isEmpty(order.getStatus())) {
            order.setStatus("active");
        }
        if (orderDao.saveOne(order)) {
            return order;
        }
        return null;
    }

    public Order editOrder(String id, Order order) {
        if (StringUtils.isEmpty(order.getForeignid()) || StringUtils.isEmpty(order.getProductid())) {
            return null;
        }
        Order oldOrder = getOrder(id, order.getForeignid(), order.getProductid());
        if (oldOrder != null) {
            order.setId(id);
            order.setLastupdate(new Date());
            if (orderDao.update(order)) {
                return order;
            }
        }
        return null;
    }

    public boolean deleteOrder(String id, String foreignid, String productid) {
        if (StringUtils.isNotEmpty(id)) {
            return orderDao.deleteOne(id, foreignid, productid);
        }
        return false;
    }

    public boolean deleteOrder(String id) {
        if (StringUtils.isNotEmpty(id)) {
            return orderDao.deleteOne(id);
        }
        return false;
    }
}
