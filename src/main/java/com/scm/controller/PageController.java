package com.scm.controller;

import com.scm.forms.UserForm;
import com.scm.helper.Message;
import com.scm.helper.MessageType;
import com.scm.model.User;
import com.scm.repo.UserRepo;
import com.scm.services.impl.UserServiceIml;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class PageController {
    @Autowired
    private UserServiceIml serviceIml;
    @Autowired
    private UserRepo repo;

    @GetMapping("/")
    public String index() {
        return "redirect:/home";
    }
    @GetMapping("/home")
    public  String home(Model model, Authentication authentication){
        model.addAttribute("name","Substring Technologies");
        model.addAttribute("city","Delhi");
        
        // Pass actual logged-in user status
        boolean isLoggedIn = authentication != null && authentication.isAuthenticated();
        if (authentication != null && authentication.isAuthenticated()) {

            String username = authentication.getName();
            // Database se pura User object dhoondho
            User user = repo.findByEmail(username).orElse(null);
            model.addAttribute("loggedInUser", user);
        } else {
            model.addAttribute("loggedInUser", null);
        }        return "home";
    }

    @RequestMapping("/about")
    public String aboutPage(Model model) {
        model.addAttribute("isLogin", true);
        System.out.println("About page loading");
        return "about";
    }

    @RequestMapping("/services")
    public String servicesPage() {
        System.out.println("services page loading");
        return "services";
    }

    @GetMapping("/contact")
    public String contact() {
        return new String("contact");
    }

    // this is showing login page
    @GetMapping("/login")
    public String login() {
        return new String("login");
    }

    @GetMapping("/signup")
    public String sign(Model model) {
        model.addAttribute("userForm", new UserForm());
        return new String("register");
    }

    //processing register

    @PostMapping("/do-register")
    public  String processRegister(@Valid @ModelAttribute UserForm userForm, BindingResult bindingResult, HttpSession httpSession){
        System.out.println("Processing Registration");

        //validate form data
        if(bindingResult.hasErrors()){
            return "register";
        }
        //save to database
//        userForm->user
        User user=new User();
        user.setName(userForm.getName());
        user.setEmail(userForm.getEmail());
        user.setAbout(userForm.getAbout());
        user.setPhoneNumber(userForm.getPhoneNumber());
        user.setPassword(userForm.getPassword());

        User saveUser=serviceIml.saveUser(user);

        Message mes=Message.builder().content("Registration Successful").type(MessageType.green).build();
        httpSession.setAttribute("message",mes);

        System.out.println("User saved");
        return "redirect:/signup";
    }




}
