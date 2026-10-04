package org.hisp.dhis.antlr.cache;

/*
 * Copyright (c) 2004-2026, University of Oslo
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

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * @author Jason P. Pickering <jason@dhis2.org>
 */
@RunWith( JUnit4.class )
public class LocalCacheTest
{
    private static final String KEY = "key";

    @Test
    public void testGetWithMappingFunctionComputesValueOnlyOnMiss()
    {
        Cache<String> cache = new SimpleCacheBuilder<String>().forRegion( "test" )
            .withMaximumSize( 10 ).build();
        CountingMappingFunction mappingFunction = new CountingMappingFunction();

        for ( int i = 0; i < 100; i++ )
        {
            assertEquals( "value-" + KEY, cache.get( KEY, mappingFunction ).get() );
        }

        assertEquals( 1, mappingFunction.calls.get() );
    }

    /**
     * A hit must not write the entry back into the cache. Writing on every hit restarts the
     * expiry timer, so a frequently read entry would never expire, and every read pays for a
     * write and a timer reschedule under the cache's timer lock.
     */
    @Test( timeout = 20000 )
    public void testGetWithMappingFunctionDoesNotRefreshExpiryOnHit()
        throws InterruptedException
    {
        Cache<String> cache = new SimpleCacheBuilder<String>().forRegion( "test" )
            .expireAfterWrite( 1, TimeUnit.SECONDS ).withMaximumSize( 10 ).build();
        CountingMappingFunction mappingFunction = new CountingMappingFunction();

        long deadline = System.currentTimeMillis() + 10000;

        while ( mappingFunction.calls.get() < 2 && System.currentTimeMillis() < deadline )
        {
            cache.get( KEY, mappingFunction );
            Thread.sleep( 50 );
        }

        assertTrue( "entry was never recomputed, so its expiry was refreshed on every hit",
            mappingFunction.calls.get() >= 2 );
    }

    private static class CountingMappingFunction
        implements MappingFunction<String>
    {
        private final AtomicInteger calls = new AtomicInteger();

        @Override
        public String getValue( String key )
        {
            calls.incrementAndGet();
            return "value-" + key;
        }
    }
}
