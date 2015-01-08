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

import net.orpiske.jms.AbstractMessage;
import net.orpiske.jms.listener.ServerListener;
import net.orpiske.jms.provider.configuration.MockConfiguration;
import net.orpiske.jms.provider.mock.MockProvider;
import net.orpiske.jms.test.runner.JmsTestRunner;
import net.orpiske.jms.test.annotations.*;
import org.junit.Before;
import org.junit.runner.RunWith;

import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.Session;


/**
 * This test validates the ability to work with topics
 */
@RunWith(JmsTestRunner.class)
@Provider(
        value = MockProvider.class,
        configuration = MockConfiguration.class)
public class JmsTopicMessageTest extends AbstractMessage {
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
    private ServerListener listener;


    @Before
    public void setUp() throws JMSException {
        super.setUp(session, producer, listener);
    }
}
