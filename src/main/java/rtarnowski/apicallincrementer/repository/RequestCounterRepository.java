package rtarnowski.apicallincrementer.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import rtarnowski.apicallincrementer.model.RequestCounter;

import java.util.Optional;

@Repository
public interface RequestCounterRepository extends JpaRepository<RequestCounter, String> {
    Optional<RequestCounter> findByLogin( String login );
}
