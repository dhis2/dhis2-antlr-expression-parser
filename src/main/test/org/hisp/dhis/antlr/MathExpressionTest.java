package org.hisp.dhis.antlr;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

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
        assertEquals( 1.0, evaluate( "1/1" ) );
        assertEquals( 0.5, evaluate( "1/2" ) );
        assertEquals( -0.7, evaluate( "-1.4/( 1 + 1 )" ) );
    }

    @Test
    public void testMultiply() {
        assertEquals( 1.0, evaluate( "1*1" ) );
        assertEquals( 2.0, evaluate( "1*2" ) );
        assertEquals( -2.8, evaluate( "-1.4*( 1 + 1 )" ) );
    }

    @Test
    public void testPower() {
        assertEquals( 1.0, evaluate( "1^10" ) );
        assertEquals( 25.0, evaluate( "5^2" ) );
        assertEquals( 1.96, evaluate( "1.4^( 1 + 1 )" ) );
    }

    @Test
    public void testModule() {
        assertEquals( 1.0, evaluate( "1%2" ) );
        assertEquals( 1.0, evaluate( "5%2" ) );
        assertEquals( 1.4, evaluate( "1.4%( 1 + 1 )" ) );
    }

    private Object evaluate(String expression) {
        return Parser.visit( expression, visitor );
    }

}