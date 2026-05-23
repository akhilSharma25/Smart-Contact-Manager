package com.scm.services.impl;

import com.scm.helper.ResourceNotFoundExpection;
import com.scm.model.Contacts;
import com.scm.model.User;
import com.scm.repo.ContactRepo;
import com.scm.services.ContactService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class ContactServiceImp implements ContactService {
    @Autowired
    private ContactRepo repo;
    @Override
    public Contacts save(Contacts contacts) {
        String contactId= UUID.randomUUID().toString();
        contacts.setId(contactId);
        return repo.save(contacts);

    }

@Override
public Contacts update(Contacts contacts) {
    // 1. Pehle check karo ki contact database mein exist karta hai ya nahi
    Contacts contactOld = repo.findById(contacts.getId())
            .orElseThrow(() -> new ResourceNotFoundExpection("Contact not found with id: " + contacts.getId()));

    // 2. Purane contact ke data ko update karo (Mapping)
    contactOld.setName(contacts.getName());
    contactOld.setEmail(contacts.getEmail());
    contactOld.setPhoneNumber(contacts.getPhoneNumber());
    contactOld.setAddress(contacts.getAddress());
    contactOld.setDescription(contacts.getDescription());
    contactOld.setFavorite(contacts.getFavorite());
    contactOld.setWebsiteLink(contacts.getWebsiteLink());
    contactOld.setLinkedLink(contacts.getLinkedLink());
    
    // Agar image update hui hai toh picture update karo
    if (contacts.getPicture() != null && !contacts.getPicture().isEmpty()) {
        contactOld.setPicture(contacts.getPicture());
        contactOld.setCloudinaryImagePublicId(contacts.getCloudinaryImagePublicId());
    }

    // 3. Database mein save kardo
    return repo.save(contactOld);
}

    @Override
    public List<Contacts> getAll() {
        return repo.findAll();
    }

    @Override
    public Contacts getById(String id) {
        return repo.findById(id).orElseThrow(()->new ResourceNotFoundExpection("Contact not found with given "+id));
    }

    @Override
    public void delete(String id) {
        com.scm.model.Contacts contacts =repo.findById(id).orElseThrow(()->new ResourceNotFoundExpection("Contact not found with given "+id));
        repo.delete(contacts);

    }

    @Override
    public List<Contacts> search(String name, String email, String phoneNumber) {
        return List.of();
    }

    @Override
    public List<Contacts> getByUserId(String userId) {

        return repo.findByUserId(userId);
    }

    @Override
    public Page<Contacts> getByUser(User user, int page, int size, String sortBy, String direction) {
        Sort sort = "asc".equalsIgnoreCase(direction) ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        var pageable = PageRequest.of(page, size, sort);
        return repo.findByUser(user, pageable);
    }

    @Override
    public Page<Contacts> searchByName(String nameKeyword, int page, int size, String sortBy, String direction, User user) {
        Sort sort = "asc".equalsIgnoreCase(direction) ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        var pageable = PageRequest.of(page, size, sort);
        return repo.findByUserAndNameContainingIgnoreCase(user, nameKeyword, pageable);
    }

    @Override
    public Page<Contacts> searchByEmail(String emailKeyword, int page, int size, String sortBy, String direction, User user) {
        Sort sort = "asc".equalsIgnoreCase(direction) ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        var pageable = PageRequest.of(page, size, sort);
        return repo.findByUserAndEmailContainingIgnoreCase(user, emailKeyword, pageable);
    }

    @Override
    public Page<Contacts> searchByPhone(String phoneKeyword, int page, int size, String sortBy, String direction, User user) {
        Sort sort = "asc".equalsIgnoreCase(direction) ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        var pageable = PageRequest.of(page, size, sort);
        return repo.findByUserAndPhoneNumberContainingIgnoreCase(user, phoneKeyword, pageable);
    }

    }