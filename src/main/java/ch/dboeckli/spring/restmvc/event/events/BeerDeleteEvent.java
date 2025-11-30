package ch.dboeckli.spring.restmvc.event.events;

import ch.dboeckli.spring.restmvc.entity.Beer;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.Authentication;

@Builder
@AllArgsConstructor
@Getter
@Setter
public class BeerDeleteEvent implements BeerEvent {

    private Beer beer;

    private Authentication authentication;
}
