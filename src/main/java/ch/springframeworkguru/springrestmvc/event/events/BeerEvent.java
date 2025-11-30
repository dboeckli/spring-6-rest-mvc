package ch.springframeworkguru.springrestmvc.event.events;

import ch.springframeworkguru.springrestmvc.entity.Beer;
import org.springframework.security.core.Authentication;

public interface BeerEvent {

    Beer getBeer();

    Authentication getAuthentication();

}
