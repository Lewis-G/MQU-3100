package Stage2Enhanced;

public class Server {

    ArrayList<int[]> queueLengths;
    ArrayList<String> serverTypes;

    Server(){
        this.servers = new ArrayList<>();
        this.serverTypes = new ArrayList<>();
    }

    public void addServerType(String serverType, int numberOfServers){

        this.serverTypes.add(inputServerType);
        this.queueLengths.add(new int[numberOfServers]);
    }




}