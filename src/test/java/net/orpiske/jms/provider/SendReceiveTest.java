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

import net.orpiske.jms.AbstractSendReceive;
import net.orpiske.jms.provider.configuration.MockConfiguration;
import net.orpiske.jms.provider.mock.MockProvider;
import net.orpiske.jms.test.annotations.Producer;
import net.orpiske.jms.test.runner.JmsTestRunner;
import net.orpiske.jms.test.annotations.Consumer;
import net.orpiske.jms.test.annotations.JmsSession;
import net.orpiske.jms.test.annotations.Provider;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Session;


/**
 * A basic synchronous send/receive test
 */
@RunWith(JmsTestRunner.class)
@Provider(
        value = MockProvider.class,
        configuration = MockConfiguration.class)
@Ignore
public class SendReceiveTest extends AbstractSendReceive {

    @Consumer
    private MessageConsumer consumer;

    @Producer
    private MessageProducer producer;

    @JmsSession
    private Session session;


    /**
     * Tests the ability to send and receive messages synchronously.
     * @throws JMSException
     */
    @Test
    public void testSendReceive() throws JMSException {
        super.execSendReceive(session, producer, consumer);
    }
}
