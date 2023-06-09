package com.kbstar.controller;


import ch.qos.logback.classic.Logger;
import com.kbstar.dto.Product;
import com.kbstar.dto.User;
import com.kbstar.service.ProductService;
import com.kbstar.service.UserService;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.validation.ObjectError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpSession;
import java.util.List;


@Slf4j
@Controller
public class MainController {
    @Autowired
    ProductService productService;
    @Autowired
    UserService userService;
    Logger logger;
    String dir = "shop/";
    // 0- 초기화면 : 127.0.0.1
    @RequestMapping("/")
    public String main(Model model) throws Exception {
        // selectAll 사용
        List<Product> list = null;
            list = productService.get();

        // list에 담은 Product를 브라우저 화면에 보여주기(jsp파일에 입력 시 명칭 allproduct)
        model.addAttribute("allproduct", list);
        return "index";
    }
    // 1-1 로그인화면 : 127.0.0.1/login
    @RequestMapping("/login")
    public String login(Model model){
        model.addAttribute("center", "login"); // center에 login페이지 표출
        return "index";
    }

    @RequestMapping("/loginimpl")
    public String loginimpl(Model model, String user_id, String user_pwd, HttpSession session) throws Exception {
        String nextPage = "loginfail";
        try {
            User user = userService.get(user_id);
            if (user != null && user.getUser_pwd().equals(user_pwd)) {
                session.setMaxInactiveInterval(100000);
                session.setAttribute("loginuser", user);
            } else {
                model.addAttribute("center", nextPage);
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
            nextPage = "loginfail";
        }
        model.addAttribute("center", dir + "center");
        return "index";
    }
    //
    @RequestMapping("/register")
    public String register(Model model){
        model.addAttribute("center", "register"); // center에 login페이지 표출
        return "index";
    }
    @RequestMapping("/registerimpl")
    public String registerimpl(Model model,
                               @Validated User user, Errors errors, HttpSession session) throws Exception {
        if(errors.hasErrors()){
            List<ObjectError> ex = errors.getAllErrors();
            for(ObjectError e:ex){
                log.info("------------------------");
                log.info(e.getDefaultMessage());
            }
            throw new Exception("형식 오류"+errors.toString());
        }
        try {
            userService.register(user);
            session.setAttribute("loginuser",user);
        } catch (Exception e) {
            throw new Exception("가입 오류");
        }
        model.addAttribute("ruser", user);
        model.addAttribute("center",dir+"center");
        return "index";
    }
    @RequestMapping("/profile")
    public String profile(Model model, HttpSession session) throws Exception {
        User user = (User) session.getAttribute("loginuser");
        String userId = user.getUser_id();
        User profileUser = userService.get(userId);
        model.addAttribute("userinfo", profileUser);
        model.addAttribute("center", "profile");
        return "index";
    }
}