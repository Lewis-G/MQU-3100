package MyClient;

public class Jobn {
    
    int jobID;
    int jobCores;
    int jobMemory;
    int jobDisk;

    String[] messageSplit;

    Jobn(){
        messageSplit = new String[7];
    }

    public void ParseJOBN(String message){

        messageSplit = message.split(" ", 7);

        jobID = Integer.parseInt(messageSplit[2]);
        jobCores = Integer.parseInt(messageSplit[2]);
        jobMemory = Integer.parseInt(messageSplit[2]);
        jobDisk = Integer.parseInt(messageSplit[2]);

        return;
    }

    public int getJobID(){
        return jobID;
    }

    public int getJobCores(){
        return jobCores;
    }

    public int getJobMemory(){
        return jobMemory;
    }

    public int getJobDisk(){
        return jobDisk;
    }
}
