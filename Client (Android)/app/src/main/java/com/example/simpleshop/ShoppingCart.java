package com.example.simpleshop;

import java.util.HashMap;

public class ShoppingCart {

    private static HashMap<Integer, Integer> orderList;

    public ShoppingCart() {
        if (orderList == null) {
            orderList = new HashMap<>();
        }
    }

    public void addProduct(Integer productId) {
        if (orderList.containsKey(productId)) {
            Integer i = orderList.get(productId);
            orderList.put(productId, ++i);
        } else {
            orderList.put(productId, 1);
        }
    }

    public HashMap<Integer, Integer> getOrderList() {
        return orderList;
    }
}
