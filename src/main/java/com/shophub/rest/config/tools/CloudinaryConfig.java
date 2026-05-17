package com.shophub.rest.config.tools;

import com.cloudinary.Cloudinary;
import com.shophub.rest.config.CommonEnvConfig;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Configuration
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CloudinaryConfig {
    CommonEnvConfig env;

    @Bean
    public Cloudinary cloudinary() {
        return new Cloudinary(Map.ofEntries(
            Map.entry("cloud_name", env.CLD_NARY_CLOUD_NAME()),
            Map.entry("api_key", env.CLD_NARY_API_KEY()),
            Map.entry("api_secret", env.CLD_NARY_API_SECRET())
        ));
    }

}
