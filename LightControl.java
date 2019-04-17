import java.net.*;
import java.math.*;
import java.io.*;
import java.util.*;
import org.apache.http.client.*;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.client.methods.*;
import org.apache.http.entity.*;
import org.apache.http.*;
import org.apache.commons.logging.*;
import org.json.*;
import java.io.BufferedReader;
import java.io.InputStreamReader;

public class LightControl{
    OutputStreamWriter osw = null;
    URL light1 = null;
    URL light2 = null;
    URL light3 = null;
    URL light4 = null;
    URL light5 = null;
    URL light17 = null;
    URL light18 = null;
    URL light19 = null;
    URL light20 = null;
    URL light21 = null;
    URL light26 = null;
    URL light27 = null;
    URL light28 = null;
    URL elmotheghost = null;
    URL kitchen = null;
    URL reading = null;
    URL doorlight = null;
    URL bedroom1 = null;
    URL bedroom2 = null;
    URL bathroom = null;
    URL everything = null;
    URL stripsonly = null;
    URL sleepstrips = null;
    URL doorlight2 = null;
    URL bedroom1_2 = null;
    URL movielights = null;
    static int TIMEOUT = 4000; //4000
    static ArrayList<Integer> lights = new ArrayList<Integer>();
    String ipAddress = "10.0.1.2";//192.168.1.214
    String jsonMessage = "";
    public LightControl(){
        try{
            light1 = new URL("http://"+ ipAddress +"/api/FWPdOe0HYtLLPlwm7dwM4PgdDiKyin7XcK-boTiF/lights/1/state");
            light2 = new URL("http://"+ ipAddress +"/api/FWPdOe0HYtLLPlwm7dwM4PgdDiKyin7XcK-boTiF/lights/2/state");
            light3 = new URL("http://"+ ipAddress +"/api/FWPdOe0HYtLLPlwm7dwM4PgdDiKyin7XcK-boTiF/lights/3/state");
            light4 = new URL("http://"+ ipAddress +"/api/FWPdOe0HYtLLPlwm7dwM4PgdDiKyin7XcK-boTiF/lights/4/state");
            light5 = new URL("http://"+ ipAddress +"/api/FWPdOe0HYtLLPlwm7dwM4PgdDiKyin7XcK-boTiF/lights/5/state");
            light17 = new URL("http://"+ ipAddress +"/api/FWPdOe0HYtLLPlwm7dwM4PgdDiKyin7XcK-boTiF/lights/17/state");
            light18 = new URL("http://"+ ipAddress +"/api/FWPdOe0HYtLLPlwm7dwM4PgdDiKyin7XcK-boTiF/lights/18/state");
            light19 = new URL("http://"+ ipAddress +"/api/FWPdOe0HYtLLPlwm7dwM4PgdDiKyin7XcK-boTiF/lights/19/state");
            light20 = new URL("http://"+ ipAddress +"/api/FWPdOe0HYtLLPlwm7dwM4PgdDiKyin7XcK-boTiF/lights/20/state");
            light21 = new URL("http://"+ ipAddress +"/api/FWPdOe0HYtLLPlwm7dwM4PgdDiKyin7XcK-boTiF/lights/21/state");
            light26 = new URL("http://"+ ipAddress +"/api/FWPdOe0HYtLLPlwm7dwM4PgdDiKyin7XcK-boTiF/lights/26/state");
            light27 = new URL("http://"+ ipAddress +"/api/FWPdOe0HYtLLPlwm7dwM4PgdDiKyin7XcK-boTiF/lights/27/state");
            light28 = new URL("http://"+ ipAddress +"/api/FWPdOe0HYtLLPlwm7dwM4PgdDiKyin7XcK-boTiF/lights/28/state");
            elmotheghost = new URL("http://" + ipAddress +"/api/FWPdOe0HYtLLPlwm7dwM4PgdDiKyin7XcK-boTiF/groups/1");
            kitchen = new URL("http://" + ipAddress +"/api/FWPdOe0HYtLLPlwm7dwM4PgdDiKyin7XcK-boTiF/groups/3");
            reading = new URL("http://" + ipAddress +"/api/FWPdOe0HYtLLPlwm7dwM4PgdDiKyin7XcK-boTiF/groups/4");
            doorlight = new URL("http://" + ipAddress +"/api/FWPdOe0HYtLLPlwm7dwM4PgdDiKyin7XcK-boTiF/groups/11");
            bedroom1 = new URL("http://" + ipAddress +"/api/FWPdOe0HYtLLPlwm7dwM4PgdDiKyin7XcK-boTiF/groups/12");
            bedroom2 = new URL("http://" + ipAddress +"/api/FWPdOe0HYtLLPlwm7dwM4PgdDiKyin7XcK-boTiF/groups/7");
            bathroom = new URL("http://" + ipAddress +"/api/FWPdOe0HYtLLPlwm7dwM4PgdDiKyin7XcK-boTiF/groups/2");
            everything = new URL("http://" + ipAddress +"/api/FWPdOe0HYtLLPlwm7dwM4PgdDiKyin7XcK-boTiF/groups/8");
            stripsonly = new URL("http://" + ipAddress +"/api/FWPdOe0HYtLLPlwm7dwM4PgdDiKyin7XcK-boTiF/groups/9");
            sleepstrips = new URL("http://" + ipAddress +"/api/FWPdOe0HYtLLPlwm7dwM4PgdDiKyin7XcK-boTiF/groups/10");
            doorlight2 = new URL("http://" + ipAddress +"/api/FWPdOe0HYtLLPlwm7dwM4PgdDiKyin7XcK-boTiF/groups/11");
            bedroom1_2 = new URL("http://" + ipAddress +"/api/FWPdOe0HYtLLPlwm7dwM4PgdDiKyin7XcK-boTiF/groups/12");
            movielights = new URL("http://" + ipAddress +"/api/FWPdOe0HYtLLPlwm7dwM4PgdDiKyin7XcK-boTiF/groups/13");
        }catch(Exception e){
            //System.out.println("error at light import " + e);
        }
    }
    public static void main(String[] args) throws Exception {
        LightControl polarBear = new LightControl();
        try{
            polarBear.putLights(true,0,0,0,true,1);
        }catch(Exception e){
            //System.out.println(e);
        }
    }
    protected void putLights(final boolean on, final int sat, final int bri, final int hue, final boolean disco, final int lights){
        Thread put_light_thread = new Thread(){
            public void run(){
                HttpURLConnection connection = null;
                URL url = null;
                try{
                    url = setURL(lights);
                    url = new URL(url.toString() + "/action");
                    connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("PUT");
                    connection.setDoOutput(true);
                    connection.setRequestProperty("Content-Type", "application/json");
                    connection.setRequestProperty("Accept", "application/json");
                    connection.setUseCaches(false);
                    connection.setAllowUserInteraction(false);
                    connection.setConnectTimeout(TIMEOUT);
                    connection.setReadTimeout(TIMEOUT);
                    osw = new OutputStreamWriter(connection.getOutputStream());
                    String command = "";
                    if(on){
                        if(!disco){
                            //putLights(on, sat, bri, hue, lights);
                            command += "{\"on\":true,\"sat\":" + sat + ",\"bri\":" + bri + ",\"hue\":" + hue + ",\"effect\":\"none\"}";
                        }else{
                            command += "{\"on\":true,\"sat\":" + 254 + ",\"bri\":" + 254 + ",\"hue\":" + 10000 + ",\"effect\":\"colorloop\"}";
                        }
                    }else{
                        command += "{\"on\":false}";
                        /*
                        if(!disco){
                            putLights(on, sat, bri, hue, lights);
                            command += "{\"on\":\"false\",\"sat\":" + sat + ",\"bri\":" + bri + ",\"hue\":" + hue + ",\"effect\":\"none\"}";
                        }else{
                            command += "{\"on\":\"false\",\"sat\":" + 254 + ",\"bri\":" + 254 + ",\"hue\":" + 10000 + ",\"effect\":\"colorloop\"}";
                        }
                        */
                    }
                    //System.out.println(command);
                    osw.write(String.format(command));osw.flush();
                    osw.close();
                    connection.getResponseCode();
                    //System.out.println("light #" + lightNumber + " changed");
                }catch(Exception e){
                    //System.out.println("put errors: " + e );
                }
            }
        };
        put_light_thread.start();
    }
    protected void putLights(final boolean on, final int sat, final int bri, final int hue, final int lights){
        Thread put_light_thread = new Thread(){
            public void run(){
                HttpURLConnection connection = null;
                URL url = null;
                try{
                    url = setURL(lights);
                    url = new URL(url.toString() + "/action");
                    connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("PUT");
                    connection.setDoOutput(true);
                    connection.setRequestProperty("Content-Type", "application/json");
                    connection.setRequestProperty("Accept", "application/json");
                    connection.setUseCaches(false);
                    connection.setAllowUserInteraction(false);
                    connection.setConnectTimeout(TIMEOUT);
                    connection.setReadTimeout(TIMEOUT);
                    osw = new OutputStreamWriter(connection.getOutputStream());
                    String command = "";
                    if(on){
                        command += "{\"on\":true,\"sat\":" + sat + ",\"bri\":" + bri + ",\"hue\":" + hue + ",\"effect\":\"none\"}";
                    }else{
                        command += "{\"on\":false,\"sat\":" + sat + ",\"bri\":" + bri + ",\"hue\":" + hue + ",\"effect\":\"none\"}";
                    }
                    //System.out.println(command);
                    osw.write(String.format(command));osw.flush();
                    osw.close();
                    connection.getResponseCode();
                    //System.out.println("light #" + lightNumber + " changed");
                }catch(Exception e){
                    //System.out.println("put errors: " + e );
                }
            }
        };
        try{
            Thread.sleep(100);
        }catch(Exception e){
            
        }
        put_light_thread.start();
    }
    protected JSONObject getLights(final int lights){
        JSONObject obj = null;
        HttpURLConnection connection = null;
        URL url = setURL(lights);
        try{
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Content-length", "0");
            connection.setUseCaches(false);
            connection.setAllowUserInteraction(false);
            connection.setConnectTimeout(TIMEOUT);
            connection.setReadTimeout(TIMEOUT);
            connection.connect();
            int status = connection.getResponseCode();
            switch(status){
                case 200:
                case 201:
                    BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    StringBuilder sb = new StringBuilder();
                    String line;
                    while ((line = br.readLine()) != null) {
                        sb.append(line + "\n");
                    }
                    br.close();
                    String lightStatus = sb.toString();
                    //System.out.println("lightStatus: " + lightStatus);
                    obj = new JSONObject(lightStatus);
                    //System.out.println("im here");
            }
        }catch(Exception e){
            //System.out.println(lights + " get errors: " + e);
        } finally {
           if (connection != null) {
              try {
                  connection.disconnect();
              } catch (Exception ex) {
                  System.out.println("errors connection error: " + ex);
              }
           }
        }
        return obj;
    }
    private URL setURL(int index){
        switch(index){
            case 1: return elmotheghost;
            case 2: return bathroom;
            case 3: return kitchen; 
            case 4: return reading; 
            case 5: return doorlight; 
            case 6: return bedroom1; 
            case 7: return bedroom2; 
            case 8: return everything; 
            case 9: return stripsonly;
            case 10: return sleepstrips;
            case 11: return doorlight2;
            case 12: return bedroom1_2;
            case 13: return movielights;
        }
        return null;
    }
    protected void hueToAngle(int hue){
        
    }
}
//192.168.1.214
//10.0.1.2