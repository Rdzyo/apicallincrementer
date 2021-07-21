package rtarnowski.apicallincrementer.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Getter
@Setter
@Entity
@NoArgsConstructor
public class RequestCounter {

    @Id
    @Column
    private String login;

    @Column
    private Integer requestCount;
}
