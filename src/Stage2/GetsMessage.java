package MyClient;

public class GetsMessage {

    int nRecs;
    int serverID;
    String serverType;

    String[] dataNSplit;
    String[] serverDetailsSplit;

    GetsMessage(){
        dataNSplit = new String[3];
        serverDetailsSplit = new String[3];
    }

    public void ParseDataMessage(String message){

        dataNSplit = message.split(" ", 3);
        nRecs = Integer.valueOf(dataNSplit[1]);
        return;
    }

    public void ParseServerDetails(String message){

        serverDetailsSplit = message.split(" ", 3);

        serverType = serverDetailsSplit[0];
        serverID = Integer.parseInt(serverDetailsSplit[1]);
        
        return;
    }

    public int getNRecs(){
        return nRecs;
    }

    public int getServerID(){
        return serverID;
    }

    public String getServerType(){
        return serverType;
    }
    
}