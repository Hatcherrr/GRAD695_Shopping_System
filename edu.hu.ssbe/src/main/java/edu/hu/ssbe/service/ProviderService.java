package edu.hu.ssbe.service;

import edu.hu.ssbe.bean.Provider;
import edu.hu.ssbe.dao.ProviderDao;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
public class ProviderService {

    @Autowired
    private ProviderDao providerDao;

    public List<Provider> getProviders() {
        return providerDao.findAll();
    }

    public Provider getProvider(String id) {
        if (StringUtils.isEmpty(id)) {
            return null;
        }
        return providerDao.findOne(id);
    }

    public Provider addProvider(Provider provider) {
        provider.setId(UUID.randomUUID().toString());
        Date now = new Date();
        provider.setCreatedon(now);
        if (providerDao.saveOne(provider)) {
            return provider;
        }
        return null;
    }

    public Provider editProvider(String id, Provider provider) {
        Provider oldProvider = getProvider(id);
        if (oldProvider != null) {
            provider.setId(id);
            if (providerDao.update(provider)) {
                return provider;
            }
        }
        return null;
    }

    public boolean deleteProvider(String id) {
        if (StringUtils.isNotEmpty(id)) {
            return providerDao.deleteOne(id);
        }
        return false;
    }
}
