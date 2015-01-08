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

import net.orpiske.jms.provider.exception.ProviderInitializationException;
import net.orpiske.jms.test.annotations.Consumer;
import net.orpiske.jms.test.annotations.Listener;
import net.orpiske.jms.test.annotations.Producer;

import javax.jms.*;

/**
 * Provides an interface on which other providers can be implemented
 */
public interface JmsProvider {

    /**
     * Starts the provider
     * @throws ProviderInitializationException if unable to start it (root
     * causes may be appended)
     */
    void start() throws ProviderInitializationException;


    /**
     * Stops the provider
     */
    void stop();


    /**
     * The current JMS session
     * @return the JMS session object
     */
    Session getSession();


    /**
     * Creates a message producer for the test
     * @param producer the producer annotation applied to the field being
     *                 processed
     * @return A message producer
     * @throws JMSException if unable to create the producer
     */
    MessageProducer createProducer(Producer producer) throws JMSException;


    /**
     * Creates a message consumer for the test
     * @param consumer the consumer annotation applied to the field being
     *                 processed
     * @return A message consumer
     * @throws JMSException if unable to create the consumer
     */
    MessageConsumer createConsumer(Consumer consumer) throws JMSException;


    /**
     * Creates a message listener for the test
     * @param listener the listener annotation applied to the field being
     *                 processed
     * @return A message listener
     * @throws JMSException if unable to create the listener
     */
    MessageListener createServerListener(Listener listener) throws JMSException;


}
