package com.kingold.educationblockchain.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import java.io.IOException;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/authcode")
public class AuthCodeController{

    /**
     * 日志
     */
    private static final Logger log = LoggerFactory.getLogger(AuthCodeController.class);

    /**
     * 前台请求得到验证码
     *
     * @param request
     * @param response
     * @throws Exception
     */
    @RequestMapping("/getCode")
    public void getCode(HttpServletRequest request, HttpServletResponse response) throws Exception {

        // 调用工具类生成的验证码
        Map<String, Object> codeMap = GenerateAuthCode.generateCodeAndPic();

        // 将四位数字的验证码保存到Session中。
        HttpSession session = request.getSession();
        session.session.setMaxInactiveInterval(15*60);
        session.setAttribute("authcode", codeMap.get("code").toString());
    }

    /**
     * 后台请求验证验证码是否正确
     *
     * @param request
     * @param response
     * @throws Exception
     */
    @RequestMapping("/checkCode")
    public void checkCode(HttpServletRequest request, HttpServletResponse response) throws Exception {

        String codeRequest = request.getParameter("authcode");
        // 验证验证码
        String sessionCode = request.getSession().getAttribute("authcode").toString();
        if (codeRequest != null && !"".equals(codeRequest) && sessionCode != null && !"".equals(sessionCode)) {
            if (codeRequest.equalsIgnoreCase(sessionCode)) {
                response.getWriter().println("1");
            } else {
                response.getWriter().println("0");
            }
        } else {
            response.getWriter().println("0");
        }
    }
}