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
package net.orpiske.jms.listener;

import net.orpiske.jms.util.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jms.*;
import java.io.Serializable;
import java.util.Map;

/**
 * A simple, non-thread safe, server listener.
 */
public abstract class ServerListener implements MessageListener {
    public static final String MESSAGE_COUNT = "properties.message.count";
    public static final String LISTENER = "properties.listener.name";

    private static final Logger logger = LoggerFactory.getLogger
            (ServerListener.class);

    /**
     * The current JMS session
     */
    private final Session session;

    /**
     * A message consumer to consume requests
     */
    private MessageConsumer messageConsumer;

    /**
     * A message produce to send replies
     */
    private MessageProducer replyProducer;

    /**
     * Used to count the number of processed messages
     */
    private int counter = 0;

    /**
     * Used to set the mock reply
     */
    private Message reply;

    /**
     * The time to live for the reply messages
     */
    private long timeToLive = Message.DEFAULT_TIME_TO_LIVE;


    /**
     * Constructor
     * @param session the JMS session
     * @param address the address to listen for requests
     * @throws JMSException
     */
    public ServerListener(Session session, final String address)
            throws JMSException
    {
        this.session = session;

        Destination origin = getDestination(session, address);

        messageConsumer = session.createConsumer(origin);
        messageConsumer.setMessageListener(this);

        replyProducer = session.createProducer(null);
        replyProducer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);
    }

    protected Session getSession() {
        return session;
    }


    /**
     * For the given address, returns its destination object (it varies whether
     * the address refers to a queue or a topic)
     * @param session the JMS session in use
     * @param address the destination address
     * @return a destination object for the given address
     * @throws JMSException
     */
    protected abstract Destination getDestination(Session session,
                                                  String address)
            throws JMSException;

    /**
     * Processes the messages on the request queue. Note: exceptions are logged
     * but not handled
     * @param message the message to process
     */
    public void onMessage(Message message) {
        logger.debug("Processing message #{}: {}", counter, message.toString());
        counter++;

        try {
            // Set the reply message properties
            reply.setJMSCorrelationID(message.getJMSCorrelationID());
            reply.setIntProperty(MESSAGE_COUNT, counter);
            reply.setStringProperty(LISTENER, this.getClass().getSimpleName());

            // Set the TTL for the message
            replyProducer.setTimeToLive(timeToLive);

            // Sends it
            replyProducer.send(message.getJMSReplyTo(), reply);

            /*
             We log the message after it has been sent because certain JMS
             message properties will be set after it has been sent (ie.:
             expired/TTL)
             */
            logger.debug("Sending reply #{}: {}", counter, reply.toString());
        } catch (JMSException e) {
            logger.error("Unable to process the message: {}", e.getMessage(),
                    e);
        }
    }

    public void setReply(byte[] bytes) throws JMSException {
        reply = Util.createMessage(session, bytes);
    }


    public void setReply(String text) throws JMSException {
        reply = Util.createMessage(session, text);
    }


    public void setReply(Serializable serializable) throws JMSException {
        reply = Util.createMessage(session, serializable);
    }

    public <V> void setReply(Map<String, V> map) throws JMSException {
       reply = Util.createMessage(session, map);
    }

    public void setTimeToLive(long expiration) {
        this.timeToLive = expiration;
    }

    public int processedCount() {
        return counter;
    }
}
