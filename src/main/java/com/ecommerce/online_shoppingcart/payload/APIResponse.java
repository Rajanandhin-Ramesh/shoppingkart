package com.ecommerce.online_shoppingcart.payload;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class APIResponse {
    public String message;
    private boolean status;
}
