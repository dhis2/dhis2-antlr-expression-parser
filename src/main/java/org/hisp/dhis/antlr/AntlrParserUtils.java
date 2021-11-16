package org.hisp.dhis.antlr;

/*
 * Copyright (c) 2004-2019, University of Oslo
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * Neither the name of the HISP project nor the names of its contributors may
 * be used to endorse or promote products derived from this software without
 * specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

import com.google.common.collect.ImmutableMap;
import org.hisp.dhis.antlr.function.*;
import org.hisp.dhis.antlr.operator.*;

import java.util.Date;

import static org.hisp.dhis.antlr.DateUtils.parseDate;
import static org.hisp.dhis.parser.expression.antlr.ExpressionParser.*;

/**
 * Utilities for ANTLR parsing
 *
 * @author Jim Grace
 */
public class AntlrParserUtils
{
    public final static double DOUBLE_VALUE_IF_NULL = 0.0;

    public final static boolean BOOLEAN_VALUE_IF_NULL = false;

    public final static ImmutableMap<Integer, AntlrExprItem> ANTLR_EXPRESSION_ITEMS = ImmutableMap.<Integer, AntlrExprItem>builder()

        // Functions

        .put( LOG, new AntlrFunctionLog() )
        .put( LOG10, new AntlrFunctionLog10() )

        // Non-comparison operators

        .put( PAREN, new AntlrOperatorGroupingParentheses() )
        .put( PLUS, new AntlrOperatorMathPlus() )
        .put( MINUS, new AntlrOperatorMathMinus() )
        .put( POWER, new AntlrOperatorMathPower() )
        .put( MUL, new AntlrOperatorMathMultiply() )
        .put( DIV, new AntlrOperatorMathDivide() )
        .put( MOD, new AntlrOperatorMathModulus() )
        .put( NOT, new AntlrOperatorLogicalNot() )
        .put( EXCLAMATION_POINT, new AntlrOperatorLogicalNot() )
        .put( AND, new AntlrOperatorLogicalAnd() )
        .put( AMPERSAND_2, new AntlrOperatorLogicalAnd() )
        .put( OR, new AntlrOperatorLogicalOr() )
        .put( VERTICAL_BAR_2, new AntlrOperatorLogicalOr() )

        // Comparison operators

        .put( EQ, new AntlrOperatorCompareEqual() )
        .put( NE, new AntlrOperatorCompareNotEqual() )
        .put( GT, new AntlrOperatorCompareGreaterThan() )
        .put( LT, new AntlrOperatorCompareLessThan() )
        .put( GEQ, new AntlrOperatorCompareGreaterThanOrEqual() )
        .put( LEQ, new AntlrOperatorCompareLessThanOrEqual() )

        .build();

    /**
     * Trim quotes from the first and last characters of a string.
     * The string must be at least two characters long.
     * The first character must be either a single or double quote.
     * The last character must be the same as the first character.
     *
     * @param str the quoted string
     * @return the unquoted string
     */
    public static String trimQuotes( String str )
    {
        if ( str.length() < 2
            || ! "'\"".contains( str.substring( 0, 1 ) )
            || str.charAt( 0 ) != str.charAt( str.length() - 1 ) )
        {
            throw new ParserExceptionWithoutContext(
                "Internal parsing error: unquoted string '" + str + "'" );
        }

        return str.substring(1, str.length() - 1);
    }

    /**
     * Casts object as Double, or throws exception.
     * If the object is null, return null.
     *
     * @param object the value to cast as a Double.
     * @return Double value.
     */
    public static Double castDouble( Object object )
    {
        return (Double) castClass( Double.class, object );
    }

    /**
     * Casts object as Double, or throws exception.
     * If the object is null, returns a default.
     *
     * @param object the value to cast as a Double.
     * @return Double value.
     */
    public static Double castDoubleDefault( Object object )
    {
        return object == null
            ? DOUBLE_VALUE_IF_NULL
            : (Double) castClass( Double.class, object );
    }

    /**
     * Casts object as Boolean, or throws exception.
     * If the object is null, return null.
     *
     * @param object the value to cast as a Boolean.
     * @return Boolean value.
     */
    public static Boolean castBoolean( Object object )
    {
        return (Boolean) castClass( Boolean.class, object );
    }

    /**
     * Casts object as Boolean, or throws exception.
     * If the object is null, returns a default.
     *
     * @param object the value to cast as a Boolean.
     * @return Boolean value.
     */
    public static Boolean castBooleanDefault( Object object )
    {
        return object == null
            ? BOOLEAN_VALUE_IF_NULL
            : (Boolean) castClass( Boolean.class, object );
    }

    /**
     * Casts object as String, or throws exception.
     *
     * @param object the value to cast as a String.
     * @return String value.
     */
    public static String castString( Object object )
    {
        return (String) castClass( String.class, object );
    }

    /**
     * Parses an object from String to Date, or throws exception.
     *
     * @param object the value to cast as a String.
     * @return String value.
     */
    public static Date castDate( Object object )
    {
        return (Date) castClass( Date.class, object );
    }

    /**
     * Checks to see whether object can be cast to the class specified,
     * or throws exception if it can't.
     *
     * @param clazz the class: Double, Boolean, or String
     * @param object the value to cast
     * @return object (if it can be cast to that class.)
     */
    public static Object castClass( Class<?> clazz, Object object )
    {
        if ( object == null )
        {
            return null;
        }

        Object result;

        if ( clazz == Double.class && ( result = makeDouble( object ) ) != null )
        {
            return result;
        }

        if ( clazz == Boolean.class && ( result = makeBoolean( object ) ) != null )
        {
            return result;
        }

        if ( clazz == Date.class && ( result = makeDate( object ) ) != null )
        {
            return result;
        }

        if ( clazz == String.class )
        {
            return makeString( object );
        }

        throw new ParserExceptionWithoutContext( "Could not cast " + object.getClass().getSimpleName() +
            " '" + object + "' to " + clazz.getSimpleName() );
    }

    /**
     * Makes object a Double, if it can be done.
     *
     * @param object object to convert
     * @return object's Double value, else null
     */
    public static Double makeDouble( Object object )
    {
        if ( object instanceof Double )
        {
            return (Double) object;
        }

        if ( object instanceof String )
        {
            try
            {
                return Double.parseDouble( (String) object );
            }
            catch ( Exception e )
            {
                return null;
            }
        }

        return null;
    }

    /**
     * Makes object a Boolean, if it can be done.
     *
     * @param object object to convert
     * @return object's Boolean value, else null
     */
    public static Boolean makeBoolean( Object object )
    {
        if ( object instanceof Boolean )
        {
            return (Boolean) object;
        }

        if ( object instanceof Double && (Double) object % 1 == 0 )
        {
            return (Double) object != 0.0;
        }

        if ( object instanceof String )
        {
            if ( "true".equalsIgnoreCase( (String) object ) )
            {
                return true;
            }

            if ( "false".equalsIgnoreCase( (String) object ) )
            {
                return false;
            }
        }

        return null;
    }

    /**
     * Makes object a Date, if it can be done.
     *
     * @param object object to convert
     * @return object's Date value, else null
     */
    public static Date makeDate( Object object )
    {
        if ( object instanceof Date )
        {
            return (Date) object;
        }
        if ( object instanceof String )
        {
            try
            {
                return parseDate( (String) object );
            }
            catch ( Exception e )
            {
                return null;
            }
        }

        return null;
    }

    /**
     * Makes object a String.
     *
     * @param object object to convert
     * @return object's String value
     */
    public static String makeString( Object object )
    {
        return object.toString();
    }
}
