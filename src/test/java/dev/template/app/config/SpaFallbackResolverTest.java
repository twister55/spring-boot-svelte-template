package dev.template.app.config;

import static org.assertj.core.api.Assertions.assertThat;

import dev.template.app.TestBase;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

/**
 * Deep links into the SPA must return index.html on a full page load
 * (see conf/WebConfig). This test needs the built frontend on the test
 * classpath, which the Gradle build guarantees: processResources depends
 * on copyWebResources -> buildWeb.
 */
class SpaFallbackResolverTest extends TestBase {

    @Autowired
    private TestRestTemplate rest;

    @Test
    void deepLinkWithoutExtensionFallsBackToIndexHtml() {
        ResponseEntity<String> response = rest.getForEntity("/some/client/route", String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).contains("<!doctype html>");
    }

    @Test
    void deepLinkWithDotInMiddleSegmentFallsBackToIndexHtml() {
        // a dot inside a non-final segment (version, email, slug) is not a file extension
        ResponseEntity<String> response = rest.getForEntity("/release/1.2/notes", String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).contains("<!doctype html>");
    }

    @Test
    void missingAssetWithExtensionIsPlainNotFound() {
        ResponseEntity<String> response = rest.getForEntity("/assets/missing.js", String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }
}
