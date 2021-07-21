package rtarnowski.apcallincrementer.dto

import org.modelmapper.ModelMapper
import rtarnowski.apicallincrementer.dto.RequestCounterDto
import rtarnowski.apicallincrementer.model.RequestCounter
import spock.lang.Specification

class RequestCounterDtoTest extends Specification {

    private ModelMapper mapper = new ModelMapper()

    def "ModelMapper should properly map RequestCounter entity to dto"() {
        given:
        RequestCounter testEntity = new RequestCounter(
                login: "testLogin",
                requestCount: 1
        )

        when:
        def result = mapper.map( testEntity, RequestCounterDto.class )

        then:
        result.properties.get( "login" ) == testEntity.properties.get( "login" )
        result.properties.get( "requestCount" ) == testEntity.properties.get( "requestCount" )
    }
}