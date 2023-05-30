package Stage2;

import java.io.*;
import java.net.*;

public class MyClient {

    Socket socket;
    DataOutputStream out;
    BufferedReader in;

    MyClient() {
        try {
            socket = new Socket("localhost", 50000);
            out = new DataOutputStream(socket.getOutputStream());
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

        } catch (IOException e) {
            e.printStackTrace();
        }
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

    public void send(String message) {
        try {
            out.write((message).getBytes());
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

    public void receiveGets(int nRecs, Message message, String tempString){
        
        // After receiving DATA and Sending Ok to preceed
        // Save first server details
        tempString = this.receiveString();
        message.parseServerDetails(tempString);

        // Temporary, skip rest of server messages
        for (int i = 1; i < nRecs; i++) {
            this.receive();
        }

        // After recieving Server details records
        this.send("OK\n");
        this.receive(); // ds-server sends '.'

        tempString = message.createSchd();

        this.send(tempString);
    }

    public static void main(String[] args) {

        // MyClient constructor creates a socket connection with input and output
        MyClient myClient = new MyClient();

        myClient.authenticate();

        myClient.send("REDY\n");
        String loopMessage = myClient.receiveString(); // ds-server sends first job

        // while loop conditions
        boolean loopMessageIsJOBN = loopMessage.substring(0, 4).equals("JOBN");
        boolean loopMessageIsJCPL = loopMessage.substring(0, 4).equals("JCPL");

        // Helper class
        Message myMessage = new Message();
        String tempString;
        int nRecs;

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
                    myClient.receiveGets(nRecs, myMessage, tempString);

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

                    myClient.receiveGets(nRecs, myMessage, tempString);
                }

                // Confirmation of job
                myClient.receive();

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