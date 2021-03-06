/*
 * Licensed to Jasig under one or more contributor license
 * agreements. See the NOTICE file distributed with this work
 * for additional information regarding copyright ownership.
 * Jasig licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License.  You may obtain a
 * copy of the License at the following location:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.jasig.cas.ticket.support;

import org.jasig.cas.TestUtils;
import org.jasig.cas.ticket.TicketGrantingTicket;
import org.jasig.cas.ticket.TicketGrantingTicketImpl;

import junit.framework.TestCase;

/**
 * @author Scott Battaglia
 * @version $Revision$ $Date$
 * @since 3.0
 */
public class ThrottledUseAndTimeoutExpirationPolicyTests extends TestCase {

    private static final long TIMEOUT = 5000;

    private ThrottledUseAndTimeoutExpirationPolicy expirationPolicy;

    private TicketGrantingTicket ticket;

    protected void setUp() throws Exception {
        this.expirationPolicy = new ThrottledUseAndTimeoutExpirationPolicy();
        this.expirationPolicy.setTimeToKillInMilliSeconds(TIMEOUT);
        this.expirationPolicy.setTimeInBetweenUsesInMilliSeconds(1000);

        this.ticket = new TicketGrantingTicketImpl("test", TestUtils
            .getAuthentication(), this.expirationPolicy);

        super.setUp();
    }

    public void testTicketIsNotExpired() {
        assertFalse(this.ticket.isExpired());
    }
    
    public void testTicketIsExpired() {
        try {
            Thread.sleep(TIMEOUT + 100);
            assertTrue(this.ticket.isExpired());
        } catch (InterruptedException e) {
            fail(e.getMessage());
        }
    }
    
    public void testTicketUsedButWithTimeout() {
        try {
            this.ticket.grantServiceTicket("test", TestUtils.getService(), this.expirationPolicy, false);
            Thread.sleep(TIMEOUT -100);
            assertFalse(this.ticket.isExpired());
        } catch (InterruptedException e) {
            fail(e.getMessage());
        }
    }
    
    public void testNotWaitingEnoughTime() {
        this.ticket.grantServiceTicket("test", TestUtils.getService(), this.expirationPolicy, false);
        assertTrue(this.ticket.isExpired());
    }
}
