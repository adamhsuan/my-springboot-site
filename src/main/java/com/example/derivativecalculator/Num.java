package com.example.derivativecalculator;
public class Num extends Expression {
    
    private double value;
    public Num()
    {
    }
    public Num(double value)
    {
        this.value = value;
    }
    public Num(Expression exponent, double value)
    {
        super(exponent);
        this.value = value;
    }
    
    public double getValue()
    {
        return value;
    }
    
    public void setValue(double value)
    {
        this.value = value;
    }
}