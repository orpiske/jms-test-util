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
package net.orpiske.jms.provider.content;

import net.orpiske.jms.content.AbstractRequestReply;
import net.orpiske.jms.defaults.Defaults;
import net.orpiske.jms.listener.ServerListener;
import net.orpiske.jms.provider.configuration.MockConfiguration;
import net.orpiske.jms.provider.mock.MockProvider;
import net.orpiske.jms.test.runner.JmsTestRunner;
import net.orpiske.jms.util.BytesReplyBuilder;
import net.orpiske.jms.util.Util;
import net.orpiske.jms.test.annotations.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.jms.*;

import static net.orpiske.jms.util.Util.randomId;
import static org.junit.Assert.assertEquals;

/**
 * Tests the ability to send/receive bytes messages
 */
@RunWith(JmsTestRunner.class)
@Provider(
        value = MockProvider.class,
        configuration = MockConfiguration.class)
public class RequestReplyBytesTest extends AbstractRequestReply<BytesMessage> {

    /**
     * This represents a message consumer on the client-side. It shares the
     * same session as the message producer.
     */
    @Consumer(address = Defaults.REPLY_TO_QUEUE)
    private MessageConsumer consumer;


    /**
     * This is the message producer. It is used to send a request to the server
     * and, in this test, represents a client sending a transaction. It shares
     * the same session as the consumer.
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

    @Override
    protected BytesMessage createRequestMessage() throws JMSException {
        BytesMessage bytesMessage = Util.createMessage(session,
                ("marco").getBytes());

        bytesMessage.setJMSCorrelationID(randomId());
        bytesMessage.setStringProperty(ServerListener.REPLY_BUILDER,
                BytesReplyBuilder.class.getName());


        return bytesMessage;
    }

    @Override
    protected void execTypeSpecificTests(BytesMessage response) throws JMSException {
        long readBytes = 0;
        long bodyLength = response.getBodyLength();

        do {
            /*
             * Just to make sure we don't overflow ... shouldn't happen, though.
             */
            if (bodyLength > Integer.MAX_VALUE) {
                bodyLength = Integer.MAX_VALUE;
            }

            byte[] data = new byte[(int) bodyLength];
            readBytes = response.readBytes(data);

            String returned = new String(data);

            assertEquals("The received message does not match the sent one",
                    "polo", returned);
        } while (readBytes < bodyLength);
    }


    /**
     * This method tests the ability to send a text transaction to a server, in
     * a request-response pattern. It sends a text message containing the string
     * "marco" and waits for a response containing the text "polo".
     *
     * @throws javax.jms.JMSException
     */
    @Test
    public void testSendReceiveMap() throws JMSException {
        execTest(session, producer, consumer);
    }


}
