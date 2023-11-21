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

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.util.List;

@RestController
class OpenApiAggregatorController {
    private final OpenApiAggregator swaggerAggregator;
    private final AppServices appServices;

    OpenApiAggregatorController(OpenApiAggregator swaggerAggregator, AppServices appServices) {
        this.swaggerAggregator = swaggerAggregator;
        this.appServices = appServices;
    }

    @CrossOrigin
    @GetMapping(value = "/api/openapi", produces = MediaType.APPLICATION_JSON_VALUE)
    ObjectNode aggregate(UriComponentsBuilder ucb) throws IOException {
        return swaggerAggregator.aggregate(
                ucb.toUriString(),
                List.of(
                        toOpenApiUrl(appServices.movies()),
                        toOpenApiUrl(appServices.posters()),
                        toOpenApiUrl(appServices.trailers())
                ));
    }

    private String toOpenApiUrl(String url) {
        return url + "/api/openapi";
    }
}
