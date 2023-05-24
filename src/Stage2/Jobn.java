package Stage2;

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
        jobCores = Integer.parseInt(messageSplit[4]);
        jobMemory = Integer.parseInt(messageSplit[5]);
        jobDisk = Integer.parseInt(messageSplit[6]);

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
