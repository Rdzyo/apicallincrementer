package rtarnowski.apicallincrementer.dto;

import lombok.Data;

@Data
public class RequestCounterDto {
    private String login;
    private Integer requestCount = 1;
}
