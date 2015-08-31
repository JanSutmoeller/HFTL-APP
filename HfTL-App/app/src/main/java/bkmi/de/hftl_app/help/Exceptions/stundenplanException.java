package bkmi.de.hftl_app.help.Exceptions;

public class stundenplanException extends Exception{
    public int fehlerNetzwerk;
    public stundenplanException(int i)
    {
        super();
        fehlerNetzwerk=i;
    }
}
