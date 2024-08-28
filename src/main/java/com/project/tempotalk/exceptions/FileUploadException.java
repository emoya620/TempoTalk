package com.project.tempotalk.exceptions;

public class FileUploadException extends RuntimeException {
    // Return an error if there is a problem with uploading an image
    public FileUploadException(String message){
        super(message);
    }
}