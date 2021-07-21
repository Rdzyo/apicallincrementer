package rtarnowski.apicallincrementer.util;

import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.HashMap;
import java.util.Map;

@Component
public class RestApiUtil {

    public Map<String, String> setUriVariables( String login ) {
        var uriVariables = new HashMap<String, String>();
        uriVariables.put( "login", login );
        return uriVariables;
    }

    public String composeUrl( String url, String login ) {
        return UriComponentsBuilder
                .fromHttpUrl( url )
                .buildAndExpand( login )
                .toUriString();
    }
}
