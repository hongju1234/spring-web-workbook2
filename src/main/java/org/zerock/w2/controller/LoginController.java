package org.zerock.w2.controller;

import lombok.extern.log4j.Log4j2;
import org.zerock.w2.dto.MemberDTO;
import org.zerock.w2.service.MemberService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

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

        log.info("Attempting login with mid: {} and mpw: {}", mid, mpw);

        try {
            // 로그인이 정상적으로 된 다면 HttpSession을 이용해 'loginInfo' 이름으로 객체 저장
            MemberDTO memberDTO = MemberService.INSTANCE.login(mid, mpw);
            HttpSession session = req.getSession();
            session.setAttribute("loginInfo", memberDTO);
            log.info("Login successful for mid: {}", mid);
            resp.sendRedirect("/todo/list");
        } catch (Exception e) {
            // 로그인이 안되는 예외 발생 시 '/login' 으로 이동 후 파라미터로 문제 사실 전달
            log.error("Login failed for mid: {}", mid, e);
            resp.sendRedirect("/login?result=error");
        }
    }
}
