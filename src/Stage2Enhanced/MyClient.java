package Stage2Enhanced;

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

    public void EndConnection(){
        try {
            out.flush();
            out.close();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void Send(String message) {
        try {
            out.write((message).getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void Receive() {
        try {
            in.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String ReceiveString() {
        try {
            return (String) in.readLine();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void Authenticate(){
        this.Send("HELO\n");
        this.Receive(); // ds-server sends 'OK'
        this.Send("AUTH lewis\n");
        this.Receive(); // ds-server sends 'OK'
    }

    public void ReceiveGets(int nRecs, Message message, String tempString){
        
        // After receiving DATA and Sending Ok to preceed
        // Save first server details
        tempString = this.ReceiveString();
        message.ParseServerDetails(tempString);

        // Temporary, skip rest of server messages
        for (int i = 1; i < nRecs; i++) {
            this.Receive();
        }

        // After recieving Server details records
        this.Send("OK\n");
        this.Receive(); // ds-server sends '.'

        tempString = message.createSchd();

        this.Send(tempString);
    }

    public static void main(String[] args) {

        // MyClient constructor creates a socket connection with input and output
        MyClient myClient = new MyClient();

        myClient.Authenticate();

        myClient.Send("REDY\n");
        String loopMessage = myClient.ReceiveString(); // ds-server sends first job

        // while loop conditions
        boolean loopMessageIsJOBN = loopMessage.substring(0, 4).equals("JOBN");
        boolean loopMessageIsJCPL = loopMessage.substring(0, 4).equals("JCPL");

        // Helper class
        Message myMessage = new Message();
        String tempString;
        int nRecs;

        while (loopMessageIsJOBN || loopMessageIsJCPL) {

            if (loopMessageIsJOBN) {

                myMessage.ParseJOBN(loopMessage);

                // Create gets command with larger than necersary cores?
                tempString = myMessage.createGetsAvail();

                myClient.Send(tempString);

                // ds-server sends 'DATA nRecs recLen'
                tempString = myClient.ReceiveString();

                myMessage.ParseDataMessage(tempString);
                nRecs = myMessage.getNRecs();

                // Send Ok to preceed
                myClient.Send("OK\n");

                if (nRecs > 0) {

                    // Could also try queueing jobs if no servers are available
                    myClient.ReceiveGets(nRecs, myMessage, tempString);

                } else {

                    myClient.Receive(); // Receive . after sending OK
                    
                    tempString = myMessage.createGetsCapable();
                    myClient.Send(tempString);

                    // ds-server sends 'DATA nRecs recLen'
                    tempString = myClient.ReceiveString();
                    myMessage.ParseDataMessage(tempString);
                    nRecs = myMessage.getNRecs();

                    // Send Ok to preceed
                    myClient.Send("OK\n");

                    myClient.ReceiveGets(nRecs, myMessage, tempString);
                }

                // Confirmation of job
                myClient.Receive();

            }   //End of IfJOBN

            // Send REDY to receive next message
            myClient.Send("REDY\n");

            loopMessage = myClient.ReceiveString(); // Server may send JOBN or JCPL

            // update while conditions for next iteration
            loopMessageIsJOBN = loopMessage.substring(0, 4).equals("JOBN");
            loopMessageIsJCPL = loopMessage.substring(0, 4).equals("JCPL");

        }

        myClient.Send("QUIT\n");
        myClient.Receive();
        myClient.EndConnection();
    }
}