package Stage2Enhanced;

public class Message {
    
    int jobID;
    int jobCores;
    int jobMemory;
    int jobDisk;

    int nRecs;
    int serverID;
    String serverType;

    String[] jobnSplit;
    String[] dataSplit;
    String[] serverSplit;
    String[] jcplSplit;

    Message(){
        jobnSplit = new String[7];
        dataSplit = new String[3];
        serverSplit = new String[3];
        jcplSplit = new String[5];
    }

    public void parseJOBN(String message){

        jobnSplit = message.split(" ", 7);

        jobID = Integer.parseInt(jobnSplit[2]);
        jobCores = Integer.parseInt(jobnSplit[4]);
        jobMemory = Integer.parseInt(jobnSplit[5]);
        jobDisk = Integer.parseInt(jobnSplit[6]);
        return;
    }

    public void parseDataMessage(String message){

        dataSplit = message.split(" ", 3);
        nRecs = Integer.valueOf(dataSplit[1]);
        return;
    }

    public void parseServerDetails(String message){

        serverSplit = message.split(" ", 3);
        serverType = serverSplit[0];
        serverID = Integer.parseInt(serverSplit[1]);
        return;
    }

    public void parseJCPLMessage(String message){

        jcplSplit = message.split(" ", 5);
        serverType = jcplSplit[3];
        serverID = Integer.parseInt(jcplSplit[4]);
        return;
    }

    public String getServerType(){
        return serverType;
    }

    public int getServerID(){
        return serverID;
    }

    public String createGetsAvail(){
        return "GETS Avail " + jobCores + " " + jobMemory + " " + jobDisk + "\n";
    }

    public String createGetsCapable(){
        return "GETS Capable " + jobCores + " " + jobMemory + " " + jobDisk + "\n";
    }

    public String createSchd(){
        return "SCHD " + jobID + " " + serverType + " " + serverID + "\n";
    }

    public int getNRecs(){
        return nRecs;
    }    

}
