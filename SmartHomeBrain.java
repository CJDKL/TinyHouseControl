import java.math.*;
import java.util.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;
import java.net.URL;
import java.io.*;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import org.json.*;
public class SmartHomeBrain{
    boolean changeInLight = false;
    
    /**
     * Needed variables for Nest Detect
     */
    boolean alarmActive = false;
    
    /**
     * Needed variables for lights
     */
    boolean colorLight = false;
    boolean isMouseClickColorRight = false;
    boolean isMouseClickColorLeft = false;
    ArrayList<Integer> lights = new ArrayList<Integer>();
    int lightCommand = 99;
    boolean lightOn = false;
    boolean reachable = false;
    int hue = 0;
    int bri = 50;
    int sat = 255;
    
    int red = 0;
    int green = 0;
    int blue = 0;
    int catchUp = 0;
    JSONObject objNest = new JSONObject();
    
    double colorPickerAngle = 0;
    int colorPickerMouseX;
    int colorPickerMouseY;
    int briPickerMouseX;
    int briPickerMouseY;
    
    boolean change = false;
    boolean temp = false;
    LightControl polarBear = new LightControl();    //This class helps controlling the lights
    
    JSONObject light_group_1_JSON = new JSONObject();
    JSONObject light_group_2_JSON = new JSONObject();
    JSONObject light_group_3_JSON = new JSONObject();
    JSONObject light_group_4_JSON = new JSONObject();
    JSONObject light_group_5_JSON = new JSONObject();
    JSONObject light_group_6_JSON = new JSONObject();
    JSONObject light_group_7_JSON = new JSONObject();
    
    int window_status = 0;
    boolean window_status_canControl = false;
    //these are for colored lights.
    boolean light_group_1_on = false;
    boolean light_group_2_on = false;
    boolean light_group_3_on = false;
    boolean light_group_4_on = false;
    boolean light_group_5_on = false;
    boolean light_group_6_on = false;
    boolean light_group_7_on = false;
    
    int light_group_1_saturation = 0;
    int light_group_2_saturation = 0;
    int light_group_3_saturation = 0;
    int light_group_4_saturation = 0;
    int light_group_5_saturation = 0;
    int light_group_6_saturation = 0;
    int light_group_7_saturation = 0;
    
    int light_group_1_brightness = 0;
    int light_group_2_brightness = 0;
    int light_group_3_brightness = 0;
    int light_group_4_brightness = 0;
    int light_group_5_brightness = 0;
    int light_group_6_brightness = 0;
    int light_group_7_brightness = 0;
    
    int light_group_1_hue = 0;
    int light_group_2_hue = 0;
    int light_group_3_hue = 0;
    int light_group_4_hue = 0;
    int light_group_5_hue = 0;
    int light_group_6_hue = 0;
    int light_group_7_hue = 0;
    
    boolean light_group_1_disco = false;
    boolean light_group_2_disco = false;
    boolean light_group_3_disco = false;
    boolean light_group_4_disco = false;
    boolean light_group_5_disco = false;
    boolean light_group_6_disco = false;
    boolean light_group_7_disco = false;
    
    Color light_group_1_color = new Color(0,0,0,0);
    Color light_group_2_color = new Color(0,0,0,0);
    Color light_group_3_color = new Color(0,0,0,0);
    Color light_group_4_color = new Color(0,0,0,0);
    Color light_group_5_color = new Color(0,0,0,0);
    Color light_group_6_color = new Color(0,0,0,0);
    Color light_group_7_color = new Color(0,0,0,0);
    /**
     * Needed for navigation
     */
    Thread lightThread = new Thread() {
        public void run() {
            while(!Thread.currentThread().isInterrupted()){
                try{
                    if(changeInLight){
                        //System.out.println("fired");
                        changeInLight = false;
                        Thread.sleep(400);
                    }
                }catch(Exception e){
                    //System.out.println("main loop error: " + e);
                }
            }
        }  
    };  
    Thread light_group_thread = new Thread(){
        public void run(){
            try{
                while(!Thread.currentThread().isInterrupted()){
                    light_group_1_JSON = polarBear.getLights(2);
                    light_group_2_JSON = polarBear.getLights(3);
                    light_group_3_JSON = polarBear.getLights(4);
                    light_group_4_JSON = polarBear.getLights(11);
                    light_group_5_JSON = polarBear.getLights(12);
                    light_group_6_JSON = polarBear.getLights(7);
                    light_group_7_JSON = polarBear.getLights(1);
                    System.out.println(light_group_7_JSON);
                    Thread.sleep(100);
                }
            }catch(Exception e){
                System.out.println("group : " + e);
            }
        }
    };
    Thread light_group_1_thread = new Thread(){
        public void run(){
            try{
                while(!Thread.currentThread().isInterrupted()){
                    light_group_1_JSON = polarBear.getLights(2);
                    Thread.sleep(100);
                }
            }catch(Exception e){
                System.out.println("group : " + e);
            }
        }
    };
    Thread light_group_2_thread = new Thread(){
        public void run(){
            try{
                while(!Thread.currentThread().isInterrupted()){
                    light_group_2_JSON = polarBear.getLights(3);
                    Thread.sleep(100);
                }
            }catch(Exception e){
                System.out.println("group : " + e);
            }
        }
    };
    Thread light_group_3_thread = new Thread(){
        public void run(){
            try{
                while(!Thread.currentThread().isInterrupted()){
                    light_group_3_JSON = polarBear.getLights(4);
                    Thread.sleep(100);
                }
            }catch(Exception e){
                System.out.println("group : " + e);
            }
        }
    };
    Thread light_group_4_thread = new Thread(){
        public void run(){
            try{
                while(!Thread.currentThread().isInterrupted()){
                    light_group_4_JSON = polarBear.getLights(11);
                    Thread.sleep(100);
                }
            }catch(Exception e){
                System.out.println("group : " + e);
            }
        }
    };
    Thread light_group_5_thread = new Thread(){
        public void run(){
            try{
                while(!Thread.currentThread().isInterrupted()){
                    light_group_5_JSON = polarBear.getLights(12);
                    Thread.sleep(100);
                }
            }catch(Exception e){
                System.out.println("group : " + e);
            }
        }
    };
    Thread light_group_6_thread = new Thread(){
        public void run(){
            try{
                while(!Thread.currentThread().isInterrupted()){
                    light_group_6_JSON = polarBear.getLights(7);
                    Thread.sleep(800);
                }
            }catch(Exception e){
                System.out.println("group : " + e);
            }
        }
    };
      
    Thread light_group_7_thread = new Thread(){
        public void run(){
            try{
                while(!Thread.currentThread().isInterrupted()){
                    light_group_7_JSON = polarBear.getLights(1);
                    Thread.sleep(800);
                }
            }catch(Exception e){
                System.out.println("group : " + e);
            }
        }
    };
    public static void main(String[] args) {
        try{
            final Process p = Runtime.getRuntime().exec("unclutter -idle 0.00001 -root");
            Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
                public void run() {
                    p.destroy();
                }
            }));
        }catch(Exception e){
            System.out.println("mouse hide error: " + e);
        }
        SmartHomeInterface ui = new SmartHomeInterface();
        ui.createCalendar();
        ui.createTimer();
        ui.setActionListener();
        ui.createFonts();
        ui.importImage();
        ui.createGUI();
        
        //ui.lightThread.start();
            
        ui.light_group_1_thread.start();
        ui.light_group_2_thread.start();
        ui.light_group_3_thread.start();
        ui.light_group_4_thread.start();
        ui.light_group_5_thread.start();
        ui.light_group_6_thread.start();
        ui.light_group_7_thread.start();
          
        
    } 
    private void angleToRGB(int colorPickerAngle){
        if(colorPickerAngle >= 0 && colorPickerAngle < 30){
            red = 255;
            green= (int)(0 + (colorPickerAngle-0)/30*125);
            blue = 0;
        }else if(colorPickerAngle >= 30 && colorPickerAngle < 60){
            red = 255;
            green= (int)(125 + (colorPickerAngle-30)/30*130);
            blue = 0;
        }else if(colorPickerAngle >= 60 && colorPickerAngle < 90){
            red = (int)(255 - (colorPickerAngle-60)/30*130);
            green= 255;
            blue = 0;
        }else if(colorPickerAngle >= 90 && colorPickerAngle < 120){
            red = (int)(125 - (colorPickerAngle-90)/30*125);
            green= 255;
            blue = 0;
        }else if(colorPickerAngle >= 120 && colorPickerAngle < 150){
            red = 0;
            green= 255;
            blue = (int)(0 + (colorPickerAngle-120)/30*125);
        }else if(colorPickerAngle >= 150 && colorPickerAngle < 180){
            red = 0;
            green= 255;
            blue = (int)(125 + (colorPickerAngle-150)/30*135);
        }else if(colorPickerAngle >= 180 && colorPickerAngle < 210){
            red = 0;
            green= (int)(255 - (colorPickerAngle-180)/30*130);
            blue = 255;
        }else if(colorPickerAngle >= 210 && colorPickerAngle < 240){
            red = 0;
            green= (int)(125 - (colorPickerAngle-210)/30*125);
            blue = 255;
        }else if(colorPickerAngle >= 240 && colorPickerAngle < 270){
            red = (int)(0 + (colorPickerAngle-240)/30*125);
            green= 0;
            blue = 255;
        }else if(colorPickerAngle >= 270 && colorPickerAngle < 300){
            red = (int)(125 + (colorPickerAngle-270)/30*135);
            green= 0;
            blue = 255;
        }else if(colorPickerAngle >= 300 && colorPickerAngle < 330){
            red = 255;
            green= 0;
            blue = (int)(255 - (colorPickerAngle-300)/30*130);
        }else if(colorPickerAngle >= 330 && colorPickerAngle < 360){
            red = 255;
            green= 0;
            blue = (int)(125 - (colorPickerAngle-330)/30*125);
        }else{
            System.out.println("error in rgb angle");
        }
        if(red > 255){
            red = 255;    //Making sure RGB Value dont go over 255
        }   
        if(green > 255){
            green = 255;    //Making sure RGB Value dont go over 255
        }
        if(blue > 255){
            blue = 255;    //Making sure RGB Value dont go over 255
        }
    }
    private int hueToAngle(int hue){
        return 360*hue/65535;
    }
    public void hueToRGB(int hue){
        angleToRGB(hueToAngle(hue));
    }
}