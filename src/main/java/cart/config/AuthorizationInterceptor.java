package cart.config;

import cart.authorization.BasicAuthorizationExtractor;
import cart.dto.AuthorizationInformation;
import cart.service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class AuthorizationInterceptor implements HandlerInterceptor {

    @Autowired
    private MemberService memberService;

    @Autowired
    private BasicAuthorizationExtractor basicAuthorizationExtractor;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        AuthorizationInformation authorizationInformation = basicAuthorizationExtractor.extract(request);
        return memberService.isValidMember(authorizationInformation);
    }
}
