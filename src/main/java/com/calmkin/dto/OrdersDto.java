package com.calmkin.dto;

import com.calmkin.pojo.OrderDetail;
import com.calmkin.pojo.Orders;
import lombok.Data;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

@Data
public class OrdersDto extends Orders {

/**
 * orderTime
 * status
 * + orderDetails(index,dishDetail)
 * + sumNum
 * amount
 */
   private List<OrderDetail> orderDetails;
}
