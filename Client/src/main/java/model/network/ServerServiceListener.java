package model.network;

public interface ServerServiceListener {
    void serverResponse(ServerServiceThread serverService, String responseMsg);
}
