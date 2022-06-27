package org.springframework.sync.diffsync.web;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.sync.diffsync.config.DiffSyncConfigurer;
import org.springframework.sync.diffsync.shadowstore.MapBasedShadowStore;
import org.springframework.sync.diffsync.shadowstore.ShadowStore;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.servlet.http.HttpSession;
import java.util.List;

@Configuration
public class SpringSyncWebConfiguration implements WebMvcConfigurer {

  @Bean
  @Scope(value = "session", proxyMode = ScopedProxyMode.TARGET_CLASS)
  public ShadowStore shadowStore(HttpSession session, List<DiffSyncConfigurer> diffSyncConfigurers) {
    for (DiffSyncConfigurer diffSyncConfigurer : diffSyncConfigurers) {
      ShadowStore shadowStore = diffSyncConfigurer.getShadowStore(session.getId());
      if (shadowStore != null) {
        return shadowStore;
      }
    }
    return new MapBasedShadowStore(session.getId());
  }

  @Override
  public void configureMessageConverters(List<HttpMessageConverter<?>> messageConverters) {
    messageConverters.add(new MappingJackson2HttpMessageConverter());
    messageConverters.add(new JsonPatchHttpMessageConverter());
  }
}
