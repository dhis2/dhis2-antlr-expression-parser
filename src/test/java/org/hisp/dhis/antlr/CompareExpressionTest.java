package org.hisp.dhis.antlr;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static org.junit.Assert.assertEquals;

@RunWith( JUnit4.class )
public class CompareExpressionTest
{
    private TestExpressionVisitor visitor = new TestExpressionVisitor();

    @Test
    public void testGreaterForDifferentTypes() {
        assertEquals( true, evaluate( "'2' > 1" ) );
        assertEquals( false, evaluate( "1 > '2'" ) );
        assertEquals( false, evaluate( "'10' > '2'" ) );
        assertEquals( true, evaluate( "10 > '2'" ) );
        assertEquals( true, evaluate( "'1' > -2" ) );
        assertEquals( true, evaluate( "'-10' < -1" ) );
        assertEquals( false, evaluate( "'2' > ( 1 + 1 )" ) );
        assertEquals( true, evaluate( "true > 0" ) );
        assertEquals( false, evaluate( "false > 1" ) );
    }

    @Test
    public void testGreater() {
        assertEquals( true, evaluate( "2 > 1" ) );
        assertEquals( false, evaluate( "1 > 2" ) );
        assertEquals( false, evaluate( "2 > ( 1 + 1 )" ) );
        assertEquals( true, evaluate( "true > false" ) );
        assertEquals( false, evaluate( "true > true" ) );
        assertEquals( false, evaluate( "'abc' > 'abc'" ) );
        assertEquals( false, evaluate( "'abc' > 'def'" ) );
    }

    @Test
    public void testGreaterOrEqual() {
        assertEquals( true, evaluate( "2 >= 1" ) );
        assertEquals( false, evaluate( "1 >= 2" ) );
        assertEquals( true, evaluate( "2 >= ( 1 + 1 )" ) );
        assertEquals( true, evaluate( "true >= false" ) );
        assertEquals( true, evaluate( "true >= true" ) );
        assertEquals( true, evaluate( "'abc' >= 'abc'" ) );
        assertEquals( false, evaluate( "'abc' >= 'def'" ) );
    }

    @Test
    public void testLess() {
        assertEquals( false, evaluate( "2 < 1" ) );
        assertEquals( true, evaluate( "1 < 2" ) );
        assertEquals( false, evaluate( "2 < ( 1 + 1 )" ) );
        assertEquals( false, evaluate( "true < false" ) );
        assertEquals( false, evaluate( "true < true" ) );
        assertEquals( false, evaluate( "'abc' < 'abc'" ) );
        assertEquals( true, evaluate( "'abc' < 'def'" ) );
    }

    @Test
    public void testLessOrEqual() {
        assertEquals( false, evaluate( "2 <= 1" ) );
        assertEquals( true, evaluate( "1 <= 2" ) );
        assertEquals( true, evaluate( "2 <= ( 1 + 1 )" ) );
        assertEquals( false, evaluate( "true <= false" ) );
        assertEquals( true, evaluate( "true <= true" ) );
        assertEquals( true, evaluate( "'abc' <= 'abc'" ) );
        assertEquals( true, evaluate( "'abc' <= 'def'" ) );
    }

    @Test
    public void testEqual() {
        assertEquals( false, evaluate( "2 == 1" ) );
        assertEquals( false, evaluate( "1 == 2" ) );
        assertEquals( true, evaluate( "2 == ( 1 + 1 )" ) );
    }

    @Test
    public void testNotEqual() {
        assertEquals( true, evaluate( "2 != 1" ) );
        assertEquals( true, evaluate( "1 != 2" ) );
        assertEquals( false, evaluate( "2 != ( 1 + 1 )" ) );
    }

    @Test
    public void testCompareDifferentTypes() {
        assertEquals( true, evaluate( "2 == '2'" ) );
        assertEquals( true, evaluate( "'2' == 2" ) );
        assertEquals( true, evaluate( "'hi' == \"hi\"" ) );
        assertEquals( true, evaluate( "'true' == true" ) );
        assertEquals( true, evaluate( "true == 'true'" ) );
    }

    @Test
    public void testDivideByZero() {
        assertEquals( false, evaluate( "2 == 2 / 0" ) );
    }

    private Object evaluate(String expression) {
        return Parser.visit( expression, visitor );
    }
}
