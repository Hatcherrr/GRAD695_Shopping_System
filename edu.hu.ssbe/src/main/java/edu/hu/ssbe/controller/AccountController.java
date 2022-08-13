package edu.hu.ssbe.controller;

import edu.hu.ssbe.bean.Account;
import edu.hu.ssbe.service.AccountService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/ds/accounts")
public class AccountController {

    private static final Logger LOGGER = LogManager.getLogger(AccountController.class);

    @Autowired
    private AccountService accountService;

    @RequestMapping(
            path = "",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity getAccounts() {
//        LOGGER.info("-----------------GET");
        List<Account> accounts = accountService.getAccounts();
        if (accounts != null && !accounts.isEmpty()) {
            return ResponseEntity.ok(accounts);
        }
        return ResponseEntity.badRequest().build();
    }

    @GetMapping(
            path = "/{id}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity getAccount(@PathVariable String id) {
        Account account = accountService.getAccount(id);
        if (account != null) {
            return ResponseEntity.ok(account);
        }
        return ResponseEntity.badRequest().build();
    }

    @RequestMapping(
            path = "",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity postAccount(@RequestBody Account account) {
//        LOGGER.info("-----------------POST");
        Account createdAccount = accountService.addAccount(account);
        if (createdAccount != null) {
            return ResponseEntity.ok(createdAccount);
        }
        return ResponseEntity.badRequest().build();
    }

    @PutMapping(
            path = "/{id}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity updateAccount(@PathVariable String id,
                                        @RequestBody Account account) {
        Account updatedAccount = accountService.editAccount(id, account);
        if (updatedAccount != null) {
            return ResponseEntity.ok(updatedAccount);
        }
        return ResponseEntity.badRequest().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity deleteAccount(@PathVariable String id) {
        if (accountService.deleteAccount(id)) {
            return ResponseEntity.ok("account deleted");
        }
        return ResponseEntity.badRequest().build();
    }
}
