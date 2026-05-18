package com.scm.services;

import org.springframework.web.multipart.MultipartFile;

public interface ImageService {
  String  uploadImage(MultipartFile multipartFile,String filenameRandom);

  String getUrlFromPublicId(String publicId);
}
