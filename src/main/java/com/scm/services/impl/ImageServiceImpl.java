package com.scm.services.impl;

import com.cloudinary.Cloudinary;
import com.cloudinary.Transformation;
import com.cloudinary.utils.ObjectUtils;
import com.scm.helper.AppConstants;
import com.scm.services.ImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;
@Service
public class ImageServiceImpl implements ImageService {
    @Autowired
    private Cloudinary cloudinary;
    @Override
    public String uploadImage(MultipartFile multipartFile,String filenameRandom) {

        try{

            byte [] data=new byte[multipartFile.getInputStream().available()];

            multipartFile.getInputStream().read(data);

            cloudinary.uploader().upload(data, ObjectUtils.asMap(
                    "public_id",filenameRandom
            ));
            return this.getUrlFromPublicId(filenameRandom);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }

    @Override
    public String getUrlFromPublicId(String publicId) {
        return cloudinary.url().transformation(
                new Transformation<>().width(AppConstants.CONTACT_IMAGE_WIDTH).height(AppConstants.CONTACT_IMAGE_HEIGHT).crop(AppConstants.CONTACT_IMAGE_CROP)
        ).generate(publicId);
    }
}
