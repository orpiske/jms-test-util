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
import net.orpiske.jms.util.Util;

import javax.jms.*;

import static org.junit.Assert.assertNull;

/**
 * This test validates the ability to expire messages
 */
public abstract class AbstractMessageExpiration {

    private static final int MESSAGE_TTL = 2000;


    protected void setUp(final MessageProducer producer, final ServerListener
                      listener) throws JMSException {
        producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);
        listener.setReply("expired polo");

        // Sets the that the listener should use for the reply message
        listener.setTimeToLive(MESSAGE_TTL);
    }


    /**
     * This method tests the ability to send a text transaction to a server, in
     * a request-response pattern. It sends a text message containing the string
     * "marco" and waits for a response containing the text "polo".
     *
     * @throws javax.jms.JMSException
     */
    protected Message execTest(final Session session,
                            final MessageProducer producer)
            throws JMSException, InterruptedException
    {

        Destination replyTo = session.createTemporaryQueue();

        // Creates the text message object
        TextMessage textMessage = session.createTextMessage("expired marco");

        String correlationId = Util.randomId();
        textMessage.setJMSCorrelationID(correlationId);
        textMessage.setJMSReplyTo(replyTo);

        // Sends it
        producer.send(textMessage);

        // Creates the reply consumer
        MessageConsumer replyConsumer = session.createConsumer(replyTo);

        // Waits longer than the message TTL, so we can simulate an expiration
        Thread.sleep(MESSAGE_TTL * 2);

        Message message = replyConsumer.receive(1000 * 3);

        assertNull("The message should have been null, since it is expired",
                message);

        return message;
    }
}
