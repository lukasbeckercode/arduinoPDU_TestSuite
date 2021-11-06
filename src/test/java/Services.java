import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;

public class Services {
    final InputStream in;
    final PrintWriter out;
    Services(InputStream in, PrintWriter out)
    {
        this.in = in;
        this.out = out ;
    }
    void sendCommand(String cmd){
        out.print(cmd);
        out.flush();
    }
    String readResponse(int le){
        StringBuilder bob = new StringBuilder();
        for(int i = 0; i<le;i++){
            try {
                bob.append((char)in.read());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return bob.toString().strip();
    }


}
