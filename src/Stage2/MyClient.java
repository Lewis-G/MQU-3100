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

    public static void main(String[] args) {

        // MyClient constructor creates a socket connection with input and output
        MyClient myClient = new MyClient();

        // Authenticate
        myClient.Send("HELO\n");
        myClient.Receive(); // ds-server sends 'OK'
        myClient.Send("AUTH lewis\n");
        myClient.Receive(); // ds-server sends 'OK'

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

                tempString = myMessage.createGetsAvail();

                myClient.Send(tempString);

                // ds-server sends 'DATA nRecs recLen'
                tempString = myClient.ReceiveString();

                myMessage.ParseDataMessage(tempString);
                nRecs = myMessage.getNRecs();

                if (nRecs > 0) {

                    // Send Ok to preceed
                    myClient.Send("OK\n");

                    // Save first server details
                    tempString = myClient.ReceiveString();
                    myMessage.ParseServerDetails(tempString);

                    // Temporary, skip rest of server messages
                    for (int i = 1; i < nRecs; i++) {
                        myClient.Receive();
                    }

                    // After recieving GETS records
                    myClient.Send("OK\n");
                    myClient.Receive(); // ds-server sends '.'

                    tempString = myMessage.createSchd();

                    myClient.Send(tempString);

                }

                // Either confirms schd or receives . if no servers avail
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