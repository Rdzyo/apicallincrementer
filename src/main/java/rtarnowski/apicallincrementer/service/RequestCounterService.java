package rtarnowski.apicallincrementer.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import rtarnowski.apicallincrementer.dto.GitHubUserDto;
import rtarnowski.apicallincrementer.dto.RequestCounterDto;
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
        var gitHubUserInfoResponse = callGitHubUserInfoApi( login );
        var gitHubUserInfo = gitHubUserInfoResponse.getBody();
        if ( gitHubUserInfoResponse.getStatusCode() == HttpStatus.BAD_REQUEST ) {
            return gitHubUserInfoResponse;
        }
        var gitHubUserFollowersCount = getGitHubUserFollowersOrReposCount( login, gitHubUserFollowersUrl );
        var gitHubUserReposCount = getGitHubUserFollowersOrReposCount( login, gitHubUserReposUrl );
        if ( Objects.nonNull( gitHubUserInfo ) ) {
            gitHubUserInfoResponse = updateCalculationsData( gitHubUserFollowersCount, gitHubUserReposCount, gitHubUserInfo );
        }
        saveOrUpdateRequestCounterData( login );
        return gitHubUserInfoResponse;
    }

    private ResponseEntity<GitHubUserDto> callGitHubUserInfoApi( String login ) {
        var uriVariables = restApiUtil.setUriVariables( login );
        var url = restApiUtil.composeUrl( gitHubUserInfoUrl, login );
        var gitHubUserInfo = ResponseEntity.ok( new GitHubUserDto() );
        try {
            gitHubUserInfo = restTemplate.getForEntity( url, GitHubUserDto.class, uriVariables );
        } catch ( RestClientException ex ) {
            log.info( "Can't find user with login {}", login );
            return ResponseEntity.badRequest().body( new GitHubUserDto() );
        }
        return gitHubUserInfo;
    }

    private Integer getGitHubUserFollowersOrReposCount( String login, String gitHubUrl ) {
        var uriVariables = restApiUtil.setUriVariables( login );
        var url = restApiUtil.composeUrl( gitHubUrl, login );
        var response = restTemplate.getForEntity( url, Object[].class, uriVariables );
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
