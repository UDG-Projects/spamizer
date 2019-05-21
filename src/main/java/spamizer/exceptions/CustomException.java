package main.java.spamizer.exceptions;

public class CustomException extends Exception{

    private String message;

    public CustomException(String message){
        this.message = message;
    }

    public String getCustomMessage(){
        return this.message;
    }

}
