package rtarnowski.apicallincrementer.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;

@Data
public class GitHubUserDto {

    private Long id;
    private String login;
    private String name;
    @JsonProperty("avatar_url")
    private String avatarUrl;
    @JsonProperty("created_at")
    private String createdAt;
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private int followers;
    @JsonProperty(value = "public_repos",access = JsonProperty.Access.WRITE_ONLY)
    private int repos;
    @Setter(AccessLevel.NONE)
    private Double calculations;

    public void setCalculations( int followersCount, int reposCount ) {
        if ( followersCount != 0 ) {
            this.calculations = 6.0 / followersCount * ( 2.0 + reposCount );
        } else {
            this.calculations = 0.0;
        }
    }
}
