package com.elles.rest.model;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class CountryGDP {
    private Short year;
    private Double value;
}
