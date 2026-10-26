package it.unina.ingsw.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/** Serves the product images from a folder on disk under /uploads/products/. */
@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Value("${app.images.path:./uploads/products/}")
    private String imagesPath;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String location = imagesPath;
        if (!location.startsWith("file:")) {
            location = "file:" + location;
        }
        if (!location.endsWith("/")) {
            location = location + "/";
        }
        registry.addResourceHandler("/uploads/products/**")
                .addResourceLocations(location)
                .setCachePeriod(3600);
    }
}
