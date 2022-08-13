package edu.hu.ssbe.service;

import edu.hu.ssbe.bean.Payment;
import edu.hu.ssbe.dao.PaymentDao;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
public class PaymentService {

    @Autowired
    private PaymentDao paymentDao;

    public List<Payment> getPayments() {
        return paymentDao.findAll();
    }

    public Payment getPayment(String id, String foreignid) {
        if (StringUtils.isEmpty(id) || StringUtils.isEmpty(foreignid)) {
            return null;
        }
        return paymentDao.findOne(id, foreignid);
    }

    public Payment getPayment(String id) {
        if (StringUtils.isEmpty(id)) {
            return null;
        }
        return paymentDao.findOne(id);
    }

    public Payment addPayment(Payment payment) {
        if (StringUtils.isEmpty(payment.getForeignid())) {
            return null;
        }
        payment.setId(UUID.randomUUID().toString());
        Date now = new Date();
        payment.setCreatedon(now);
        payment.setLastupdate(now);
        if (StringUtils.isEmpty(payment.getStatus())) {
            payment.setStatus("active");
        }
        if (paymentDao.saveOne(payment)) {
            return payment;
        }
        return null;
    }

    public Payment editPayment(String id, Payment payment) {
        if (StringUtils.isEmpty(payment.getForeignid())) {
            return null;
        }
        Payment oldPayment = getPayment(id, payment.getForeignid());
        if (oldPayment != null) {
            payment.setId(id);
            payment.setLastupdate(new Date());
            if (paymentDao.update(payment)) {
                return payment;
            }
        }
        return null;
    }

    public boolean deletePayment(String id, String foreignid) {
        if (StringUtils.isNotEmpty(id)) {
            return paymentDao.deleteOne(id, foreignid);
        }
        return false;
    }

    public boolean deletePayment(String id) {
        if (StringUtils.isNotEmpty(id)) {
            return paymentDao.deleteOne(id);
        }
        return false;
    }
}
