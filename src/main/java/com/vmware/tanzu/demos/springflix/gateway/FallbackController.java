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

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.net.URI;
import java.time.LocalDate;
import java.util.List;

@Controller
class FallbackController {
    private final Movie bootifulMovie = new Movie("bootiful", "Spring Boot 3.2",
            LocalDate.of(2023, 6, 13));
    private final MovieTrailers bootifulTrailers = new MovieTrailers("bootiful", List.of(
            new MovieTrailers.Item(URI.create("https://www.youtube.com/watch?v=dMhpDdR6nHw"), bootifulMovie.releaseDate)
    ));
    @Value("classpath:/fallback/poster-not-found.png")
    private Resource posterNotFound;
    @Value("classpath:/fallback/maintenance.html")
    private Resource maintenance;
    @Value("classpath:/fallback/bootiful.jpg")
    private Resource bootifulPoster;

    @GetMapping(value = "/fallback/poster-not-found.png", produces = MediaType.IMAGE_PNG_VALUE)
    @ResponseBody
    Resource posterNotFound() {
        return posterNotFound;
    }

    @GetMapping(value = "/fallback/maintenance.html", produces = MediaType.TEXT_HTML_VALUE)
    ResponseEntity<Resource> maintenance() {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(maintenance);
    }

    @GetMapping(value = "/fallback/movies/upcoming", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    List<Movie> upcomingMovies() {
        return List.of(bootifulMovie);
    }

    @GetMapping(value = "/api/v1/movies/bootiful", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    Movie bootifulDetails() {
        return bootifulMovie;
    }

    @GetMapping(value = "/api/v1/trailers/bootiful")
    @ResponseBody
    MovieTrailers bootifulVideo() {
        return bootifulTrailers;
    }

    @GetMapping(value = "/api/v1/posters/bootiful", produces = MediaType.IMAGE_JPEG_VALUE)
    @ResponseBody
    Resource bootifulPoster() {
        return bootifulPoster;
    }

    record Movie(String id, String title, LocalDate releaseDate) {
    }

    record MovieTrailers(String id, List<Item> trailers) {
        record Item(URI videoUri, LocalDate publishedAt) {
        }
    }
}
