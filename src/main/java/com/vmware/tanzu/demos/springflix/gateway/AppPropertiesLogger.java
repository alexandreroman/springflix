/*
 * Copyright (c) 2023 VMware, Inc. or its affiliates
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.vmware.tanzu.demos.springflix.gateway;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
class AppPropertiesLogger {
    private final Logger logger = LoggerFactory.getLogger(AppPropertiesLogger.class);
    private final AppProperties appProperties;

    AppPropertiesLogger(AppProperties appProperties) {
        this.appProperties = appProperties;
    }

    @EventListener
    void onApplicationReady(ApplicationReadyEvent e) {
        logger.info("Using Web UI URL: {}", appProperties.webui());
        logger.info("Using Movies URL: {}", appProperties.movies());
        logger.info("Using Posters URL: {}", appProperties.posters());
        logger.info("Using Trailers URL: {}", appProperties.trailers());
    }
}
