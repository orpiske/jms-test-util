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
package net.orpiske.jms.browser;

import net.orpiske.jms.defaults.Defaults;
import net.orpiske.jms.listener.ServerListener;
import net.orpiske.jms.util.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jms.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;


/**
 * Tests the ability to browse the queues
 */
public abstract class AbstractQueueBrowser implements Callback {
    private static final Logger logger = LoggerFactory.getLogger
            (AbstractQueueBrowser.class);

    private static final int MESSAGE_COUNT = 10;

    private int count;




    private void sendMessage(final Session session,
                             final MessageProducer producer, int num)
            throws JMSException
    {
        Destination replyTo = session.createQueue(Defaults.REPLY_TO_QUEUE);

        TextMessage textMessage = session.createTextMessage("marco" + num);
        textMessage.setJMSCorrelationID(Util.randomId());
        textMessage.setJMSReplyTo(replyTo);
        textMessage.setJMSExpiration(300000);

        producer.send(textMessage);
    }

    /**
     * Sets up the test by sending 10 messages. That will generate 10 replies,
     * which will then be browsed
     * @throws JMSException
     */
    protected void setUp(final Session session, final MessageProducer producer,
                      final ServerListener listener)
            throws JMSException
    {
        producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);
        listener.setReply("polo");

        for (int i = 0; i < MESSAGE_COUNT; i++) {
            sendMessage(session, producer, i);
        }
    }


    /**
     * Helper method to wait for responses
     * @throws Exception
     */
    private void waitForReplies(final ServerListener listener) throws Exception {
        int tentatives = 10;

        while (listener.processedCount() < 10) {
            Thread.sleep(1000);

            tentatives--;
            if (tentatives == 0) {
                break;
            }
        }
    }


    /**
     * This method tests the ability to browse through a queue. The test will
     * send 10 messages. Then, it will wait for their replies and browse through
     * the reply queue. Ideally, there should be 10 messages in the reply
     * queue, however this may depend on the system speed.
     *
     * @throws javax.jms.JMSException
     */
    protected void execTest(final Session session, final MessageConsumer consumer,
                         final ServerListener listener)
            throws Exception
    {
        /*
         * Depending on the system speed, it may take some time to process
         * the messages asynchronously, so we wait for a few seconds.
         */
        waitForReplies(listener);

        Browsable browsable = new SimpleBrowser();


        Queue queue = session.createQueue(Defaults.REPLY_TO_QUEUE);

        /*
         * Set this' method execute as the callback for the browser, so we can
         * validate sent vs. received later
         */
        browsable.browse(session, queue, this);

        assertEquals("The number of messages in the queue does not match "
                + "the expected value", MESSAGE_COUNT, count);

        /*
         * Validates the replies
         */
        for (int i = 0; i < count; i++) {
            Message message = consumer.receive(1000 * 5);

            TextMessage ret = null;
            if (message instanceof TextMessage) {
                ret = (TextMessage) message;
            } else {
                fail("The received message is not a text message: "
                        + (message == null ? "null" : message.getClass()));
            }

            assertEquals("The received message does not match the sent one",
                    "polo", ret.getText());
        }
    }


    /**
     * This is our callback method
     * @param object the current element (exchange) being inspected in the queue
     */
    public void execute(Object object) {
        logger.debug("Executing call #{}", count);
        count++;
    }
}
