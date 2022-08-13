package edu.hu.ssbe.service;

import edu.hu.ssbe.bean.Product;
import edu.hu.ssbe.dao.ProductDao;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
public class ProductService {

    @Autowired
    private ProductDao productDao;

    public List<Product> getProducts() {
        return productDao.findAll();
    }

    public Product getProduct(String id) {
        if (StringUtils.isEmpty(id)) {
            return null;
        }
        return productDao.findOne(id);
    }

    public Product addProduct(Product product) {
        product.setId(UUID.randomUUID().toString());
        Date now = new Date();
        product.setCreatedon(now);
        product.setLastupdate(now);
        if (StringUtils.isEmpty(product.getStatus())) {
            product.setStatus("inStock");
        }
        if (productDao.saveOne(product)) {
            return product;
        }
        return null;
    }

    public Product editProduct(String id, Product product) {
        Product oldProduct = getProduct(id);
        if (oldProduct != null) {
            product.setId(id);
            product.setProviderid(oldProduct.getProviderid());
            product.setLastupdate(new Date());
            if (productDao.update(product)) {
                return product;
            }
        }
        return null;
    }

    public boolean deleteProduct(String id) {
        if (StringUtils.isNotEmpty(id)) {
            return productDao.deleteOne(id);
        }
        return false;
    }
}
