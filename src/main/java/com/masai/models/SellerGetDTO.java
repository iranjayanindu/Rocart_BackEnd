package com.masai.models;

import lombok.*;

import java.util.List;
@Data
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SellerGetDTO {
    private Integer sellerId;
    private String firstName;
    private String lastName;
    private String mobile;
    private String emailId;
    private List<Product> product;
}
