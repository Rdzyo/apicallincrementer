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

    @Transactional
    public GitHubUserDto getGitHubUserInfo( String login ) {
        checkIfLoginIsNotEmptyOrNull( login );
        // Get GitHub user info
        var gitHubUserInfo = callGitHubUserInfoApi( login );
        var gitHubUserFollowersCount = gitHubUserInfo.getFollowers();
        var gitHubUserReposCount = gitHubUserInfo.getRepos();
        gitHubUserInfo.setCalculations( gitHubUserFollowersCount, gitHubUserReposCount );
        // Check if user already exists
        var requestCounterEntity = requestCounterRepository.findByLogin( login );
        // Create new RequestCounter entity or increment requestCount in existing one
        if ( requestCounterEntity.isPresent() ) {
            updateRequestCounter( requestCounterEntity.get() );
        } else {
            createRequestCounter( login );
        }
        return gitHubUserInfo;
    }

    private GitHubUserDto callGitHubUserInfoApi( String login ) {
        var url = restApiUtil.composeUrl( gitHubUserInfoUrl, login );
        ResponseEntity<GitHubUserDto> gitHubUserInfo;
        try {
            gitHubUserInfo = restTemplate.getForEntity( url, GitHubUserDto.class );
        } catch ( RestClientException ex ) {
            log.info( "Can't find user with login {}", login );
            throw new UserNotFoundException();
        }
        return Objects.requireNonNull( gitHubUserInfo.getBody() );
    }

    private void updateRequestCounter( RequestCounter requestCounterEntity ) {
        var requestCount = requestCounterEntity.getRequestCount() + 1;
        requestCounterEntity.setRequestCount( requestCount );
        requestCounterRepository.save( requestCounterEntity );
    }

    private void createRequestCounter( String login ) {
        var requestCounterDto = new RequestCounterDto();
        requestCounterDto.setLogin( login );
        var requestCounterEntityToCreate = modelMapper.map( requestCounterDto, RequestCounter.class );
        requestCounterRepository.save( requestCounterEntityToCreate );
    }

    private void checkIfLoginIsNotEmptyOrNull(String login) {
        if( login == null || login.trim().isEmpty()) {
            throw new IllegalArgumentException("Login variable cannot be empty or null");
        }
    }
}
