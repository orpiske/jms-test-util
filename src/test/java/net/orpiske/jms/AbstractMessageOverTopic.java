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
package net.orpiske.jms;

import net.orpiske.jms.listener.ServerListener;
import net.orpiske.jms.util.StringReplyBuilder;
import org.junit.Test;

import javax.jms.*;

import static net.orpiske.jms.util.Util.randomId;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;


public abstract class AbstractMessageOverTopic {
    private TextMessage requestMessage;
    private Message responseMessage;


    public void setUp(Session session, MessageProducer producer,
                      ServerListener listener) throws JMSException {
        producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);


        Destination replyTo = session.createTemporaryQueue();

        requestMessage = session.createTextMessage("marco");

        String correlationId = randomId();
        requestMessage.setJMSCorrelationID(correlationId);
        requestMessage.setJMSReplyTo(replyTo);

        requestMessage.setStringProperty(ServerListener.REPLY_BUILDER,
                StringReplyBuilder.class.getName());

        producer.send(requestMessage);

        MessageConsumer replyConsumer = session.createConsumer(replyTo);
        responseMessage = replyConsumer.receive(1000 * 5);
    }


    /**
     * This method tests the ability to send a text transaction to a server, in
     * a request-response pattern. It sends a text message containing the string
     * "marco" and waits for a response containing the text "polo".
     *
     * @throws javax.jms.JMSException
     */
    @Test
    public void testMessageId() throws JMSException {
        String correlationId = requestMessage.getJMSCorrelationID();
        String receivedId = responseMessage.getJMSCorrelationID();

        assertEquals("The received message ID (" + receivedId + ") does not " +
                        "match the sent one (" + correlationId + ")",
                correlationId, receivedId);
    }


    /**
     * Assert that we don't have a reply message from the future :)
     * Since we are running embedded, we should not have problems with
     * different clocks between the producer / broker / consumer
     *
     * @throws javax.jms.JMSException
     */
    @Test
    public void testTimeStamp() throws JMSException {
        long requestTimeStamp = requestMessage.getJMSTimestamp();
        long responseTimeStamp = responseMessage.getJMSTimestamp();

        assertTrue("The response message timestamp " + responseTimeStamp +
                        " is older than the request message timestamp " +
                        requestTimeStamp,
                (responseTimeStamp >= requestTimeStamp));
    }


    /**
     * Tests the ability to read integer properties
     * @throws javax.jms.JMSException
     */
    @Test
    public void testIntProperty() throws JMSException {
        int messageCount = responseMessage.getIntProperty(
                ServerListener.MESSAGE_COUNT);

        assertTrue("The message count should've been equal to or greater than" +
                " 1", (messageCount >= 1));
    }


    /**
     * Asserts that we are using the default priority
     * @throws javax.jms.JMSException
     */
    @Test
    public void testMessagePriority() throws JMSException {
        int priority = responseMessage.getJMSPriority();

        assertEquals("We should've been using the default priority",
                Message.DEFAULT_PRIORITY, priority);
    }
}
