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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.io.IOException;
import java.util.List;

@Component
class OpenApiAggregator {
    private final Logger logger = LoggerFactory.getLogger(OpenApiAggregator.class);
    private final ObjectMapper objectMapper;
    private final String apiTitle;
    private final String apiVersion;

    OpenApiAggregator(ObjectMapper objectMapper,
                      @Value("${app.api.title:Public API}") String apiTitle,
                      @Value("${app.api.version:1}") String apiVersion) {
        this.objectMapper = objectMapper;
        this.apiTitle = apiTitle;
        this.apiVersion = apiVersion;
    }

    ObjectNode aggregate(String baseUrl, List<String> swaggerUrls) throws IOException {
        final RestClient client = RestClient.create();

        final var result = objectMapper.createObjectNode();
        result.put("openapi", "3.0.1");

        final var info = objectMapper.createObjectNode();
        info.put("title", apiTitle);
        info.put("version", apiVersion);
        result.set("info", info);

        final var server = objectMapper.createObjectNode();
        server.put("url", baseUrl);
        result.set("servers", objectMapper.createArrayNode().add(server));

        final var paths = objectMapper.createObjectNode();
        final var components = objectMapper.createObjectNode();
        final var schemas = objectMapper.createObjectNode();
        result.set("paths", paths);
        result.set("components", components);
        components.set("schemas", schemas);

        for (final String swaggerUrl : swaggerUrls) {
            try {
                logger.debug("Reading OpenAPI definition from location: {}", swaggerUrl);
                final var resp = client.get().uri(swaggerUrl).retrieve().toEntity(ObjectNode.class);
                final var swagger = resp.getBody();
                if (swagger != null) {
                    copy(swagger, paths, "paths");

                    final var swaggerComponents = (ObjectNode) swagger.get("components");
                    if (swaggerComponents != null) {
                        copy(swaggerComponents, schemas, "schemas");
                    }
                }
            } catch (RestClientException e) {
                logger.warn("Failed to read OpenAPI definition from location: {}", swaggerUrl, e);
            }
        }
        return result;
    }

    private void copy(ObjectNode source, ObjectNode target, String att) {
        final var node = source.get(att);
        if (node instanceof ObjectNode) {
            target.setAll((ObjectNode) node);
        }
    }
}
