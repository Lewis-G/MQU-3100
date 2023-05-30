package Stage2Enhanced;

import java.util.ArrayList;

public class Server {

    ArrayList<int[]> queueLengths;
    ArrayList<String> serverTypes;

    Server(){
        this.serverTypes = new ArrayList<>();
        this.serverTypes = new ArrayList<>();
    }

    public void addServerType(String serverType, int numberOfServers){
        this.serverTypes.add(serverType);
        this.queueLengths.add(new int[numberOfServers]);
    }

    public int getQueueLength(String serverType, int serverID){

        int serverTypePlacement = this.serverTypes.indexOf(serverType);
        return this.queueLengths.get(serverTypePlacement)[serverID];
    }

    public void increaseQueueLength(String serverType, int serverID){

        int serverTypePlacement = this.serverTypes.indexOf(serverType);
        this.queueLengths.get(serverTypePlacement)[serverID]++;
        return;
    }

    public void decreaseQueueLength(String serverType, int serverID){

        int serverTypePlacement = this.serverTypes.indexOf(serverType);
        this.queueLengths.get(serverTypePlacement)[serverID]--;
        return;
    }



}