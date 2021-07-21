package rtarnowski.apicallincrementer.util;

import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

@Component
public class RestApiUtil {

    public String composeUrl( String url, String login ) {
        return UriComponentsBuilder
                .fromHttpUrl( url )
                .buildAndExpand( login )
                .toUriString();
    }
}
