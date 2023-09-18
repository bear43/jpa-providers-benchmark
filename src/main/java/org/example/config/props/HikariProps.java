package org.example.config.props;

import com.zaxxer.hikari.HikariConfig;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@ConfigurationProperties("hikari-ds")
@Configuration
public class HikariProps extends HikariConfig {

}
