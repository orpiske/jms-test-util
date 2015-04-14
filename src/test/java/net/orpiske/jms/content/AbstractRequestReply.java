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
package net.orpiske.jms.content;

import net.orpiske.jms.defaults.Defaults;

import javax.jms.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 * Tests the ability to send/receive bytes messages
 */
public abstract class AbstractRequestReply<T extends Message> {
    protected abstract T createRequestMessage() throws JMSException;

    protected abstract void execTypeSpecificTests(T response) throws JMSException;

    protected void execTest(Session session, MessageProducer producer,
                            MessageConsumer consumer) throws
            JMSException {
        Destination replyTo = session.createQueue(Defaults.REPLY_TO_QUEUE);

        Message request = createRequestMessage();
        request.setJMSReplyTo(replyTo);

        producer.send(request);

        Message response = consumer.receive(1000 * 5);

        if (response == null) {
            fail("The received message is null");
        }


        if (response.getClass() != request.getClass()) {
            fail("The received message class is not the expected type: "
                    + response.getClass());
        }

        assertEquals("The correlation ID don't match:",
                request.getJMSCorrelationID(),
                response.getJMSCorrelationID());

        execTypeSpecificTests((T) response);
    }
}
