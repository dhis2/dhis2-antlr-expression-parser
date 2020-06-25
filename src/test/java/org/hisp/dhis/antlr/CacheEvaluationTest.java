package org.hisp.dhis.antlr;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static org.junit.Assert.assertEquals;

@RunWith( JUnit4.class )
public class CacheEvaluationTest
{

    private TestExpressionVisitor visitor = new TestExpressionVisitor();

    @Test
    public void test2ConsecutiveCallsWithANdWithoutCacheHasTheSameResult() {
        String expression = "true and true";
        assertEquals( evaluateWithNoCache( expression ), evaluate( expression ) );
        assertEquals( evaluateWithNoCache( expression ), evaluate( expression ) );
    }

    private Object evaluate(String expression) {
        return Parser.visit( expression, visitor );
    }

    private Object evaluateWithNoCache(String expression) {
        return Parser.visit( expression, visitor, false );
    }
}