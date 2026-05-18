package com.scm.services;

import com.scm.model.Contacts;
import com.scm.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ContactService {
    Contacts save(Contacts contacts);
    Contacts update(Contacts contacts);

    List<Contacts> getAll();

    Contacts getById(String id);

    void delete(String id);

    List<Contacts> search(String name,String email,String phoneNumber);

    List<Contacts> getByUserId(String userId);
    Page<Contacts> getByUser(User user,int page,int size,String sortBy);

    Page<Contacts> searchByName(String nameKeyword,int page,int size,String sortBy,User user);
    Page<Contacts> searchByEmail(String emailKeyword,int page,int size,String sortBy,User user);
    Page<Contacts> searchByPhone(String phoneKeyword,int page,int size,String sortBy,User user);

}
