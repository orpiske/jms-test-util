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
package net.orpiske.jms.provider.configuration;

import net.orpiske.jms.provider.ProviderConfiguration;
import net.orpiske.jms.provider.mock.MockProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class configures the provider once it has been initialized
 */
public class MockConfiguration implements ProviderConfiguration<MockProvider> {
    private static final Logger logger = LoggerFactory.getLogger
            (MockConfiguration.class);

    /**
     * The address used by the broker
     */
    public static final String CONNECTOR = "tcp://localhost:61616";


    /**
     * Configure the provider
     * @param provider the provider to configure
     */
    public void configure(MockProvider provider) {
        logger.info("Configuring the provider");

    }
}
