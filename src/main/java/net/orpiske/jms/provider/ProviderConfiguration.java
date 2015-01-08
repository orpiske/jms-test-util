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
package net.orpiske.jms.provider;


/**
 * Provides an interface on which the providers can be configured (ie.: it can
 * be used to set addresses, ports, data directories and other parameters that
 * may vary according environment or purpose)
 *
 * @param <T>
 */
public interface ProviderConfiguration<T extends JmsProvider> {

    /**
     * Configures the provider
     *
     * @param provider the provider object
     */
    void configure(T provider);
}
