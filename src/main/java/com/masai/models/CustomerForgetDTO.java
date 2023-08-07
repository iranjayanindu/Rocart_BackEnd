package com.masai.models;

import lombok.*;

@Data
@Setter
@Getter
@Builder
public class CustomerForgetDTO {
    private String mobileNo;
    private String emailId;
}
