package ch.springframeworkguru.springrestmvc.event.events;

import ch.springframeworkguru.springrestmvc.entity.Beer;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.Authentication;

@Builder
@AllArgsConstructor
@Getter
@Setter
public class BeerPatchEvent implements BeerEvent {
    
    private Beer beer;
    
    private Authentication authentication;
}
