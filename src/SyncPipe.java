import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class SyncPipe {
    private JFrame frmGui;
    //public JTextArea jTextArea;
    public String discName = "C:";

    public static void main(String[] args) {

        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    JFrame.setDefaultLookAndFeelDecorated(true);
                    SyncPipe window = new SyncPipe();
                    window.frmGui.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public SyncPipe()
    {
        initialize();
    }

    private void initialize()
    {

        frmGui = new JFrame("System analizer");
        frmGui.setBounds(100, 100, 240, 150);
        frmGui.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frmGui.setBackground(Color.WHITE);
        frmGui.getContentPane().setLayout(null);

        JLabel label = new JLabel("SYSTEM DATA: ");
        label.setBounds(11,11, 250,14);
        label.setBackground(Color.WHITE);
        label.setFont(new Font("Serif", Font.BOLD, 15));
        frmGui.getContentPane().add(label);

        JTextArea textArea = new JTextArea();
        textArea.setBounds(2,30, 250, 48);
        String command = "fsutil volume diskfree " + discName;
        String output = executeCommand(command);
        String memory = output.replaceAll("[^0-9\n]", "");
        String[] result = memory.split("\n");
        String str = new String();
        if(isInternetReachable())
        {
            str = "Підключено.";
        }
        else { str = "Відключено."; }
        textArea.setText(" Загальна к-ть байтів: " + result[1] + "\n К-ть вільних байтів: " + result[0]
                + "\n З'єднання: " + str);
        textArea.setEditable(false);
        frmGui.getContentPane().add(textArea);
        toFile();
    }

    public void toFile()
    {
        try(FileWriter writer = new FileWriter("C:\\Users\\User\\SystemAnalizer\\log.txt", false))
        {
            SimpleDateFormat dateFormat = null;
            dateFormat = new SimpleDateFormat("dd/MM/YYYY hh:mm:ss");
            String textData = dateFormat.format(new Date());

            String str = "";
            str = isInternetReachable() ? "Підключено." : "Відключено.";

            String text_to_file = textData + " З'єднання: " + str;
            writer.write(text_to_file);
            text_to_file = "";

            String command = "fsutil volume diskfree " + discName;
            String output = executeCommand(command);
            String memory = output.replaceAll("[^0-9\n]", "");
            String[] result = memory.split("\n");

            text_to_file = "\nК-ть вільних байтів: " + result[0];
            writer.write(text_to_file);

            writer.flush();
        }
        catch(IOException ex){

            System.out.println(ex.getMessage());
        }
    }

    //checks for connection to the internet through dummy request
    public static boolean isInternetReachable()
    {
        try {
            //make a URL to a known source
            URL url = new URL("http://www.google.com");

            //open a connection to that source
            HttpURLConnection urlConnect = (HttpURLConnection)url.openConnection();
            //System.out.println("HttpURLConnection: " + urlConnect.getContent());
        } catch (UnknownHostException e) {
            // TODO Auto-generated catch block
            //e.printStackTrace();
            return false;
        }
        catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return false;
        }
        return true;
    }

    private static String executeCommand(String command) {

        StringBuffer output = new StringBuffer();

        Process p;
        try {
            p = Runtime.getRuntime().exec(command);
            p.waitFor();
            BufferedReader reader =
                    new BufferedReader(new InputStreamReader(p.getInputStream()));

            String line = "";
            while ((line = reader.readLine())!= null) {
                output.append(line + "\n");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return output.toString();
    }
}
