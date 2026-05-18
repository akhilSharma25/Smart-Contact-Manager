package com.scm.validators;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.web.multipart.MultipartFile;



public class FileValidator implements ConstraintValidator<ValidFile,MultipartFile> {
     private static final long MAX_FILE_SIZE=1024*1024*5;// 5MB


    @Override
    public boolean isValid(MultipartFile file, ConstraintValidatorContext constraintValidatorContext) {
        if(file==null || file.isEmpty()){
            constraintValidatorContext.disableDefaultConstraintViolation();
            constraintValidatorContext.buildConstraintViolationWithTemplate("FILE cannot be empty").addConstraintViolation();
            return false;
        }

        if(file.getSize() > MAX_FILE_SIZE){
            constraintValidatorContext.disableDefaultConstraintViolation();
            constraintValidatorContext.buildConstraintViolationWithTemplate("FILE size should be less than 5 MB").addConstraintViolation();
            return false;
        }

        //resolution check
//        try {
//            java.awt.image.BufferedImage bufferedImage =ImageIO.read(file.getInputStream());
//            if(b)
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }


        return  true;
    }
}
