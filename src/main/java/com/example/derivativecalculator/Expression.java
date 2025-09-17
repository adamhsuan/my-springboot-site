package com.example.derivativecalculator;

import java.util.*;
import java.lang.Math;

public class Expression {

    private ArrayList<Expression> terms = new ArrayList<Expression>();
    private Expression exponent;
    private Expression outerExpression;
    private boolean isNoParenthesisArgument = false;
    private boolean isAbsoluteValue = false;
 
    public void printExpressionSymbolically()
    {
        System.out.println(Calculator.convertExpressionToString(this));
    }
    public Expression()
    {
    }
    
    public Expression(Expression exponent)
    {
        this.exponent = exponent; 
    }
    

    public Expression(Expression exponent,boolean isNoParenthesisArgument)
    {
        this.exponent = exponent; 
        this.isNoParenthesisArgument = isNoParenthesisArgument;
    }
    
    public Expression(ArrayList<Expression> terms)
    {
        this.terms = terms;
    }
    public Expression(ArrayList<Expression> terms, Expression exponent)
    {
        this.terms = terms;
        this.exponent = exponent;
    }

    public void setIsAbsoluteValue(boolean value)
    {
        this.isAbsoluteValue = value;
    }
    
    public boolean getIsAbsoluteValue()
    {
        return isAbsoluteValue;
    }
    
    public Expression getFirstFactor()
    {
        return ((Term)terms.get(0)).getFactors().get(0);
    }
    public Term getFirstTerm()
    {
        return (Term) terms.get(0);
    }
    public static Expression createExpressionWithFactor(Expression other)
    {
        Expression newExpression = new Expression();
        newExpression.addFactor(other);
        return newExpression;
    }
    
    public boolean hasOneFactor()
    {
        return (terms.size() == 1 && getFirstTerm().getFactors().size() == 1);
    }
    public static Expression createDeepCopy(Expression other)
    {
        Expression newExpression=new Expression();
        if (other.isInstanceOfExpression())
        {
            if (other.getIsAbsoluteValue())
                newExpression.setIsAbsoluteValue(true);
            for (Expression term : other.getTerms())
            {
                newExpression.addTerm(createDeepCopy(term));
            }
        }
        else if (other instanceof Term)
        {
            newExpression = new Term();
            for (Expression factor : ((Term)other).getFactors())
            {
                ((Term)newExpression).addFactor(createDeepCopy(factor));
            }
        }
        else if (other instanceof Num)
        {
            newExpression = new Num();
            ((Num)newExpression).setValue(((Num)other).getValue());
        }
        else if (other instanceof Variable)
        {
            newExpression = new Variable();
            ((Variable)newExpression).setSymbol(((Variable)other).getSymbol());
        }
        else if (other.isInstanceOfFunction())
        {
            newExpression = new Function();
            ((Function)newExpression).setType(((Function)other).getType());
            ((Function)newExpression).setArgument(createDeepCopy(((Function)other).getArgument()));
        }
        if (other.getExponent()!=null)
            newExpression.setExponent(createDeepCopy(other.getExponent()));
        return newExpression;
    }
    
    public ArrayList<Expression> getTerms()
    {
        return terms;
    }
    
    public void setTerms(ArrayList<Expression> terms)
    {
        this.terms = new ArrayList<Expression>();
        for (Expression term : terms)
            this.addTerm(term);
    }
    public Expression getExponent()
    {
        return exponent;
    }
    
    public void setExponent(Expression exponent)
    {
        this.exponent = exponent;
        if (exponent!=null)
            exponent.setOuterExpression(this.getOuterExpression());
    }
    
    public void addTerm(Expression term)
    {
        terms.add(term);
        term.setOuterExpression(this);

    }
    public boolean isInstanceOfFunction()
    {
        if (this instanceof Function)
            return true;
        return false;
    }
    public boolean isInstanceOfExpression()
    {
        if (!(this instanceof Term || this instanceof Num || this.isInstanceOfFunction() || this instanceof Variable))
            return true;
        return false;
    }
    public static ArrayList<Expression> removeEmptyExpressions(ArrayList<Expression> expressions)
    {
        ArrayList<Expression> newExpressions = new ArrayList<Expression>();
        for (Expression expression : expressions)
        {   
            if (expression.getExponent()!=null)
            {
                Expression newExponent;
                if (expression.getExponent().isInstanceOfExpression())
                    newExponent = new Expression(removeEmptyExpressions(expression.getExponent().getTerms()));
                else
                    newExponent = expression.getExponent();
                expression.setExponent(newExponent);
            }
            if (expression.isInstanceOfFunction() && ((Function)expression).getArgument()!=null)
            {
                Expression newArgument = new Expression(removeEmptyExpressions(((Function)expression).getArgument().getTerms()));
                ((Function)expression).setArgument(newArgument);
            }
            if (expression.isInstanceOfExpression() && expression.getTerms()!=null)
            {
                ArrayList<Expression> newTerms = removeEmptyExpressions(expression.getTerms());
                expression.setTerms(newTerms);
            }
            else if (expression instanceof Term && ((Term)expression).getFactors()!=null)
            {
                ArrayList<Expression> newFactors = removeEmptyExpressions(((Term)expression).getFactors());
                ((Term)expression).setFactors(newFactors);
            }
            if (!(expression.isInstanceOfExpression() && (expression.getTerms()==null || expression.getTerms().size()==0) || expression instanceof Term && (((Term)expression).getFactors() == null || ((Term)expression).getFactors().size()==0)))
            {
                newExpressions.add(expression);
            }
        }
        return newExpressions;
        
    }
    public void addFactor(Expression factor)
    {
        Term term = new Term(new ArrayList<Expression>());
        addTerm(term);
        term.addFactor(factor);
    }
    public Expression getOuterExpression()
    {
        return outerExpression;
    }
    
    public void setOuterExpression(Expression outerExpression)
    {
        this.outerExpression = outerExpression;
    }
    
    public boolean getIsNoParenthesisArgument()
    {
        return isNoParenthesisArgument;
    }
    public void setIsNoParenthesisArgument(boolean isNoParenthesisArgument)
    {
        this.isNoParenthesisArgument = isNoParenthesisArgument;
    }
    public void removeTerm(int index)
    {
        terms.remove(index);
    }
    public boolean equals(Expression other)
    {
        if (this.getExponent() == null && other.getExponent() != null || this.getExponent() != null && other.getExponent() == null)
            return false;
        if (this.getExponent() != null)
        {
            Expression thisExponent = this.getExponent();
            Expression otherExponent = other.getExponent();
            if (!(thisExponent.equals(otherExponent)))
            {
                return false;
            }
        }
        if (this instanceof Term && other instanceof Term)
        {
            ArrayList<Expression> thisFactors = new ArrayList<Expression>(((Term)this).getFactors());
            ArrayList<Expression> otherFactors = new ArrayList<Expression>(((Term)other).getFactors());
            for (int i=thisFactors.size()-1; i>=0; i--)
            {
                for (int j=otherFactors.size()-1; j>=0; j--)
                {
                    if (i<thisFactors.size() && (thisFactors.get(i)).equals(otherFactors.get(j)))
                    {
                        thisFactors.remove(thisFactors.get(i));
                        otherFactors.remove(otherFactors.get(j));
                    }
                }
            }
            if (thisFactors.size()==0 && otherFactors.size()==0)
                return true;
            return false;
        }
        else if (this.isInstanceOfFunction() && other.isInstanceOfFunction())
        {
            if (((String)((Function)this).getType()).equals(((String)((Function)other).getType())) && ((Function)this).getArgument().equals(((Function)other).getArgument()))
                return true;
            return false;
            
        }
        else if (this instanceof Variable && other instanceof Variable)
        {
            if(((Variable)this).getSymbol().equals(((Variable)other).getSymbol()))
                return true;
            return false;
        }
        else if (this instanceof Num && other instanceof Num)
        {
            if (((Num)this).getValue() == ((Num)other).getValue())
            {
                return true;
            }
            return false;
        }
        else if (this.isInstanceOfExpression() && other.isInstanceOfExpression() && this.getIsAbsoluteValue() == other.getIsAbsoluteValue())
        {
            ArrayList<Expression> thisTerms = new ArrayList<Expression>(this.getTerms());
            ArrayList<Expression> otherTerms = new ArrayList<Expression>(other.getTerms());
            for (int i=thisTerms.size()-1; i>=0; i--)
            {
                for (int j=otherTerms.size()-1; j>=0; j--)
                {
                    if (i<thisTerms.size() && (thisTerms.get(i)).equals(otherTerms.get(j)))
                    {
                        thisTerms.remove(i);
                        otherTerms.remove(j);
                    }
                }
            }
            if (thisTerms.size()==0 && otherTerms.size()==0)
                return true;
        }
        return false;
    }
    
    public static boolean isNumeric(Expression expression)
    {
        if (expression.isInstanceOfExpression())
        {
            for (Expression term : expression.getTerms())
            {
                if (!(isNumeric(term)))
                {
                    return false;
                }
            }
            return true;
        }
        else if (expression instanceof Term)
        {
            for (Expression factor : ((Term)expression).getFactors())
            {
                if (!(isNumeric(factor)))
                {
                    return false;
                }
            }
            return true;
        }
        else if (expression instanceof Num)
        {
            return true;
        }
        return false;
    }
    
    public void simplifyNumericExpression()
    {
        
        //exponents should be either null or -1
        //must pass isNumeric() and be an expression containing terms
        //terms should have one value in the numerator and one in the denominator
        //if an exponent is rational it won't simplify
        //there shouldnt be any absolute values, those should have been cleared in simplify term
        setTerms(removeEmptyExpressions(getTerms()));
        removeExtraOnesAndZeroes();
        double numerator=0;
        double denominator=1;
        Expression negativeOne = createExpressionWithFactor(new Num(-1));
        for (int i=0; i<terms.size(); i++)
        {
            if (((Term)terms.get(i)).getFactors().size()==1 && ((Term)terms.get(i)).getFactors().get(0).getExponent()!=null && ((Term)terms.get(i)).getFactors().get(0).getExponent().equals(negativeOne))
            {
                ((Term)terms.get(i)).addFactor(new Num(1));
            }
            double termNumerator = 0;
            double termDenominator = 1;
            boolean containsRational=false;
            for (Expression factor : ((Term)terms.get(i)).getFactors())
            {
                if (factor.getExponent()==null)
                {
                    termNumerator = ((Num)factor).getValue();
                }
                else if (factor.getExponent().equals(negativeOne))
                    termDenominator = ((Num)factor).getValue();
                else
                    containsRational=true;
            }
            if (containsRational==false)
            {
                numerator=numerator*termDenominator+termNumerator*denominator;
                denominator*=termDenominator;
                terms.remove(i);
                i--;
            }
        }
        if (numerator!=0 || terms.size()==0)
        {
            Term newTerm = new Term();
            newTerm.addFactor(new Num(numerator));
            newTerm.addFactor(new Num(negativeOne,denominator));
            newTerm.simplifyTerm();
            addTerm(newTerm);
        }
        setTerms(removeEmptyExpressions(getTerms()));
        removeExtraOnesAndZeroes();
    }
    
    public void moveInnerExpressionsOut()
    {
        if(true)
        {
            for (int i=0; i<terms.size(); i++)
            {

                if ((((Term)terms.get(i)).getFactors().size()==1 && ((Term)terms.get(i)).getFactors().get(0).isInstanceOfExpression()) && !((Term)terms.get(i)).getFactors().get(0).getIsAbsoluteValue() && ((Term)terms.get(i)).getFactors().get(0).getExponent()==null)
                {  
                    Expression innerExpression = ((Term)terms.get(i)).getFactors().get(0);
                    for (Expression term : innerExpression.getTerms())
                        addTerm(term);
                    terms.remove(i);
                    i--;
                }
            }
        }
        
    }
    public void removeExtraOnesAndZeroes()
    {
        for (int i=0; i<terms.size(); i++)
        {
            boolean equalsZero=false;
            for (int j=0; j<((Term)terms.get(i)).getFactors().size(); j++)
            {
                Expression factor = ((Term)terms.get(i)).getFactors().get(j);
                if (factor.isInstanceOfFunction())
                {
                    Expression newExpression = createExpressionWithFactor(new Variable("e"));
                    if (((Function)factor).getType()=="ln"&&((Function)factor).getArgument()!=null&&((Function)factor).getArgument().equals(newExpression))
                        factor=new Num(1);
                }
                if (factor instanceof Num)
                {
                    if (((Num)factor).getValue()==0)
                    {
                    equalsZero=true;
                    break;
                    }
                    else if (((Num)factor).getValue()==1 && (((Num)factor).getExponent()==null ||  isNumeric(((Num)factor).getExponent())) && ((Term)terms.get(i)).getFactors().size()>1)
                    {
                        ArrayList<Expression> factors = ((Term)terms.get(i)).getFactors();
                        factors.remove(j);
                        ((Term)terms.get(i)).setFactors(factors);
                        j--;
                    }
                }
                
            }
            if (equalsZero==true)
            {
                if (terms.size()>1)
                {
                    terms.remove(i);
                    i--;
                }
                else
                {
                    ArrayList<Expression> factor = new ArrayList<Expression>();
                    factor.add(new Num(0));
                    ((Term)terms.get(i)).setFactors(factor);
                }
                
            }
        }
    }
    
    public void combineLikeTerms()
    {
        if (terms.size()>1)
        {
            if (isNumeric(this))
            {
                simplifyNumericExpression();
            }
            else
            {
                for (int i=0; i<terms.size(); i++)
                {
                    Term newTerm = new Term();
                    Expression numericFactorsExpression = new Expression();
                    Expression nonNumericFactorsExpression = new Expression();
                    Term numericFactors = new Term();
                    numericFactors.addFactor(new Num(1));
                    Term nonNumericFactors = new Term();
                    for (Expression factor : ((Term)terms.get(i)).getFactors())
                    {
                        if (isNumeric(factor))
                            numericFactors.addFactor(factor);
                        else
                            nonNumericFactors.addFactor(factor);
                    }
                    
                    Term numericFactors2 = (Term) createDeepCopy(numericFactors);
                    Term nonNumericFactors2 = (Term) createDeepCopy(nonNumericFactors);
                    for (int j=i+1; j<terms.size(); j++)
                    {
                        Term numericFactors3 = new Term();
                        numericFactors3.addFactor(new Num(1));
                        Term nonNumericFactors3 = new Term();
                        for (Expression factor : ((Term)terms.get(j)).getFactors())
                        {
                        if (isNumeric(factor))
                            numericFactors3.addFactor(factor);
                        else
                            nonNumericFactors3.addFactor(factor);
                        }
                        if (nonNumericFactors.equals(nonNumericFactors3))
                        {
                            numericFactorsExpression.addTerm(numericFactors3);
                            terms.remove(j);
                            j--;
                        }
                    }
                    
                    if (numericFactorsExpression.getTerms().size()>0)
                    {
                        numericFactorsExpression.addTerm(numericFactors);
                        numericFactorsExpression.simplifyNumericExpression();
                        nonNumericFactorsExpression.addTerm(nonNumericFactors);
                        newTerm.addFactor(numericFactorsExpression);
                        newTerm.addFactor(nonNumericFactorsExpression);
                        newTerm.simplifyTerm();
                        ((Term)terms.get(i)).setFactors(newTerm.getFactors());
                    }
                    
                    else
                    {
                        Expression newExpression = new Expression();
                        newExpression.addTerm(numericFactors2);
                        newExpression.simplifyNumericExpression();
                        numericFactors2=(Term)(newExpression.getTerms().get(0));
                        ((Term)terms.get(i)).setFactors(((Term)numericFactors2).getFactors());
                        for (Expression factor : ((Term)nonNumericFactors2).getFactors())
                        {
                            ((Term)terms.get(i)).addFactor(factor);
                        }
                        
                    }
        
                }
            }
        }
    }
    
    public void convertOneHalfPowersToSqrt()
    {
        Expression oneHalf = createExpressionWithFactor(new Num(2));
        oneHalf.getFirstFactor().setExponent(createExpressionWithFactor(new Num(-1)));
        Expression negativeOneHalf = createDeepCopy(oneHalf);
        negativeOneHalf.addFactor(new Num(-1));

        for (int i=0; i<terms.size(); i++)
        {
            for (int j=0; j<((Term)terms.get(i)).getFactors().size(); j++)
            {
                Expression factor = ((Term)terms.get(i)).getFactors().get(j);
                if (factor.isInstanceOfExpression() && factor.getTerms().size()>0)
                    factor.convertOneHalfPowersToSqrt();
                if (factor.getExponent()!=null && (factor.getExponent().equals(oneHalf) || factor.getExponent().equals(negativeOneHalf)))
                {
                    Function sqrtFactor = new Function();
                    sqrtFactor.setType("sqrt");
                    sqrtFactor.setArgument(createExpressionWithFactor(factor));
                    if (factor.getExponent().equals(negativeOneHalf))
                        sqrtFactor.setExponent(createExpressionWithFactor(new Num(-1)));
                    factor.setExponent(null);
                    ((Term)terms.get(i)).getFactors().set(j,sqrtFactor);
                }
            }
        }
    }
    public void simplifyExpression()
    {
        setTerms(removeEmptyExpressions(getTerms()));
        for (int i=0; i<terms.size(); i++) 
        {
            ((Term)terms.get(i)).simplifyTerm();
        }
        moveInnerExpressionsOut();
        removeExtraOnesAndZeroes();
        combineLikeTerms();
    }
    
    public Expression takeDerivative(String variableName)
    {
        
        Expression newExpression = new Expression();
        if (this.getExponent()!=null)
        {
            Term newTerm = new Term();
            
            Expression base1 = createDeepCopy(this);
            Expression base2 = createDeepCopy(this);
            Expression base3 = createDeepCopy(this);
            
            Expression exponent = createDeepCopy(this.getExponent());
            Expression exponent2 = createDeepCopy(this.getExponent());
            
            base1.setExponent(null);
            base2.setExponent(null);
            base3.setExponent(null);
            
            Expression newExpression2 = new Expression();
            Term newTerm1 = new Term();
            newTerm1.addFactor(base3.takeDerivative(variableName));
            
            Expression newExpression3 = createExpressionWithFactor(base1);

            newExpression3.setExponent(createExpressionWithFactor(new Num(-1)));
            newTerm1.addFactor(newExpression3);
            newTerm1.addFactor(exponent2);
            Term newTerm2 = new Term();
            newTerm2.addFactor(exponent.takeDerivative(variableName));
            newTerm2.addFactor(new Function("ln",createExpressionWithFactor(base2)));
            newExpression2.addTerm(newTerm1);
            newExpression2.addTerm(newTerm2);
            newTerm.addFactor(newExpression2);
            newTerm.addFactor(this);
            newExpression.addTerm(newTerm);
        }
        else if (isInstanceOfExpression())
        {
            if (isAbsoluteValue) 
            {

                Term term = new Term();
                Expression nonAbsoluteValueExpression = createDeepCopy(this);
                nonAbsoluteValueExpression.setIsAbsoluteValue(false);
                term.addFactor(nonAbsoluteValueExpression);
                Expression absoluteValueExpression = createDeepCopy(this);
                absoluteValueExpression.setExponent(createExpressionWithFactor(new Num(-1)));
                term.addFactor(absoluteValueExpression);
                term.addFactor(createDeepCopy(nonAbsoluteValueExpression).takeDerivative(variableName));
                newExpression.addTerm(term);
                
            }
            else
            {
                for (Expression term : getTerms())
                {
                    term = term.takeDerivative(variableName);
                    
                    newExpression.addTerm(term);
                }
            }
            
        }
        else if (this instanceof Term)
        {

            for (int i=0; i<((Term)this).getFactors().size(); i++)
            {
                Term newTerm = new Term();
                for (int j=0; j<((Term)this).getFactors().size(); j++)
                {
                    if (i==j)
                        newTerm.addFactor(createDeepCopy(((Term)this).getFactors().get(i)).takeDerivative(variableName));
                    else
                        newTerm.addFactor(createDeepCopy(((Term)this).getFactors().get(j)));
                }
                newExpression.addTerm(newTerm);
            }
            Term newTerm2 = new Term();
            newTerm2.addFactor(newExpression);
            ((Term)newTerm2).simplifyTerm();
            return newTerm2;
        }
        else if (this instanceof Num)
        {
            return new Num(0);
        }
        
        else if (this instanceof Variable)
        {
            if (((Variable)this).getSymbol().equals(variableName))
            {
                return new Num(1);
            }
            else
                return new Num(0);
        }
        else if (this.isInstanceOfFunction())
        {
            String type = ((Function)this).getType();
            Term newTerm = new Term();
            Expression argument = createDeepCopy(((Function)this).getArgument());
            if (type.equals("sin"))
            {
                Function newFunction = new Function("cos",argument);
                newTerm.addFactor(createDeepCopy(((Function)this).getArgument()).takeDerivative(variableName));
                newTerm.addFactor(newFunction);
                newExpression.addTerm(newTerm);
            }
            else if (type.equals("cos"))
            {
                
                Function newFunction = new Function("sin",argument);
                newTerm.addFactor(new Num(-1));
                newTerm.addFactor(createDeepCopy(((Function)this).getArgument()).takeDerivative(variableName));
                newTerm.addFactor(newFunction);
                newExpression.addTerm(newTerm);
            }
            else if (type.equals("tan"))
            {
                Function newFunction = new Function(createExpressionWithFactor(new Num(2)),"sec",((Function)this).getArgument());
                newTerm.addFactor(createDeepCopy(((Function)this).getArgument()).takeDerivative(variableName));
                newTerm.addFactor(newFunction);
                newExpression.addTerm(newTerm);
            }
            else if (type.equals("cot"))
            {
                Function newFunction = new Function(createExpressionWithFactor(new Num(2)),"csc",((Function)this).getArgument());
                newTerm.addFactor(new Num(-1));
                newTerm.addFactor(createDeepCopy(((Function)this).getArgument()).takeDerivative(variableName));
                newTerm.addFactor(newFunction);
                newExpression.addTerm(newTerm);
            }
            else if (type.equals("sec"))
            {
                Expression argument1 = createDeepCopy(((Function)this).getArgument());
                Expression argument2 = createDeepCopy(((Function)this).getArgument());
                Function newFunction1 = new Function("sec",argument1);
                Function newFunction2 = new Function("tan",argument2);
                newTerm.addFactor(createDeepCopy(((Function)this).getArgument()).takeDerivative(variableName));
                newTerm.addFactor(newFunction1);
                newTerm.addFactor(newFunction2);
                newExpression.addTerm(newTerm);
            }
            else if (type.equals("csc"))
            {
                Expression argument1 = createDeepCopy(((Function)this).getArgument());
                Expression argument2 = createDeepCopy(((Function)this).getArgument());
                Function newFunction1 = new Function("csc",argument1);
                Function newFunction2 = new Function("cot",argument2);
                newTerm.addFactor(new Num(-1));
                newTerm.addFactor(createDeepCopy(((Function)this).getArgument()).takeDerivative(variableName));
                newTerm.addFactor(newFunction1);
                newTerm.addFactor(newFunction2);
                newExpression.addTerm(newTerm);
            }
            
            else if (type.equals("arcsin") || type.equals("arccos") || type.equals("arctan") || type.equals("arccot") || type.equals("arcsec") || type.equals("arccsc"))
            {
                Expression newExpression2 = new Expression();
                Expression newExpression3 = new Expression();
                Term newTerm2 = new Term();
                newTerm2.addFactor(new Num(-1));
                if (!(type.equals("arctan") || type.equals("arccot")))
                    newTerm2.addFactor(new Num(new Num(-1),2));
                newExpression3.addTerm(newTerm2);
                newExpression2.setExponent(newExpression3);
                if (!(type.equals("arcsec") || type.equals("arccsc")))
                    newExpression2.addFactor(new Num(1));
                Expression newExpression4 = new Expression();
                Expression argument1 = createDeepCopy(((Function)this).getArgument());
                argument1.setExponent(createExpressionWithFactor(new Num(2)));
                Term newTerm3 = new Term();
                if (type.equals("arcsin") || type.equals("arccos"))
                    newTerm3.addFactor(new Num(-1));
                newTerm3.addFactor(argument1);
                newExpression4.addTerm(newTerm3);
                newExpression2.addFactor(newExpression4);
                newTerm.addFactor(createDeepCopy(((Function)this).getArgument()).takeDerivative(variableName));
                if (type.equals("arcsec") || type.equals("arccsc"))
                {
                    newExpression2.addFactor(new Num(-1));
                    Expression absoluteValueExpression = createDeepCopy(((Function)this).getArgument());
                    absoluteValueExpression.setIsAbsoluteValue(true);
                    absoluteValueExpression.setExponent(createExpressionWithFactor(new Num(-1)));
                    newTerm.addFactor(absoluteValueExpression);
                }
                newTerm.addFactor(newExpression2);

                if (type.equals("arccos") || type.equals("arccot") || type.equals("arccsc"))
                {
                    newTerm.addFactor(new Num(-1));
                }
                newExpression.addTerm(newTerm);
            }
            
            else if (type.equals("ln"))
            {
                newTerm.addFactor(createDeepCopy(((Function)this).getArgument()).takeDerivative(variableName));
                Expression exponent = new Expression();

                if (((Function)this).getExponent()!=null)
                {
                    Term exponentTerm = new Term();
                    exponentTerm.addFactor(((Function)this).getExponent());
                    exponentTerm.addFactor(new Num(-1));
                    exponent.addTerm(exponentTerm);
                }
                else
                    exponent.addFactor(new Num(-1));

                argument.setExponent(exponent);
                newTerm.addFactor(argument);

                newExpression.addTerm(newTerm);
            }
        }
        newExpression.simplifyExpression();

        return newExpression;
    }


public Expression evaluateAtPoint(String variable, Expression point)
{
    Expression expression = createDeepCopy(this);
    if (expression.getExponent()!=null)
    {
        Expression newExponent = expression.getExponent().evaluateAtPoint(variable,point);
        expression.setExponent(newExponent);
    }
    if (expression.isInstanceOfExpression())
    {
        Expression newExpression = new Expression();
        for (Expression term : expression.getTerms())
        {
            newExpression.addTerm(term.evaluateAtPoint(variable,point));
        }
        newExpression.simplifyExpression();
        return newExpression;
    }
    else if (expression instanceof Term)
    {
        Term newTerm = new Term();
        for (Expression factor : ((Term)expression).getFactors())
        {
            newTerm.addFactor(factor.evaluateAtPoint(variable,point));
        }
        newTerm.simplifyTerm();
        return newTerm;
    }
    else if (expression instanceof Variable)
    {     
        if (((Variable)expression).getSymbol().equals(variable))
            if (expression.getExponent()!=null)
                {
                    Expression newExpression = createExpressionWithFactor(point);
                    newExpression.setExponent(expression.getExponent());
                    return newExpression;
                }
            else
                return point;
        else
            return createDeepCopy(expression);
    }
    else if (expression instanceof Num)
    {
        return createDeepCopy(expression);
    }
    else if (expression.isInstanceOfFunction())
    {
        Function newFunction = new Function();
        newFunction.setType(((Function)expression).getType());
        newFunction.setArgument(((Function)expression).getArgument().evaluateAtPoint(variable,point));
        if (((Function)expression).getExponent()!=null)
            newFunction.setExponent(((Function)expression).getExponent().evaluateAtPoint(variable,point));
        return newFunction;
    }
    else return createDeepCopy(expression);
}
//simplifes, turning fractions into decimals and plugging in values for functions, exponents, etc.
public double evaluateAtPointNumerical(String variable, Double point)
{
    if (this.isInstanceOfExpression())
    {
        double sum = 0;
        for (Expression term : this.getTerms())
        {
            sum += term.evaluateAtPointNumerical(variable,point);
        }
        return sum;
    }
    else if (this instanceof Term)
    {
        double product = 1;
        for (Expression factor : ((Term)this).getFactors())
        {
            product *= factor.evaluateAtPointNumerical(variable,point);
        }
        return product;
    }
    else if (this instanceof Variable)
    {
        if (((Variable)this).getSymbol().equals(variable))
        {
            if (point != null)
                if (this.getExponent()!=null)
                    return Math.pow(point,this.getExponent().evaluateAtPointNumerical(variable,point));
                else
                    return point;
            else
                throw new IllegalArgumentException("Point must be a numeric value.");
        }
        else
            throw new IllegalArgumentException("Cannot evaluate expression with undefined variable " + ((Variable)this).getSymbol());
    }
    else if (this instanceof Num)
    {
        if (this.getExponent()!=null)
        {
            return Math.pow(((Num)this).getValue(),this.getExponent().evaluateAtPointNumerical(variable,point));
        }
        else
            return ((Num)this).getValue();
    }
    else if (this.isInstanceOfFunction())
    {
        double argumentValue = ((Function)this).getArgument().evaluateAtPointNumerical(variable,point);
        double baseValue = 0;
        String type = ((Function)this).getType();
        if (type.equals("sin"))
            baseValue = (double)Math.sin(argumentValue);
        else if (type.equals("cos"))
            baseValue = (double)Math.cos(argumentValue);
        else if (type.equals("tan"))
            baseValue = (double)Math.tan(argumentValue);
        else if (type.equals("cot"))
            baseValue = (double)(1/Math.tan(argumentValue));
        else if (type.equals("sec"))
            baseValue = (double)(1/Math.cos(argumentValue));
        else if (type.equals("csc"))
            baseValue = (double)(1/Math.sin(argumentValue));
        else if (type.equals("arcsin"))
            baseValue = (double)Math.asin(argumentValue);
        else if (type.equals("arccos"))
            baseValue = (double)Math.acos(argumentValue);
        else if (type.equals("arctan"))
            baseValue = (double)Math.atan(argumentValue);
        else if (type.equals("arccot"))
            baseValue = (double)(Math.PI/2 - Math.atan(argumentValue));
        else if (type.equals("arcsec"))
            baseValue = (double)Math.acos(1/argumentValue);
        else if (type.equals("arccsc"))
            baseValue = (double)Math.asin(1/argumentValue);
        else if (type.equals("ln"))
            baseValue = (double)Math.log(argumentValue);
        else if (type.equals("sqrt"))
            baseValue = (double)Math.sqrt(argumentValue);
        if (this.getExponent()!=null)
        {
            double exponentValue = this.getExponent().evaluateAtPointNumerical(variable,point);
            return (double)Math.pow(baseValue,exponentValue);
        }
        else
            return baseValue;
}
    else
        throw new IllegalArgumentException("Cannot evaluate expression with undefined variable.");
}
public String convertExpressionToString()
    {
        Expression expression = createDeepCopy(this);
        Expression oneHalf = Expression.createExpressionWithFactor(new Num(2));
        oneHalf.getFirstFactor().setExponent(Expression.createExpressionWithFactor(new Num(-1)));
        Expression negativeOneHalf = Expression.createDeepCopy(oneHalf);
        negativeOneHalf.addFactor(new Num(-1));
        if (expression.getExponent() != null)
        {
            if (expression.getExponent().equals(oneHalf) || expression.getExponent().equals(negativeOneHalf))
            {
                Function sqrtExpression = new Function();
                sqrtExpression.setType("sqrt");
                if (expression.getExponent().equals(negativeOneHalf))
                    sqrtExpression.setExponent(Expression.createExpressionWithFactor(new Num(-1)));
                Expression argument = Expression.createDeepCopy(expression);
                argument.setExponent(null);
                argument = Expression.createExpressionWithFactor(argument);
                sqrtExpression.setArgument(argument);
                expression = sqrtExpression;
            }
            
        }
        String string = "";     
        if (expression.isInstanceOfExpression())
        {
            if (expression.getIsAbsoluteValue())
                string+="|";
            else
                string+="(";
            for (Expression term : expression.getTerms())
            {
                String newString=term.convertExpressionToString();
                if (newString.length()>0&&newString.charAt(0)=='-'&&!(string.equals("(")))
                    string=string.substring(0,string.length()-1);
                string+=newString;
                if (string.length()>0)
                {
                    if (string.charAt(string.length()-1)=='*')
                    {
                        string=string.substring(0,string.length()-1);
                    }
                }
                string+="+";
            }
            if (expression.getTerms().size()>0)
            {
                string=string.substring(0,string.length()-1);
            }
            if (expression.getIsAbsoluteValue())
                string+="|";
            else
                string+=")";
            if (expression.getOuterExpression()==null)
                string=string.substring(1,string.length()-1);
        }
        else if (expression instanceof Term)
        {
            Term denominatorTerm = new Term();
            for (Expression factor : ((Term)expression).getFactors())
            {
                boolean inDenominator=false;
                if (factor.getExponent()!=null&&factor.getExponent().getTerms().size()==1)
                {
                    for (Expression exponentFactor: ((Term)factor.getExponent().getTerms().get(0)).getFactors())
                    {
                        if (exponentFactor instanceof Num && ((Num)exponentFactor).getValue()<0)
                        {
                            Expression newFactor = Expression.createDeepCopy(factor);
                            Term newExponentTerm = (Term) Expression.createDeepCopy(factor.getExponent().getTerms().get(0));
                            newExponentTerm.addFactor(new Num(-1));
                            Expression newExponentExpression = new Expression();
                            newExponentExpression.addTerm(newExponentTerm);
                            newExponentExpression.simplifyExpression();
                            newFactor.setExponent(newExponentExpression);
                            denominatorTerm.addFactor(newFactor);
                            inDenominator=true;
                            break;
                        }
                    }
                }
                if (inDenominator==false)
                {
                    if (string.equals("-1"))
                    {
                        string="-";
                    }
                    if (string.length()>0)
                    {
                        if (Character.isDigit(string.charAt(string.length()-1)) && factor instanceof Num)
                        {
                            string+="*";
                        }
                    }
                    if (factor instanceof Num && ((Num)factor).getValue()<0 && !string.equals(""))
                        string+="*";
                    if (!(factor instanceof Num && ((Num)factor).getValue()==1 && string.equals("") && ((Term)expression).getFactors().size()>1))
                        string+=factor.convertExpressionToString();
                        
                }
            }
            denominatorTerm.simplifyTerm();
            if (denominatorTerm.getFactors().size()>0&&!(denominatorTerm.getFactors().size()==1&&denominatorTerm.getFactors().get(0) instanceof Num&&((Num)denominatorTerm.getFactors().get(0)).getValue()==1&&denominatorTerm.getFactors().get(0).getExponent()==null))
            {
                if (string.length()>0)
                {
                    if (string.charAt(string.length()-1)=='*')
                        string=string.substring(0,string.length()-1);
                }
                else
                    string+="1";
                string+="/";
                if (denominatorTerm.getFactors().size()>1)
                {
                    string+="(";
                    string+=denominatorTerm.convertExpressionToString();
                    string+=")";
                }
                else
                {
                    string+=denominatorTerm.convertExpressionToString();
                }   
            }
            if (string.length()>0&&string.charAt(string.length()-1)=='*')
                string=string.substring(0,string.length()-1);
        }
        else if (expression instanceof Num)
        {
            double value = ((Num)expression).getValue();
            if (value<Integer.MAX_VALUE && value>Integer.MIN_VALUE && value==(int)value)
                string+=(String)(Integer.toString((int)value));
            else
                string+=value;
        }
        else if (expression instanceof Variable)
        {
            string+=((Variable)expression).getSymbol();
        }
        else if (expression instanceof Function)
        {
            if (expression.getExponent()!=null)
                string+="(";
            string+=((Function)expression).getType();
            string+="(";
            String argument = ((Function)expression).getArgument().convertExpressionToString();
            if (argument.length()>0 && argument.charAt(0)=='(')
                argument = argument.substring(1,argument.length()-1);
            string+=argument;
            string+=")";
            if (expression.getExponent()!=null)
                string+=")";
        }
        int index=string.length();
        if (expression.getExponent()!=null)
        {
            string+="^"+expression.getExponent().convertExpressionToString();
            if (string.length()>index+2 && string.charAt(index+1)=='('&&expression.getExponent().getTerms().size()==1 && ((Term)expression.getExponent().getTerms().get(0)).getFactors().size()==1 && ((Term)expression.getExponent().getTerms().get(0)).getFactors().get(0).getExponent()==null)
            {
                string=string.substring(0,index+1)+string.substring(index+2,string.length()-1);
                if (((Term)expression.getExponent().getTerms().get(0)).getFactors().get(0) instanceof Num && ((Num)((Term)expression.getExponent().getTerms().get(0)).getFactors().get(0)).getValue()==1)
                    string=string.substring(0,string.length()-2);
            }
            string+="*";
        }
        return string;
    }
}