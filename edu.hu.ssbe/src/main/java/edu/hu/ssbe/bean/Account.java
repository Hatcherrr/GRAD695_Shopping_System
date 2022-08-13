package edu.hu.ssbe.bean;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Account {
    private String id;
    private String kind;
    private String username;
    private String credentials;
    private String password;
    @JsonFormat( pattern="yyyy-MM-dd HH:mm:ss")
    private Date createdon;
    @JsonFormat( pattern="yyyy-MM-dd HH:mm:ss")
    private Date lastupdate;
    private String address1;
    private String address2;
    private String address3;
    private String city;
    private String state;
    private String country;
    private int zipcode;
    private String email;
    private String phone;
    private String status;
}
