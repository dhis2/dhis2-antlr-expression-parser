package org.hisp.dhis.antlr;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static org.junit.Assert.assertEquals;

@RunWith( JUnit4.class )
public class ValidExpressionTest
{
    private TestExpressionVisitor visitor = new TestExpressionVisitor();

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Test
    public void testValidExpression() {
        assertEquals( "a", evaluate( "'a'" ) );
        assertEquals( true, evaluate( "true" ) );
        assertEquals( "true", evaluate( "'true'" ) );
        assertEquals( 34.0, evaluate( "34" ) );
        assertEquals( "34", evaluate( "'34'" ) );
        assertEquals( "34.0", evaluate( "'34.0'" ) );
    }

    @Test
    public void testInvalidSyntaxExpression1()
    {
        exception.expect( ParserException.class );
        exception.expectMessage("Invalid string token '_' at line:1 character:10" );

        evaluate( "1+1+1 > 0 _ 1" );
    }

    @Test
    public void testInvalidSyntaxExpression2()
    {
        exception.expect( ParserException.class );
        exception.expectMessage("Invalid string token 'a' at line:1 character:8" );

        evaluate( "1 + 1 + a" );
    }

    @Test
    public void testInvalidSyntaxExpression3()
    {
        exception.expect( ParserException.class );
        exception.expectMessage("Invalid string token '(' at line:1 character:7" );

        evaluate( "1 + 1  ( 2 + 2 )" );
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