package Stage2Enhanced;

public class Server {

    ArrayList<int[]> queueLengths;
    ArrayList<String> serverTypes;

    Server(){
        this.servers = new ArrayList<>();
        this.serverTypes = new ArrayList<>();
    }

    public void addServerType(String inputServerType){

        this.serverTypes.add(inputServerType);
        

    }




}