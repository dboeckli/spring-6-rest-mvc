package ch.dboeckli.spring.restmvc.event.events;

import ch.dboeckli.spring.restmvc.entity.Beer;
import org.springframework.security.core.Authentication;

public interface BeerEvent {

    Beer getBeer();

    Authentication getAuthentication();

}
