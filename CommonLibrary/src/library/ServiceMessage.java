package library;

public class ServiceMessage {
    //inner Client
    public static final String TRY_TO_CONNECT = "try_to_connect";
    public static final String NO_CONNECT = "cant_connect";
    public static final String CONNECTED_TO_SERVER = "connected_to_server";
    public static final String MAIN_INFO = "main_info";
    public static final String FILE_SAVED = "file_saved";
    public static final String CONNECTION_LOST = "connection_lost";

    //Client to Server
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
    public static final String INFO = "information";
    public static final String SAVE_FILE_INFO = "info_to_save_file";
}
