package org.hisp.dhis.antlr;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static org.junit.Assert.assertEquals;

@RunWith( JUnit4.class )
public class ValidExpressionTest
{
    private TestExpressionVisitor visitor = new TestExpressionVisitor();

    @Test
    public void testValidExpression() {
        assertEquals( "a", evaluate( "'a'" ) );
        assertEquals( true, evaluate( "true" ) );
        assertEquals( "true", evaluate( "'true'" ) );
        assertEquals( 34.0, evaluate( "34" ) );
        assertEquals( "34", evaluate( "'34'" ) );
        assertEquals( "34.0", evaluate( "'34.0'" ) );
    }

    @Test(expected = ParserException.class)
    public void testInvalidSyntaxExpression() {
        evaluate( "'2' >)_ 1" );
    }

    @Test(expected = ParserExceptionWithoutContext.class)
    public void testExpressionWithValidSyntaxAndNotSupportedItem() {
        evaluate( "2 > #{not_supported}" );
    }

    @Test(expected = ParserException.class)
    public void testExpressionWithValidSyntaxAndNotSupportedVariable() {
        evaluate( "2 > V{not_supported}" );
    }

    @Test(expected = ParserExceptionWithoutContext.class)
    public void testExpressionWithValidSyntaxAndNotSupportedAttribute() {
        evaluate( "2 > A{not_supported}" );
    }

    private Object evaluate(String expression) {
        return Parser.visit( expression, visitor );
    }

}