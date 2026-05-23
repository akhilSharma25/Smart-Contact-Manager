package com.scm.controller;

import com.scm.model.Contacts;
import com.scm.services.ContactService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class ApiController {

    @Autowired
     public ContactService service;

    @GetMapping("/contacts/{contactId}")
    public Contacts getContact(@PathVariable String contactId){

        return  service.getById(contactId);
    }
}
