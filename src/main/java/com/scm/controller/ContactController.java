package com.scm.controller;

import com.scm.forms.ContactForm;
import com.scm.forms.ContactSearchForm;
import com.scm.helper.AppConstants;
import com.scm.helper.Helper;
import com.scm.helper.Message;
import com.scm.helper.MessageType;
import com.scm.model.Contacts;
import com.scm.model.User;
import com.scm.repo.UserRepo;
import com.scm.services.ContactService;
import com.scm.services.ImageService;
import com.scm.services.UserService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Controller
@RequestMapping("/user/contacts")
public class ContactController {

    @Autowired
private ContactService service;

    @Autowired
    private ImageService imageService;

    @Autowired
    private UserService userService;
    @Autowired
    private UserRepo repo;
    @RequestMapping("/add")
    // add contact page: handler
    public String addContactView(Model model) {
        ContactForm contactForm = new ContactForm();

        contactForm.setFavorite(true);
        model.addAttribute("contactForm", contactForm);
        return "user/add_contact";
    }


    @PostMapping("/add")
    public String saveContact(@Valid @ModelAttribute ContactForm contactForm, BindingResult bindingResult, Authentication authentication, HttpSession session,Model model){

        if(bindingResult.hasErrors()){
            return "user/add_contact";
        }

        //upload krna kaa code
        String filenameRandom= UUID.randomUUID().toString();

        String fileUrl=imageService.uploadImage(contactForm.getContactImage(),filenameRandom);
        String username= Helper.getEmailOfLoggedInUser(authentication);
        Optional<User> user=repo.findByEmail(username);
        Contacts contacts =new Contacts();
        contacts.setName(contactForm.getName());
        contacts.setFavorite(contactForm.isFavorite());
        contacts.setEmail(contactForm.getEmail());
        contacts.setAddress(contactForm.getAddress());
        contacts.setDescription(contactForm.getDescription());
        contacts.setPhoneNumber(contactForm.getPhoneNumber());
        contacts.setUser(user.get());
        contacts.setWebsiteLink(contactForm.getWebsiteLink());
        contacts.setLinkedLink(contactForm.getLinkedInLink());
        contacts.setPicture(fileUrl);
        System.out.println(contacts);
        service.save(contacts);
        Message mes=Message.builder().content("Contact Save Successfully").type(MessageType.green).build();
        session.setAttribute("message",mes);
        return "redirect:/user/contacts/add";

    }


    @GetMapping
    public  String viewContacts(@RequestParam(value = "page",defaultValue = "0") int page,
                                @RequestParam(value = "size",defaultValue = "10") int size,
                                @RequestParam(value = "sortBy",defaultValue = "name") String sortBy,
                                @RequestParam(value = "direction",defaultValue = "asc") String direction,
                                @ModelAttribute ContactSearchForm contactSearchForm,

                                Authentication authentication, Model model){

       String username= Helper.getEmailOfLoggedInUser(authentication);
                 User user1=repo.findByEmail(username).get();
               Page<Contacts> pageContact=  service.getByUser(user1,page,size,sortBy);
               model.addAttribute("pageContact",pageContact);

               model.addAttribute("pageSize", AppConstants.PAGE_SIZE);
        model.addAttribute("contactSearchForm",contactSearchForm);

        return "user/contacts";
    }


    @GetMapping("/search")
    public  String searchHandler(@RequestParam(value = "page",defaultValue = "0") int page,
                                 @RequestParam(value = "size",defaultValue = "10") int size,
                                 @RequestParam(value = "sortBy",defaultValue = "name") String sortBy,
                                 @RequestParam(value = "direction",defaultValue = "asc") String direction,
                                 @RequestParam("field") String field,
                                 Authentication authentication,
                                 @ModelAttribute ContactSearchForm contactSearchForm,
                                 @RequestParam("keyword") String keyword,Model model){

        String username = Helper.getEmailOfLoggedInUser(authentication);
        User user1 = repo.findByEmail(username).get();
        Page<Contacts>pageContact=null;
        if(contactSearchForm.getField().equalsIgnoreCase("name")){
            pageContact = service.searchByName(contactSearchForm.getKeyword(), page, size, sortBy, user1);
        }else if(contactSearchForm.getField().equalsIgnoreCase("email")){
            pageContact = service.searchByEmail(contactSearchForm.getKeyword(), page, size, sortBy, user1);
        }else if(contactSearchForm.getField().equalsIgnoreCase("phone")){
            pageContact = service.searchByPhone(contactSearchForm.getKeyword(), page, size, sortBy, user1);
        }

        model.addAttribute("pageContact",pageContact);
        model.addAttribute("contactSearchForm",contactSearchForm);
        model.addAttribute("pageSize", AppConstants.PAGE_SIZE);
        return "user/search";
    }

}
