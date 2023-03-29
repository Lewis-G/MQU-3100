import java.io.*;
import java.net.*;

public class Week4Client {

    Socket socket;
    public static void main(String[] args) {
        try {

            Socket s = new Socket("localhost", 50000);
            
            DataOutputStream dout = new DataOutputStream(s.getOutputStream());

            dout.write(("HELO\n").getBytes());

            BufferedReader in = new BufferedReader(new InputStreamReader(s.getInputStream()));

            String str = (String) in.readLine();
            System.out.println("message=" + str);

            dout.write(("AUTH lewis\n").getBytes());

            String str2 = (String) in.readLine();
            System.out.println("message=" + str2);

            dout.write(("REDY\n").getBytes());

            String str3 = (String) in.readLine();
            System.out.println("message=" + str3);

            dout.write(("GETS Capable 2 300 500\n").getBytes());

            String str4 = (String) in.readLine();
            System.out.println("message=" + str4);

            dout.write(("OK\n").getBytes());
            String str5= (String) in.readLine();
            System.out.println("message=" + str5);

            dout.write(("OK\n").getBytes());
            String str6= (String) in.readLine();
            System.out.println("message=" + str6);

            dout.write(("OK\n").getBytes());
            String str7= (String) in.readLine();
            System.out.println("message=" + str7);

            dout.write(("SCHD 0 small 0\n").getBytes());

            String str8= (String) in.readLine();
            System.out.println("message=" + str8);

            dout.write(("QUIT\n").getBytes());
            String str9= (String) in.readLine();
            System.out.println("message=" + str9);

            dout.flush();
            dout.close();
            s.close();

        } catch (Exception e) {
            System.out.println(e);
        }
    }

}