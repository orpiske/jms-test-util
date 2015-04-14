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
import net.orpiske.jms.provider.configuration.ActiveMqConfiguration;
import net.orpiske.jms.content.fixtures.DummySerializable;
import net.orpiske.jms.defaults.Defaults;
import net.orpiske.jms.listener.ServerListener;
import net.orpiske.jms.provider.activemq.ActiveMqProvider;
import net.orpiske.jms.content.fixtures.Fixtures;
import net.orpiske.jms.test.runner.JmsTestRunner;
import net.orpiske.jms.util.SerializableReplyBuilder;
import net.orpiske.jms.util.Util;
import net.orpiske.jms.test.annotations.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.jms.*;
import java.io.Serializable;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;


/**
 * Tests the ability to send/receive object messages
 */
@RunWith(JmsTestRunner.class)
@Provider(
        value = ActiveMqProvider.class,
        configuration = ActiveMqConfiguration.class)
public class RequestReplyObjectTest extends
        AbstractRequestReply<ObjectMessage> {

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
        producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);
    }

    @Override
    protected ObjectMessage createRequestMessage() throws JMSException {
        DummySerializable dummySerializable = Fixtures.newDummySerializable("marco");

        ObjectMessage objectMessage =
                session.createObjectMessage(dummySerializable);

        objectMessage.setJMSCorrelationID(Util.randomId());
        objectMessage.setStringProperty(ServerListener.REPLY_BUILDER,
                SerializableReplyBuilder.class.getName());
        return objectMessage;
    }

    @Override
    protected void execTypeSpecificTests(ObjectMessage response) throws JMSException {
        Object returnedObject = response.getObject();

        if (!(returnedObject instanceof Serializable)) {
            fail("The received message does not contain a valid serializable: "
                    + (returnedObject == null ? "null" :
                    returnedObject.getClass()));
        }
        else {
            DummySerializable returnedSerializable =
                    (DummySerializable) returnedObject;

            assertEquals("The received message does not match the sent one",
                    "polo", returnedSerializable.getSomeData());
        }
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
    }
}
