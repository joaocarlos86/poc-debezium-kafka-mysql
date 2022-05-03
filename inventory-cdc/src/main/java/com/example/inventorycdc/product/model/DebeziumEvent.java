package com.example.inventorycdc.product.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Data
@ToString
public class DebeziumEvent {
    private String op;
    private Date ts_ms;
    private Product before;
    private Product after;
}
