package dev.template.app.config;

import java.io.IOException;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.resource.PathResourceResolver;

/**
 * Serves the SvelteKit SPA (adapter-static with fallback: _app/fallback.html).
 * Deep links like /some/route must return a full page on a full page load —
 * Spring's static resource handling does not do that by itself. Rule:
 * a path that is not an existing file, has no extension in its final segment,
 * and is not /api/** first tries the matching prerendered page (adapter-static
 * writes /about as about.html), then falls back to the SPA shell; everything
 * else keeps default behavior (API paths get a JSON 404, missing assets get a
 * plain 404). The shell lives under _app/ (kit.appDir) because the SvelteKit
 * server refuses to serve any /_app/* route, so a prerendered page can never
 * collide with the shell file.
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/**")
                .addResourceLocations("classpath:/static/")
                .resourceChain(true)
                .addResolver(new SpaFallbackResolver());
    }

    private static final class SpaFallbackResolver extends PathResourceResolver {

        private static final Resource FALLBACK_HTML = new ClassPathResource("/static/_app/fallback.html");

        @Override
        protected Resource getResource(String resourcePath, Resource location) throws IOException {
            // super checks existence and that the resolved path stays inside location
            Resource requested = super.getResource(resourcePath, location);
            if (requested != null) {
                return requested;
            }
            // no extension on the final path segment → an SPA deep link, not a missing asset
            // (a dot in an earlier segment, e.g. /release/1.2/notes, does not count)
            if (!resourcePath.startsWith("api/") && StringUtils.getFilenameExtension(resourcePath) == null) {
                // a prerendered page (adapter-static writes /about as about.html) wins over the SPA shell
                Resource prerendered = super.getResource(resourcePath + ".html", location);
                return prerendered != null ? prerendered : FALLBACK_HTML;
            }
            return null;
        }
    }
}
