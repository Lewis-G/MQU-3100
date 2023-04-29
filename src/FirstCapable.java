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
            in.readLine();   //ds-server sends 'OK'
            dout.write(("AUTH lewis\n").getBytes());
            in.readLine();   //ds-server sends 'OK'

            dout.write(("REDY\n").getBytes());
            String loopMessage = (String) in.readLine();  //ds-server sends first job
            
            //while loop conditions
            boolean loopMessageIsJOBN = loopMessage.substring(0, 4).equals("JOBN");
            boolean loopMessageIsJCPL = loopMessage.substring(0, 4).equals("JCPL");

            String toServer;
            int[] jobDetails = new int[4];

            String dataN;
            String[] dataNSplit = new String[3];
            int numberOfRecords;

            String firstServer;
            String[] serverDetails = new String[3];

            while (loopMessageIsJOBN || loopMessageIsJCPL) {

                System.out.println();
                System.out.println("loop Message is:" + loopMessage);

                if(loopMessageIsJOBN){

                    jobDetails = ParseJOBN(loopMessage);

                    toServer = "GETS Capable " + jobDetails[1] + " " + jobDetails[2] + " " + jobDetails[3] + "\n";
                    System.out.println("Gets Message is:" + toServer);
                    dout.write((toServer).getBytes());

                    dataN = (String) in.readLine();  //ds-server sends 'DATA nRecs recLen'

                    System.out.println("DataN message is: " + dataN);

                    //Split the DATA message, and isolate nRecs
                    dataNSplit = dataN.split(" ", 3);
                    numberOfRecords = Integer.valueOf(dataNSplit[1]);

                    dout.write(("OK\n").getBytes());
                    firstServer = (String) in.readLine();

                    //skip rest of GETS messages
                    for(int i = 1; i < numberOfRecords; i++){
                        in.readLine();
                    }

                    // After recieving GETS records
                    dout.write(("OK\n").getBytes());
                    in.readLine();    //ds-server sends '.'
                    System.out.println("nRecs is:" + numberOfRecords);

                    if(numberOfRecords > 0){

                        serverDetails = firstServer.split(" ", 3);

                        toServer = "SCHD " + jobDetails[0] + " " + serverDetails[0] + " " + serverDetails[1] + "\n";
                        System.out.println("Schd Message is:" + toServer);
                        dout.write((toServer).getBytes());

                        loopMessage = (String) in.readLine(); //ds-server confirms that the job is being scheduled

                        dout.write(("REDY\n").getBytes());
                        loopMessage = (String) in.readLine(); //If there a more jobs, then JOBN is sent

                    } else {
                        loopMessage = "QUIT";
                    }

                } else if(loopMessageIsJCPL) {

                    //Job is completed
                    dout.write(("REDY\n").getBytes());
                    loopMessage = (String) in.readLine();
                }

                //update while conditions for next iteration
                loopMessageIsJOBN = loopMessage.substring(0, 4).equals("JOBN");
                loopMessageIsJCPL = loopMessage.substring(0, 4).equals("JCPL");

            }// End of while loop

            //After NONE is sent, end communication and close the connection
            dout.write(("QUIT\n").getBytes());
            in.readLine();
            dout.flush();
            dout.close();
            s.close();

        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public static int[] ParseJOBN(String jobn) {

        String[] jobnSplit = jobn.split(" ", 7);
        int[] result = new int[4];

        result[0] = Integer.parseInt(jobnSplit[2]); //jobN
        result[1] = Integer.parseInt(jobnSplit[4]); //cores requirement
        result[2] = Integer.parseInt(jobnSplit[5]); //memory requirement
        result[3] = Integer.parseInt(jobnSplit[6]); //disk requirement

        return result;
    }
}