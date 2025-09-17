package com.example.derivativecalculator;

import org.springframework.web.bind.annotation.*;

/* 
@RestController
@RequestMapping("/api")
public class DerivativeController {

    private final Calculator calculator;

    public DerivativeController(Calculator calculator) {
        this.calculator = calculator;
    }
    
    // Use GET for processing input
    @GetMapping("/process")
    public String process(@RequestParam String input, @RequestParam String variable, @RequestParam(defaultValue = "1") String order) {
        try {
            String derivativeString = calculator.getDerivative(input,variable,order); // Calls your Calculator.java function
            return derivativeString;
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }
    */
    /*
    // ...existing code...
    @PostMapping("/process")
    public String process(@RequestParam String input) {
        try {
            return calculator.getDerivative(input);
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }
    */
// ...existing code...

@RestController
@RequestMapping("/api")
public class DerivativeController {

    private final Calculator calculator;

    public DerivativeController(Calculator calculator) {
        this.calculator = calculator;
    }

    // GET for processing input
    @GetMapping("/process")
    public DerivativeResponse process(
            @RequestParam String input,
            @RequestParam(defaultValue = "x") String variable,
            @RequestParam(defaultValue = "1") String order,
            @RequestParam(required = false) String point) {
        try {
            // Call the updated Calculator method that supports evaluation at a point
            return calculator.getDerivative(input, variable, order, point);
        } catch (Exception e) {
            return new DerivativeResponse("Error: " + e.getMessage(), null,null);
        }
    }
}

