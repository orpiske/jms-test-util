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

import net.orpiske.jms.listener.ReplyBuilder;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;
import java.util.HashMap;
import java.util.Map;

public class MapReplyBuilder implements ReplyBuilder {
    private Map<String, String> getReplyMap() {
        Map<String, String> replyMap = new HashMap<String, String>();

        replyMap.put("key4", "value4");
        replyMap.put("key5", "value5");
        replyMap.put("key6", "value6");

        return replyMap;
    }


    public Message build(Session session, Message request) throws JMSException {
        return Util.createMessage(session, getReplyMap());
    }
}
