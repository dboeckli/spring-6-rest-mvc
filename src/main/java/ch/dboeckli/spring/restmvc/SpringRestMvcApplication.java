package ch.dboeckli.spring.restmvc;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.web.config.EnableSpringDataWebSupport;

import static org.springframework.data.web.config.EnableSpringDataWebSupport.PageSerializationMode.VIA_DTO;

@SpringBootApplication
// the page elements are under now page not in the root anymore:
/*
{
  "content": [ ... ],     // Die eigentlichen Daten bleiben hier
  "page": {               // NEU: Metadaten sind verschachtelt
    "totalElements": 10,
    "totalPages": 2,
    "size": 5,
    "number": 0
  }
}
e.g. instead of response.totalElements it is under response.page.totalElements
and
$.totalElements in $.page.totalElements
 */
@EnableSpringDataWebSupport(pageSerializationMode = VIA_DTO)
public class SpringRestMvcApplication {
    public static void main(String[] args) {
        SpringApplication.run(SpringRestMvcApplication.class, args);
    }

}
