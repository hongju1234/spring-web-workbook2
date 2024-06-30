package org.zerock.w2.controller;

import lombok.extern.log4j.Log4j2;
import org.zerock.w2.dto.MemberDTO;
import org.zerock.w2.service.MemberService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.util.UUID;

@WebServlet("/login")
@Log4j2
public class LoginController extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws
            ServletException, IOException {

        log.info("login get...................");

        req.getRequestDispatcher("/WEB-INF/todo/login.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws
            ServletException, IOException {

        log.info("login post...........");

        String mid = req.getParameter("mid");
        String mpw = req.getParameter("mpw");

        String auto = req.getParameter("auto");
        // 'auto'라는 이름으로 체크박스에서 전송되는 값이 'on'인지
        boolean rememberMe = auto != null && auto.equals("on");

        try {
            // 로그인이 정상적으로 된 다면 HttpSession을 이용해 'loginInfo' 이름으로 객체 저장
            MemberDTO memberDTO = MemberService.INSTANCE.login(mid, mpw);

            // rememberMe라는 변수가 true라면 UUID를 이용해 임의의 번호 생성
            if(rememberMe) {
                String uuid = UUID.randomUUID().toString();

                MemberService.INSTANCE.updateUuid(mid, uuid);
                memberDTO.setUuid(uuid);

                // 브라우저에 remember-me 이름의 쿠키 생성하여 전송
                Cookie rememberCookie =
                        new Cookie("remember-me", uuid);
                rememberCookie.setMaxAge(60*60*24*7); // 쿠키의 유효기간은 1주일
                rememberCookie.setPath("/");

                resp.addCookie(rememberCookie);
            }

            HttpSession session = req.getSession();
            session.setAttribute("loginInfo", memberDTO);
            resp.sendRedirect("/todo/list");

        } catch (Exception e) {
            resp.sendRedirect("/login?result=error");
        }
    }
}
