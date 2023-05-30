package Stage2Enhanced;

import java.io.*;
import java.net.*;

public class MyClient {

    Socket socket;
    DataOutputStream out;
    BufferedReader in;
    Message message;
    Server server;

    MyClient() {
        try {
            this.socket = new Socket("localhost", 50000);
            this.out = new DataOutputStream(socket.getOutputStream());
            this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.message = new Message();
            this.server = new Server();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Message getMessage(){
        return this.message;
    }

    public Server getServer(){
        return this.server;
    }

    public void endConnection(){
        try {
            out.flush();
            out.close();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void send(String toServer) {
        try {
            out.write((toServer).getBytes());
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void receive() {
        try {
            in.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String receiveString() {
        try {
            return (String) in.readLine();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void authenticate(){
        this.send("HELO\n");
        this.receive(); // ds-server sends 'OK'
        this.send("AUTH lewis\n");
        this.receive(); // ds-server sends 'OK'
    }

    public void sendReceiveGetsAll(){

        this.send("GETS All\n");

        String dataMessage = this.receiveString();

        this.message.parseDataMessage(dataMessage);

        int nRecs = this.message.getNRecs();

        this.send("OK\n");
        
        String lastServerType = "";
        String temporaryString;
        String currentServerType = "";
        int serverCount = 1;

        for (int i = 0; i < nRecs; i++){

            temporaryString = this.receiveString();
            this.message.parseServerDetails(temporaryString);
            currentServerType = this.message.getServerType();

            if(!currentServerType.equals(lastServerType)){
                
                if(i != 0){
                    this.server.addServerType(lastServerType, serverCount);
                    serverCount = 1;
                }

            } else {
                serverCount++;
            }

            lastServerType = currentServerType;
        }

        // Add the last server type
        this.server.addServerType(lastServerType, serverCount);

        // After recieving Server details records
        this.send("OK\n");
        this.receive(); // ds-server sends '.'
    }

    public void receiveGets(int nRecs, String tempString){
        
        // After receiving DATA and Sending Ok to preceed
        // Save first server details
        tempString = this.receiveString();
        this.message.parseServerDetails(tempString);

        // Temporary, skip rest of server messages
        for (int i = 1; i < nRecs; i++) {
            this.receive();
        }

        // After recieving Server details records
        this.send("OK\n");
        this.receive(); // ds-server sends '.'

        tempString = this.message.createSchd();

        this.send(tempString);
    }

    public void receiveGetsEnhanced(int nRecs, String tempString){
        
        // After receiving DATA and Sending Ok to preceed

        // The first server in the list to set the lowestQueue variable
        tempString = this.receiveString();
        this.message.parseServerDetails(tempString);

        String currentServerType = this.message.getServerType();
        int currentServerID = this.message.getServerID();
        int currentQueueLength = this.server.getQueueLength(currentServerType, currentServerID);

        if(currentQueueLength == 0){

            // Skip rest of server details messages
            for (int i = 1; i < nRecs; i++){
                this.receive();
            }

            this.send("OK\n");
            this.receive(); // Receive .

            tempString = this.message.createSchd();
            this.send(tempString);
            return;
        }

        String lowestServerType = currentServerType;
        int lowestServerID = currentServerID;
        int lowestQueueLength = currentQueueLength;
        String lowestServerDetails = tempString;

        // Iterate list searching for a queue length of 0.
        // If there is no queue length equal to 0, use the server
        // with the shortest queue

        for (int i = 1; i < nRecs; i++){

            tempString = this.receiveString();
            this.message.parseServerDetails(tempString);

            currentServerType = this.message.getServerType();
            currentServerID = this.message.getServerID();
            currentQueueLength = this.server.getQueueLength(currentServerType, currentServerID);

            if (currentQueueLength < lowestQueueLength){
                lowestServerType = currentServerType;
                lowestServerID = currentServerID;
                lowestQueueLength = currentQueueLength;
                lowestServerDetails = tempString;
            }
        } // End of server details messages

        this.message.parseServerDetails(lowestServerDetails);
        this.server.increaseQueueLength(lowestServerType, lowestServerID);
        
        this.send("OK\n");
        this.receive(); // Receive .

        tempString = this.message.createSchd();
        this.send(tempString);
    }
    public static void main(String[] args) {

        // MyClient constructor creates a socket connection with input and output
        MyClient myClient = new MyClient();

        myClient.authenticate();

        myClient.send("REDY\n");
        String loopMessage = myClient.receiveString(); // ds-server sends first job

        myClient.sendReceiveGetsAll();

        // while loop conditions
        boolean loopMessageIsJOBN = loopMessage.substring(0, 4).equals("JOBN");
        boolean loopMessageIsJCPL = loopMessage.substring(0, 4).equals("JCPL");

        // Helper classes
        Message myMessage = myClient.getMessage();
        Server myServer = myClient.getServer();

        String tempString;
        int nRecs;
        int queueLength;

        int jcplServerID;
        String jcplServerType = "";

        while (loopMessageIsJOBN || loopMessageIsJCPL) {

            if (loopMessageIsJOBN) {

                myMessage.parseJOBN(loopMessage);

                // Create gets command with larger than necersary cores?
                tempString = myMessage.createGetsAvail();

                myClient.send(tempString);

                // ds-server sends 'DATA nRecs recLen'
                tempString = myClient.receiveString();

                myMessage.parseDataMessage(tempString);
                nRecs = myMessage.getNRecs();

                // Send Ok to preceed
                myClient.send("OK\n");

                if (nRecs > 0) {

                    // Could also try queueing jobs if no servers are available
                    myClient.receiveGets(nRecs, tempString);

                } else {

                    myClient.receive(); // Receive . after sending OK
                    
                    tempString = myMessage.createGetsCapable();
                    myClient.send(tempString);

                    // ds-server sends 'DATA nRecs recLen'
                    tempString = myClient.receiveString();
                    myMessage.parseDataMessage(tempString);
                    nRecs = myMessage.getNRecs();

                    // Send Ok to preceed
                    myClient.send("OK\n");

                    myClient.receiveGetsEnhanced(nRecs, tempString);
                }

                // Confirmation of job
                myClient.receive();

            } else {

                myMessage.parseJCPLMessage(loopMessage);
                jcplServerID = myMessage.getServerID();
                jcplServerType = myMessage.getServerType();

                queueLength = myServer.getQueueLength(jcplServerType, jcplServerID);

                if (queueLength > 0){
                    myServer.decreaseQueueLength(jcplServerType, jcplServerID);
                }
            }

            // Send REDY to receive next message
            myClient.send("REDY\n");

            loopMessage = myClient.receiveString(); // Server may send JOBN or JCPL

            // update while conditions for next iteration
            loopMessageIsJOBN = loopMessage.substring(0, 4).equals("JOBN");
            loopMessageIsJCPL = loopMessage.substring(0, 4).equals("JCPL");

        }

        myClient.send("QUIT\n");
        myClient.receive();
        myClient.endConnection();
    }
}