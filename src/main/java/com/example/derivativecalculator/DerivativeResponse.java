package com.example.derivativecalculator;

public class DerivativeResponse {
    private String derivative;
    private String valueAtPoint; // optional, can be null
    private String numericalValueAtPoint; // optional, can be null

    public DerivativeResponse(String derivative, String valueAtPoint, String numericalValueAtPoint) {
        this.derivative = derivative;
        this.valueAtPoint = valueAtPoint;
        this.numericalValueAtPoint = numericalValueAtPoint;
    }

    public String getDerivative() {
        return derivative;
    }

    public String getValueAtPoint() {
        return valueAtPoint;
    }
    public String getNumericalValueAtPoint() {
        return numericalValueAtPoint;
    }
}
