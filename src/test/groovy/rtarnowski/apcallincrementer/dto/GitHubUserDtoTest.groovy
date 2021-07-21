package rtarnowski.apcallincrementer.dto

import rtarnowski.apicallincrementer.dto.GitHubUserDto
import spock.lang.Specification

import java.time.LocalDateTime

class GitHubUserDtoTest extends Specification {

    def "Calculation field should return 0 when followersCount is 0"() {
        given:
        GitHubUserDto testGitHubUser = new GitHubUserDto(
                id: 10,
                login: "testLogin",
                name: "testName",
                avatarUrl: "testUrl.com",
                createdAt: LocalDateTime.now(),
        )
        int followersCount = 0
        int reposCount = 2

        when:
        testGitHubUser.setCalculations( followersCount, reposCount )

        then:
        testGitHubUser.properties.get( "calculations" ) == 0
    }

    def "Calculations field is equal to given equation"() {
        GitHubUserDto testGitHubUser = new GitHubUserDto(
                id: 10,
                login: "testLogin",
                name: "testName",
                avatarUrl: "testUrl.com",
                createdAt: LocalDateTime.now(),
        )
        int followersCount = 10
        int reposCount = 2

        when:
        testGitHubUser.setCalculations( followersCount, reposCount )

        then:
        testGitHubUser.properties.get( "calculations" ) == (6.0 / followersCount * (2.0 + reposCount))
    }
}
