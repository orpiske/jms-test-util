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

import javax.jms.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public abstract class AbstractSendReceive {

    /**
     * Tests the ability to send and receive messages synchronously.
     *
     * @throws javax.jms.JMSException
     */
    protected TextMessage execSendReceive(final Session session,
                                       final MessageProducer producer,
                                       final MessageConsumer consumer)
            throws JMSException
    {
        TextMessage textMessage = session.createTextMessage("test");

        producer.send(textMessage);

        Message message = consumer.receive(5000);

        TextMessage ret = null;
        if (message instanceof TextMessage) {
            ret = (TextMessage) message;

            assertEquals("The received message does not match the sent one",
                    "test", ret.getText());
        } else {
            fail("The received message is not a text message: "
                    + (message == null ? "null" : message.getClass()));
        }

        return ret;
    }
}
