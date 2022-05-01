package com.example.payment.controller;

import com.example.payment.entity.Order;
import com.example.payment.service.PaypalService;
import com.paypal.api.payments.Links;
import com.paypal.api.payments.Payment;
import com.paypal.base.rest.PayPalRESTException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Controller
public class PaypalController {

    @Autowired
    private PaypalService paypalService;

    public static final String successUrl = "http://localhost:8080/pay/success";
    public static final String cancelUrl = "http://localhost:8080/pay/cancel";

    @GetMapping(value="/pay")
    public ResponseEntity<String> payment(@RequestParam("price") double price,
    		HttpServletRequest request) throws PayPalRESTException {
        try {
        	Order theOrder = Order.builder().price(price).intent("sale").method("paypal").currency("USD").description("payment").build();
        	request.getAttributeNames().asIterator().forEachRemaining(s->System.out.println(s.toString()));
            Payment thePayment = paypalService.createPayment(theOrder.getPrice(), theOrder.getCurrency(),
                    theOrder.getMethod(), theOrder.getIntent(), theOrder.getDescription(), cancelUrl, successUrl);
            for (Links links: thePayment.getLinks()){
                if(links.getRel().equals("approval_url")){
                	System.out.println(links.getHref());
                    return ResponseEntity.ok( links.getHref() );
                }
            }
        }
        catch (PayPalRESTException payPalRESTException) {
            payPalRESTException.printStackTrace();
        }
        return ResponseEntity.ok("");
    }
    @GetMapping("/success")
    public ResponseEntity<String> successPay(@RequestParam("paymentId") String paymentId, @RequestParam("PayerID") String payerId) {
        try {
            Payment payment = paypalService.executePayment(paymentId, payerId);
            System.out.println(payment.toJSON());
            if (payment.getState().equals("approved")) {
                return ResponseEntity.ok( "success");
            }
        } catch (PayPalRESTException e) {
            System.out.println(e.getMessage());
        }
        return ResponseEntity.ok( "redirect:/");
    }

}
