/**
 Copyright 2014 Otavio Rodolfo Piske

 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at

 http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
 */
package net.orpiske.jms.provider;

import net.orpiske.jms.AbstractMessageExpiration;
import net.orpiske.jms.provider.configuration.ActiveMqConfiguration;
import net.orpiske.jms.listener.ServerListener;
import net.orpiske.jms.provider.activemq.ActiveMqProvider;
import net.orpiske.jms.test.annotations.JmsSession;
import net.orpiske.jms.test.annotations.Producer;
import net.orpiske.jms.test.runner.JmsTestRunner;
import net.orpiske.jms.test.annotations.Listener;
import net.orpiske.jms.test.annotations.Provider;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.jms.*;

import static org.junit.Assert.assertNull;

/**
 * This test validates the ability to expire messages
 */
@RunWith(JmsTestRunner.class)
@Provider(
        value = ActiveMqProvider.class,
        configuration = ActiveMqConfiguration.class)
public class JmsMessageExpirationTest extends AbstractMessageExpiration {
    /**
     * This is the message producer. It is used to send a request to the server
     * and, in this test, represents a client sending a transaction. It shares
     * the same session as the consumer
     */
    @Producer
    private MessageProducer producer;


    /**
     * The session object, shared between the consumer and the producer
     */
    @JmsSession
    private Session session;


    /**
     * This represents a server. It receives the transactions sent by a
     * producer (represented, here, by the 'producer' field) pseudo-process it
     * and returns a message that is read by a consumer (represented by the
     * 'consumer' field in this test).
     */
    @Listener
    private ServerListener listener;


    @Before
    public void setUp() throws JMSException {
        super.setUp(producer, listener);
    }


    /**
     * This method tests the ability to send a text transaction to a server, in
     * a request-response pattern. It sends a text message containing the string
     * "marco" and waits for a response containing the text "polo".
     *
     * @throws javax.jms.JMSException
     */
    @Test
    public void testMessageExpiration() throws JMSException,
            InterruptedException
    {
        super.execTest(session, producer);
    }
}
