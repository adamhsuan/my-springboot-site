package com.example.derivativecalculator;
import org.springframework.stereotype.Component;
import java.util.*;
@Component
public class Calculator
{   
    public DerivativeResponse getDerivative(String input, String variable, String order, String point) {
        if (input.equals("only The Universe knows this"))
        {
            return new DerivativeResponse("https://adamhsuan.github.io/happy-birthday/",null,null);
        }
        try {
            // validate order
            for (char c : order.toCharArray()) {
                if (!Character.isDigit(c)) {
                    throw new IllegalArgumentException("Order must be a positive integer.");
                }
            }
            int n = Integer.parseInt(order);
            if (n < 1) {
                throw new IllegalArgumentException("Order must be a positive integer.");
            } else if (n > 10) {
                throw new IllegalArgumentException("Order too high. Please enter an order less than or equal to 10.");
            }

            // build expression
            Expression expression = convertStringToExpression(input);
            expression.simplifyExpression();

            // differentiate n times
            for (int i = 0; i < n; i++) {
                expression = expression.takeDerivative(variable);
                expression.simplifyExpression();
            }

            // convert derivative back to string
            String derivativeString = expression.convertExpressionToString();

            // optional evaluation
            String valueString = null;
            String numericalValueString = null;
            if (point != null && !point.trim().isEmpty()) 
            {
                Expression pointExpr = convertStringToExpression(point.trim());
                Expression valueExpr = expression.evaluateAtPoint(variable, pointExpr); 
                valueString = valueExpr.convertExpressionToString();
                double pointFloat = pointExpr.evaluateAtPointNumerical(null, null);
                if (pointFloat>Integer.MIN_VALUE && pointFloat<Integer.MAX_VALUE && pointFloat==(int)pointFloat)
                    numericalValueString = (int)expression.evaluateAtPointNumerical(variable, pointFloat)+"";
                else 
                    numericalValueString = expression.evaluateAtPointNumerical(variable, pointFloat)+"";
            }
            return new DerivativeResponse(derivativeString, valueString,numericalValueString);

        } catch (Exception e) {
            return new DerivativeResponse("Error: " + e.getMessage(), null,null);
        }
    }

    public static Expression convertStringToExpression(String string)
    {
        string+=" ";
        Expression currentExpression = new Expression();
        Expression expression = new Expression();
        expression.addFactor(currentExpression);
        boolean isNum = false;
        float currentNum = 0;
        int decimalDigits = -1;
        boolean openAbsoluteValue = true;
        for (int i=0; i<string.length(); i++)
        {
            char character = string.charAt(i);
            if (character==' ')
            {
                currentExpression.setIsNoParenthesisArgument(false);
            }
            if ((!(Character.isDigit(character) || character=='.') && isNum==true))
            {
                Expression number;
                if (decimalDigits==-1)
                    number = new Num(currentExpression.getExponent(),currentNum);
                else
                {
                    number = new Expression(currentExpression.getExponent());
                    Term numberTerm = new Term();
                    numberTerm.addFactor(new Num(currentNum));
                    numberTerm.addFactor(new Num(Expression.createExpressionWithFactor(new Num(-1)),(float)Math.pow(10,decimalDigits)));
                    number.addTerm(numberTerm);
                }
                Term outerTerm = (Term) currentExpression.getOuterExpression();
                outerTerm.addFactor(number);
                if (currentExpression.getIsNoParenthesisArgument())
                {
                    outerTerm = (Term) outerTerm.getOuterExpression().getOuterExpression();

                }
                if (character=='^')
                    currentExpression = number;
                else
                {
                    currentExpression = new Expression();
                    outerTerm.addFactor(currentExpression);
                    currentNum = 0;
                    isNum=false;
                }
                currentExpression.setOuterExpression(outerTerm);
                decimalDigits = -1;
            }
            if (Character.isDigit(character))
            {
                isNum = true;
                currentNum = currentNum*10+(character-'0');
                if (decimalDigits!=-1)
                    decimalDigits++;
            }
            else if (character == '.')
            {
                decimalDigits = 0;
            }
            else if (character == '/')
            {
                
                Expression exponentExpression = new Expression();
                exponentExpression.addFactor(new Num(-1));
                currentExpression.setExponent(exponentExpression);
            }
            else if (character == '+' || character == '-')
            {
                Expression outerExpression = currentExpression.getOuterExpression().getOuterExpression();
                currentExpression = new Expression();
                if (character=='-')
                {
                    Term newTerm = new Term(new ArrayList<Expression>());
                    newTerm.addFactor(new Num(-1));
                    newTerm.addFactor(currentExpression);
                    outerExpression.addTerm(newTerm);
                }
                else
                    outerExpression.addFactor(currentExpression);
            }
            else if (Character.isLetter(character))
            {
                boolean isFunction = false;
                for (String functionName : new String[]{"sin","cos","tan","cot","sec","csc","arcsin","arccos","arctan","arccot","arcsec","arccsc","ln","sqrt"})
                {
                    if (i+functionName.length()<string.length() && string.substring(i,i+functionName.length()).equals(functionName))
                    {
                        isFunction = true;
                        i+=functionName.length()-1;
                        Term outerTerm = (Term) currentExpression.getOuterExpression();
                        Function function = new Function(currentExpression.getExponent(),functionName);
                        outerTerm.addFactor(function);
                        Expression argument = new Expression();
                        function.setArgument(argument);
                        currentExpression = new Expression();
                        argument.addFactor(currentExpression);
                        if (string.charAt(i+1)=='(')
                            i++;
                        else
                        {
                            currentExpression.setIsNoParenthesisArgument(true);
                        }
                    }
                }
                if (isFunction==false)
                {
                    Term outerTerm = (Term) currentExpression.getOuterExpression();
                    Variable variable = new Variable(currentExpression.getExponent(),Character.toString(character));
                    outerTerm.addFactor(variable);
                    if (currentExpression.getIsNoParenthesisArgument())
                    {
                        outerTerm = (Term) outerTerm.getOuterExpression().getOuterExpression();

                    }
                    variable.setOuterExpression(outerTerm);
                    currentExpression = new Expression();
                    if (i+1<string.length() && string.charAt(i+1)=='^')
                    {
                        currentExpression = variable;
                    }
                    else
                    {
                        outerTerm.addFactor(currentExpression);
                    }

                }
            }
            else if (character == '(' || character == '|' && openAbsoluteValue == true)
            {
                Expression newExpression = new Expression(currentExpression.getExponent());
                currentExpression.getOuterExpression().addFactor(newExpression);
                currentExpression = new Expression();
                newExpression.addFactor(currentExpression);
                if (character == '|')
                {
                    newExpression.setIsAbsoluteValue(true);
                    openAbsoluteValue = false;
                }
                else
                    openAbsoluteValue = true;
            }
            else if (character == ')' || character == '|' && openAbsoluteValue == false)
            {
                Expression outerExpression = currentExpression.getOuterExpression().getOuterExpression();
                if (i+1<string.length() && string.charAt(i+1)=='^')
                {
                    currentExpression = outerExpression;
                }
                else
                {
                    currentExpression = new Expression();
                    ((Term) outerExpression.getOuterExpression()).addFactor(currentExpression);
                }                
                if (!(character == ')' && outerExpression.getOuterExpression().getIsAbsoluteValue()))
                    openAbsoluteValue = true;
            }
            else if (character =='^')
            {   
                if (isNum=true)
                {
                    currentNum = 0;
                    isNum=false;
                }

                Expression base = currentExpression;
                base.setOuterExpression(currentExpression.getOuterExpression());
                Num negativeOne = new Num(-1);
                if (currentExpression.getExponent()!=null)
                {
                    currentExpression.setExponent(negativeOne);
                }
                Expression exponent = currentExpression.getExponent();
                currentExpression = new Expression();
                if (exponent==negativeOne)
                {
                    exponent=new Expression();
                    base.setExponent(exponent);
                    Term exponentTerm = new Term();
                    exponent.addTerm(exponentTerm);
                    exponentTerm.addFactor(negativeOne);
                    exponentTerm.addFactor(currentExpression);
                }
                else
                {
                    exponent = Expression.createExpressionWithFactor(currentExpression);
                    base.setExponent(exponent);
                }
                if (string.charAt(i+1)=='(')
                    i++;
                else
                    currentExpression.setIsNoParenthesisArgument(true);
                

            }
        }
        return expression;
    }
public static String convertExpressionToString(Expression expression)
    {
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
                String newString=convertExpressionToString(term);
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
                        string+=convertExpressionToString(factor);
                        
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
                    string+=convertExpressionToString(denominatorTerm);
                    string+=")";
                }
                else
                {
                    string+=convertExpressionToString(denominatorTerm);
                }   
            }
            if (string.length()>0&&string.charAt(string.length()-1)=='*')
                string=string.substring(0,string.length()-1);
        }
        else if (expression instanceof Num)
        {
            string+=((Num)expression).getValue();
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
            String argument = convertExpressionToString(((Function)expression).getArgument());
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
            string+="^"+convertExpressionToString(expression.getExponent());
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
