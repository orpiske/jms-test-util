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
package net.orpiske.jms.util;

import javax.jms.*;
import java.io.Serializable;
import java.util.Map;
import java.util.Random;

/**
 * General usage utilities
 */
public final class Util {

    /**
     * Restricted constructor
     */
    private Util() {
    }


    /**
     * Creates a pseudo-random ID.
     * @return Pseudo-random ID as a String object
     */
    public static String randomId() {
        Random random = new Random(System.currentTimeMillis() +
                Math.round(Math.random()));
        long randomLong = random.nextLong();

        return Long.toHexString(randomLong);
    }


    /**
     * Creates a text message object
     * @param session the JMS session
     * @param text the message payload
     * @return A JMS message object of the appropriate type
     * @throws JMSException
     */
    public static TextMessage createMessage(Session session, String text)
            throws JMSException
    {
        TextMessage message = session.createTextMessage();

        message.setText(text);

        return message;
    }


    /**
     * Creates a byte message object
     * @param session the JMS session
     * @param bytes the message payload
     * @return A JMS message object of the appropriate type
     * @throws JMSException
     */
    public static BytesMessage createMessage(Session session, byte[] bytes)
            throws JMSException
    {
        BytesMessage message = session.createBytesMessage();

        message.writeBytes(bytes);

        return message;
    }



    /**
     * Creates a map message object
     * @param session the JMS session
     * @param map the message payload
     * @return A JMS message object of the appropriate type
     * @throws JMSException
     */
    public static <V> MapMessage createMessage(Session session,
                                               Map<String, V> map)
            throws JMSException
    {
        MapMessage mapMessage = session.createMapMessage();

        for (String key : map.keySet()) {
            V value = map.get(key);

            mapMessage.setObject(key, value);
        }

        return mapMessage;
    }


    /**
     * Creates an object message object
     * @param session the JMS session
     * @param serializable the message payload
     * @return A JMS message object of the appropriate type
     * @throws JMSException
     */
    public static ObjectMessage createMessage(Session session,
                                              Serializable serializable)
            throws JMSException
    {
        ObjectMessage message = session.createObjectMessage();

        message.setObject(serializable);

        return message;
    }
}
