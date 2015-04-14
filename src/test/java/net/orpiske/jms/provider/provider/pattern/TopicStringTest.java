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
package net.orpiske.jms.provider.pattern;

import net.orpiske.jms.defaults.Defaults;
import net.orpiske.jms.listener.ServerListener;
import net.orpiske.jms.pattern.AbstractTopic;
import net.orpiske.jms.provider.activemq.ActiveMqProvider;
import net.orpiske.jms.provider.configuration.ActiveMqConfiguration;
import net.orpiske.jms.test.runner.JmsTestRunner;
import net.orpiske.jms.test.annotations.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.jms.*;

import static org.junit.Assert.fail;


/**
 * Tests the ability to send/receive string messages through topics
 */
@RunWith(JmsTestRunner.class)
@Provider(
        value = ActiveMqProvider.class,
        configuration = ActiveMqConfiguration.class)
public class TopicStringTest extends AbstractTopic<TextMessage> {

    /**
     * This represents a message consumer on the client-side. It shares the
     * same session as the message producer.
     */
    @Consumer(address = Defaults.REPLY_TO_QUEUE)
    private MessageConsumer consumer;


    /**
     * This is the message producer. It is used to send a request to the server
     * and, in this test, represents a client sending a transaction. It shares
     * the same session as the consumer
     */
    @Producer(type = EndPointType.TOPIC)
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
    @Listener(endPointType = EndPointType.TOPIC)
    private ServerListener listener1;

    @Listener(endPointType = EndPointType.TOPIC)
    private ServerListener listener2;

    private int count = 0;

    @Before
    public void setUp() throws JMSException {
        producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);
    }

    @Override
    protected TextMessage createRequestMessage() throws JMSException {
        TextMessage request = session.createTextMessage("marco");

        request.setStringProperty(ServerListener.REPLY_BUILDER,
                TopicStringReplyBuilder.class.getName());

        return request;
    }

    @Override
    protected void execTypeSpecificTests(TextMessage response) throws JMSException {
        count++;
    }

    /**
     * This method tests the ability to send a text transaction to a server, in
     * a request-response pattern. It sends a text message containing the string
     * "marco" and waits for a response containing the text "polo".
     *
     * @throws javax.jms.JMSException
     */
    @Test
    public void testSendReceiveText() throws JMSException {
        execTest(session, producer, consumer);

        if (count != 2) {
            fail("Failed to receive enough responses");
        }
    }
}
