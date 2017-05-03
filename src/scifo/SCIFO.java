/*
 *
 */
package scifo;


import java.net.*;
import java.io.*;
import edu.cmu.sphinx.api.Configuration;
import edu.cmu.sphinx.api.LiveSpeechRecognizer;
import edu.cmu.sphinx.api.SpeechResult;
import com.sun.speech.freetts.Voice;
import com.sun.speech.freetts.VoiceManager;
/**
 *
 * @author Pedro Frau
 */
public class SCIFO {
    
    private static final String VOICENAME_kevin16 = "kevin16";
    private String text; // string to speech
    
    public SCIFO(String text) {
        this.text = text;
    }

    public void speak() {
        Voice voice;
        VoiceManager voiceManager = VoiceManager.getInstance();
        voice = voiceManager.getVoice(VOICENAME_kevin16);
        voice.allocate();
        voice.speak(text);
    }
    
    static boolean motorsCheck(int check){
        String ck = "";
        if (check == 0){
            ck = "Motors haven't been turned on yet";
            SCIFO cktxt = new SCIFO(ck);
            cktxt.speak();
            return false;
        }
        else{
            return true;
        }
    }
    
    public static void main(String[] args)throws IOException
    {
        // TODO code application logic here
        Configuration configuration = new Configuration();

        configuration
                .setAcousticModelPath("resource:/edu/cmu/sphinx/models/en-us/en-us");
        configuration
                .setDictionaryPath("file:control.dic");
        configuration
                .setLanguageModelPath("file:control.lm");
        
        String text = "";
        
        try{
        Socket soc = new Socket("192.168.1.33",80);//Here you must change IP address and use your target's address
        DataInputStream din = new DataInputStream(soc.getInputStream());    
        //Create object of Output Stream  to write on socket 
        DataOutputStream dout = new DataOutputStream(soc.getOutputStream());
        
        
        LiveSpeechRecognizer recognizer = new LiveSpeechRecognizer(configuration);
        // Start recognition process pruning previously cached data.
        recognizer.startRecognition(true);
        SpeechResult result;
        String order;
        String action="";
        int check = 0;
        while ((result = recognizer.getResult()) != null) {
            order = result.getHypothesis();
            String[] finalorder = order.split(" ");
            //if (finalorder.length == 3){
            try{
                if(finalorder[0].equals("SCIFO")){
                    System.out.println(finalorder[2]);
                    
                    switch (finalorder[1]){
                        case "INTRODUCE":
                            if(finalorder[2].equals("YOURSELF")){
                                text = "Hello, my name is Saifo, and I'm a speech controlled quadcopter. "
                                +"I've been created in 2016 and my father is Pedro Frau. I've been designed to listen to"
                                +"simple commands and act in consequence. Nowadays, I'm not able to fly following voice"
                                +"orders, it might be dangerous for humans as I could hurt someone. Anyway I can make a "
                                + "demo.";
                                SCIFO introtxt = new SCIFO(text);
                                introtxt.speak();
                            }
                        case "GO":
                            if (motorsCheck(check) == true){
                                switch (finalorder[2]){
                                    case "UP":
                                        action = "w";
                                        text = "Going up, sir";
                                        SCIFO uptxt = new SCIFO(text);
                                        uptxt.speak();
                                        break;
                                    case "DOWN":
                                        action = "s";
                                        text = "Going down, sir";
                                        SCIFO downtxt = new SCIFO(text);
                                        downtxt.speak();
                                        break;
                                    case "RIGHT":
                                        action = "e";
                                        text = "Going right, sir";
                                        SCIFO righttxt = new SCIFO(text);
                                        righttxt.speak();
                                        break;
                                    case "LEFT":
                                        text = "Going left, sir";
                                        SCIFO lefttxt = new SCIFO(text);
                                        lefttxt.speak();
                                        action = "q";
                                        break;
                                }
                                break;
                            }
                            break;
                        case "START":
                            if(finalorder[2].equals("MOTORS")){
                                action = "1";
                                check = 1;
                                text = "Starting motors";
                                SCIFO ONtxt = new SCIFO(text);
                                ONtxt.speak();
                            }
                            break;
                        case "STOP":
                            if(finalorder[2].equals("MOTORS")){
                                action = "0";
                                check = 0;
                                text = "Stoping motors";
                                SCIFO ONtxt = new SCIFO(text);
                                ONtxt.speak();
                            }
                            break;
                        case "ROTATE":
                            if(motorsCheck(check) == true){
                                switch(finalorder[2]){
                                    case "LEFT":
                                        action = "a";
                                        text = "Rotating left";
                                        SCIFO Rlefttxt = new SCIFO(text);
                                        Rlefttxt.speak();
                                        break;
                                    case "RIGHT":
                                        action = "d";
                                        text = "Rotating right";
                                        SCIFO Rrighttxt = new SCIFO(text);
                                        Rrighttxt.speak();
                                        break;
                                }
                                break;
                            }
                        }

                    System.out.println(action);
                    dout.writeUTF(action);//sends command to server
                }
            }
            catch(IndexOutOfBoundsException i){
                if (finalorder.length == 1){
                    int x=(Math.random()<0.5)?0:1;
                    String ierror1 ="";
                    if (x==0){
                        ierror1 = "What?";
                    }
                    else{
                        ierror1= "Tell me";
                    }
                    SCIFO itxt1 = new SCIFO(ierror1);
                    itxt1.speak();
                }
                else{
                    String ierror = "You are giving incomplete commands. Please use three words orders";
                    SCIFO itxt = new SCIFO(ierror);
                    itxt.speak();
                }
            }
            /*}
            else{
                System.out.println("Sorry");
            }*/
        }
        recognizer.stopRecognition();
        soc.close();  //close port 
        din.close();  //close input stream      
        dout.close(); //close output stream 
        }
        catch(SocketException s){
            String socerror = s.getMessage() + ". Please check the quadcopter connection";
            SCIFO soctxt = new SCIFO(socerror);
            soctxt.speak();
        }
    }
    
}
