package ch.dboeckli.spring.restmvc.event.events;

import ch.dboeckli.spring.restmvc.entity.Beer;
import lombok.*;
import org.springframework.security.core.Authentication;

@Builder
@AllArgsConstructor
@Getter
@Setter
@ToString
public class BeerDeleteEvent implements BeerEvent {

    private Beer beer;

    private Authentication authentication;
}
