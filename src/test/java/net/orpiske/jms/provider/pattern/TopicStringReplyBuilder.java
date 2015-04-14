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
package net.orpiske.jms.provider.pattern;

import net.orpiske.jms.listener.ReplyBuilder;
import net.orpiske.jms.util.Util;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;

public class TopicStringReplyBuilder implements ReplyBuilder {
    private static int count;

    private void increment() {
        count++;
    }

    synchronized public Message build(Session session, Message request) throws
            JMSException {
        increment();


        return Util.createMessage(session, "polo" + count);
    }
}
