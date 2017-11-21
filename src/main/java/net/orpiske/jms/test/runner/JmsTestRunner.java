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
package net.orpiske.jms.test.runner;

import net.orpiske.jms.provider.JmsProvider;
import net.orpiske.jms.provider.ProviderConfiguration;
import net.orpiske.jms.provider.exception.ProviderInitializationException;
import net.orpiske.jms.provider.mock.MockProvider;
import net.orpiske.jms.test.annotations.*;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.InitializationError;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import java.lang.reflect.Field;


/**
 * An specialization of the stock JUnit test runner, that is capable of running
 * an embedded JMS provider and inject JMS-specific items into the test object,
 * such as the JMS session, a stock MessageConsumer, a stock MessageProducer and
 * message listeners.
 */
public class JmsTestRunner extends BlockJUnit4ClassRunner {
    private static final Logger logger = LoggerFactory.getLogger
            (JmsTestRunner.class);

    private JmsProvider jmsProvider = null;

    /**
     * Constructor
     * @param klass the test class
     * @throws InitializationError if unable to initialize the test class
     */
    public JmsTestRunner(Class<?> klass) throws InitializationError {
        super(klass);

        Provider provider = klass.getAnnotation(Provider.class);

        /*
         * This one shouldn't happen if parent class checks for null
         *
         * TODO: check parent class
         */
        if (provider == null) {
            throw new InitializationError("Invalid JMS-based test: the provider"
                    + " is null");
        }

        /**
         * Creates the provider
         */
        createProvider(provider);

        /**
         * ... then configure it.
         */
        configureProvider(provider);
    }


    /**
     * Starts the provider
     * @throws InitializationError
     */
    private void startProvider() throws InitializationError {
        if (jmsProvider == null) {
            throw new InitializationError("Invalid JMS provider: null");
        }

        try {
            jmsProvider.start();
        } catch (ProviderInitializationException e) {
            e.printStackTrace();
            throw new InitializationError(e);
        }
    }


    /**
     * Configure the provider using the Provider annotation information
     * @param provider the provider annotation
     * @throws InitializationError
     */
    private void configureProvider(Provider provider)
            throws InitializationError
    {
        Class<? extends ProviderConfiguration> configClass =
                provider.configuration();
        ProviderConfiguration providerConfiguration;

        try {
            providerConfiguration = configClass.newInstance();

        } catch (InstantiationException e) {
            throw new InitializationError(e);
        } catch (IllegalAccessException e) {
            throw new InitializationError(e);
        }
        providerConfiguration.configure(jmsProvider);
    }


    /**
     * Creates the provider
     * @param provider
     * @throws InitializationError
     */
    private void createProvider(Provider provider) throws InitializationError {
        Class<? extends JmsProvider> providerClass = provider.value();
        if (providerClass == null) {
            logger.warn("A provider was not given, therefore using the mock " +
                    "one");
            providerClass = MockProvider.class;
        }

        try {
            jmsProvider = providerClass.newInstance();

        } catch (InstantiationException e) {
            throw new InitializationError(e);
        } catch (IllegalAccessException e) {
            throw new InitializationError(e);
        }
    }


    /**
     * Creates the test object (overrides stock JUnit code).
     * @return
     * @throws Exception
     */
    @Override
    protected Object createTest() throws Exception {
        Object o = super.createTest();

        for (Field f : o.getClass().getDeclaredFields()) {
            Consumer consumer = f.getAnnotation(Consumer.class);

            if (consumer != null) {
                injectConsumers(o, f, consumer);
            } else {
                Producer producer = f.getAnnotation(Producer.class);

                if (producer != null) {
                    injectProducers(o, f, producer);
                } else {
                    JmsSession jmsSession = f.getAnnotation(JmsSession.class);

                    if (jmsSession != null) {
                        injectSession(o, f, jmsSession);
                    }
                    else {
                        Listener listener = f.getAnnotation(Listener.class);
                        if (listener != null) {
                            injectListener(o, f, listener);
                        }
                    }
                }
            }
        }

        return o;
    }

    /*
     * TODO: I can certainly do better. This method is dangerous as it does not
     * check for the object type. I need to improve this.
     */
    private void injectConsumers(Object o, Field f, Consumer consumer)
            throws IllegalAccessException, JMSException {
        logger.trace("Injecting a consumer into the test object");

        MessageConsumer messageConsumer =
                jmsProvider.createConsumer(consumer);

        f.setAccessible(true);
        f.set(o, messageConsumer);
        f.setAccessible(false);
    }


    /*
     * TODO: same problem as injectConsumers().
     */
    private void injectProducers(Object o, Field f, Producer producer)
            throws IllegalAccessException, JMSException
    {
        logger.trace("Injecting a producer into the test object");
       MessageProducer messageProducer =
                jmsProvider.createProducer(producer);

        f.setAccessible(true);
        f.set(o, messageProducer);
        f.setAccessible(false);

    }


    /*
     * TODO: same problem as injectConsumers().
     */
    private void injectSession(Object o, Field f, JmsSession jmsSession)
            throws IllegalAccessException, JMSException {

        logger.trace("Injecting the JMS session into the test object");

        f.setAccessible(true);
        f.set(o, jmsProvider.getSession());
        f.setAccessible(false);

    }

    /*
     * TODO: same problem as injectConsumers().
     */
    private void injectListener(Object o, Field f, Listener listener)
            throws IllegalAccessException, JMSException {

        logger.trace("Injecting the listener into the test object");
        f.setAccessible(true);
        f.set(o, jmsProvider.createServerListener(listener));
        f.setAccessible(false);

    }


    @Override
    public void run(RunNotifier notifier) {
        logger.info("Starting the JMS provider");
        try {
            startProvider();
        } catch (InitializationError e) {
            logger.error("Unable to start the provider: {}", e.getMessage(), e);
        }

        super.run(notifier);
        jmsProvider.stop();
        logger.info("Stopping the JMS provider");
    }
}
