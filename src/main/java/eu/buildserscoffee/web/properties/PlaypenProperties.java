package eu.buildserscoffee.web.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties("playpen.connection")
public class PlaypenProperties {
    String name;
    String secretKey;
    String uuid;
    String ip;
    String port;
}
