package com.example.splitter;

import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DataConfig {

  @Bean
  public DataSource configureDB(
      @Value("${spring.datasource.url}") String url,
      @Value("${spring.datasource.username}") String user,
      @Value("${spring.datasource.password}") String pw
  ) {
    return DataSourceBuilder.create()
        .url(url)
        .username(user)
        .password(pw)
        .build();
  }

}
