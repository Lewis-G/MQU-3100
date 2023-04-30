package MyClient;

import java.io.*;
import java.net.*;

public class FirstCapable {

    Socket socket;

    public static void main(String[] args) {
        try {

            // Create a socket with input and output
            Socket s = new Socket("localhost", 50000);
            DataOutputStream dout = new DataOutputStream(s.getOutputStream());
            BufferedReader in = new BufferedReader(new InputStreamReader(s.getInputStream()));

            // Authenticate
            dout.write(("HELO\n").getBytes());
            in.readLine(); // ds-server sends 'OK'
            dout.write(("AUTH lewis\n").getBytes());
            in.readLine(); // ds-server sends 'OK'

            dout.write(("REDY\n").getBytes());
            String loopMessage = (String) in.readLine(); // ds-server sends first job
            String toServer;

            // while loop conditions
            boolean loopMessageIsJOBN = loopMessage.substring(0, 4).equals("JOBN");
            boolean loopMessageIsJCPL = loopMessage.substring(0, 4).equals("JCPL");

            Jobn jobn = new Jobn();
            GetsMessage getsMessage = new GetsMessage();

            while (loopMessageIsJOBN || loopMessageIsJCPL) {

                if (loopMessageIsJOBN) {

                    jobn.ParseJOBN(loopMessage);

                    toServer = "GETS Capable " + jobn.getJobCores() + " " + jobn.getJobMemory() + " "
                            + jobn.getJobDisk() + "\n";
                    dout.write((toServer).getBytes());

                    // ds-server sends 'DATA nRecs recLen'
                    // Isolate nRecs by parsing this message with the GetsMessage object
                    getsMessage.ParseDataMessage(in.readLine());

                    // Send Ok to preceed,
                    dout.write(("OK\n").getBytes());

                    if (getsMessage.getNRecs() > 0) {

                        // Get the server details from the first message
                        
                        getsMessage.ParseServerDetails(in.readLine());

                        // skip rest of GETS messages
                        for (int i = 1; i < getsMessage.getNRecs(); i++) {
                            in.readLine();
                        }

                        // After recieving GETS records
                        dout.write(("OK\n").getBytes());
                        in.readLine(); // ds-server sends '.'

                        toServer = "SCHD " + jobn.getJobID() + " " + getsMessage.getServerType() + " "
                                + getsMessage.getServerID() + "\n";
                        dout.write((toServer).getBytes());

                        in.readLine(); // ds-server confirms that the job is being scheduled

                    } else {
                        in.readLine(); // ds-server sends .

                    } // End of nRecs if/else statements

                } // End of loopMessageIsJOBN

                // Send REDY to receive next message
                dout.write(("REDY\n").getBytes());
                loopMessage = (String) in.readLine(); //Server may send JOBN or JCPL

                // update while conditions for next iteration
                loopMessageIsJOBN = loopMessage.substring(0, 4).equals("JOBN");
                loopMessageIsJCPL = loopMessage.substring(0, 4).equals("JCPL");

            } // End of while loop

            // After NONE is sent, end communication and close the connection
            dout.write(("QUIT\n").getBytes());
            in.readLine();
            dout.flush();
            dout.close();
            s.close();

        } catch (Exception e) {
            System.out.println(e);
        }
    }
}