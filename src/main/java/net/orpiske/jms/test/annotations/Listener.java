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
package net.orpiske.jms.test.annotations;


import net.orpiske.jms.defaults.Defaults;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * Applied to the message listener fields, so that they can be injected during
 * test start up
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface Listener {

    /**
     * The address on which the listener will wait for requests
     *
     * @return
     */
    String address() default Defaults.REQUEST_QUEUE;

    /**
     * The endpoint type
     *
     * @return
     */
    EndPointType endPointType() default EndPointType.QUEUE;
}
