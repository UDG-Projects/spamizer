package spamizer.exceptions;

public class BadPercentageException extends Exception {

    private String message;

    public BadPercentageException(String message){
        this.message = message;
    }

    public String getCustomMessage(){
        return this.message;
    }
}
