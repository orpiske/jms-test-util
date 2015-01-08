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
package net.orpiske.jms.provider.browser;

import net.orpiske.jms.browser.AbstractQueueBrowser;
import net.orpiske.jms.defaults.Defaults;
import net.orpiske.jms.listener.ServerListener;
import net.orpiske.jms.provider.configuration.MockConfiguration;
import net.orpiske.jms.provider.mock.MockProvider;
import net.orpiske.jms.test.runner.JmsTestRunner;
import net.orpiske.jms.test.annotations.*;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Session;


/**
 * Tests the ability to browse the queues
 */
@RunWith(JmsTestRunner.class)
@Provider(
        value = MockProvider.class,
        configuration = MockConfiguration.class)
@Ignore
public class QueueBrowserTest extends AbstractQueueBrowser {
    private static final Logger logger = LoggerFactory.getLogger
            (QueueBrowserTest.class);

    private static final int MESSAGE_COUNT = 10;

    private int count;

    /**
     * This represents a message consumer on the client-side. It shares the
     * same session as the message producer. In this test, it is tied to the
     * default reply queue
     */
    @Consumer(address = Defaults.REPLY_TO_QUEUE)
    private MessageConsumer consumer;


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




    /**
     * Sets up the test by sending 10 messages. That will generate 10 replies,
     * which will then be browsed
     * @throws javax.jms.JMSException
     */
    @Before
    public void setUp() throws JMSException {
        super.setUp(session, producer, listener);
    }


    /**
     * This method tests the ability to browse through a queue. The test will
     * send 10 messages. Then, it will wait for their replies and browse through
     * the reply queue. Ideally, there should be 10 messages in the reply
     * queue, however this may depend on the system speed.
     *
     * @throws javax.jms.JMSException
     */
    @Test
    public void testQueueBrowsing() throws Exception {
       super.execTest(session, consumer, listener);
    }
}
