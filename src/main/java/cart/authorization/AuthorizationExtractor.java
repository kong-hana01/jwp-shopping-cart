package cart.authorization;

import javax.servlet.http.HttpServletRequest;

public interface AuthorizationExtractor<T> {

    String AUTHORIZATION = "Authorization";

    T extract(HttpServletRequest request);

    T extract(String header);
}
