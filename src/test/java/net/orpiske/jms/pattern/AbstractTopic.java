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
package net.orpiske.jms.pattern;

import net.orpiske.jms.defaults.Defaults;

import javax.jms.*;

import static net.orpiske.jms.util.Util.randomId;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;


/**
 * Tests the ability to send/receive string messages through topics
 */
public abstract class AbstractTopic<T extends Message> {

    protected abstract T createRequestMessage() throws JMSException;

    protected abstract void execTypeSpecificTests(T response) throws JMSException;


    protected void execTest(Session session, MessageProducer producer,
                            MessageConsumer consumer) throws
            JMSException {
        String correlationId = randomId();
        Destination replyTo = session.createQueue(Defaults.REPLY_TO_QUEUE);

        Message request = createRequestMessage();
        request.setJMSReplyTo(replyTo);
        request.setJMSCorrelationID(correlationId);

        producer.send(request);

        int count = 0;
        Message response = consumer.receive(1000 * 5);
        while (response != null) {
            String receivedId = response.getJMSCorrelationID();

            assertEquals("The received message ID does not match the sent one",
                    correlationId, receivedId);

            if (response.getClass() != request.getClass()) {
                fail("The received message class is not the expected type: "
                        + response.getClass());
            }

            execTypeSpecificTests((T) response);

            count++;
            response = consumer.receive(1000 * 5);
        }
    }
}
