package rtarnowski.apicallincrementer.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import rtarnowski.apicallincrementer.dto.GitHubUserDto;
import rtarnowski.apicallincrementer.service.RequestCounterService;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequiredArgsConstructor
@RequestMapping(produces = APPLICATION_JSON_VALUE)
public class RequestCounterController {

    private final RequestCounterService requestCounterService;

    @GetMapping("/users/{login}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<GitHubUserDto> getGitHubUserInfo( @PathVariable String login ) {
        return requestCounterService.getGitHubUserInfo( login );
    }
}
