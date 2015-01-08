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
package net.orpiske.jms.provider.mock;

import com.mockrunner.mock.jms.JMSMockObjectFactory;
import com.mockrunner.mock.jms.MockConnectionFactory;
import net.orpiske.jms.provider.AbstractProvider;
import net.orpiske.jms.provider.exception.ProviderInitializationException;
import net.orpiske.jms.test.annotations.EndPointType;

import javax.jms.*;

/**
 * A mockrunner-based JMS mock class
 */
public class MockProvider extends AbstractProvider {
    private JMSMockObjectFactory mock = new JMSMockObjectFactory();

    @Override
    public void start() throws ProviderInitializationException {
        if (session == null) {
            try {
                connection = newConnection();

                session = connection.createSession(false,
                        Session.AUTO_ACKNOWLEDGE);
            }
            catch (JMSException e) {
                throw new ProviderInitializationException(e.getMessage(), e);
            }
        }
    }

    @Override
    public void stop() {

    }

    @Override
    protected Destination getDestination(EndPointType type, String address)
            throws JMSException
    {
        Destination destination;

        if (type == EndPointType.TOPIC) {
            if (address.length() == 0) {
                destination = mock.getDestinationManager().createQueue(address);
            }
            else {
                destination = mock.getDestinationManager().createTopic(address);
            }
        }
        else {
            if (address.length() == 0) {
                destination = mock.getDestinationManager().createQueue(null);
            }
            else {
                destination = mock.getDestinationManager().createQueue(address);
            }
        }

        return destination;
    }

    @Override
    protected Connection newConnection() throws ProviderInitializationException {


        MockConnectionFactory connectionFactory =
                mock.createMockConnectionFactory();

        try {
            return connectionFactory.createConnection();
        } catch (JMSException e) {
            throw new ProviderInitializationException(e.getMessage(), e);
        }
    }
}
