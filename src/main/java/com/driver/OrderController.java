package com.driver;

import java.util.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("orders")
public class OrderController {


    HashMap<String , Order> order_map = new HashMap<>();
    HashMap<String, DeliveryPartner> partner_map = new HashMap<>();
    HashMap<String,List<String>> partner_order_map = new HashMap<>();

    HashMap<String,Integer> order_time_map = new HashMap<>();
    @PostMapping("/add-order")
    public ResponseEntity<String> addOrder(@RequestBody Order order){

        order_map.put(order.getId(),order);
        List<Integer> time = new ArrayList<>();
        time.add(order.getDeliveryTime());
        Collections.sort(time);
        order_time_map.put(order.getId(),order.getDeliveryTime());
        return new ResponseEntity<>("New order added successfully", HttpStatus.CREATED);
    }
    //url - http://localhost:8098/orders/add-order

    @PostMapping("/add-partner/{partnerId}")
    public ResponseEntity<String> addPartner(@PathVariable String partnerId){



        DeliveryPartner partner = new DeliveryPartner(partnerId);
        partner_map.put(partnerId,partner);
        return new ResponseEntity<>("New delivery partner added successfully", HttpStatus.CREATED);
    }
    //url -  http://localhost:8098/orders/add-partner
    @PutMapping("/add-order-partner-pair/{orderId}/{partnerId}")
    public ResponseEntity<String> addOrderPartnerPair(@PathVariable String orderId, @PathVariable String partnerId){

        //This is basically assigning that order to that partnerId
        if(partner_order_map.containsKey(partnerId))
        {
            partner_order_map.get(partnerId).add(orderId);
        }
        else
        {
            List<String> orders = new ArrayList<>();
            orders.add(orderId);
            partner_order_map.put(partnerId,orders);
        }

        return new ResponseEntity<>("New order-partner pair added successfully", HttpStatus.CREATED);
    }
    //url - http://localhost:8098/orders/add-order-partner-pair
    @GetMapping("/get-order-by-id/{orderId}")
    public ResponseEntity<Order> getOrderById(@PathVariable String orderId){

         Order order= null;
        if(order_map.containsKey(orderId))
        {
            order = order_map.get(orderId);

        }

        //order should be returned with an orderId.

        return new ResponseEntity<>(order, HttpStatus.CREATED);

    }
 //url - http://localhost:8098/orders/get-order-by-id
    @GetMapping("/get-partner-by-id/{partnerId}")
    public ResponseEntity<DeliveryPartner> getPartnerById(@PathVariable String partnerId){

        DeliveryPartner deliveryPartner = null;

        //deliveryPartner should contain the value given by partnerId

        if(partner_map.containsKey(partnerId))
        {
            deliveryPartner = partner_map.get(partnerId);
        }
        return new ResponseEntity<>(deliveryPartner, HttpStatus.CREATED);
    }
//url - http://localhost:8098/orders/get-partner-by-id
    @GetMapping("/get-order-count-by-partner-id/{partnerId}")
    public ResponseEntity<Integer> getOrderCountByPartnerId(@PathVariable String partnerId){

        Integer orderCount = 0;

        if(partner_order_map.containsKey(partnerId))
        {
            orderCount = partner_order_map.get(partnerId).size();
        }


        //orderCount should denote the orders given by a partner-id

        return new ResponseEntity<>(orderCount, HttpStatus.CREATED);
    }

    @GetMapping("/get-orders-by-partner-id/{partnerId}")
    public ResponseEntity<List<String>> getOrdersByPartnerId(@PathVariable String partnerId){
        List<String> orders = null;
        if(partner_order_map.containsKey(partnerId))
        {
            orders = partner_order_map.get(partnerId);
        }

        //orders should contain a list of orders by PartnerId

        return new ResponseEntity<>(orders, HttpStatus.CREATED);
    }
    //url - http://localhost:8098/orders/get-orders-by-partner-id
    @GetMapping("/get-all-orders")
    public ResponseEntity<List<String>> getAllOrders(){
        List<String> orders = null;

        for(String key : order_map.keySet())
        {
            orders.add(key);
        }

        //Get all orders
        return new ResponseEntity<>(orders, HttpStatus.CREATED);
    }

    @GetMapping("/get-count-of-unassigned-orders")
    public ResponseEntity<Integer> getCountOfUnassignedOrders(){
        Integer countOfOrders = 0;
        HashSet<String> order_set = new HashSet<>();
         for(String key : partner_order_map.keySet())
         {
             List<String> order_id = partner_order_map.get(key);
             for(String str : order_id)
             {
                 order_set.add(str);
             }
         }

         for(String str : order_map.keySet())
         {
             if(order_set.contains(str))
             {
                 countOfOrders++;
             }
         }
        //Count of orders that have not been assigned to any DeliveryPartner

        return new ResponseEntity<>(countOfOrders, HttpStatus.CREATED);
    }

    @GetMapping("/get-count-of-orders-left-after-given-time")
    public ResponseEntity<Integer> getOrdersLeftAfterGivenTimeByPartnerId(@PathVariable String time, @PathVariable String partnerId){

        Integer countOfOrders = 0;

        //countOfOrders that are left after a particular time of a DeliveryPartner
        int a = Integer.valueOf(time.substring(0,3));
        int b = Integer.valueOf((time.substring(4,time.length())));
        int t = (a*60) + b;
        List<String> list = partner_order_map.get(partnerId);
        for(String str : list)
        {
            if(t< order_time_map.get(str))
            {
                countOfOrders++;
            }
        }
        return new ResponseEntity<>(countOfOrders, HttpStatus.CREATED);
    }

    @GetMapping("/get-last-delivery-time")
    public ResponseEntity<String> getLastDeliveryTimeByPartnerId(@PathVariable String partnerId){
        String time = null;
        int max_time = 0;
        //Return the time when that partnerId will deliver his last delivery order.
        List<String> list = partner_order_map.get(partnerId);
        for(String str : list)
        {
           if(max_time < order_time_map.get(str))
           {
               max_time = order_time_map.get(str);
           }
        }
        int hour = max_time/60;
        int m = max_time%60;
        time = Integer.toString(hour) + Integer.toString(m);
        return new ResponseEntity<>(time, HttpStatus.CREATED);
    }

    @DeleteMapping("/delete-partner-by-id/{partnerId}")
    public ResponseEntity<String> deletePartnerById(@PathVariable String partnerId){

        //Delete the partnerId
        //And push all his assigned orders to unassigned orders.
        partner_map.remove(partnerId);
        partner_order_map.remove(partnerId);
        return new ResponseEntity<>(partnerId + " removed successfully", HttpStatus.CREATED);
    }

    @DeleteMapping("/delete-order-by-id/{orderId}")
    public ResponseEntity<String> deleteOrderById(@PathVariable String orderId){

        //Delete an order and also
        //remove it from the assigned order of that partnerId
        order_map.remove(orderId);
        for(String key : partner_order_map.keySet())
        {
            List<String> list = partner_order_map.get(key);
            for(String str : list)
            {
                if(str.equals(orderId))
                {
                    list.remove(orderId);
                    partner_order_map.remove(key);
                    partner_order_map.put(key,list);
                }
            }

        }
        return new ResponseEntity<>(orderId + " removed successfully", HttpStatus.CREATED);
    }
}
