package rtarnowski.apcallincrementer.util

import org.springframework.beans.factory.annotation.Autowired
import rtarnowski.apicallincrementer.util.RestApiUtil
import spock.lang.Specification

class RestApiUtilTest extends Specification {

    RestApiUtil restApiUtil = new RestApiUtil()

    def "Login property is correctly added to uri variables"() {
        given:
        def login = "testLogin"

        when:
        def result = restApiUtil.setUriVariables( login )

        then:
        result.get( "login" ) == login
    }

    def "Uri should be composed from given url and login"() {
        given:
        def url = "https://testUrl.com/{login}"
        def login = "testLogin"
        def composedUri = "https://testUrl.com/testLogin"

        when:
        def result = restApiUtil.composeUrl( url, login )

        then:
        result.equalsIgnoreCase( composedUri )
    }
}
