package com.example.derivativecalculator;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class DerivativeController {

    private final Calculator calculator;

    public DerivativeController(Calculator calculator) {
        this.calculator = calculator;
    }
    
    // Use GET for processing input
    @GetMapping("/process")
    public String process(@RequestParam String input, @RequestParam String variable) {
        try {
            String derivativeString = calculator.getDerivative(input,variable); // Calls your Calculator.java function
            return derivativeString;
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }
        
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
}
