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

            // Authenticate
            dout.write(("HELO\n").getBytes());
            in.readLine();   //ds-server sends 'OK'
            dout.write(("AUTH lewis\n").getBytes());
            in.readLine();   //ds-server sends 'OK'

            dout.write(("REDY\n").getBytes());
            String firstJOBN = (String) in.readLine();  //ds-server sends first job, stored in variable for later use

            dout.write(("GETS All\n").getBytes());
            String dataN = (String) in.readLine();  //ds-server sends 'DATA nRecs recLen'
            //nRecs indicates the number of subsequent messages, used in the for loop condition

            //Split the DATA message, and isolate nRecs
            String[] dataSplit = dataN.split(" ", 3);
            int numberOfRecords = Integer.valueOf(dataSplit[1]);

            // Respond with OK to recieve GETS records
            dout.write(("OK\n").getBytes());

            //These variables are used in the for loop
            String tempMessage = "";    //Stores the message from ds-server, copied over by each iteration
            String[] tempMessageSplit;  //Each server message is split into an array

            int tempNumCores = 0;   //Stores the number of cores, taken from ds-server message
            int greatestNumCores = -1;  //Stores the greatest number of cores at any point of the loop

            String tempServerType;   //Stores server type, taken from ds-server message
            String biggestServerType = "";  //Stores the name of first known largest server at any point of the loop

            int numberOfLargestServers = 1; //Stores the number of servers that belong to the largest server type

            for (int i = 0; i < numberOfRecords; i++) {

                tempMessage = (String) in.readLine();
                tempMessageSplit = tempMessage.split(" ", 8);

                tempServerType = tempMessageSplit[0];
                tempNumCores = Integer.parseInt(tempMessageSplit[4]);

                if (tempNumCores > greatestNumCores) {

                    //A larger server type has been identifed
                    biggestServerType = tempServerType;
                    numberOfLargestServers = 1;
                    greatestNumCores = tempNumCores;

                } else if (tempNumCores == greatestNumCores && biggestServerType.equals(tempServerType)) {
                    
                    //A subsequent server of the largest type has been identified
                    numberOfLargestServers++;
                }
            }

            // After recieving GETS records
            dout.write(("OK\n").getBytes());
            in.readLine();    //ds-server sends '.'

            String schdMessage = "";    //Stores each SCHD String before sending the command
            int counter = 0;    //Stores the current server in the queue
            String loopMessage = firstJOBN; //Stores JOBN, JCPL, and NONE messages sent by ds-server
            
            //while loop conditions
            boolean loopMessageIsJOBN = loopMessage.substring(0, 4).equals("JOBN");
            boolean loopMessageIsJCPL = loopMessage.substring(0, 4).equals("JCPL");

            while (loopMessageIsJOBN || loopMessageIsJCPL) {

                if (loopMessageIsJCPL) {

                    //Job is completed
                    dout.write(("REDY\n").getBytes());
                    loopMessage = (String) in.readLine();

                } else {

                    //Calls a static function, returns jobID
                    int currentJobID = ParseJOBN(loopMessage);

                    schdMessage = "SCHD " + currentJobID + " " + biggestServerType + " " + counter + "\n";
                    dout.write((schdMessage).getBytes());

                    loopMessage = (String) in.readLine(); //ds-server confirms that the job is beign scheduled

                    dout.write(("REDY\n").getBytes());
                    loopMessage = (String) in.readLine(); //If there a more jobs, then JOBN is sent

                    counter++;
                    //ensures that SCHD is sent to non-existant server
                    if (counter == numberOfLargestServers) {
                        counter = 0;
                    }

                }//end of else block

            //update while conditions for next iteration
            loopMessageIsJOBN = loopMessage.substring(0, 4).equals("JOBN");
            loopMessageIsJCPL = loopMessage.substring(0, 4).equals("JCPL");

            } // end of while loop

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

    public static int ParseJOBN(String inputJOBN) {

        String[] InputJOBNSplit = inputJOBN.split(" ", 7);
        String jobID = InputJOBNSplit[2];
        // String coresRequirement = jobNSplit[4];
        // String memoryRequirement = jobNSplit[5];
        // String diskRequirement = jobNSplit[6];

        return Integer.parseInt(jobID);
    }
}