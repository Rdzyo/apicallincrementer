package rtarnowski.apicallincrementer.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import rtarnowski.apicallincrementer.dto.GitHubUserDto;
import rtarnowski.apicallincrementer.dto.RequestCounterDto;
import rtarnowski.apicallincrementer.exception.UserNotFoundException;
import rtarnowski.apicallincrementer.model.RequestCounter;
import rtarnowski.apicallincrementer.repository.RequestCounterRepository;
import rtarnowski.apicallincrementer.util.RestApiUtil;

import java.util.Objects;

@Service
@Slf4j
@RequiredArgsConstructor
public class RequestCounterService {

    private final RestTemplate restTemplate;
    private final RequestCounterRepository requestCounterRepository;
    private final RestApiUtil restApiUtil;
    private final ModelMapper modelMapper;
    @Value("${github.user.info.url}")
    private String gitHubUserInfoUrl;
    @Value("${github.user.followers.url}")
    private String gitHubUserFollowersUrl;
    @Value("${github.user.repos.url}")
    private String gitHubUserReposUrl;

    @Transactional
    public ResponseEntity<GitHubUserDto> getGitHubUserInfo( String login ) {
        // Get GitHub user info
        var gitHubUserInfoResponse = callGitHubUserInfoApi( login );
        var gitHubUserInfo = gitHubUserInfoResponse.getBody();
        // Get followers count for calculations field
        var gitHubUserFollowersCount = getGitHubUserFollowersOrReposCount( login, gitHubUserFollowersUrl );
        // Get repos count for calculations field
        var gitHubUserReposCount = getGitHubUserFollowersOrReposCount( login, gitHubUserReposUrl );
        if ( Objects.nonNull( gitHubUserInfo ) ) {
            // Set calculations field in GitHubUserDto
            gitHubUserInfoResponse = updateCalculationsData( gitHubUserFollowersCount, gitHubUserReposCount, gitHubUserInfo );
        }
        // Create new RequestCounter entity or increment requestCount in existing one
        saveOrUpdateRequestCounterData( login );
        return gitHubUserInfoResponse;
    }

    private ResponseEntity<GitHubUserDto> callGitHubUserInfoApi( String login ) {
        var url = restApiUtil.composeUrl( gitHubUserInfoUrl, login );
        ResponseEntity<GitHubUserDto> gitHubUserInfo;
        try {
            gitHubUserInfo = restTemplate.getForEntity( url, GitHubUserDto.class );
        } catch ( RestClientException ex ) {
            log.info( "Can't find user with login {}", login );
            throw new UserNotFoundException();
        }
        return gitHubUserInfo;
    }

    private Integer getGitHubUserFollowersOrReposCount( String login, String gitHubUrl ) {
        var url = restApiUtil.composeUrl( gitHubUrl, login );
        var response = restTemplate.getForEntity( url, Object[].class );
        var responseBody = response.getBody();
        if ( Objects.nonNull( responseBody ) ) {
            return responseBody.length;
        } else {
            return 0;
        }
    }

    private void saveOrUpdateRequestCounterData( String login ) {
        var requestCounterEntity = requestCounterRepository.findByLogin( login );
        if ( requestCounterEntity.isPresent() ) {
            var requestCount = requestCounterEntity.get().getRequestCount() + 1;
            requestCounterEntity.get().setRequestCount( requestCount );
            requestCounterRepository.save( requestCounterEntity.get() );
        } else {
            var requestCounterDto = new RequestCounterDto();
            requestCounterDto.setLogin( login );
            var requestCounterEntityToCreate = modelMapper.map( requestCounterDto, RequestCounter.class );
            requestCounterRepository.save( requestCounterEntityToCreate );
        }
    }

    private ResponseEntity<GitHubUserDto> updateCalculationsData( int followersCount, int reposCount, GitHubUserDto gitHubUserInfo ) {
        gitHubUserInfo.setCalculations( followersCount, reposCount );
        return ResponseEntity.ok( gitHubUserInfo );
    }
}
