package edu.hu.ssbe.controller;

import edu.hu.ssbe.bean.Provider;
import edu.hu.ssbe.service.ProviderService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/ds/providers")
public class ProviderController {

    private static final Logger LOGGER = LogManager.getLogger(ProviderController.class);

    @Autowired
    private ProviderService providerService;

    @RequestMapping(
            path = "",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity getProviders() {
//        LOGGER.info("-----------------GET");
        List<Provider> providers = providerService.getProviders();
        if (providers != null && !providers.isEmpty()) {
            return ResponseEntity.ok(providers);
        }
        return ResponseEntity.badRequest().build();
    }

    @GetMapping(
            path = "/{id}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity getProvider(@PathVariable String id) {
        Provider provider = providerService.getProvider(id);
        if (provider != null) {
            return ResponseEntity.ok(provider);
        }
        return ResponseEntity.badRequest().build();
    }

    @RequestMapping(
            path = "",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity postProvider(@RequestBody Provider provider) {
//        LOGGER.info("-----------------POST");
        Provider createdProvider = providerService.addProvider(provider);
        if (createdProvider != null) {
            return ResponseEntity.ok(createdProvider);
        }
        return ResponseEntity.badRequest().build();
    }

    @PutMapping(
            path = "/{id}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity updateProvider(@PathVariable String id,
                                         @RequestBody Provider provider) {
        Provider updatedProvider = providerService.editProvider(id, provider);
        if (updatedProvider != null) {
            return ResponseEntity.ok(updatedProvider);
        }
        return ResponseEntity.badRequest().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity deleteProvider(@PathVariable String id) {
        if (providerService.deleteProvider(id)) {
            return ResponseEntity.ok("provider deleted");
        }
        return ResponseEntity.badRequest().build();
    }
}
