package org.hisp.dhis.antlr.operator;

/*
 * Copyright (c) 2004-2020, University of Oslo
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

import org.hisp.dhis.antlr.ParserExceptionWithoutContext;

import java.util.List;

import static org.hisp.dhis.antlr.AntlrParserUtils.makeBoolean;
import static org.hisp.dhis.antlr.AntlrParserUtils.makeDouble;
import static org.hisp.dhis.antlr.AntlrParserUtils.makeString;

/**
 * Abstract class for compare operators
 *
 * @author Jim Grace
 */
public abstract class AntlrOperatorCompare
    extends AntlrComputeFunction
{
    /**
     * Compares two Doubles, Booleans, or Strings.
     *
     * @param values the values to compare
     * @return the results of the comparison.
     */
    protected int compare( List<Object> values )
    {
        Object o1 = values.get( 0 );
        Object o2 = values.get( 1 );

        Double d1;
        Double d2;

        if ( ( o1 instanceof Double || o2 instanceof Double ) &&
            ( d1 = makeDouble( o1 ) ) != null &&
            ( d2 = makeDouble( o2 ) ) != null )
        {
            return d1.compareTo( d2 );
        }

        Boolean b1;
        Boolean b2;

        if ( ( o1 instanceof Boolean || o2 instanceof Boolean ) &&
            ( b1 = makeBoolean( o1 ) ) != null &&
            ( b2 = makeBoolean( o2 ) ) != null )
        {
            return b1.compareTo( b2 );
        }

        if ( o1 instanceof String || o2 instanceof String )
        {
            return makeString( o1 ).compareTo( makeString( o2 ) );
        }

        throw new ParserExceptionWithoutContext( "Could not compare " +
            o1.getClass().getSimpleName() + " '" + o1 + "' to " +
            o2.getClass().getSimpleName() + " '" + o2 + "'" );
    }

    /**
     * For a comparison, if any argument value is null, return null.
     * (If any argument is Double.NaN, the comparison should proceed and
     * return a Boolean value.)
     */
    @Override
    protected boolean invalidArg( Object value )
    {
        return value == null;
    }
}
