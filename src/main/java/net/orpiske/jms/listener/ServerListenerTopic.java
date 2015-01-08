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

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Session;


/**
 * A server listener implementation that works with topics
 */
public class ServerListenerTopic extends ServerListener {
    public ServerListenerTopic(Session session, String address)
            throws JMSException
    {
        super(session, address);
    }

    @Override
    protected Destination getDestination(Session session, String address)
            throws JMSException {
        return session.createTopic(address);
    }
}
