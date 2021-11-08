package org.hisp.dhis.antlr;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static org.junit.Assert.assertEquals;

@RunWith( JUnit4.class )
public class LogicalExpressionTest
{

    private TestExpressionVisitor visitor = new TestExpressionVisitor();

    @Test
    public void testNumericLiteralsAsBooleans() {
        assertEquals( true, evaluate( "1 and 2" ) );
        assertEquals( true, evaluate( "1 && 2" ) );
        assertEquals( false, evaluate( "0 && 1" ) );
        assertEquals( false, evaluate( "0 && 1" ) );
        assertEquals( false, evaluate( "0 && 0" ) );
        assertEquals( false, evaluate( "1.0 && 0" ) );
    }

    @Test(expected = ParserExceptionWithoutContext.class)
    public void testDoubleAsBoolean() {
        assertEquals( true, evaluate( "1.1 and 2" ) );
    }

    @Test
    public void testAnd() {
        assertEquals( true, evaluate( "true and true" ) );
        assertEquals( true, evaluate( "true && true" ) );
        assertEquals( false, evaluate( "true && false" ) );
        assertEquals( false, evaluate( "false && true" ) );
        assertEquals( false, evaluate( "false && false" ) );
    }

    @Test
    public void testOr() {
        assertEquals( true, evaluate( "true or true" ) );
        assertEquals( true, evaluate( "true || true" ) );
        assertEquals( true, evaluate( "true || false" ) );
        assertEquals( true, evaluate( "false || true" ) );
        assertEquals( false, evaluate( "false || false" ) );
    }

    @Test
    public void testNot() {
        assertEquals( true, evaluate( "!false" ) );
        assertEquals( false, evaluate( "!true" ) );
        assertEquals( true, evaluate( "not false" ) );
        assertEquals( false, evaluate( "not true" ) );
    }

    private Object evaluate(String expression) {
        return Parser.visit( expression, visitor );
    }

}