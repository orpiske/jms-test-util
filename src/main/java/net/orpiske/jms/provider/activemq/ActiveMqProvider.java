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
package net.orpiske.jms.provider.activemq;

import net.orpiske.jms.provider.AbstractProvider;
import net.orpiske.jms.provider.exception.ProviderInitializationException;
import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.broker.BrokerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jms.JMSException;
import java.net.URISyntaxException;

/**
 * A sample ActiveMQ provider
 */
public class ActiveMqProvider extends AbstractProvider {
    private static final Logger logger = LoggerFactory.getLogger
            (ActiveMqProvider.class);

    private final BrokerService broker;

    private String uri;


    /**
     * Constructor
     */
    public ActiveMqProvider() {
        logger.debug("Creating a new ActiveMQ Provider");

        broker = new BrokerService();


    }


    /**
     * Gets the internal broker implementation
     * @return
     */
    public BrokerService getBroker() {
        return broker;
    }


    public void start() throws ProviderInitializationException {
        if (!broker.isStarted()) {
            try {
                broker.start();
            } catch (Exception e) {
                throw new ProviderInitializationException("Unable to start " +
                        "embedded ActiveMQ: " + e.getMessage(), e);
            }

            connection = newConnection();
            session = newSession();
        }
    }

    public void stop() {
        try {
            broker.stop();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    protected ActiveMQConnection newConnection()
            throws ProviderInitializationException
    {
        ActiveMQConnection ret = null;

        try {
            ret = ActiveMQConnection.makeConnection(uri);
        } catch (JMSException e) {
            throw new ProviderInitializationException("Unable to connect to the"
                    + " embedded broker", e);
        } catch (URISyntaxException e) {
            throw new ProviderInitializationException("Invalid URL", e);
        }

        try {
            ret.start();
        } catch (JMSException e) {
            throw new ProviderInitializationException("Unable to start the " +
                    "embedded broker", e);
        }

        return ret;
    }


    public void setUri(final String uri) {
        this.uri = uri;
    }
}
