package dev.template.app.config;

import static org.assertj.core.api.Assertions.assertThat;

import dev.template.app.TestBase;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

/**
 * Deep links into the SPA must return the fallback shell on a full page load,
 * while prerendered pages must return their own baked HTML instead (see
 * conf/WebConfig). This test needs the built frontend on the test classpath,
 * which the Gradle build guarantees: processResources depends on
 * copyWebResources -> buildWeb.
 */
class SpaFallbackResolverTest extends TestBase {

    @Autowired
    private TestRestTemplate rest;

    @Test
    void deepLinkWithoutExtensionFallsBackToShell() {
        ResponseEntity<String> response = rest.getForEntity("/some/client/route", String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).contains("<!doctype html>").doesNotContain("About this app");
    }

    @Test
    void deepLinkWithDotInMiddleSegmentFallsBackToShell() {
        // a dot inside a non-final segment (version, email, slug) is not a file extension
        ResponseEntity<String> response = rest.getForEntity("/release/1.2/notes", String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).contains("<!doctype html>").doesNotContain("About this app");
    }

    @Test
    void missingAssetWithExtensionIsPlainNotFound() {
        ResponseEntity<String> response = rest.getForEntity("/assets/missing.js", String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void prerenderedAboutPageIsServedDirectly() {
        ResponseEntity<String> response = rest.getForEntity("/about", String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).contains("About this app");
    }

    @Test
    void prerenderedHomePageIsServedDirectly() {
        ResponseEntity<String> response = rest.getForEntity("/", String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).contains("to your new");
    }
}
