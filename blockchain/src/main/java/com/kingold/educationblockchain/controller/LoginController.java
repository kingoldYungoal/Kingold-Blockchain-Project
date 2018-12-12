package com.kingold.educationblockchain.controller;

import com.google.gson.Gson;
import com.kingold.educationblockchain.bean.*;
import com.kingold.educationblockchain.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/home")
public class LoginController {
    @Autowired
    private ParentInformationService mParentInfomationService;
    @Autowired
    private TeacherInformationService mTeacherInfomationService;
    private Gson gson;

    @RequestMapping("/login")
    public String UserLogin(){
        return "/login";
    }

    @RequestMapping(value = "/loginauth",method = RequestMethod.GET)
    @ResponseBody
    public String UserLoginAuth(@RequestParam(value = "phonenumber", required = true)String phonenumber, @RequestParam(value = "role", required = true)int role){
        gson = new Gson();
        boolean flag = false;
        //role 為1，代表家長，為2，代表教師
        if (role == 1) {
            ParentInformation parentInformation = mParentInfomationService.FindParentInformationByPhone(phonenumber);
            if (parentInformation != null) {
                flag = true;
            }
        }else{
            TeacherInformation teacherInformation = mTeacherInfomationService.FindTeacherInformationByPhone(phonenumber);
            if (teacherInformation != null) {
                flag = true;
            }
        }
        return gson.toJson(flag);
    }
}
