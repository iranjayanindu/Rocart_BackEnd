package com.masai.models;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@Setter
@Getter
@NoArgsConstructor
public class CategoryProductCountDTO {
    private CategoryEnum category;
    private long productCount;
}
