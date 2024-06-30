package org.zerock.w2.filter;

import lombok.extern.log4j.Log4j2;
import org.zerock.w2.dto.MemberDTO;
import org.zerock.w2.service.MemberService;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Arrays;
import java.util.Optional;

@WebFilter(urlPatterns = {"/todo/*"})
@Log4j2
public class LoginCheckFilter implements Filter {
    @Override
    public void doFilter(ServletRequest request, ServletResponse response,
                         FilterChain chain) throws IOException, ServletException {

        log.info("Login check filter......");

        HttpServletRequest req = (HttpServletRequest)request;
        HttpServletResponse resp = (HttpServletResponse)response;

        HttpSession session = req.getSession();

        // 세션에 loginInfo 존재 확인
        if(session.getAttribute("loginInfo") != null) {
            chain.doFilter(request,response);
            return;
        }

        // session 에 loginInfo 값이 없다면 쿠키를 체크
        Cookie cookie = findCookie(req.getCookies(), "remember-me");

        // 쿠키가 없다면 로그인 페이지로
        if(cookie == null) {
            log.info("쿠키 없음, 로그인 페이지로 리다이렉트");
            resp.sendRedirect("/login");
            return;
        }

        // 쿠키가 존재한다면
//        log.info("cookie는 존재하는 상황");
        log.info("쿠키 존재: " + cookie.getValue());
        // uuid 값
        String uuid = cookie.getValue();

        try {
            // 데이터베이스 확인
            MemberDTO memberDTO = MemberService.INSTANCE.getByUUID(uuid);

            log.info("쿠키의 값으로 조회한 사용자 정보: " + memberDTO);
            if(memberDTO == null) {
                throw new Exception("쿠키 값이 유효하지 않음");
            }
            // 회원 정보를 세션에 추가
            session.setAttribute("loginInfo", memberDTO);
            log.info("세션에 사용자 정보 추가: " + memberDTO);
            chain.doFilter(request, response);
        } catch (Exception e) {
            log.error("로그인 체크 중 에러 발생", e);
            resp.sendRedirect("/login");
        }
    }

    private Cookie findCookie(Cookie[] cookies, String name) {

        if(cookies == null || cookies.length == 0) {
            return null;
        }

        Optional<Cookie> result = Arrays.stream(cookies)
                .filter(ck -> ck.getName().equals(name))
                .findFirst();

        return result.isPresent() ? result.get() : null;
    }
}
