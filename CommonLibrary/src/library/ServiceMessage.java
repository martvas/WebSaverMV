package library;

public class ServiceMessage {
    //Client
    public static final String LOGIN_ATTEMPT = "login_attempt";
    public static final String REGISTR = "registration";
    public static final String ADD_FILE = "add_file";
    public static final String DELETE_FILE = "delete_file";
    public static final String SAVE_FILE = "save_file";

    //Server
    public static final String LOGIN_ACCEPTED = "login_accepted";
    public static final String LOGIN_CANCELED = "login_canceled";
    public static final String UPDATE = "update";
    public static final String REGISTRATION_ACCEPTED = "registration_accepted";
    public static final String REGISTRATION_CANCELED = "registration_canceled";

}
