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
            String str = (String) in.readLine(); // Is it neccersary to check for 'OK'?
            System.out.println("message=" + str);

            dout.write(("AUTH lewis\n").getBytes());
            String str2 = (String) in.readLine();
            System.out.println("message=" + str2);

            dout.write(("REDY\n").getBytes());
            String jobN = (String) in.readLine(); // Client sends first job
            System.out.println("message=" + jobN);

            dout.write(("GETS All\n").getBytes());
            String dataN = (String) in.readLine();
            System.out.println("message=" + dataN);

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

            //After recieving GETS records
            dout.write(("OK\n").getBytes());
            String noMoreInfo = (String) in.readLine();
            System.out.println("message=" + noMoreInfo);
            dout.write(("REDY\n").getBytes());

            String loopMessage = (String) in.readLine();
            System.out.println("message=" + loopMessage);
            int counter = 0;

            while(loopMessage.substring(0, 4).equals("JOBN")){

                int currentJobID = ParseJobN(loopMessage);

                String schdMessage = "SCHD " + currentJobID + " " + biggestServerType + " " + counter +"\n";
                dout.write((schdMessage).getBytes());

                loopMessage = (String) in.readLine();   //server sends message saying that the job is beign scheduled

                dout.write(("REDY\n").getBytes());
                loopMessage = (String) in.readLine();   //If there a more jobs, then JOBN is sent

                counter++;
                if(counter == numberOfLargestServers){
                    counter = 0;
                }
            }

            dout.write(("QUIT\n").getBytes());
            String str9 = (String) in.readLine();
            System.out.println("message=" + str9);

            dout.flush();
            dout.close();
            s.close();

        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public static int ParseJobN(String inputJobN){

        String[] InputJobNSplit = inputJobN.split(" ", 7);
        String jobID = InputJobNSplit[2];
        //String coresRequirement = jobNSplit[4];
        //String memoryRequirement = jobNSplit[5];
        //String diskRequirement = jobNSplit[6];

        return Integer.parseInt(jobID);
    }

}