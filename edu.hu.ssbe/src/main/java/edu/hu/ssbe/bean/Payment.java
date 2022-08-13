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
public class Payment {
    private String id;
    private String foreignid;
    @JsonFormat( pattern="yyyy-MM-dd HH:mm:ss")
    private Date createdon;
    @JsonFormat( pattern="yyyy-MM-dd HH:mm:ss")
    private Date lastupdate;
    private String method;
    private String cardNumber;
    private Long amount;
    private String status;
}
