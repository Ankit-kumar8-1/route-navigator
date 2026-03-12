package com.navigation.route_navigator.exceptions;

public class ResourceNotFoundException extends  RuntimeException{
    public  ResourceNotFoundException(String message){
        super(message);
    }
}
