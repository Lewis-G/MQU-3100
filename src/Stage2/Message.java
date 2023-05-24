package Stage2;

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

    Message(){
        jobnSplit = new String[7];
        dataSplit = new String[3];
        serverSplit = new String[3];
    }

    public void ParseJOBN(String message){

        jobnSplit = message.split(" ", 7);

        jobID = Integer.parseInt(jobnSplit[2]);
        jobCores = Integer.parseInt(jobnSplit[4]);
        jobMemory = Integer.parseInt(jobnSplit[5]);
        jobDisk = Integer.parseInt(jobnSplit[6]);
        return;
    }

    public void ParseDataMessage(String message){

        dataSplit = message.split(" ", 3);
        nRecs = Integer.valueOf(dataSplit[1]);
        return;
    }

    public void ParseServerDetails(String message){

        serverSplit = message.split(" ", 3);
        serverType = serverSplit[0];
        serverID = Integer.parseInt(serverSplit[1]);
        return;
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
