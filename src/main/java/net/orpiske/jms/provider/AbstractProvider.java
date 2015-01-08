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

import net.orpiske.jms.listener.ServerListenerQueue;
import net.orpiske.jms.listener.ServerListenerTopic;
import net.orpiske.jms.provider.exception.ProviderInitializationException;
import net.orpiske.jms.test.annotations.Consumer;
import net.orpiske.jms.test.annotations.Listener;
import net.orpiske.jms.test.annotations.Producer;
import net.orpiske.jms.test.annotations.EndPointType;

import javax.jms.*;

public abstract class AbstractProvider implements JmsProvider {
    protected Session session;
    protected Connection connection;

    abstract public void start() throws ProviderInitializationException;

    abstract public void stop();

    protected abstract Connection newConnection()
        throws ProviderInitializationException;

    protected Session newSession() throws ProviderInitializationException {
        try {
            return connection.createSession(false,
                    Session.AUTO_ACKNOWLEDGE);
        } catch (JMSException e) {
            throw new ProviderInitializationException("Unable to create " +
                    "a JMS session", e);
        }

    }

    public Session getSession() {
        return session;
    }

    protected Destination getDestination(EndPointType type, String address)
            throws JMSException
    {
        Destination destination;

        if (type == EndPointType.TOPIC) {
            if (address.length() == 0) {
                destination = session.createTopic(null);
            }
            else {
                destination = session.createTopic(address);
            }
        }
        else {
            if (address.length() == 0) {
                destination = session.createQueue(null);
            }
            else {
                destination = session.createQueue(address);
            }
        }

        return destination;
    }

    public MessageProducer createProducer(Producer producer)
            throws JMSException
    {
        Destination destination = getDestination(producer.type(),
                producer.address());

        return session.createProducer(destination);
    }


    public MessageConsumer createConsumer(Consumer consumer)
            throws JMSException
    {
        Destination destination = getDestination(consumer.type(),
                consumer.address());

        String correlationId = consumer.correlationId();

        if (correlationId.length() == 0) {
            return session.createConsumer(destination);
        }
        else {
            return session.createConsumer(destination, correlationId);
        }
    }



    public MessageListener createServerListener(Listener listener)
            throws JMSException
    {
        Session serverSession =
                connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        MessageListener ret;

        if (listener.endPointType() == EndPointType.QUEUE) {
            ret = new ServerListenerQueue(serverSession, listener.address());
        }
        else {
            ret = new ServerListenerTopic(serverSession, listener.address());
        }

        return ret;
    }
}
