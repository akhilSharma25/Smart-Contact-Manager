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
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

        if (contactForm.getContactImage() == null || contactForm.getContactImage().isEmpty()) {
            bindingResult.rejectValue("contactImage", "contactImage", "Please upload a profile image");
        }

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
        contacts.setWebsiteLink(Helper.normalizeUrl(contactForm.getWebsiteLink()));
        contacts.setLinkedLink(Helper.normalizeUrl(contactForm.getLinkedInLink()));
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

        String username = Helper.getEmailOfLoggedInUser(authentication);
        User user1 = repo.findByEmail(username).orElseThrow(() -> new RuntimeException("Authenticated user not found"));
        Page<Contacts> pageContact = service.getByUser(user1, page, size, sortBy, direction);
        model.addAttribute("pageContact", pageContact);
        model.addAttribute("pageSize", AppConstants.PAGE_SIZE);
        model.addAttribute("direction", direction);
        model.addAttribute("sortBy", sortBy);
        model.addAttribute("contactSearchForm", contactSearchForm);

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
                                 @RequestParam("keyword") String keyword,
                                 Model model){

        String username = Helper.getEmailOfLoggedInUser(authentication);
        User user1 = repo.findByEmail(username).orElseThrow(() -> new RuntimeException("Authenticated user not found"));
        contactSearchForm.setField(field);
        contactSearchForm.setKeyword(keyword);

        Page<Contacts> pageContact;
        if ("email".equalsIgnoreCase(field)) {
            pageContact = service.searchByEmail(keyword, page, size, sortBy, direction, user1);
        } else if ("phone".equalsIgnoreCase(field)) {
            pageContact = service.searchByPhone(keyword, page, size, sortBy, direction, user1);
        } else {
            pageContact = service.searchByName(keyword, page, size, sortBy, direction, user1);
        }

        model.addAttribute("pageContact", pageContact);
        model.addAttribute("contactSearchForm", contactSearchForm);
        model.addAttribute("pageSize", AppConstants.PAGE_SIZE);
        model.addAttribute("direction", direction);
        model.addAttribute("sortBy", sortBy);
        return "user/search";
    }

    @RequestMapping("/delete/{contactId}")
    public String deleteContact(
            @PathVariable("contactId") String contactId,
            HttpSession session,
            Authentication authentication) {
        String username = Helper.getEmailOfLoggedInUser(authentication);
        User currentUser = repo.findByEmail(username).orElseThrow(() -> new RuntimeException("Authenticated user not found"));
        Contacts contact = service.getById(contactId);
        if (contact.getUser() == null || !contact.getUser().getEmail().equalsIgnoreCase(currentUser.getEmail())) {
            throw new RuntimeException("Contact not found or access denied");
        }
        service.delete(contactId);

        session.setAttribute("message",
                Message.builder()
                        .content("Contact is Deleted successfully !! ")
                        .type(MessageType.green)
                        .build()

        );

        return "redirect:/user/contacts";
    }

    @GetMapping("/view/{contactId}")
    public String updateContactFormView(
            @PathVariable("contactId") String contactId,
            Authentication authentication,
            Model model) {

        String username = Helper.getEmailOfLoggedInUser(authentication);
        User currentUser = repo.findByEmail(username).orElseThrow(() -> new RuntimeException("Authenticated user not found"));
        Contacts contact = service.getById(contactId);
        if (contact.getUser() == null || !contact.getUser().getEmail().equalsIgnoreCase(currentUser.getEmail())) {
            throw new RuntimeException("Contact not found or access denied");
        }

        ContactForm contactForm = new ContactForm();
        contactForm.setName(contact.getName());
        contactForm.setEmail(contact.getEmail());
        contactForm.setPhoneNumber(contact.getPhoneNumber());
        contactForm.setAddress(contact.getAddress());
        contactForm.setDescription(contact.getDescription());
        contactForm.setFavorite(contact.getFavorite());
        contactForm.setWebsiteLink(contact.getWebsiteLink());
        contactForm.setLinkedInLink(contact.getLinkedLink());
        contactForm.setPicture(contact.getPicture());
        ;
        model.addAttribute("contactForm", contactForm);
        model.addAttribute("contactId", contactId);

        return "user/update_contact";
    }

    @GetMapping("/modal/{contactId}")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getContactModalData(
            @PathVariable("contactId") String contactId,
            Authentication authentication) {

        String username = Helper.getEmailOfLoggedInUser(authentication);
        User currentUser = repo.findByEmail(username).orElseThrow(() -> new RuntimeException("Authenticated user not found"));
        Contacts contact = service.getById(contactId);
        if (contact.getUser() == null || !contact.getUser().getEmail().equalsIgnoreCase(currentUser.getEmail())) {
            return ResponseEntity.status(403).body(Map.of("error", "Access denied"));
        }

        Map<String, Object> contactData = new HashMap<>();
        contactData.put("id", contact.getId());
        contactData.put("name", contact.getName());
        contactData.put("email", contact.getEmail());
        contactData.put("phoneNumber", contact.getPhoneNumber());
        contactData.put("address", contact.getAddress());
        contactData.put("description", contact.getDescription());
        contactData.put("websiteLink", contact.getWebsiteLink());
        contactData.put("linkedLink", contact.getLinkedLink());
        contactData.put("picture", contact.getPicture());
        contactData.put("favorite", contact.getFavorite());

        return ResponseEntity.ok(contactData);
    }

    @RequestMapping(value = "/update/{contactId}", method = RequestMethod.POST)
    public String updateContact(@PathVariable("contactId") String contactId,
                                @Valid @ModelAttribute ContactForm contactForm,
                                BindingResult bindingResult,
                                Authentication authentication,
                                HttpSession session,
                                Model model) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("contactId", contactId);
            return "user/update_contact";
        }

        String username = Helper.getEmailOfLoggedInUser(authentication);
        User currentUser = repo.findByEmail(username).orElseThrow(() -> new RuntimeException("Authenticated user not found"));
        Contacts con = service.getById(contactId);
        if (con.getUser() == null || !con.getUser().getEmail().equalsIgnoreCase(currentUser.getEmail())) {
            throw new RuntimeException("Contact not found or access denied");
        }

        con.setId(contactId);
        con.setName(contactForm.getName());
        con.setEmail(contactForm.getEmail());
        con.setPhoneNumber(contactForm.getPhoneNumber());
        con.setAddress(contactForm.getAddress());
        con.setDescription(contactForm.getDescription());
        con.setFavorite(contactForm.isFavorite());
        con.setWebsiteLink(Helper.normalizeUrl(contactForm.getWebsiteLink()));
        con.setLinkedLink(Helper.normalizeUrl(contactForm.getLinkedInLink()));

        if (contactForm.getContactImage() != null && !contactForm.getContactImage().isEmpty()) {
            String fileName = UUID.randomUUID().toString();
            String imageUrl = imageService.uploadImage(contactForm.getContactImage(), fileName);
            con.setCloudinaryImagePublicId(fileName);
            con.setPicture(imageUrl);
            contactForm.setPicture(imageUrl);
        } else {
            System.out.println("file is empty");
        }

        service.update(con);
        session.setAttribute("message",
                Message.builder()
                        .content("Contact Updated Successfully")
                        .type(MessageType.green)
                        .build());

        return "redirect:/user/contacts";
    }
}
