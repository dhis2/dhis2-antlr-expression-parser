package org.hisp.dhis.antlr;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.math.BigDecimal;
import java.math.MathContext;

import static org.hisp.dhis.antlr.AntlrParserUtils.castDouble;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

@RunWith( JUnit4.class )
public class MathExpressionTest
{
    private TestExpressionVisitor visitor = new TestExpressionVisitor();

    @Test
    public void testSum() {
        assertEquals( 2.0, evaluate( "1 + 1" ) );
        assertEquals( 2.3, evaluate( "1.3 + 1" ) );
        assertEquals( 3.3, evaluate( "1.3 + ( 1 + 1 )" ) );
    }

    @Test
    public void testSub() {
        // This is why we need to convert everything to BigDecimal in the antlr math operators
        // Double and Float cannot be used for precise calculations
        // https://docs.oracle.com/javase/tutorial/java/nutsandbolts/datatypes.html
        assertNotEquals( 0.3, 1.3 - 1);
        assertEquals( 0.0, evaluate( "+ 1 - 1" ) );
        assertEquals( 0.3, evaluate( "1.3 - 1" ) );
        assertEquals( -0.7, evaluate( "1.3 - ( 1 + 1 )" ) );
    }

    @Test
    public void testDivide() {
        BigDecimal doubleWithLotsOfDigitsAfterComma = BigDecimal.valueOf( (Double) evaluate( "10/3" ) ).round( MathContext.DECIMAL64 );
        assertEquals( BigDecimal.valueOf( 3.333333333333333 ), doubleWithLotsOfDigitsAfterComma );
        assertEquals( 1.0, evaluate( "1/1" ) );
        assertEquals( 0.5, evaluate( "1/2" ) );
        assertEquals( -0.7, evaluate( "-1.4/( 1 + 1 )" ) );
        assertEquals( 1.0, evaluate( "( 1 / 1000 ) * 1000" ) );
        assertEquals( Double.NaN, evaluate( "1.0/( 1 - 1 )" ) );
    }

    @Test
    public void testMultiply() {
        assertEquals( 1.0, evaluate( "1*1" ) );
        assertEquals( 2.0, evaluate( "1*2" ) );
        assertEquals( -2.8, evaluate( "-1.4*( 1 + 1 )" ) );
    }

    @Test
    public void testLog() {
        assertEquals( 4.605170, evaluateDouble( "log(100)" ), .000001 );
        assertEquals( -0.693147, evaluateDouble( "log( .5 )" ), .000001 );
        assertEquals( 3, evaluateDouble( "log(8,2)" ), .000001 );
        assertEquals( 2, evaluateDouble( "log(256, 16)" ), .000001 );
        assertEquals( 2, evaluateDouble( "log( 100, 10 )" ), .000001 );
    }

    @Test
    public void testLog10() {
        assertEquals( 2, evaluateDouble( "log10(100)" ), .000001 );
        assertEquals( -0.301030, evaluateDouble( "log10( .5 )" ), .000001 );
    }

    @Test
    public void testPower() {
        assertEquals( 1.0, evaluate( "1^10" ) );
        assertEquals( 25.0, evaluate( "5^2" ) );
        assertEquals( 1.96, evaluate( "1.4^( 1 + 1 )" ) );
        assertEquals( 1.0, evaluate( "1.4^( 1 - 1 )" ) );
        assertEquals( 4.0, evaluate( "2^2" ) );
        assertEquals( 0.25, evaluate( "2^-2" ) );
    }

    @Test
    public void testModule() {
        assertEquals( 1.0, evaluate( "1%2" ) );
        assertEquals( 1.0, evaluate( "5%2" ) );
        assertEquals( 1.4, evaluate( "1.4%( 1 + 1 )" ) );
        assertEquals( Double.NaN, evaluate( "1.0%( 1 - 1 )" ) );
    }

    private Object evaluate( String expression ) {
        return Parser.visit( expression, visitor );
    }

    private double evaluateDouble( String expression ) {
        return (double) evaluate( expression );
    }
}
