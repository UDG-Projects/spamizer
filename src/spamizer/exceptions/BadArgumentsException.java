package spamizer.exceptions;

public class BadArgumentsException extends Exception {

    private String message;

    public BadArgumentsException(String message){
        this.message = message;
    }

    public String getCustomMessage(){
        return this.message;
    }

}
