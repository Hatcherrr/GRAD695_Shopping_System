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
public class Provider {
    private String id;
    private String name;
    @JsonFormat( pattern="yyyy-MM-dd HH:mm:ss")
    private Date createdon;
    private String contact1;
    private String contact2;
}
