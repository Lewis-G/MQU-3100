import java.io.*;
import java.net.*;

public class MyClient {

    Socket socket;

    public static void main(String[] args) {
        try {

            // Create a socket with input and output
            Socket s = new Socket("localhost", 50000);
            DataOutputStream dout = new DataOutputStream(s.getOutputStream());
            BufferedReader in = new BufferedReader(new InputStreamReader(s.getInputStream()));

            String tempInputString = "";

            // Authenticate
            dout.write(("HELO\n").getBytes());
            tempInputString = (String) in.readLine();
            //System.out.println("server = " + tempInputString);

            dout.write(("AUTH lewis\n").getBytes());
            tempInputString = (String) in.readLine();
            //System.out.println("server = " + tempInputString);

            dout.write(("REDY\n").getBytes());
            tempInputString = (String) in.readLine(); // Client sends first job
            //System.out.println("server = " + tempInputString);

            dout.write(("GETS All\n").getBytes());
            String dataN = (String) in.readLine();
            //System.out.println("server = " + dataN);

            // Sort through data message
            String[] dataSplit = dataN.split(" ", 3);
            int numberOfRecords = Integer.valueOf(dataSplit[1]);

            // Respond with OK to recieve GETS records
            dout.write(("OK\n").getBytes());

            String biggestServerType = "";
            String tempRecord = "";

            int numberOfLargestServers = 1;
            int tempNumCores = 0;
            int greatestNumCores = 0;

            for (int i = 0; i < numberOfRecords; i++) {

                tempRecord = (String) in.readLine();
                String[] tempRecordSplit = tempRecord.split(" ", 8);

                tempNumCores = Integer.parseInt(tempRecordSplit[4]);

                if (tempNumCores > greatestNumCores) {

                    biggestServerType = tempRecordSplit[0];
                    numberOfLargestServers = 1;
                    greatestNumCores = tempNumCores;

                } else {
                    numberOfLargestServers++;
                }
            }

            // After recieving GETS records
            dout.write(("OK\n").getBytes());
            tempInputString = (String) in.readLine();
            //System.out.println("server = " + tempInputString);
            dout.write(("REDY\n").getBytes());

            String loopMessage = (String) in.readLine();
            String schdMessage = "";
            //System.out.println("server = " + loopMessage);
            int counter = 0;

            boolean loopMessageIsJOBN = loopMessage.substring(0, 4).equals("JOBN");
            boolean loopMessageIsJCPL = loopMessage.substring(0, 4).equals("JCPL");

            while (loopMessageIsJOBN || loopMessageIsJCPL) {

                //System.out.println("(Loop) Server message is " + loopMessage + "\n");

                if (loopMessageIsJCPL) {

                    dout.write(("REDY\n").getBytes());
                    loopMessage = (String) in.readLine();

                } else {

                    int currentJobID = ParseJOBN(loopMessage);

                    schdMessage = "SCHD " + currentJobID + " " + biggestServerType + " " + counter + "\n";
                    //System.out.println("(Loop) Client message is " + schdMessage);
                    dout.write((schdMessage).getBytes());

                    loopMessage = (String) in.readLine(); // server sends message saying that the job is beign scheduled

                    dout.write(("REDY\n").getBytes());
                    loopMessage = (String) in.readLine(); // If there a more jobs, then JOBN is sent

                    counter++;
                    if (counter == numberOfLargestServers) {
                        counter = 0;
                        // loop of boolean values, after schd bool = false, after jcpl bool = true
                    }

                }//end of else block

            //update while conditions for next iteration
            loopMessageIsJOBN = loopMessage.substring(0, 4).equals("JOBN");
            loopMessageIsJCPL = loopMessage.substring(0, 4).equals("JCPL");

            } // end of while loop

            //System.out.println("(After last loop) Server message is " + loopMessage);

            dout.write(("QUIT\n").getBytes());
            tempInputString = (String) in.readLine();
            //System.out.println("server = " + tempInputString);

            dout.flush();
            dout.close();
            s.close();

        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public static int ParseJOBN(String inputJOBN) {

        String[] InputJOBNSplit = inputJOBN.split(" ", 7);
        String jobID = InputJOBNSplit[2];
        // String coresRequirement = jobNSplit[4];
        // String memoryRequirement = jobNSplit[5];
        // String diskRequirement = jobNSplit[6];

        return Integer.parseInt(jobID);
    }

    public static String ParseJCPL(String inputJCPL) {

        // String[] inputJCPLSplit = inputJCPL.split(" ", 5);

        // String jobID = inputJCPLSplit[2];
        // String serverType = inputJCPLSplit[3];
        // String serverID = inputJCPLSplit[4];

        return "";
    }

}