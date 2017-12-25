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
    public String discName = "C:";
    public static String line = "";

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

        String str = new String();
        str = isInternetReachable() ? "Підключено." : "Відключено.";

        String result[] = getFreeSpaceCMD();
        textArea.setText(" Загальна к-ть байтів: " + result[1] + "\n К-ть вільних байтів: " + result[0]
                + "\n З'єднання: " + str);
        textArea.setEditable(false);
        frmGui.getContentPane().add(textArea);
        toFile();
    }

    public boolean isFileEmpty(String path) throws IOException {
        File file = new File(path);
        BufferedReader br = new BufferedReader(new FileReader(file));
        StringBuffer sf = new StringBuffer();

        String line = "";
        while ((line = br.readLine()) != null)
        {
            sf.append(line);
        }
        sf.toString();
        if(sf.equals("")) { return true; }
        else return false;

    }

    //varname[0] - free bytes
    //varname[1] - total bytes
    //varname[2] - avail free bytes
    public String[] getFreeSpaceCMD()
    {
        String command = "fsutil volume diskfree " + discName;
        String output = executeCommand(command);
        String memory = output.replaceAll("[^0-9\n]", "");
        String[] result = memory.split("\n");

        return result;
    }

    //Do changes in log.txt file
    //Input reachability connection
    //And changes in free memory
    public void toFile()
    {
        String path = "C:\\Users\\User\\SystemAnalizer\\log.txt";
        try(FileWriter writer = new FileWriter(path, true))
        {
            SimpleDateFormat dateFormat = null;
            dateFormat = new SimpleDateFormat("dd/MM/YYYY hh:mm:ss");
            String textData = dateFormat.format(new Date());

            String str = "";
            str = isInternetReachable() ? "Підключено." : "Відключено.";
            String text_to_file = null;

            String result[] = getFreeSpaceCMD();
            if(isFileEmpty(path))
            {
                text_to_file = textData + " З'єднання: " + str + "\n";
                writer.append(text_to_file);
                text_to_file = "";
                text_to_file = "К-ть вільних байтів: " + result[0] + "\n";
                writer.append(text_to_file);
            }
            else {
                try {
                    File file = new File(path);
                    BufferedReader fin = new BufferedReader(new FileReader(file));

                    StringBuffer fstr = new StringBuffer();
                    while ((line = fin.readLine()) != null)
                    {
                        fstr.append(line);
                    }
                    String output = fstr.toString();
                    int lastIndex = output.lastIndexOf(":");
                    String partString = output.substring(lastIndex + 2);
                    //String getLastString = splitString[splitString.length];
                    //System.out.println("splitString[splitString.length: " + splitString[splitString.length]);
                    //System.out.println("getLastString: " + getLastString);

                    text_to_file = textData + " З'єднання: " + str + "\n";
                    writer.append(text_to_file);

                    //System.out.println("Кусок дерьма: " + partString);
                    text_to_file = "Вільне місце змінилось з " + partString + " до: " + result[0] + "\n";
                    writer.append(text_to_file);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                } catch (NullPointerException e){
                    e.printStackTrace();
                }

            }

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
            urlConnect.getContent();
        } catch (UnknownHostException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
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
