package com.kingold.educationblockchain.controller;

import com.kingold.educationblockchain.bean.CertInfo;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.client.HttpClientErrorException;

import javax.servlet.http.HttpServletRequest;
import java.util.List;


@Controller

public class StudentInfoAndCertsController {

    @RequestMapping(value="/certList",method = RequestMethod.GET)
    public String list(ModelMap model, HttpServletRequest request) {
        try {
            model.addAttribute("title", "学生信息及所获证书");
            CommonController commonController =new CommonController();
            List<CertInfo> json=  commonController.QueryCertByCRMId(request.getParameter("crm_id"),request.getParameter("channel"));
            model.addAttribute("json", json);
        }
        catch (HttpClientErrorException ex)
        {
            String errorMessage = ex.getResponseBodyAsString();
            model.addAttribute("err_message", errorMessage);
            return "error";
        }

        return "studentinfoandcerts";
    }
}
