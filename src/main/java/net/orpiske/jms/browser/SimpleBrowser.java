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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jms.JMSException;
import javax.jms.Queue;
import javax.jms.QueueBrowser;
import javax.jms.Session;
import java.util.Enumeration;


/**
 * A simple queue browser that traverses through the queues and runs the input
 * call back code
 */
public class SimpleBrowser implements Browsable {
    private static final Logger logger = LoggerFactory.getLogger
            (SimpleBrowser.class);


    /*
     * @see Browsable#browse(Session, Queue, Callback)
     */
    public void browse(Session session, Queue queue, Callback callback)
            throws JMSException
    {
        logger.trace("Browsing the queue {}", queue.getQueueName());

        QueueBrowser queueBrowser = session.createBrowser(queue);
        Enumeration enumeration = queueBrowser.getEnumeration();

        while (enumeration.hasMoreElements()) {
            Object object = enumeration.nextElement();

            callback.execute(object);
        }
    }

}
