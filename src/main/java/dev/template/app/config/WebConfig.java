package dev.template.app.config;

import java.io.IOException;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.resource.PathResourceResolver;

/**
 * Serves the SvelteKit SPA (adapter-static with fallback: index.html).
 * Deep links like /some/route must return index.html on a full page load —
 * Spring's static resource handling does not do that by itself. Rule:
 * a path that is not an existing file, has no extension, and is not /api/**
 * falls back to index.html; everything else keeps default behavior
 * (API paths get a JSON 404, missing assets get a plain 404).
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

        private static final Resource INDEX_HTML = new ClassPathResource("/static/index.html");

        @Override
        protected Resource getResource(String resourcePath, Resource location) throws IOException {
            Resource requested = location.createRelative(resourcePath);
            if (requested.exists() && requested.isReadable()) {
                return requested;
            }
            if (!resourcePath.startsWith("api/") && !resourcePath.contains(".")) {
                return INDEX_HTML;
            }
            return null;
        }
    }
}
