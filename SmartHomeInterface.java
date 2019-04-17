import java.awt.*;
import java.awt.font.TextLayout;
import java.awt.geom.Rectangle2D;
import javax.swing.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.text.ParseException;
import java.util.Date;
import java.math.*;
import java.util.*;
import javax.swing.border.*;
import javax.swing.event.*;
import java.awt.event.*;
import java.net.URL;
import java.io.*;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import org.json.*;
import java.awt.font.TextAttribute;
public class SmartHomeInterface extends SmartHomeBrain implements ActionListener{
    javax.swing.Timer generalTimer;
    javax.swing.Timer lightClickTimer;
    javax.swing.Timer lightChangeTimer;
    javax.swing.Timer lightUpdateTimer;
    javax.swing.Timer window_timer;
    int window_timer_constant = 0;
    //JFrame
    JFrame window;
    TabPanel tabPanel = new TabPanel();
    ContentPanel contentPanel = new ContentPanel();
    
    Color color_black = new Color(0,0,0);
    Color color_gray = new Color(217,217,217);
    Color color_darkGray = new Color(151,151,151);
    Color color_lightGray = new Color(245,245,245);
    Color color_white = new Color(255,255,255);
    Color color_lightBlue= new Color(244,248,252);
    Color color_blue = new Color(119,170,184);
    
    int[] skylightWindowX = {224,224,501,501};
    int[] skylightWindowY = {313,403,403,185};
    Polygon skylightWindow = new Polygon(skylightWindowX,skylightWindowY,4);
    
    
    //this section contains images
    BufferedImage colorWheel;
    BufferedImage leftTabLight;
    BufferedImage leftTabSelect;
    BufferedImage leftTab;
    BufferedImage floorplan;
    BufferedImage twentycolor;
    BufferedImage discoSymbol;
    BufferedImage discoSymbolBig;
    BufferedImage lightOn;
    BufferedImage lightOff;
    
    //this section contains fonts
    Font bold12_31;
    Font regular15;
    Font regular49;
    Font bold11;
    Font bold11_2;
    Font demibold11_1;
    Font demibold11_20;
    Font demibold11_a;
    Font demibold11_b;
    Font regular60;
    Font regular36_25;
    
    
    //These are for communication between the 2 panels
    boolean canClick = true;
    boolean lightMenuRetract = false;
    boolean lightMenuExpand = false;
    boolean lightMenuExtended = false;
    boolean lightMenuExpandAnimation = false;
    boolean lightMenuExpandAnimationOver = false;
    boolean lightMenuExpandClick = false;
    boolean lightMenuActive = false;
    
    boolean windowOpen = false;
    boolean window_changed = false;
    //These are the variables for date
    private String time;
    GregorianCalendar date = new GregorianCalendar();
    boolean twentyfourhours = false;
    boolean morning = true;
    String[] days = {"","SUN","MON", "TUE", "WED", "THU", "FRI", "SAT"};
    String[] months = {"JAN","FEB","MAR","APR","MAY","JUN","JUL","AUG","SEP","OCT","NOV","DEC"};
    int tabSection = 80; //80 is home, 160 is lights, 240 is temp, 320 is blinds
    int section = 80;
    
    
    Thread window_thread = new Thread(){};
    //These are threads for the lights to work
    Thread light_setting_1_thread = new Thread(){};
    Thread light_setting_2_thread = new Thread(){};
    Thread light_setting_3_thread = new Thread(){};
    Thread light_setting_4_thread = new Thread(){};
    Thread light_setting_5_thread = new Thread(){};
    Thread light_setting_6_thread = new Thread(){};
    Thread light_1_thread = new Thread(){};
    Thread light_2_thread = new Thread(){};
    Thread light_3_thread = new Thread(){};
    Thread light_4_thread = new Thread(){};
    Thread light_5_thread = new Thread(){};
    Thread light_6_thread = new Thread(){};
    
    boolean controlling_light1_slider = false;
    boolean controlling_light2_slider = false;
    boolean controlling_light3_slider = false;
    boolean controlling_light4_slider = false;
    boolean controlling_light5_slider = false;
    boolean controlling_light6_slider = false;
    boolean controlling_colorwheel = false;
    
    Color tempColor = new Color(0,0,0);
    
    //These are variables for the lights
    boolean light_group_1_Changed = false;
    boolean light_group_2_Changed = false;
    boolean light_group_3_Changed = false;
    boolean light_group_4_Changed = false;
    boolean light_group_5_Changed = false;
    boolean light_group_6_Changed = false;
    
    //Window scripts
    String pythonOpen = "/home/pi/Desktop/Open.py";
    String pythonClose = "/home/pi/Desktop/Close.py";
    String[] cmd = new String[2];
    
                           // Process p = Runtime.getRuntime().exec("/home/pi/Desktop/tinyhouse/Open.py");
    public SmartHomeInterface(){
    }
    public static void main(String[] args) {
        try{
            final Process p = Runtime.getRuntime().exec("unclutter -idle 0.00001 -root");
            Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
                public void run() {
                    p.destroy();
                }
            }));
        }catch(Exception e){
            //system.println("mouse hide error: " + e);
        }
        SmartHomeInterface ui = new SmartHomeInterface();
        
        ui.createCalendar();
        ui.createTimer();
        ui.setActionListener();
        ui.createFonts();
        ui.importImage();
        ui.createGUI();
    } 
    public void createCalendar(){
        date = new GregorianCalendar();
        date.setTimeZone(TimeZone.getTimeZone("America/Los_Angeles"));
        int hourtime = date.get(Calendar.HOUR_OF_DAY);
        String timetext = "";
        if(hourtime > 11){
            morning = false;
        }else{
            morning = true;
        }
        if(date.get(Calendar.HOUR_OF_DAY) < 10 && twentyfourhours){
            timetext += "0";
        }
        if(date.get(Calendar.HOUR) < 10 && date.get(Calendar.HOUR) != 0 && !twentyfourhours){
            timetext += "0";
        }
        if(!twentyfourhours){
            if(!morning){
                hourtime = hourtime - 12;
            }
            if(hourtime == 0){
                hourtime = 12;
            }
            timetext += hourtime;
            timetext += ":";
        }else if(twentyfourhours){
            timetext += hourtime;
            timetext += ":";
        }
        if(date.get(Calendar.MINUTE) < 10){
            timetext += "0";
        }
        timetext += date.get(Calendar.MINUTE);
        time = timetext;
    }
    public void createTimer(){
        generalTimer = new javax.swing.Timer(50,this);
        generalTimer.start();
        
        lightClickTimer = new javax.swing.Timer(1000,this);
        
        lightChangeTimer = new javax.swing.Timer(400,this);
        lightChangeTimer.start();
        
        lightUpdateTimer = new javax.swing.Timer(400,this);
        lightUpdateTimer.setInitialDelay(3000);
        
        window_timer = new javax.swing.Timer(200,this);
    }
    public void setActionListener(){
    }
    public void createFonts(){
        Map<TextAttribute, Object> attributes = new HashMap<>();
        
        bold12_31 = new Font("Avenir Next", Font.PLAIN, 12);
        bold12_31 = bold12_31.deriveFont(Collections.singletonMap(TextAttribute.WEIGHT, TextAttribute.WEIGHT_BOLD));
        bold12_31 = bold12_31.deriveFont(Collections.singletonMap(TextAttribute.TRACKING, 0.31));
        
        demibold11_1 = new Font("Avenir Next", Font.PLAIN, 11);
        demibold11_1 = demibold11_1.deriveFont(Collections.singletonMap(TextAttribute.WEIGHT, TextAttribute.WEIGHT_DEMIBOLD));
        demibold11_1 = demibold11_1.deriveFont(Collections.singletonMap(TextAttribute.TRACKING, 0.25));
        
        demibold11_20 = new Font("Avenir Next", Font.PLAIN, 11);
        demibold11_20 = demibold11_20.deriveFont(Collections.singletonMap(TextAttribute.WEIGHT, TextAttribute.WEIGHT_DEMIBOLD));
        demibold11_20 = demibold11_20.deriveFont(Collections.singletonMap(TextAttribute.TRACKING, 3.2));
        
        demibold11_a = new Font("Avenir Next", Font.PLAIN, 11);
        demibold11_a = demibold11_a.deriveFont(Collections.singletonMap(TextAttribute.WEIGHT, TextAttribute.WEIGHT_DEMIBOLD));
        demibold11_a = demibold11_a.deriveFont(Collections.singletonMap(TextAttribute.TRACKING, 1));
        
        demibold11_b = new Font("Avenir Next", Font.PLAIN, 11);
        demibold11_b = demibold11_a.deriveFont(Collections.singletonMap(TextAttribute.WEIGHT, TextAttribute.WEIGHT_DEMIBOLD));
        demibold11_b = demibold11_a.deriveFont(Collections.singletonMap(TextAttribute.TRACKING, 0.30));
        
        regular36_25 = new Font("Avenir Next", Font.PLAIN, 36);
        regular36_25 = regular36_25.deriveFont(Collections.singletonMap(TextAttribute.WEIGHT, TextAttribute.WEIGHT_REGULAR));
        regular36_25 = regular36_25.deriveFont(Collections.singletonMap(TextAttribute.TRACKING, 0.25));
        
        
        regular15 = new Font("Avenir Next", Font.PLAIN, 15);
        regular49 = new Font("Avenir Next", Font.PLAIN, 49);
        bold11 = new Font("Avenir Next", Font.PLAIN, 11);
        bold11 = bold11.deriveFont(Collections.singletonMap(TextAttribute.WEIGHT, TextAttribute.WEIGHT_BOLD));
        bold11 = bold11.deriveFont(Collections.singletonMap(TextAttribute.TRACKING, 0.1));
        
        bold11_2 = new Font("Avenir Next", Font.PLAIN, 11);
        bold11_2 = bold11_2.deriveFont(Collections.singletonMap(TextAttribute.WEIGHT, TextAttribute.WEIGHT_BOLD));
        bold11_2 = bold11_2.deriveFont(Collections.singletonMap(TextAttribute.TRACKING, 0.2));
        
        regular60 = new Font("Avenir Next", Font.PLAIN, 60);
        regular15 = regular15.deriveFont(Collections.singletonMap(TextAttribute.WEIGHT, TextAttribute.WEIGHT_REGULAR));
        regular49 = regular49.deriveFont(Collections.singletonMap(TextAttribute.WEIGHT, TextAttribute.WEIGHT_REGULAR));
        regular60 = regular60.deriveFont(Collections.singletonMap(TextAttribute.WEIGHT, TextAttribute.WEIGHT_REGULAR));
    }
    public void importImage(){
        try{ 
            lightOn = ImageIO.read(new File("lightOn.png"));
            lightOff = ImageIO.read(new File("lightOff.png"));
            discoSymbol = ImageIO.read(new File("discoSymbol.png"));
            discoSymbolBig = ImageIO.read(new File("discoSymbolBig.png"));
            colorWheel = ImageIO.read(new File("colorwheel.png"));
            twentycolor = ImageIO.read(new File("twentycolor.png"));
            leftTabLight = ImageIO.read(new File("LeftTabLight.png"));  
            leftTabSelect = ImageIO.read(new File("LeftTabSelect.png"));  
            leftTab = ImageIO.read(new File("LeftTab.png")); 
            floorplan = ImageIO.read(new File("floorplan.png"));
        }catch(IOException e){
            //system.println("importImage failed: " + e);
        }
    }
    public void createGUI(){
        window = new JFrame("Smart Home");  //Create a new Frame Named Smart Home
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);  //Set the default Close Operation to Exiting
        int screenWidth = (int)(window.getToolkit().getScreenSize().getWidth());    //Get the screen width
        int screenHeight = (int)(window.getToolkit().getScreenSize().getHeight());  //Get the screen height
        int frameWidth = 800;   //Initialize frame width
        int frameHeight = 480;  //Initialize frame height
        window.setUndecorated(true);   //Make top bar disappear if "true
        window.setBounds(screenWidth/2 - frameWidth/2, screenHeight/2 - frameHeight/2, frameWidth, frameHeight);    //Puts JFrame in the center of screen
        window.setResizable(false); //Window is resizable if it is true
        window.setMinimumSize(new Dimension(800,480));  //Set the minimum size of the window
        window.setMaximumSize(new Dimension(800,480));  //Set the minimum size of the window
        window.setLayout(null);
        
        tabPanel.setLayout(null);
        tabPanel.setBackground(color_lightGray);
        tabPanel.setBounds(0,0,100,480);
        tabPanel.addMouseListener(tabPanel);
        tabPanel.addMouseMotionListener(tabPanel);
        window.add(tabPanel);
        
        contentPanel.setLayout(null);
        contentPanel.setBackground(color_white);
        contentPanel.setBounds(100,0,700,480);
        contentPanel.addMouseListener(contentPanel);
        contentPanel.addMouseMotionListener(contentPanel);
        
        window.add(contentPanel);
        window.pack();  //Ensures all panels are the right sizes
        window.setVisible(true);    //Frame is visiable if it is true
    }
    public int easeIn(double t, double b, double c, double d) {
        t /= d;
        return (int)(c*t*t + b);
    }
    public void angleToRGB(){
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
            ////system.println("error in rgb angle");
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
    private void hueToAngle(){
        colorPickerAngle = 360*hue/65535;
    } 
    public void actionPerformed(ActionEvent a){
        if(a.getSource() == generalTimer){
            createCalendar();
            if(!canClick)
                lightClickTimer.start();
            if(!light_group_1_Changed && !light_group_2_Changed && !light_group_3_Changed && !light_group_4_Changed && !light_group_5_Changed && !light_group_6_Changed){
                
                if(!lightUpdateTimer.isRunning()){
                    lightUpdateTimer.start();
                }
            }else{
                lightUpdateTimer.stop();
            }
            contentPanel.repaint();
        }
        if(a.getSource() == lightClickTimer){
            canClick = true;
            lightClickTimer.stop();
        }
        if(a.getSource() == lightChangeTimer){
            if(light_group_1_Changed)
                light_1_group_setting();
            if(light_group_2_Changed)
                light_2_group_setting();
            if(light_group_3_Changed)
                light_3_group_setting();
            if(light_group_4_Changed)
                light_4_group_setting();
            if(light_group_5_Changed)
                light_5_group_setting();
            if(light_group_6_Changed)
                light_6_group_setting();
        }
        if(a.getSource() == lightUpdateTimer){
            try{
                String temp;
                if(!light_group_1_Changed){
                    light_group_1_on =  light_group_1_JSON.getJSONObject("action").getBoolean("on");
                    light_group_1_saturation = light_group_1_JSON.getJSONObject("action").getInt("sat");
                    light_group_1_brightness = light_group_1_JSON.getJSONObject("action").getInt("bri");
                    light_group_1_hue = light_group_1_JSON.getJSONObject("action").getInt("hue");
                    hueToRGB(light_group_1_hue);
                    light_group_1_color = new Color(red,green,blue,light_group_1_saturation);
                    temp = light_group_1_JSON.getJSONObject("action").getString("effect");
                    light_group_1_disco = temp.equals("colorloop");
                    if(!light_group_1_on)
                        light_group_1_brightness = 0;
                }
                if(!light_group_2_Changed){
                    light_group_2_on =  light_group_2_JSON.getJSONObject("action").getBoolean("on");
                    light_group_2_saturation = light_group_2_JSON.getJSONObject("action").getInt("sat");
                    light_group_2_brightness = light_group_2_JSON.getJSONObject("action").getInt("bri");
                    light_group_2_hue = light_group_2_JSON.getJSONObject("action").getInt("hue");
                    hueToRGB(light_group_2_hue);
                    light_group_2_color = new Color(red,green,blue,light_group_2_saturation);
                    temp = light_group_2_JSON.getJSONObject("action").getString("effect");
                    light_group_2_disco = temp.equals("colorloop");
                    if(!light_group_2_on)
                        light_group_2_brightness = 0;
                }
                if(!light_group_3_Changed){
                    light_group_3_on =  light_group_3_JSON.getJSONObject("action").getBoolean("on");
                    light_group_3_saturation = light_group_3_JSON.getJSONObject("action").getInt("sat");
                    light_group_3_brightness = light_group_3_JSON.getJSONObject("action").getInt("bri");
                    light_group_3_hue = light_group_3_JSON.getJSONObject("action").getInt("hue");
                    hueToRGB(light_group_3_hue);
                    light_group_3_color = new Color(red,green,blue,light_group_3_saturation);
                    temp = light_group_3_JSON.getJSONObject("action").getString("effect");
                    light_group_3_disco = temp.equals("colorloop");
                    if(!light_group_3_on)
                        light_group_3_brightness = 0;
                }
                if(!light_group_4_Changed){
                    light_group_4_on =  light_group_4_JSON.getJSONObject("action").getBoolean("on");
                    light_group_4_saturation = light_group_4_JSON.getJSONObject("action").getInt("sat");
                    light_group_4_brightness = light_group_4_JSON.getJSONObject("action").getInt("bri");
                    light_group_4_hue = light_group_4_JSON.getJSONObject("action").getInt("hue");
                    hueToRGB(light_group_4_hue);
                    light_group_4_color = new Color(red,green,blue,light_group_4_saturation);
                    temp = light_group_4_JSON.getJSONObject("action").getString("effect");
                    light_group_4_disco = temp.equals("colorloop");
                    if(!light_group_4_on)
                        light_group_4_brightness = 0;
                }
                if(!light_group_5_Changed){
                    light_group_5_on =  light_group_5_JSON.getJSONObject("action").getBoolean("on");
                    light_group_5_saturation = light_group_5_JSON.getJSONObject("action").getInt("sat");
                    light_group_5_brightness = light_group_5_JSON.getJSONObject("action").getInt("bri");
                    light_group_5_hue = light_group_5_JSON.getJSONObject("action").getInt("hue");
                    hueToRGB(light_group_5_hue);
                    light_group_5_color = new Color(red,green,blue,light_group_5_saturation);
                    temp = light_group_5_JSON.getJSONObject("action").getString("effect");
                    light_group_5_disco = temp.equals("colorloop");
                    if(!light_group_5_on)
                        light_group_5_brightness = 0;
                }
                if(!light_group_6_Changed){
                    light_group_6_on =  light_group_6_JSON.getJSONObject("action").getBoolean("on");
                    light_group_6_saturation = light_group_6_JSON.getJSONObject("action").getInt("sat");
                    light_group_6_brightness = light_group_6_JSON.getJSONObject("action").getInt("bri");
                    light_group_6_hue = light_group_6_JSON.getJSONObject("action").getInt("hue");
                    hueToRGB(light_group_6_hue);
                    light_group_6_color = new Color(red,green,blue,light_group_6_saturation);
                    temp = light_group_6_JSON.getJSONObject("action").getString("effect");
                    light_group_6_disco = temp.equals("colorloop");
                    if(!light_group_6_on)
                        light_group_6_brightness = 0;
                }
            }catch(Exception e){
                //system.println("update exception: " + e);
            } 
        }
        //If timer constant is not 0 and it is the first time being ran, processes for relay is created based on the light
        if(a.getSource() == window_timer){
            if(window_timer_constant >= 50){
                //system.println("run stop code");
                window_timer.stop();
                window_timer_constant = 0;
            }else{
                cmd[0] = "python";
                if(windowOpen){
                    cmd[1] = pythonOpen;
                }else{
                    cmd[1] = pythonClose;
                }
                switch(window_timer_constant){
                    case 0: try{
                                if(windowOpen){
                                    Process p = Runtime.getRuntime().exec("/home/pi/Desktop/tinyhouse/Open.py");
                                }else{
                                    Process p = Runtime.getRuntime().exec("/home/pi/Desktop/tinyhouse/Close.py");
                                }
                            }catch(Exception e){
                                //system.println("window opening code: " + e);
                            }
                            break;
                }
                window_timer_constant++;
            }
        }
    }
    private void light_1_group_setting(){
        canClick = false;
        light_1_thread = new Thread(){
            public void run(){
                polarBear.putLights(light_group_1_on, light_group_1_saturation, light_group_1_brightness, light_group_1_hue, light_group_1_disco, 2);
                //system.println("group 1 fired");
            }
        };
        light_1_thread.start();
        light_group_1_Changed = false;
        changeInLight = true;
    }  
    private void light_2_group_setting(){
        canClick = false;
        light_2_thread = new Thread(){
            public void run(){
                polarBear.putLights(light_group_2_on, light_group_2_saturation, light_group_2_brightness, light_group_2_hue, light_group_2_disco, 3);
                //system.println("group 2 fired");
            }
        };
        light_2_thread.start();
        light_group_2_Changed = false;
        changeInLight = true;
    }  
    private void light_3_group_setting(){
        canClick = false;
        light_3_thread = new Thread(){
            public void run(){
                polarBear.putLights(light_group_3_on, light_group_3_saturation, light_group_3_brightness, light_group_3_hue, light_group_3_disco, 4);
                //system.println("group 3 fired");
            }
        };
        light_3_thread.start();
        light_group_3_Changed = false;
        changeInLight = true;
    }  
    private void light_4_group_setting(){
        canClick = false;
        light_4_thread = new Thread(){
            public void run(){
                polarBear.putLights(light_group_4_on, light_group_4_saturation, light_group_4_brightness, light_group_4_hue, light_group_4_disco, 11);
                ////system.println("group 4 fired");
            }
        };
        light_4_thread.start();
        light_group_4_Changed = false;
        changeInLight = true;
    }  
    private void light_5_group_setting(){
        canClick = false;
        light_5_thread = new Thread(){
            public void run(){
                polarBear.putLights(light_group_5_on, light_group_5_saturation, light_group_5_brightness, light_group_5_hue, light_group_5_disco, 12);
                ////system.println("group 5 fired");
            }
        };
        light_5_thread.start();
        light_group_5_Changed = false;
        changeInLight = true;
    }  
    private void light_6_group_setting(){
        canClick = false;
        light_6_thread = new Thread(){
            public void run(){
                polarBear.putLights(light_group_6_on, light_group_6_saturation, light_group_6_brightness, light_group_6_hue, light_group_6_disco, 7);
                ////system.println("group 6 fired");
            }
        };
        light_6_thread.start();
        light_group_6_Changed = false;
        changeInLight = true;
    }  
    private void open_window(){
        cmd[1] = pythonOpen;
        try{
            Process p = Runtime.getRuntime().exec("/home/pi/Desktop/tinyhouse/Open.py");
        }catch(Exception e){
        }
    }
    private void close_window(){
        cmd[1] = pythonClose;
        try{
            Process p = Runtime.getRuntime().exec("/home/pi/Desktop/tinyhouse/Close.py");
        }catch(Exception e){
        }
    }
    class TabPanel extends JPanel implements MouseListener, ActionListener, MouseMotionListener{
        javax.swing.Timer leftTabTimer = new javax.swing.Timer(5,this);
        javax.swing.Timer leftTabTimer2 = new javax.swing.Timer(10,this);
        javax.swing.Timer resetTimer = new javax.swing.Timer(100,this);
        int systemResetTimer = 0;
        boolean systemResetTimerActive = false;
        int tempTabSection = tabSection;
        int speed = 0;
        int animationProgress = 0;
        final double frameChange = 0.22;
        double frame = 0;
        boolean change = false;
        boolean greater = false;
        boolean lesser = false;
        public void paintComponent(Graphics g){
            super.paintComponent(g);
            Graphics2D g1 = (Graphics2D)g;
            g1.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            if(!lightMenuExpand && !lightMenuExpandAnimation && !lightMenuExpandAnimationOver && !lightMenuRetract){
                g1.drawImage(leftTab,0,0,100,480,null);
                g1.drawImage(leftTabSelect, 0, tabSection-1, 100, tabSection+81, 0, tabSection*2-2, 200, tabSection*2+162, null);
                leftTabTimer.start();
            }
            if(lightMenuExpand){
                lightMenuExpandAnimation = true;
                lightMenuExpand = false;
                lightMenuRetract = false;
                lightMenuExpandAnimationOver = false;
                leftTabTimer2.start();
            }
            if(lightMenuExpandAnimation){
                g1.drawImage(leftTab,0,0,100,480,null);
                g1.drawImage(leftTabSelect, 0, 160-1, 100, 160+81, 0, 160*2-2, 200, 160*2+162, null);
                g1.drawImage(leftTab, 0, 240+animationProgress, 100, 400, 0, 480, 200, (400-animationProgress)*2, null);
                g1.drawImage(leftTabLight, 0, 240, 100, 240+animationProgress, 0, (400-animationProgress)*2, 200, 400*2, null);
                canClick = false;
                leftTabTimer2.start();
            }
            if(lightMenuExpandAnimationOver){
                g1.drawImage(leftTabLight,0,0,100,480,null);
                lightMenuActive = true;
                lightMenuExtended = true;
                leftTabTimer2.start();
            }
            if(lightMenuRetract){
               g1.drawImage(leftTab,0,0,100,480,null);
               g1.drawImage(leftTabSelect, 0, tabSection-1, 100, tabSection+81, 0, tabSection*2-2, 200, tabSection*2+162, null);
               g1.drawImage(leftTab, 0, 240+animationProgress, 100, 400, 0, 480, 200, (400-animationProgress)*2, null);
               g1.drawImage(leftTabLight, 0, 240, 100, 240+animationProgress, 0, (400-animationProgress)*2, 200, 400*2, null);
               canClick = false;
               lightMenuExtended = false;
               leftTabTimer2.start();
            }
            //This section draws the background of the tabs
            ////system.println("tabSection: " + tabSection);
        }
        public void mouseClicked (MouseEvent m){
            ////system.println(canClick + " " + change + " " + greater);
            if(m.getSource() == this && canClick){
                ////system.println(m.getX() + ", " + m.getY());
                int mouseX = m.getX();
                int mouseY = m.getY();
                if(mouseY >= 80 && mouseY < 160){
                    tempTabSection = 80;
                    lightMenuActive = false;
                    lightMenuExpand = false;
                    lightMenuExtended = false;
                    lightMenuExpandClick = false;
                    lightMenuExpandAnimation = false;
                    if(lightMenuExpandAnimationOver){
                        lightMenuExpandAnimationOver = false;
                        lightMenuRetract = true;
                    }
                    leftTabTimer2.stop();
                }
                if(mouseY >= 160 && mouseY < 240){
                    ////system.println("lightMenuActive: " + lightMenuActive);
                    ////system.println("lightMenuExtended: " + lightMenuExtended);
                    ////system.println("lightMenuExpand: " + lightMenuExpand);
                    ////system.println("lightMenuExpandClick: " + lightMenuExpandClick);
                    ////system.println("lightMenuExpandAnimation: " + lightMenuExpandAnimation);
                    ////system.println("animation progress: " + animationProgress);
                    //Sys//system.printlntem.out.println("tempTabSection: " + tempTabSection);
                    ////system.println("tabSection: " + tabSection);
                    if(!lightMenuExtended){
                        ////system.println("im here");
                        lightMenuExpandAnimation = false;
                        lightMenuExpandAnimationOver = false;
                        lightMenuRetract = false;
                    }else{
                        ////system.println("im here 2");
                        if(lightMenuExpandAnimationOver){
                            lightMenuRetract = true;
                            lightMenuExpandAnimationOver = false;
                        }
                    }
                    tempTabSection = 160;
                }
                if(mouseY >= 240 && mouseY < 320){
                    if(!lightMenuExtended){
                        tempTabSection = 240;
                        lightMenuExpand = false;
                        lightMenuExpandClick = false;
                        lightMenuActive = false;
                    }else{
                        section = 400;
                        lightMenuExpandClick = true;
                        lightMenuActive = true;
                    }
                }
                if(mouseY >= 320 && mouseY < 400){
                    if(lightMenuExtended){
                        section = 480;
                        lightMenuExpandClick = true;
                        lightMenuActive = true;
                    }
                }
                if(!lightMenuExpandClick){
                    if(tabSection != tempTabSection){
                        change = true;
                        canClick = false;
                        frame = 0;
                        ////system.println("1");
                    }
                    if(tempTabSection > tabSection){
                        greater = true;
                        ////system.println("2");
                        speed = tempTabSection / 80 - tabSection / 80;
                    }
                    if(tempTabSection < tabSection){
                        lesser = true;
                        ////system.println("3");
                        speed = tabSection / 80 - tempTabSection / 80;
                    }
                    if(tempTabSection == 160){
                        change = true;
                    }
                }
            }
            ////system.println("lightMenuExpandClick: " + lightMenuExpandClick);
            ////system.println("lightMenuExpand: " + lightMenuExpand);
            ////system.println("lightMenuExpandAnimation: " + lightMenuExpandAnimation);
            ////system.println("lightMenuExpandAnimationOver: " + lightMenuExpandAnimationOver);
            ////system.println("animationProgress: " + animationProgress);
        }
        public void actionPerformed(ActionEvent a){
            if(a.getSource() == leftTabTimer){
                if(change && greater){
                    ////system.println("tabSection: " + tabSection);
                    switch(speed){
                        case 1: tabSection+= easeIn(frame + frameChange,0,1,1.001) - easeIn(frame,0,1,1.001);
                        case 2: tabSection+= (easeIn(frame + frameChange,0,1,0.71) - easeIn(frame,0,1,0.71))*speed;
                        case 3: tabSection+= (easeIn(frame + frameChange,0,1,0.58) - easeIn(frame,0,1,0.58))*speed;
                    }
                    frame += frameChange;
                    ////system.println("POSITIVE");
                    if(tabSection >= tempTabSection){
                        change = false;
                        greater = false;
                        
                        tabSection = tempTabSection;
                        section = tabSection;
                        if(tabSection == 160){
                            lightMenuExpand = true;
                            lightMenuExpandClick = true;
                        }else{
                            lightMenuExpand = false;
                        }
                    }
                    repaint();
                }else if(change && lesser){
                    ////system.println("tabSection: " + tabSection);
                    switch(speed){
                        case 1: tabSection-= easeIn(frame + frameChange,0,1,1.001) - easeIn(frame,0,1,1.001);
                        case 2: tabSection-= (easeIn(frame + frameChange,0,1,0.71) - easeIn(frame,0,1,0.71))*speed;
                        case 3: tabSection-= (easeIn(frame + frameChange,0,1,0.58) - easeIn(frame,0,1,0.58))*speed;
                    }
                    frame += frameChange;
                    ////system.println("NEGATIVE");
                    if(tabSection <= tempTabSection){
                        change = false;
                        lesser = false;
                        
                        tabSection = tempTabSection;
                        section = tabSection;
                        if(tabSection == 160){
                            lightMenuExpand = true;
                            lightMenuExpandClick = true;
                        }else{
                            lightMenuExpand = false;
                        }
                    }
                }else if(change && lightMenuActive){
                    switch(speed){
                        case 1: tabSection-= easeIn(frame + frameChange,0,1,1.001) - easeIn(frame,0,1,1.001);
                        case 2: tabSection-= (easeIn(frame + frameChange,0,1,0.71) - easeIn(frame,0,1,0.71))*speed;
                        case 3: tabSection-= (easeIn(frame + frameChange,0,1,0.58) - easeIn(frame,0,1,0.58))*speed;
                    }
                    frame += frameChange;
                    ////system.println("NEGATIVE");
                    if(tabSection <= tempTabSection){
                        change = false;
                        lesser = false;
                        
                        tabSection = tempTabSection;
                        section = tabSection;
                        if(tabSection == 160){
                            lightMenuExpand = true;
                            lightMenuExpandClick = true;
                        }else{
                            lightMenuExpand = false;
                        }
                    }
                    ////system.println("weeee");
                }
                repaint();
            }
            if(a.getSource() == leftTabTimer2){
                if(lightMenuRetract){
                    if(animationProgress > 0){
                        animationProgress-= 10;
                    }else {
                        animationProgress = 0;
                        lightMenuExpandAnimationOver = false;
                        lightMenuExpand = false;
                        lightMenuExpandClick = false;
                        lightMenuRetract = false;
                        lightMenuActive = true;
                        
                    }
                }else if(lightMenuExpandAnimation){
                    if(animationProgress < 160){
                        animationProgress+=4;
                    }else {
                        animationProgress = 160;
                        lightMenuExpandAnimation = false;
                        lightMenuExpandAnimationOver = true;
                        
                    }
                }
                ////system.println("timer 2 running: i= " + i);
                repaint();
            }
            if(a.getSource() == resetTimer){
                systemResetTimer++;
                ////system.println("clicked");
                if(systemResetTimer > 50){
                    try{
                        System.exit(0);
                        //Process p = Runtime.getRuntime().exec("sudo reboot");
                        systemResetTimer = 0;
                    }catch(Exception e){
                        systemResetTimer = 0;
                        //system.println("Shutdown Fail");
                    }
                }
            }
        }
        public void mouseEntered (MouseEvent m){
        }
        public void mouseExited (MouseEvent m){
        }
        public void mousePressed (MouseEvent m){
            if(m.getSource() == this && canClick){
                ////system.println(m.getX() + ", " + m.getY());
                int mouseX = m.getX();
                int mouseY = m.getY();
                if(mouseY > 80 && mouseY < 160 && tabSection == 80){
                    resetTimer.start();
                    systemResetTimer = 0;
                }
            }
        }
        public void mouseMoved(MouseEvent m){
        }
        public void mouseDragged(MouseEvent m){
            if(m.getSource() == this && canClick){
                ////system.println("meme");
                int mouseX = m.getX();
                int mouseY = m.getY();
                if(mouseY < 80 || mouseY > 160 || mouseX > 99 || tabSection != 80){
                    resetTimer.stop();
                    systemResetTimer = 0;
                }
            }
        }
        public void mouseReleased (MouseEvent m){
            if(m.getSource() == this && canClick){
                ////system.println(m.getX() + ", " + m.getY());
                int mouseX = m.getX();
                int mouseY = m.getY();
                if(mouseY >= 80 && mouseY < 160 || tabSection == 80){
                    resetTimer.stop();
                    systemResetTimer = 0;
                }
            }
        }
        public int easeIn(double t, double b, double c, double d) {
            t /= d;
            return (int)(c*t*t + b);
        }
    }
    class ContentPanel extends JPanel implements MouseListener, MouseMotionListener, ActionListener{
        javax.swing.Timer refresh = new javax.swing.Timer(100,this);
        javax.swing.Timer contentTimer = new javax.swing.Timer(5,this);
        javax.swing.Timer contentTimer2 = new javax.swing.Timer(10,this); ///best 10
        javax.swing.Timer contentTimer3 = new javax.swing.Timer(5,this); 
        
        int windowConstant = 0;
        int timerConstant = 0;
        int speed = 0;
        double frame = 0;
        boolean change = false;
        boolean greater = false;
        boolean lesser = false;
        int drawnCircle;
        boolean newCircle = false;
        Map<TextAttribute, Object> attributes = new HashMap<>();
        String datePrint1 = "FRI, "; //on computer
        String datePrint2 = "SEP "; //on computer
        String datePrint3 = "12"; //on computer
        
        int[] circles = new int[20];
        
        ArrayList<Integer> temp = new ArrayList<Integer>();
        public void paintComponent(Graphics g){
            super.paintComponent(g);
            Graphics2D g1 = (Graphics2D)g;
            g1.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON); 
            g1.setStroke(new BasicStroke(1));
            //This section writes the words rEvolve House on the panel
            switch(section){
                //This is the section for the home menu
                case 80:
                    g1.setColor(color_blue);
                    g1.setFont(regular36_25);
                    g1.drawString("rEvolve", 88, 254);
                    g1.setColor(color_darkGray);
                    g1.setFont(demibold11_20);
                    g1.drawString("HOUSE", 90, 272); //on computer
                    //g1.drawString("H          O          U          S          E", 88, 272); //on pi
                    //This section draws the line in the center
                    g1.setColor(color_darkGray);
                    g1.drawLine(349,40,349,440);
                    //This secion writes the time
                    int xx = 450;
                    int yy = 257;
                    g1.setColor(color_blue);
                    if(twentyfourhours){
                        g1.setFont(regular60);
                    }else{
                        g1.setFont(regular49);
                    }
                    
                    g1.drawString(time, xx,yy);
                    //This section writes the AM/PM
                    g1.setColor(new Color(240,240,240));
                    g1.setFont(demibold11_b);
                    if(twentyfourhours){
                    }else{
                        if(morning){
                            //g1.drawString("PM", 580, 257); //on computer
                            g1.drawString("PM", 597, 257); //on pi
                            g1.setColor(color_blue);
                            g1.setFont(bold11);
                            //g1.drawString("AM", 580, 245); //on computer
                            g1.drawString("AM", 597, 245); //on pi
                        }else{
                            //g1.drawString("AM", 580, 245); //on computer
                            g1.drawString("AM", 597, 245); //on pi
                            g1.setColor(color_blue);
                            g1.setFont(bold11);
                            //g1.drawString("PM", 580, 257); //on computer
                            g1.drawString("PM", 597, 257); //on pi
                        }
                    }
                    //This section writes the date 
                    g1.setColor(color_darkGray);
                    g1.setFont(demibold11_a);
                    
                    datePrint1 = days[date.get(Calendar.DAY_OF_WEEK)];
                    datePrint2 = "," + months[date.get(Calendar.MONTH)] + " ";
                    datePrint3 = "";
                    if(Calendar.DATE < 10){
                        datePrint3 += "0";
                    }
                    datePrint3 += Calendar.DATE + "";
                    
                    g1.drawString(datePrint1, 456, 284);
                    g1.drawString(datePrint2, 510, 284);
                    g1.drawString(datePrint3, 593, 284);
                    break;
                case 240:
                case 320:
                    g1.setColor(color_black);
                    g1.setFont(regular15);
                    g1.drawString("A      B      O      U      T", 74, 61);
                    
                    g1.setColor(color_darkGray);
                    g1.setFont(bold11_2);
                    g1.drawString("The rEvolve House is a revolutionary self sustaining tiny house", 74,90);
                    g1.drawString("that is designed with a human and a dog in mind. The house",74, 105);
                    g1.drawString("features a 161 sq-foot roof deck that is accessible by a spiral ", 74,120);
                    g1.drawString("staircase and a 40 ft diameter colossun deck. The colossun deck ", 74,135);
                    g1.drawString("rotates the house to point towards the sun to increase gain on ", 74,150);
                    g1.drawString("the eight solar panels mounted to the house. The energy generated", 74,165);
                    g1.drawString("is stored four salt water batteries which are non-toxic and ", 74,180);
                    g1.drawString("completely recyclable. The house also boasts a spacious living ", 74,195);
                    g1.drawString("area that feels open and inviting. We hope the rEvolve House will", 74,210);
                    g1.drawString("revolutionize the way people think about and build tiny houses.", 74,225);
                    
                    g1.setColor(color_black);
                    g1.setFont(regular15);
                    g1.drawString("T      H      E            T      E      A      M", 74, 270);
                    g1.setColor(color_darkGray);
                    g1.setFont(bold11_2);
                    g1.drawString("Anna Harris, Gabe Christ, George Giannos, Jack Dinkelspiel, ", 74,297);
                    g1.drawString("James LeClercq, JJ Galvin, Jonathan Borst, Jun Chang, Marcus", 74,312);
                    g1.drawString("Grassi, Martin Prado, Michael Heffernnan, Nico Metais, ", 74,327);
                    g1.drawString("Samantha Morehead, Taylor Mau, Thomas Chung", 74,342);
                    
                    g1.drawString("Special thanks to: Dr. Tim Hight", 74,395);
                    g1.drawString("In loving memory of Papa Reites", 74,410);
                    
                    break;
                case 160:
                case 400:
                    //Do after animation is complete
                    if(lightMenuExpandAnimationOver || lightMenuActive){
                        //this section draws the label MAP on the top left corner
                        g1.setColor(color_black);
                        g1.setFont(regular15);
                        g1.drawString("M      A      P", 74, 61);
                        
                        
                        //this section draws the circle buttons on the bottom
                        g1.setColor(new Color(250,255,183));
                        g1.fillOval(65,361,60,60);
                        g1.setColor(color_darkGray);
                        g1.drawOval(65,361,60,60);
                        
                        g1.drawOval(169,361,60,60);
                        g1.drawOval(271,361,60,60);
                        g1.drawOval(372,361,60,60);
                        g1.drawImage(discoSymbolBig, 474,361,60,60, null);
                        g1.drawOval(574,361,60,60);
                        
                        //this section lables the circle buttons on the botton
                        g1.setColor(color_darkGray);
                        g1.setFont(bold12_31);
                        g1.drawString("NATURAL", 57, 445);
                        g1.drawString("READING", 164, 445);
                        g1.drawString("MOVIE", 274, 445);
                        g1.drawString("SLEEP", 377, 445);
                        g1.drawString("DISCO", 476, 445);
                        g1.drawString("OFF", 589, 445);
                        
                        //this section draws the dividing line between the top and the bottom
                        g1.setColor(color_darkGray);
                        g1.drawLine(50,335,650,335);
                        
                        //this section draws the floorplan
                        g1.drawImage(floorplan, 75, 95, 550, 200, null);
                        
                        if(light_group_1_on){
                            g1.drawImage(lightOn, 110, 107, 44, 44, null);
                        }else{
                            g1.drawImage(lightOff, 110, 107, 44, 44, null);
                        }
                        if(light_group_2_on){
                            g1.drawImage(lightOn, 236, 242, 44, 44, null);
                        }else{
                            g1.drawImage(lightOff, 236, 242, 44, 44, null);
                        }
                        if(light_group_3_on){
                            g1.drawImage(lightOn, 286, 107, 44, 44, null);
                        }else{
                            g1.drawImage(lightOff, 286, 107, 44, 44, null);
                        }
                        if(light_group_4_on){
                            g1.drawImage(lightOn, 368, 242, 44, 44, null);
                        }else{
                            g1.drawImage(lightOff, 368, 242, 44, 44, null);
                        }
                        if(light_group_5_on){
                            g1.drawImage(lightOn, 474, 107, 44, 44, null);
                        }else{
                            g1.drawImage(lightOff, 474, 107, 44, 44, null);
                        }
                        if(light_group_6_on){
                            g1.drawImage(lightOn, 537, 107, 44, 44, null);
                        }else{
                            g1.drawImage(lightOff, 537, 107, 44, 44, null);
                        }
                        
                    }
                    break;
                case 480:
                    //This section contains the dimming and color methods of the lights
                    if(lightMenuExpandAnimationOver || lightMenuActive){
                        //this section draws the label MAP on the top left corner
                        g1.setColor(color_black);
                        g1.setFont(regular15);
                        g1.drawString("R      O      O      M", 74, 61);
                        //this section draws the slider for the brightness
                        g1.setColor(color_darkGray);
                        g1.drawLine(95,100,95,320);
                        g1.drawLine(202,100,202,320);
                        g1.drawLine(302,100,302,320);
                        g1.drawLine(403,100,403,320);
                        g1.drawLine(503,100,503,320);
                        g1.drawLine(603,100,603,320);
                        
                        //this section draws the handles on the sliders
                        g1.setColor(color_darkGray);
                        g1.fillOval(85, (254 - light_group_1_brightness) * 220 / 254 + 90,21,21);
                        g1.fillOval(192,(254 - light_group_2_brightness) * 220 / 254 + 90,21,21);
                        g1.fillOval(292,(254 - light_group_3_brightness) * 220 / 254 + 90,21,21);
                        g1.fillOval(393,(254 - light_group_4_brightness) * 220 / 254 + 90,21,21);
                        g1.fillOval(493,(254 - light_group_5_brightness) * 220 / 254 + 90,21,21);
                        g1.fillOval(593,(254 - light_group_6_brightness) * 220 / 254 + 90,21,21);
                        
                        //this section lables the circle buttons on the sliders
                        if(!light_group_1_disco){
                            g1.setColor(light_group_1_color);
                            g1.fillOval(69,344,50,50);
                            g1.setColor(color_gray);
                            g1.drawOval(69,344,50,50);
                        }else{
                            g1.drawImage(discoSymbol, 79,344,50,50, null);
                        }
                        if(!light_group_2_disco){
                            g1.setColor(light_group_2_color);
                            g1.fillOval(178,344,50,50);
                            g1.setColor(color_gray);
                            g1.drawOval(178,344,50,50);
                        }else{
                            g1.drawImage(discoSymbol, 188,344,50,50, null);
                        }
                        if(!light_group_3_disco){
                            g1.setColor(light_group_3_color);
                            g1.fillOval(278,344,50,50);
                            g1.setColor(color_gray);
                            g1.drawOval(278,344,50,50);
                        }else{
                            g1.drawImage(discoSymbol, 288,344,50,50, null);
                        }
                        if(!light_group_4_disco){
                            g1.setColor(light_group_4_color);
                            g1.fillOval(378,344,50,50);
                            g1.setColor(color_gray);
                            g1.drawOval(378,344,50,50);
                        }else{
                            g1.drawImage(discoSymbol, 388,344,50,50, null);
                        }
                        if(!light_group_5_disco){
                            g1.setColor(light_group_5_color);
                            g1.fillOval(478,344,50,50);
                            g1.setColor(color_gray);
                            g1.drawOval(478,344,50,50);
                        }else{
                            g1.drawImage(discoSymbol, 488,344,50,50, null);
                        }
                        if(!light_group_6_disco){
                            g1.setColor(light_group_6_color);
                            g1.fillOval(578,344,50,50);
                            g1.setColor(color_gray);
                            g1.drawOval(578,344,50,50);
                        }else{
                            g1.drawImage(discoSymbol, 578,344,50,50, null);
                        }
                        
                        g1.setColor(color_darkGray);
                        g1.setFont(bold12_31);
                        g1.drawString("BATHROOM", 47, 419);
                        g1.drawString("KITCHEN", 168, 419);
                        g1.drawString("READING", 268, 419);
                        g1.drawString("DOORLIGHT", 357, 419);
                        g1.drawString("BED1", 485, 419);
                        g1.drawString("BED2", 585, 419);
                        
                        //this section draws the dividing line between the top and the bottom
                        
                    }
                    break;
                case 481:
                case 482:
                case 483:
                case 484:
                case 485:
                case 486:
                    g1.setColor(color_black);
                    g1.setFont(regular15);
                    g1.drawString("R      O      O      M", 74, 61);
                    //animation target is 116
                    if(section == 481){
                        g1.setColor(color_darkGray);
                        g1.drawLine(95 + timerConstant,100,95 + timerConstant,320);
                        g1.setColor(color_darkGray);
                        g1.fillOval(85 + timerConstant,(254 - light_group_1_brightness) * 220 / 254 + 90,21,21);
                        if(!light_group_1_disco){
                            g1.setColor(light_group_1_color);
                            g1.fillOval(69 + timerConstant,344,50,50);
                            g1.setColor(color_gray);
                            g1.drawOval(69 + timerConstant,344,50,50);
                        }else{
                            g1.drawImage(discoSymbol, 69 + timerConstant,344,50,50, null);
                        }
                        g1.setColor(color_darkGray);
                        g1.setFont(bold12_31);
                        g1.drawString("BATHROOM", 50 + timerConstant, 419);
                    }
                    if(section == 482){
                        g1.setColor(color_darkGray);
                        g1.drawLine(202 + timerConstant,100,202 + timerConstant,320);
                        g1.setColor(color_darkGray);
                        g1.fillOval(192 + timerConstant,(254 - light_group_2_brightness) * 220 / 254 + 90,21,21);
                        if(!light_group_2_disco){
                            g1.setColor(light_group_2_color);
                            g1.fillOval(178 + timerConstant,344,50,50);
                            g1.setColor(color_gray);
                            g1.drawOval(178 + timerConstant,344,50,50);
                        }else{
                            g1.drawImage(discoSymbol, 178 + timerConstant,344,50,50, null);
                        }
                        g1.setColor(color_darkGray);
                        g1.setFont(bold12_31);
                        g1.drawString("KITCHEN", 168 + timerConstant, 419);
                    }
                    if(section == 483){
                        g1.setColor(color_darkGray);
                        g1.drawLine(302 + timerConstant,100,302 + timerConstant,320);
                        g1.setColor(color_darkGray);
                        g1.fillOval(292 + timerConstant,(254 - light_group_3_brightness) * 220 / 254 + 90,21,21);
                        if(!light_group_3_disco){
                            g1.setColor(light_group_3_color);
                            g1.fillOval(278 + timerConstant,344,50,50);
                            g1.setColor(color_gray);
                            g1.drawOval(278 + timerConstant,344,50,50);
                        }else{
                            g1.drawImage(discoSymbol, 278 + timerConstant,344,50,50, null);
                        }
                        g1.setColor(color_darkGray);
                        g1.setFont(bold12_31);
                        g1.drawString("READING", 268 + timerConstant, 419);
                    }
                    if(section == 484){
                        g1.setColor(color_darkGray);
                        g1.drawLine(403 + timerConstant,100,403 + timerConstant,320);
                        g1.setColor(color_darkGray);
                        g1.fillOval(393 + timerConstant,(254 - light_group_4_brightness) * 220 / 254 + 90,21,21);
                        if(!light_group_4_disco){
                            g1.setColor(light_group_4_color);
                            g1.fillOval(378 + timerConstant,344,50,50);
                            g1.setColor(color_gray);
                            g1.drawOval(378 + timerConstant,344,50,50);
                        }else{
                            g1.drawImage(discoSymbol, 378 + timerConstant,344,50,50, null);
                        }
                        g1.setColor(color_darkGray);
                        g1.setFont(bold12_31);
                        g1.drawString("DOORLIGHT", 357 + timerConstant, 419);
                    }
                    if(section == 485){
                        g1.setColor(color_darkGray);
                        g1.drawLine(503 + timerConstant,100,503 + timerConstant,320);
                        g1.setColor(color_darkGray);
                        g1.fillOval(493 + timerConstant,(254 - light_group_5_brightness) * 220 / 254 + 90,21,21);
                        if(!light_group_5_disco){
                            g1.setColor(light_group_5_color);
                            g1.fillOval(478 + timerConstant,344,50,50);
                            g1.setColor(color_gray);
                            g1.drawOval(478 + timerConstant,344,50,50);
                        }else{
                            g1.drawImage(discoSymbol, 478 + timerConstant,344,50,50, null);
                        }
                        g1.setColor(color_darkGray);
                        g1.setFont(bold12_31);
                        g1.drawString("BEDROOM 1", 466 + timerConstant, 419);
                    }
                    if(section == 486){
                        g1.setColor(color_darkGray);
                        g1.drawLine(603 + timerConstant,100,603 + timerConstant,320);
                        g1.setColor(color_darkGray);
                        g1.fillOval(593 + timerConstant,(254 - light_group_6_brightness) * 220 / 254 + 90,21,21);
                        if(!light_group_6_disco){
                            g1.setColor(light_group_6_color);
                            g1.fillOval(578 + timerConstant,344,50,50);
                            g1.setColor(color_gray);
                            g1.drawOval(578 + timerConstant,344,50,50);
                        }else{
                            g1.drawImage(discoSymbol, 578 + timerConstant,344,50,50, null);
                        }
                        g1.setColor(color_darkGray);
                        g1.setFont(bold12_31);
                        g1.drawString("BEDROOM 2", 564 + timerConstant, 419);
                    }
                    contentTimer.start();
                    break;
                case 491:
                case 492:
                case 493:
                case 494:
                case 495:
                case 496:
                    g1.setColor(color_black);
                    g1.setFont(regular15);
                    g1.drawString("R      O      O      M", 74, 61);
                    g1.setColor(color_darkGray);
                    g1.drawLine(116,100,116,320);
                    //animation target is 116
                    if(section == 491){
                        g1.setColor(color_darkGray);
                        g1.fillOval(106,(254 - light_group_1_brightness) * 220 / 254 + 90,21,21);
                        if(!light_group_1_disco){
                            g1.setColor(light_group_1_color);
                            g1.fillOval(90,344,50,50);
                            g1.setColor(color_gray);
                            g1.drawOval(90,344,50,50);
                        }else{
                            g1.drawImage(discoSymbol, 90,344,50,50, null);
                        }
                        g1.setColor(color_darkGray);
                        g1.setFont(bold12_31);
                        g1.drawString("BATHROOM", 72, 419);
                    }
                    if(section == 492){
                        g1.setColor(color_darkGray);
                        g1.fillOval(106,(254 - light_group_2_brightness) * 220 / 254 + 90,21,21);
                        if(!light_group_2_disco){
                            g1.setColor(light_group_2_color);
                            g1.fillOval(90,344,50,50);
                            g1.setColor(color_gray);
                            g1.drawOval(90,344,50,50);
                        }else{
                            g1.drawImage(discoSymbol, 90,344,50,50, null);
                        }
                        g1.setColor(color_darkGray);
                        g1.setFont(bold12_31);
                        g1.drawString("KITCHEN", 79, 419);
                    }
                    if(section == 493){
                        g1.setColor(color_darkGray);
                        g1.fillOval(106,(254 - light_group_3_brightness) * 220 / 254 + 90,21,21);
                        if(!light_group_3_disco){
                            g1.setColor(light_group_3_color);
                            g1.fillOval(90,344,50,50);
                            g1.setColor(color_gray);
                            g1.drawOval(90,344,50,50);
                        }else{
                            g1.drawImage(discoSymbol, 90,344,50,50, null);
                        }
                        g1.setColor(color_darkGray);
                        g1.setFont(bold12_31);
                        g1.drawString("READING", 76, 419);
                    }
                    if(section == 494){
                        g1.setColor(color_darkGray);
                        g1.fillOval(106,(254 - light_group_4_brightness) * 220 / 254 + 90,21,21);
                        if(!light_group_4_disco){
                            g1.setColor(light_group_4_color);
                            g1.fillOval(90,344,50,50);
                            g1.setColor(color_gray);
                            g1.drawOval(90,344,50,50);
                        }else{
                            g1.drawImage(discoSymbol, 90,344,50,50, null);
                        }
                        g1.setColor(color_darkGray);
                        g1.setFont(bold12_31);
                        g1.drawString("DOORLIGHT", 62, 419);
                    }
                    if(section == 495){
                        g1.setColor(color_darkGray);
                        g1.fillOval(106,(254 - light_group_5_brightness) * 220 / 254 + 90,21,21);
                        if(!light_group_5_disco){
                            g1.setColor(light_group_5_color);
                            g1.fillOval(90,344,50,50);
                            g1.setColor(color_gray);
                            g1.drawOval(90,344,50,50);
                        }else{
                            g1.drawImage(discoSymbol, 90,344,50,50, null);
                        }
                        g1.setColor(color_darkGray);
                        g1.setFont(bold12_31);
                        g1.drawString("BEDROOM 1", 74, 419);
                    }
                    if(section == 496){
                        g1.setColor(color_darkGray);
                        g1.fillOval(106,(254 - light_group_6_brightness) * 220 / 254 + 90,21,21);
                        if(!light_group_6_disco){
                            g1.setColor(light_group_6_color);
                            g1.fillOval(90,344,50,50);
                            g1.setColor(color_gray);
                            g1.drawOval(90,344,50,50);
                        }else{
                            g1.drawImage(discoSymbol, 90,344,50,50, null);
                        }
                        g1.setColor(color_darkGray);
                        g1.setFont(bold12_31);
                        g1.drawString("BEDROOM 2", 74, 419);
                    }
                    g1.setColor(new Color(255,186,188,circles[0])); g1.fillOval(241,96,50,50); 
                    g1.setColor(new Color(255,232,191,circles[1])); g1.fillOval(327,96,50,50); 
                    g1.setColor(new Color(255,247,146,circles[2])); g1.fillOval(412,96,50,50); 
                    g1.setColor(new Color(213,230,160,circles[3])); g1.fillOval(498,96,50,50); 
                    g1.setColor(new Color(197,227,255,circles[4])); g1.fillOval(583,96,50,50); 
                    g1.setColor(new Color(255,110,127,circles[5])); g1.fillOval(241,172,50,50); 
                    g1.setColor(new Color(255,202,131,circles[6])); g1.fillOval(327,172,50,50); 
                    g1.setColor(new Color(255,251,158,circles[7])); g1.fillOval(412,172,50,50); 
                    g1.setColor(new Color(152,203,101,circles[8])); g1.fillOval(498,172,50,50); 
                    g1.setColor(new Color(152,212,255,circles[9])); g1.fillOval(583,172,50,50); 
                    g1.setColor(new Color(233,44,73,circles[10])); g1.fillOval(241,248,50,50); 
                    g1.setColor(new Color(246,126,33,circles[11])); g1.fillOval(327,248,50,50); 
                    g1.setColor(new Color(255,249,0,circles[12])); g1.fillOval(412,248,50,50); 
                    g1.setColor(new Color(83,165,61,circles[13])); g1.fillOval(498,248,50,50); 
                    g1.setColor(new Color(107,183,238,circles[14])); g1.fillOval(583,248,50,50); 
                    g1.setColor(new Color(180,30,54,circles[15])); g1.fillOval(241,324,50,50); 
                    g1.setColor(new Color(202,95,18,circles[16])); g1.fillOval(327,324,50,50); 
                    g1.setColor(new Color(255,222,0,circles[17])); g1.fillOval(412,324,50,50); 
                    g1.setColor(new Color(0,111,40,circles[18])); g1.fillOval(498,324,50,50); 
                    g1.setColor(new Color(1,112,175,circles[19])); g1.fillOval(583,324,50,50); 
                    boolean tempBool = true;
                    int bob = 0;
                    for(int i = 0; i < 20; i++){
                        bob+= circles[i];
                        tempBool = tempBool && (circles[i] == 255);
                    }
                    bob/=20;
                    //This draws the square for the pop up tab
                    g1.setColor(new Color(245,245,245, bob));
                    g1.fillRect(670,216,20,40);
                    //This Draws the Arrow on the pop up tab
                    g1.setColor(new Color(151,151,151, bob)); 
                    g1.drawLine(676,226,684,235); 
                    g1.drawLine(676,246,684,236);
                    //This draws the circles indicating the page
                    g1.setColor(new Color(151,151,151,bob));
                    g1.fillOval(422,402,10,10);
                    g1.setColor(new Color(217,217,217,bob));
                    g1.fillOval(441,402,10,10);
                    
                    
                    contentTimer2.start();
                    
                    //system.println(tempBool);
                    if(tempBool){
                        contentTimer2.stop();
                        switch(section){
                            case 491: section = 501; break;
                            case 492: section = 502; break;
                            case 493: section = 503; break;
                            case 494: section = 504; break;
                            case 495: section = 505; break;
                            case 496: section = 506; break;
                        }
                    }
                    break;
                case 501:
                case 502:
                case 503:
                case 504:
                case 505:
                case 506:
                    g1.setColor(color_black);
                    g1.setFont(regular15);
                    g1.drawString("R      O      O      M", 74, 61);
                    g1.setColor(color_darkGray);
                    g1.drawLine(116,100,116,320);
                    if(section == 501){
                        g1.setColor(color_darkGray);
                        g1.fillOval(106,(254 - light_group_1_brightness) * 220 / 254 + 90,21,21);
                        if(!light_group_1_disco){
                            g1.setColor(light_group_1_color);
                            g1.fillOval(90,344,50,50);
                            g1.setColor(color_gray);
                            g1.drawOval(90,344,50,50);
                        }else{
                            g1.drawImage(discoSymbol, 90,344,50,50, null);
                        }
                        g1.setColor(color_darkGray);
                        g1.setFont(bold12_31);
                        g1.drawString("BATHROOM", 72, 419);
                    }
                    if(section == 502){
                        g1.setColor(color_darkGray);
                        g1.fillOval(106,(254 - light_group_2_brightness) * 220 / 254 + 90,21,21);
                        if(!light_group_2_disco){
                            g1.setColor(light_group_2_color);
                            g1.fillOval(90,344,50,50);
                            g1.setColor(color_gray);
                            g1.drawOval(90,344,50,50);
                        }else{
                            g1.drawImage(discoSymbol, 90,344,50,50, null);
                        }
                        g1.setColor(color_darkGray);
                        g1.setFont(bold12_31);
                        g1.drawString("KITCHEN", 79, 419);
                    }
                    if(section == 503){
                        g1.setColor(color_darkGray);
                        g1.fillOval(106,(254 - light_group_3_brightness) * 220 / 254 + 90,21,21);
                        if(!light_group_3_disco){
                            g1.setColor(light_group_3_color);
                            g1.fillOval(90,344,50,50);
                            g1.setColor(color_gray);
                            g1.drawOval(90,344,50,50);
                        }else{
                            g1.drawImage(discoSymbol, 90,344,50,50, null);
                        }
                        g1.setColor(color_darkGray);
                        g1.setFont(bold12_31);
                        g1.drawString("READING", 76, 419);
                    }
                    if(section == 504){
                        g1.setColor(color_darkGray);
                        g1.fillOval(106,(254 - light_group_4_brightness) * 220 / 254 + 90,21,21);
                        if(!light_group_4_disco){
                            g1.setColor(light_group_4_color);
                            g1.fillOval(90,344,50,50);
                            g1.setColor(color_gray);
                            g1.drawOval(90,344,50,50);
                        }else{
                            g1.drawImage(discoSymbol, 90,344,50,50, null);
                        }
                        g1.setColor(color_darkGray);
                        g1.setFont(bold12_31);
                        g1.drawString("DOORLIGHT", 62, 419);
                    }
                    if(section == 505){
                        g1.setColor(color_darkGray);
                        g1.fillOval(106,(254 - light_group_5_brightness) * 220 / 254 + 90,21,21);
                        if(!light_group_5_disco){
                            g1.setColor(light_group_5_color);
                            g1.fillOval(90,344,50,50);
                            g1.setColor(color_gray);
                            g1.drawOval(90,344,50,50);
                        }else{
                            g1.drawImage(discoSymbol, 90,344,50,50, null);
                        }
                        g1.setColor(color_darkGray);
                        g1.setFont(bold12_31);
                        g1.drawString("BEDROOM 1", 74, 419);
                    }
                    if(section == 506){
                        g1.setColor(color_darkGray);
                        g1.fillOval(106,(254 - light_group_6_brightness) * 220 / 254 + 90,21,21);
                        if(!light_group_6_disco){
                            g1.setColor(light_group_6_color);
                            g1.fillOval(90,344,50,50);
                            g1.setColor(color_gray);
                            g1.drawOval(90,344,50,50);
                        }else{
                            g1.drawImage(discoSymbol, 90,344,50,50, null);
                        }
                        g1.setColor(color_darkGray);
                        g1.setFont(bold12_31);
                        g1.drawString("BEDROOM 2", 74, 419);
                    }
                    //this draws the twenty colors
                    g1.drawImage(twentycolor, 241,95,393,279,null);
                    //this draws the pop up tab
                    g1.setColor(color_lightGray);
                    g1.fillRect(670,216,20,40);
                    //This Draws the Arrow on the pop up tab
                    g1.setColor(color_darkGray); 
                    g1.drawLine(676,226,684,235); 
                    g1.drawLine(676,246,684,236);
                    //This draws the circles indicate the page
                    g1.setColor(color_darkGray);
                    g1.fillOval(422,402,10,10);
                    g1.setColor(color_gray);
                    g1.fillOval(441,402,10,10);
                    break;
                case 511:
                case 512:
                case 513:
                case 514:
                case 515:
                case 516:
                    g1.setColor(color_black);
                    g1.setFont(regular15);
                    g1.drawString("R      O      O      M", 74, 61);
                    g1.setColor(color_darkGray);
                    g1.drawLine(116,100,116,320);
                    if(section == 511){
                        g1.setColor(color_darkGray);
                        g1.fillOval(106,(254 - light_group_1_brightness) * 220 / 254 + 90,21,21);
                        if(!light_group_1_disco){
                            g1.setColor(light_group_1_color);
                            g1.fillOval(90,344,50,50);
                            g1.setColor(color_gray);
                            g1.drawOval(90,344,50,50);
                        }else{
                            g1.drawImage(discoSymbol, 90,344,50,50, null);
                        }
                        g1.setColor(color_darkGray);
                        g1.setFont(bold12_31);
                        g1.drawString("BATHROOM", 72, 419);
                    }
                    if(section == 512){
                        g1.setColor(color_darkGray);
                        g1.fillOval(106,(254 - light_group_2_brightness) * 220 / 254 + 90,21,21);
                        if(!light_group_2_disco){
                            g1.setColor(light_group_2_color);
                            g1.fillOval(90,344,50,50);
                            g1.setColor(color_gray);
                            g1.drawOval(90,344,50,50);
                        }else{
                            g1.drawImage(discoSymbol, 90,344,50,50, null);
                        }
                        g1.setColor(color_darkGray);
                        g1.setFont(bold12_31);
                        g1.drawString("KITCHEN", 79, 419);
                    }if(section == 513){
                        g1.setColor(color_darkGray);
                        g1.fillOval(106,(254 - light_group_3_brightness) * 220 / 254 + 90,21,21);
                        if(!light_group_3_disco){
                            g1.setColor(light_group_3_color);
                            g1.fillOval(90,344,50,50);
                            g1.setColor(color_gray);
                            g1.drawOval(90,344,50,50);
                        }else{
                            g1.drawImage(discoSymbol, 90,344,50,50, null);
                        }
                        g1.setColor(color_darkGray);
                        g1.setFont(bold12_31);
                        g1.drawString("READING", 76, 419);
                    }
                    if(section == 514){
                        g1.setColor(color_darkGray);
                        g1.fillOval(106,(254 - light_group_4_brightness) * 220 / 254 + 90,21,21);
                        if(!light_group_4_disco){
                            g1.setColor(light_group_4_color);
                            g1.fillOval(90,344,50,50);
                            g1.setColor(color_gray);
                            g1.drawOval(90,344,50,50);
                        }else{
                            g1.drawImage(discoSymbol, 90,344,50,50, null);
                        }
                        g1.setColor(color_darkGray);
                        g1.setFont(bold12_31);
                        g1.drawString("DOORLIGHT", 62, 419);
                    }
                    if(section == 515){
                        g1.setColor(color_darkGray);
                        g1.fillOval(106,(254 - light_group_5_brightness) * 220 / 254 + 90,21,21);
                        if(!light_group_5_disco){
                            g1.setColor(light_group_5_color);
                            g1.fillOval(90,344,50,50);
                            g1.setColor(color_gray);
                            g1.drawOval(90,344,50,50);
                        }else{
                            g1.drawImage(discoSymbol, 90,344,50,50, null);
                        }
                        g1.setColor(color_darkGray);
                        g1.setFont(bold12_31);
                        g1.drawString("BEDROOM 1", 74, 419);
                    }
                    if(section == 516){
                        g1.setColor(color_darkGray);
                        g1.fillOval(106,(254 - light_group_6_brightness) * 220 / 254 + 90,21,21);
                        if(!light_group_6_disco){
                            g1.setColor(light_group_6_color);
                            g1.fillOval(90,344,50,50);
                            g1.setColor(color_gray);
                            g1.drawOval(90,344,50,50);
                        }else{
                            g1.drawImage(discoSymbol, 90,344,50,50, null);
                        }
                        g1.setColor(color_darkGray);
                        g1.setFont(bold12_31);
                        g1.drawString("BEDROOM 2", 74, 419);
                    }
                    g1.drawImage(colorWheel, 298,95,279,279, null);
                    //this draws the pop up tab
                    g1.setColor(color_lightGray);
                    g1.fillRect(670,216,20,40);
                    //This Draws the Arrow on the pop up tab
                    g1.setColor(color_darkGray); 
                    
                    g1.drawLine(684,226,676,235); 
                    g1.drawLine(684,246,676,236);
                    //This draws the circles indicate the page
                    g1.setColor(color_gray);
                    g1.fillOval(422,402,10,10);
                    g1.setColor(color_darkGray);
                    g1.fillOval(441,402,10,10);
            }
        }
        public void mouseClicked (MouseEvent m){
            int mouseX = m.getX();
            int mouseY = m.getY();
            //System.out.println(m.getX() + " " + m.getY());
            int detectionRadius1 = (int)Math.pow(32, 2);
            int detectionRadius2 = (int)Math.pow(36, 2);
            int detectionRadius3 = (int)Math.pow(45, 2);
            if(canClick){
                if(section == 80){
                    if(mouseX < 613 && mouseX > 450 && mouseY < 284 && mouseY > 207){
                        if(twentyfourhours){
                            twentyfourhours = false;
                        }else{
                            twentyfourhours = true;
                        }
                    }
                }
                //this section handles the toggle section of the lights 
                if(section == 160 || section == 400){
                    if(Math.pow(mouseY - 129, 2) + Math.pow(mouseX - 132, 2) < detectionRadius1){
                        canClick = false;
                        //system.println("1");
                        if(light_group_1_on){
                            light_group_1_on = false;
                            light_group_1_saturation = 57;
                            light_group_1_brightness = 0;
                            light_group_1_hue = 11274;
                            light_group_1_disco = false;
                        }else{
                            light_group_1_on = true;
                            light_group_1_saturation = 57;
                            light_group_1_brightness = 254;
                            light_group_1_hue = 11274;
                            light_group_1_disco = false;
                        }
                        light_group_1_Changed = true;
                    }
                    if(Math.pow(mouseY - 264, 2) + Math.pow(mouseX - 258, 2) < detectionRadius1){
                        canClick = false;
                        //system.println("2");
                        if(light_group_2_on){
                            light_group_2_on = false;
                            light_group_2_saturation = 57;
                            light_group_2_brightness = 0;
                            light_group_2_hue = 11274;
                            light_group_2_disco = false;
                        }else{
                            light_group_2_on = true;
                            light_group_2_saturation = 57;
                            light_group_2_brightness = 254;
                            light_group_2_hue = 11274;
                            light_group_2_disco = false;
                        }
                        light_group_2_Changed = true;
                    }
                    if(Math.pow(mouseY - 129, 2) + Math.pow(mouseX - 308, 2) < detectionRadius1){
                        canClick = false;
                        //system.println("3");
                        if(light_group_3_on){
                            light_group_3_on = false;
                            light_group_3_saturation = 57;
                            light_group_3_brightness = 0;
                            light_group_3_hue = 11274;
                            light_group_3_disco = false;
                        }else{
                            light_group_3_on = true;
                            light_group_3_saturation = 57;
                            light_group_3_brightness = 254;
                            light_group_3_hue = 11274;
                            light_group_3_disco = false;
                        }
                        light_group_3_Changed = true;
                    }
                    if(Math.pow(mouseY - 264, 2) + Math.pow(mouseX - 392, 2) < detectionRadius1){
                        canClick = false;
                        //system.println("4");
                        if(light_group_4_on){
                            light_group_4_on = false;
                            light_group_4_saturation = 57;
                            light_group_4_brightness = 0;
                            light_group_4_hue = 11274;
                            light_group_4_disco = false;
                        }else{
                            light_group_4_on = true;
                            light_group_4_saturation = 57;
                            light_group_4_brightness = 254;
                            light_group_4_hue = 11274;
                            light_group_4_disco = false;
                        }
                        light_group_4_Changed = true;
                    }
                    if(Math.pow(mouseY - 129, 2) + Math.pow(mouseX - 496, 2) < detectionRadius1){
                        canClick = false;
                        //system.println("5");
                        if(light_group_5_on){
                            light_group_5_on = false;
                            light_group_5_saturation = 57;
                            light_group_5_brightness = 0;
                            light_group_5_hue = 11274;
                            light_group_5_disco = false;
                        }else{
                            light_group_5_on = true;
                            light_group_5_saturation = 57;
                            light_group_5_brightness = 254;
                            light_group_5_hue = 11274;
                            light_group_5_disco = false;
                        }
                        light_group_5_Changed = true;
                    }
                    if(Math.pow(mouseY - 129, 2) + Math.pow(mouseX - 559, 2) < detectionRadius1){
                        canClick = false;
                        //system.println("6");
                        if(light_group_6_on){
                            light_group_6_on = false;
                            light_group_6_saturation = 57;
                            light_group_6_brightness = 0;
                            light_group_6_hue = 11274;
                            light_group_6_disco = false;
                        }else{
                            light_group_6_on = true;
                            light_group_6_saturation = 57;
                            light_group_6_brightness = 254;
                            light_group_6_hue = 11274;
                            light_group_6_disco = false;
                        }
                        light_group_6_Changed = true;
                    }
                    //This section triggers the presets
                    if(Math.pow(mouseY - 391, 2) + Math.pow(mouseX - 95, 2) < detectionRadius3){
                        light_1_setting();
                        canClick = false;
                    }
                    if(Math.pow(mouseY - 391, 2) + Math.pow(mouseX - 199, 2) < detectionRadius3){
                        light_2_setting();
                        canClick = false;
                    }
                    if(Math.pow(mouseY - 391, 2) + Math.pow(mouseX - 301, 2) < detectionRadius3){
                        light_3_setting();
                        canClick = false;
                    }
                    if(Math.pow(mouseY - 391, 2) + Math.pow(mouseX - 402, 2) < detectionRadius3){
                        light_4_setting();
                        canClick = false;
                    }
                    if(Math.pow(mouseY - 391, 2) + Math.pow(mouseX - 504, 2) < detectionRadius3){
                        light_5_setting();
                        canClick = false;
                    }
                    if(Math.pow(mouseY - 391, 2) + Math.pow(mouseX - 604, 2) < detectionRadius3){
                        light_6_setting();
                        canClick = false;
                    }
                }
                if(section == 240 && !window_timer.isRunning()){
                    canClick = false;
                    //system.println("window section clicked");
                    if(!windowOpen){
                        open_window();
                    }else{
                        close_window();
                    }
                    windowOpen = !windowOpen;
                    window_timer_constant = 0;
                    section = 241;
                    window_timer.start();
                    contentTimer3.start();
                    canClick = false;
                }
                //asdfg
                if(section == 480){
                    if(Math.pow(mouseY - 359, 2) + Math.pow(mouseX - 94, 2) < detectionRadius2){
                        //system.println("Bathroom Slider Button Clicked");
                        timerConstant = 0;
                        section = 481;
                    }
                    if(Math.pow(mouseY - 359, 2) + Math.pow(mouseX - 203, 2) < detectionRadius2){
                        //system.println("Kitchen Slider Button Clicked");
                        timerConstant = 0;
                        section = 482;
                    }
                    if(Math.pow(mouseY - 359, 2) + Math.pow(mouseX - 303, 2) < detectionRadius2){
                        //system.println("Reading Slider Button Clicked");
                        timerConstant = 0;
                        section = 483;
                    }
                    if(Math.pow(mouseY - 359, 2) + Math.pow(mouseX - 403, 2) < detectionRadius2){
                        //system.println("Doorlight Slider Button Clicked");
                        timerConstant = 0;
                        section = 484;
                    }
                    if(Math.pow(mouseY - 359, 2) + Math.pow(mouseX - 503, 2) < detectionRadius2){
                        //system.println("Bedroom 1 Slider Button Clicked");
                        timerConstant = 0;
                        section = 485;
                    }
                    if(Math.pow(mouseY - 359, 2) + Math.pow(mouseX - 603, 2) < detectionRadius2){
                        //system.println("Bedroom 2 Slider Button Clicked");
                        timerConstant = 0;
                        section = 486;
                    }
                }
                if(section == 501 || section == 502 || section == 503 || section == 504 || section == 505 || section == 506){
                    int tempNumber = 0;
                    int tempSat = 0;
                    int tempBri = 254;
                    int tempHue = 0;
                    boolean colorButtonClicked = false;
                    if(Math.pow(mouseY - 120, 2) + Math.pow(mouseX - 266, 2) < detectionRadius3){
                        //system.println("Color 0");
                        canClick = false;
                        colorButtonClicked = true;
                        tempColor = new Color(255,186,188);
                        tempHue = 65218;
                        tempSat = 68;
                    }
                    if(Math.pow(mouseY - 120, 2) + Math.pow(mouseX - 352, 2) < detectionRadius3){
                        //system.println("Color 1");
                        canClick = false;
                        colorButtonClicked = true;
                        tempColor = new Color(255,232,191);
                        tempHue = 6997;
                        tempSat = 63;
                    }
                    if(Math.pow(mouseY - 120, 2) + Math.pow(mouseX - 437, 2) < detectionRadius3){
                        //system.println("Color 2");
                        canClick = false;
                        colorButtonClicked = true;
                        tempColor = new Color(255,247,146);
                        tempHue = 10121;
                        tempSat = 108;
                    }
                    if(Math.pow(mouseY - 120, 2) + Math.pow(mouseX - 523, 2) < detectionRadius3){
                        //system.println("Color 3");
                        canClick = false;
                        colorButtonClicked = true;
                        tempColor = new Color(213,230,160); 
                        tempHue = 13574;
                        tempSat = 148;
                        tempSat = 194;
                    }
                    if(Math.pow(mouseY - 120, 2) + Math.pow(mouseX - 608, 2) < detectionRadius3){
                        //system.println("Color 4");
                        canClick = false;
                        colorButtonClicked = true;
                        tempColor = new Color(197,227,255);
                        tempHue = 38041;
                        tempSat = 57;
                    }
                    if(Math.pow(mouseY - 197, 2) + Math.pow(mouseX - 266, 2) < detectionRadius3){
                        //system.println("Color 5");
                        canClick = false;
                        colorButtonClicked = true;
                        tempColor = new Color(255,110,127);
                        tempHue = 64255;
                        tempSat = 144;
                    }
                    if(Math.pow(mouseY - 197, 2) + Math.pow(mouseX - 352, 2) < detectionRadius3){
                        //system.println("Color 6");
                        canClick = false;
                        colorButtonClicked = true;
                        tempColor = new Color(255,202,131);
                        tempHue = 6253;
                        tempSat = 123;
                    }
                    if(Math.pow(mouseY - 197, 2) + Math.pow(mouseX - 437, 2) < detectionRadius3){
                        //system.println("Color 7");
                        canClick = false;
                        colorButtonClicked = true;
                        tempColor = new Color(255,251,158);
                        tempHue = 10472;
                        tempSat = 96;
                    }
                    if(Math.pow(mouseY - 197, 2) + Math.pow(mouseX - 524, 2) < detectionRadius3){
                        //system.println("Color 8");
                        canClick = false;
                        colorButtonClicked = true;
                        tempColor = new Color(152,203,101); 
                        tempHue = 16383;
                        tempSat = 127;
                        tempBri = 202;
                    }
                    if(Math.pow(mouseY - 197, 2) + Math.pow(mouseX - 608, 2) < detectionRadius3){
                        //system.println("Color 9");
                        canClick = false;
                        colorButtonClicked = true;
                        tempColor = new Color(152,212,255);
                        tempHue = 37327;
                        tempSat = 102;
                    }
                    if(Math.pow(mouseY - 273, 2) + Math.pow(mouseX - 266, 2) < detectionRadius3){
                        //system.println("Color 10");
                        canClick = false;
                        colorButtonClicked = true;
                        tempColor = new Color(233,44,73); 
                        tempHue = 63858;
                        tempSat = 206;
                        tempBri = 232;
                    }
                    if(Math.pow(mouseY - 273, 2) + Math.pow(mouseX - 352, 2) < detectionRadius3){
                        //system.println("Color 11");
                        canClick = false;
                        colorButtonClicked = true;
                        tempColor = new Color(246,126,33);
                        tempHue = 4769;
                        tempSat = 219;
                        tempBri = 245;
                    }
                    if(Math.pow(mouseY - 273, 2) + Math.pow(mouseX - 437, 2) < detectionRadius3){
                        //system.println("Color 12");
                        canClick = false;
                        colorButtonClicked = true;
                        tempColor = new Color(255,249,0);
                        tempHue = 10665;
                        tempSat = 254;
                    }
                    if(Math.pow(mouseY - 273, 2) + Math.pow(mouseX - 523, 2) < detectionRadius3){
                        //system.println("Color 13");
                        canClick = false;
                        colorButtonClicked = true;
                        tempColor = new Color(83,165,61); 
                        tempHue = 19534;
                        tempSat = 160;
                        tempBri = 164;
                    }
                    if(Math.pow(mouseY - 273, 2) + Math.pow(mouseX - 608, 2) < detectionRadius3){
                        //system.println("Color 14");
                        canClick = false;
                        colorButtonClicked = true;
                        tempColor = new Color(107,183,238); 
                        tempHue = 37353;
                        tempSat = 139;
                        tempBri = 164;
                    }
                    if(Math.pow(mouseY - 349, 2) + Math.pow(mouseX - 266, 2) < detectionRadius3){
                        //system.println("Color 15");
                        canClick = false;
                        colorButtonClicked = true;
                        tempColor = new Color(180,30,54); 
                        tempHue = 63787;
                        tempSat = 211;
                        tempBri = 179;
                    }
                    if(Math.pow(mouseY - 349, 2) + Math.pow(mouseX - 352, 2) < detectionRadius3){
                        //system.println("Color 16");
                        canClick = false;
                        colorButtonClicked = true;
                        tempColor = new Color(202,95,18); 
                        tempHue = 4571;
                        tempSat = 231;
                        tempBri = 201;
                    }
                    if(Math.pow(mouseY - 349, 2) + Math.pow(mouseX - 437, 2) < detectionRadius3){
                        //system.println("Color 17");
                        canClick = false;
                        colorButtonClicked = true;
                        tempColor = new Color(255,222,0); 
                        tempHue = 9509;
                        tempSat = 254;
                    }
                    if(Math.pow(mouseY - 349, 2) + Math.pow(mouseX - 523, 2) < detectionRadius3){
                        //system.println("Color 18");
                        canClick = false;
                        colorButtonClicked = true;
                        tempColor = new Color(0,111,40); 
                        tempHue = 25780;
                        tempSat = 254;
                        tempBri = 110;
                    }
                    if(Math.pow(mouseY - 349, 2) + Math.pow(mouseX - 608, 2) < detectionRadius3){
                        //system.println("Color 19");
                        canClick = false;
                        colorButtonClicked = true;
                        tempColor = new Color(1,112,175);
                        tempHue = 36721;
                        tempSat = 252;
                        tempBri = 174;
                    }
                    if(colorButtonClicked){
                        switch(section){
                            case 501: tempNumber = 11; light_group_1_color = tempColor; light_group_1_saturation = tempSat; light_group_1_on = true; 
                            light_group_1_brightness = tempBri; light_group_1_hue = tempHue; light_group_1_disco = false; light_group_1_Changed = true; break;
                            
                            case 502: tempNumber = 1; light_group_2_color = tempColor; light_group_2_saturation = tempSat; light_group_2_on = true; 
                            light_group_2_brightness = tempBri; light_group_2_hue = tempHue; light_group_2_disco = false; light_group_2_Changed = true; break;
                            
                            case 503: tempNumber = 2; light_group_3_color = tempColor; light_group_3_saturation = tempSat; light_group_3_on = true; 
                            light_group_3_brightness = tempBri; light_group_3_hue = tempHue; light_group_3_disco = false; light_group_3_Changed = true; break;
                            
                            case 504: tempNumber = 3; light_group_4_color = tempColor; light_group_4_saturation = tempSat; light_group_4_on = true; 
                            light_group_4_brightness = tempBri; light_group_4_hue = tempHue; light_group_4_disco = false; light_group_4_Changed = true; break;
                            
                            case 505: tempNumber = 4; light_group_5_color = tempColor; light_group_5_saturation = tempSat; light_group_5_on = true; 
                            light_group_5_brightness = tempBri; light_group_5_hue = tempHue; light_group_5_disco = false; light_group_5_Changed = true; break;
                            
                            case 506: tempNumber = 5; light_group_6_color = tempColor; light_group_6_saturation = tempSat; light_group_6_on = true; 
                            light_group_6_brightness = tempBri; light_group_6_hue = tempHue; light_group_6_disco = false; light_group_6_Changed = true; break;
                        }
                        colorButtonClicked = false;
                        red = tempColor.getRed();
                        green = tempColor.getGreen();
                        blue = tempColor.getBlue();
                        //system.println(red + " " + green + " " + blue);
                    }
                    if(mouseY >= 216 && mouseY<=256 && mouseX >= 670 && mouseX <= 690){
                        switch(section){
                            case 501: section = 511; break;
                            case 502: section = 512; break;
                            case 503: section = 513; break;
                            case 504: section = 514; break;
                            case 505: section = 515; break;
                            case 506: section = 516; break;
                        }
                        canClick = false;
                    }
                }
                if(section == 511 || section == 512 || section == 513 || section == 514 || section == 515 || section == 516){
                    if(mouseY >= 216 && mouseY<=256 && mouseX >= 670 && mouseX <= 690 && canClick){
                        switch(section){
                            case 511: section = 501; break;
                            case 512: section = 502; break;
                            case 513: section = 503; break;
                            case 514: section = 504; break;
                            case 515: section = 505; break;
                            case 516: section = 506; break;
                        }
                    }
                }
            }
        }
        public void actionPerformed(ActionEvent a){
            if(a.getSource() == contentTimer){
                if(section == 481){
                    timerConstant++;
                    if(95 + timerConstant == 116){
                        contentTimer.stop();
                        timerConstant = 0;
                        //system.println("stop timer");
                        randomCircle();
                        section = 491;
                    }
                }
                if(section == 482){
                    timerConstant -= ((202 + timerConstant)-116)*0.08 + 1;
                    if(202 + timerConstant == 116){
                        contentTimer.stop();
                        timerConstant = 0;
                        //system.println("stop timer");
                        randomCircle();
                        section = 492;
                    }
                }
                if(section == 483){
                    timerConstant -= ((302 + timerConstant)-116)*0.08 + 1;
                    if(302 + timerConstant == 116){
                        contentTimer.stop();
                        timerConstant = 0;
                        //system.println("stop timer");
                        randomCircle();
                        section = 493;
                    }
                }
                if(section == 484){
                    timerConstant -= ((403 + timerConstant)-116)*0.08 + 1;
                    if(403 + timerConstant == 116){
                        contentTimer.stop();
                        timerConstant = 0;
                        //system.println("stop timer");
                        randomCircle();
                        section = 494;
                    }
                }
                if(section == 485){
                    timerConstant -= ((503 + timerConstant)-116)*0.08 + 1;
                    if(503 + timerConstant == 116){
                        contentTimer.stop();
                        timerConstant = 0;
                        //system.println("stop timer");
                        randomCircle();
                        section = 495;
                    }
                }
                if(section == 486){
                    timerConstant -= ((603 + timerConstant)-116)*0.08 + 1;
                    if(603 + timerConstant == 116){
                        contentTimer.stop();
                        timerConstant = 0;
                        //system.println("stop timer");
                        randomCircle();
                        section = 496;
                    }
                }
                repaint();
            }
            if(a.getSource() == contentTimer2){
                int random2 = (int)(Math.random()*20);
                int random = (int)(Math.random()*(random2+1));
                int tempSum = 0;
                for(int i = 0; i < 14; i++){
                    tempSum += circles[random];
                }
                if(tempSum >= 255*14){
                    random = (int)(10 + Math.random()*10);
                }
                if(circles[random] < 255){
                    if(circles[random] < 20){
                        circles[random]+=30;
                    }else if(circles[random] < 50){
                        circles[random]+=50;
                    }else if(circles[random] < 255){
                        circles[random]+=70;
                    }
                }
                if(circles[random] > 255){
                    circles[random] = 255;
                }
                for(int i = 0; i < 20; i++){
                    if(circles[i] >=255){
                        circles[i] = 255;
                    }
                    //system.print(circles[i]+ " ");
                }
                //system.println();
            }
            if(a.getSource() == contentTimer3){
                windowConstant++;
                canClick = false;
                if(windowConstant > 100){
                    contentTimer3.stop();
                    windowConstant = 0;
                    section = 240;
                }
            }
            repaint();
        }
        public void randomCircle(){
            temp.clear();
            for(int i = 0; i < 20; i++){
                temp.add(i);
            }
            newCircle = true;
            long seed = System.nanoTime();
            Collections.shuffle(temp, new Random(seed));
            Arrays.fill(circles, 0);
        }
        public void mouseEntered (MouseEvent m){
        }
        public void mouseExited (MouseEvent m){
        }
        public void mousePressed (MouseEvent m){
            int mouseX = m.getX();
            int mouseY = m.getY();
            if(section == 480){
                if(mouseY >= 80 && mouseY <= 325 && mouseX >= 69 && mouseX <= 119){
                    light_group_1_brightness = 254 - ((mouseY - 90) * 254)/225;
                    light_group_1_on = true;
                    if(light_group_1_brightness <= 0){
                        light_group_1_brightness = 0;
                        light_group_1_on = false;
                    }else if(light_group_1_brightness > 254){
                        light_group_1_brightness = 254;
                    }
                    light_group_1_Changed = true;
                    controlling_light1_slider = true;
                }
                if(mouseY >= 80 && mouseY <= 325 && mouseX >= 178 && mouseX <= 228){
                    light_group_2_brightness = 254 - ((mouseY - 90) * 254)/225;
                    light_group_2_on = true;
                    if(light_group_2_brightness <= 0){
                        light_group_2_brightness = 0;
                        light_group_2_on = false;
                    }else if(light_group_2_brightness > 254){
                        light_group_2_brightness = 254;
                    }
                    light_group_2_Changed = true;
                    controlling_light2_slider = true;
                }
                if(mouseY >= 80 && mouseY <= 325 && mouseX >= 278 && mouseX <= 328){
                    light_group_3_brightness = 254 - ((mouseY - 90) * 254)/225;
                    light_group_3_on = true;
                    if(light_group_3_brightness <= 0){
                        light_group_3_brightness = 0;
                        light_group_3_on = false;
                    }else if(light_group_3_brightness > 254){
                        light_group_3_brightness = 254;
                    }
                    light_group_3_Changed = true;
                    controlling_light3_slider = true;
                }
                if(mouseY >= 80 && mouseY <= 325 && mouseX >= 378 && mouseX <= 428){
                    light_group_4_brightness = 254 - ((mouseY - 90) * 254)/225;
                    light_group_4_on = true;
                    if(light_group_4_brightness <= 0){
                        light_group_4_brightness = 0;
                        light_group_4_on = false;
                    }else if(light_group_4_brightness > 254){
                        light_group_4_brightness = 254;
                    }
                    light_group_4_Changed = true;
                    controlling_light4_slider = true;
                }
                if(mouseY >= 80 && mouseY <= 325 && mouseX >= 478 && mouseX <= 528){
                    light_group_5_brightness = 254 - ((mouseY - 90) * 254)/225;
                    light_group_5_on = true;
                    if(light_group_5_brightness <= 0){
                        light_group_5_brightness = 0;
                        light_group_5_on = false;
                    }else if(light_group_5_brightness > 254){
                        light_group_5_brightness = 254;
                    }
                    light_group_5_Changed = true;
                    controlling_light5_slider = true;
                }
                if(mouseY >= 80 && mouseY <= 325 && mouseX >= 578 && mouseX <= 628){
                    light_group_6_brightness = 254 - ((mouseY - 90) * 254)/225;
                    light_group_6_on = true;
                    if(light_group_6_brightness <= 0){
                        light_group_6_brightness = 0;
                        light_group_6_on = false;
                    }else if(light_group_6_brightness > 254){
                        light_group_6_brightness = 254;
                    }
                    light_group_6_Changed = true;
                    controlling_light6_slider = true;
                }
                repaint();
            }
            if(section == 501 || section == 502 || section == 503 || section == 504 || section == 505 || section == 506 
            || section == 511 || section == 512 || section == 513 || section == 514 || section == 515 || section == 516){
                if(mouseX >= 86 && mouseX <= 146 && mouseY >= 90 && mouseY <= 325){
                    switch(section){
                        case 501:
                        case 511:   controlling_light1_slider = true; 
                                    light_group_1_brightness = 254 - ((mouseY - 90) * 254)/225;
                                    light_group_1_on = true;
                                    if(light_group_1_brightness <= 0){
                                         light_group_1_on = false;
                                         light_group_1_brightness = 0;
                                    }else if(light_group_1_brightness > 254){
                                        light_group_1_brightness = 254;
                                    }
                                    //system.println(light_group_1_brightness);
                                    light_group_1_Changed = true;
                                    break;
                        case 502:
                        case 512:   controlling_light2_slider = true; 
                                    light_group_2_brightness = 254 - ((mouseY - 90) * 254)/225;
                                    light_group_2_on = true;
                                    if(light_group_2_brightness <= 0){
                                         light_group_2_on = false;
                                         light_group_2_brightness = 0;
                                    }else if(light_group_2_brightness > 254){
                                        light_group_2_brightness = 254;
                                    }
                                    //system.println(light_group_2_brightness);
                                    light_group_2_Changed = true;
                                    break;
                        case 503:
                        case 513:   controlling_light3_slider = true; 
                                    light_group_3_brightness = 254 - ((mouseY - 90) * 254)/225;
                                    light_group_3_on = true;
                                    if(light_group_3_brightness <= 0){
                                         light_group_3_on = false;
                                         light_group_3_brightness = 0;
                                    }else if(light_group_3_brightness > 254){
                                        light_group_3_brightness = 254;
                                    }
                                    //system.println(light_group_3_brightness);
                                    light_group_3_Changed = true;
                                    break;
                        case 504:
                        case 514:   controlling_light4_slider = true; 
                                    light_group_4_brightness = 254 - ((mouseY - 90) * 254)/225;
                                    light_group_4_on = true;
                                    if(light_group_4_brightness <= 0){
                                         light_group_4_on = false;
                                         light_group_4_brightness = 0;
                                    }else if(light_group_4_brightness > 254){
                                        light_group_4_brightness = 254;
                                    }
                                    //system.println(light_group_4_brightness);
                                    light_group_4_Changed = true;
                                    break;
                        case 505:
                        case 515:   controlling_light5_slider = true; 
                                    light_group_5_brightness = 254 - ((mouseY - 90) * 254)/225;
                                    light_group_5_on = true;
                                    if(light_group_5_brightness <= 0){
                                         light_group_5_on = false;
                                         light_group_5_brightness = 0;
                                    }else if(light_group_5_brightness > 254){
                                        light_group_5_brightness = 254;
                                    }
                                    //system.println(light_group_5_brightness);
                                    light_group_5_Changed = true;
                                    break;
                        case 506:
                        case 516:   controlling_light6_slider = true; 
                                    light_group_6_brightness = 254 - ((mouseY - 90) * 254)/225;
                                    light_group_6_on = true;
                                    if(light_group_6_brightness <= 0){
                                         light_group_6_on = false;
                                         light_group_6_brightness = 0;
                                    }else if(light_group_6_brightness > 254){
                                        light_group_6_brightness = 254;
                                    }
                                    //system.println(light_group_6_brightness);
                                    light_group_6_Changed = true;
                                    break;
                    }
                }
            }
            if(section == 511 || section == 512 || section == 513 || section == 514 || section == 515 || section == 516){
                if(Math.pow(mouseY - 234, 2) + Math.pow(mouseX - 432, 2) < 134*134){
                    controlling_colorwheel = true;
                    if(controlling_colorwheel){
                        colorPickerAngle =  (Math.toDegrees(Math.atan2(mouseX - 438, 234 - mouseY))+360)%360;
                        angleToRGB();
                        hue = (int)((colorPickerAngle/360)*65535);
                        int tempDis = (int)Math.sqrt((mouseX-438)*(mouseX-438) + (mouseY-234)*(mouseY-234));
                        int tempSat = 254-254*(150-tempDis)/150;    
                        //system.println(tempDis);
                        if(tempSat <= 0){
                            tempSat = 0;
                        }
                        if(tempSat >= 254){
                            tempSat = 254;
                        }
                        sat = tempSat;
                        switch(section){
                            case 511: light_group_1_color = new Color(red,green,blue,sat); light_group_1_hue = hue; light_group_1_saturation = tempSat; light_group_1_Changed = true; break;
                            case 512: light_group_2_color = new Color(red,green,blue,sat); light_group_2_hue = hue; light_group_2_saturation = tempSat; light_group_2_Changed = true; break;
                            case 513: light_group_3_color = new Color(red,green,blue,sat); light_group_3_hue = hue; light_group_3_saturation = tempSat; light_group_3_Changed = true; break;
                            case 514: light_group_4_color = new Color(red,green,blue,sat); light_group_4_hue = hue; light_group_4_saturation = tempSat; light_group_4_Changed = true; break;
                            case 515: light_group_5_color = new Color(red,green,blue,sat); light_group_5_hue = hue; light_group_5_saturation = tempSat; light_group_5_Changed = true; break;
                            case 516: light_group_6_color = new Color(red,green,blue,sat); light_group_6_hue = hue; light_group_6_saturation = tempSat; light_group_6_Changed = true; break;
                        }
                    }
                }
            }
        }
        public void mouseMoved(MouseEvent m){   
        }
        public void mouseDragged(MouseEvent m){
            int mouseX = m.getX();
            int mouseY = m.getY();
            if(section == 480){
                if(controlling_light1_slider){
                    light_group_1_brightness = 254 - ((mouseY - 90) * 254)/225;
                    light_group_1_on = true;
                    if(light_group_1_brightness <= 0){
                        light_group_1_on = false;
                        light_group_1_brightness = 0;
                    }else if(light_group_1_brightness > 254){
                        light_group_1_brightness = 254;
                    }
                    //system.println(light_group_1_brightness);
                    light_group_1_Changed = true;
                }
                if(controlling_light2_slider){
                    light_group_2_brightness = 254 - ((mouseY - 90) * 254)/225;
                    light_group_2_on = true;
                    if(light_group_2_brightness <= 0){
                        light_group_2_on = false;
                        light_group_2_brightness = 0;
                    }else if(light_group_2_brightness > 254){
                        light_group_2_brightness = 254;
                    }
                    light_group_2_Changed = true;
                }
                if(controlling_light3_slider){
                    light_group_3_brightness = 254 - ((mouseY - 90) * 254)/225;
                    light_group_3_on = true;
                    if(light_group_3_brightness <= 0){
                        light_group_3_on = false;
                        light_group_3_brightness = 0;
                    }else if(light_group_3_brightness > 254){
                        light_group_3_brightness = 254;
                    }
                    light_group_3_Changed = true;
                }
                if(controlling_light4_slider){
                    light_group_4_brightness = 254 - ((mouseY - 90) * 254)/225;
                    light_group_4_on = true;
                    if(light_group_4_brightness <= 0){
                        light_group_4_on = false;
                        light_group_4_brightness = 0;
                    }else if(light_group_4_brightness > 254){
                        light_group_4_brightness = 254;
                    }
                    light_group_4_Changed = true;
                }
                if(controlling_light5_slider){
                    light_group_5_brightness = 254 - ((mouseY - 90) * 254)/225;
                    light_group_5_on = true;
                    if(light_group_5_brightness <= 0){
                        light_group_5_on = false;
                        light_group_5_brightness = 0;
                    }else if(light_group_5_brightness > 254){
                        light_group_5_brightness = 254;
                    }
                    light_group_5_Changed = true;
                }
                if(controlling_light6_slider){
                    light_group_6_brightness = 254 - ((mouseY - 90) * 254)/225;
                    light_group_6_on = true;
                    if(light_group_6_brightness <= 0){
                        light_group_6_on = false;
                        light_group_6_brightness = 0;
                    }else if(light_group_6_brightness > 254){
                        light_group_6_brightness = 254;
                    }
                    light_group_6_Changed = true;
                }
                repaint();
            }
            if(section == 501 || section == 502 || section == 503 || section == 504 || section == 505 || section == 506){
                if(controlling_light1_slider || controlling_light2_slider || controlling_light3_slider || 
                   controlling_light4_slider || controlling_light5_slider || controlling_light6_slider){
                    switch(section){
                        case 501: light_group_1_brightness = 254 - ((mouseY - 90) * 254)/225; 
                                    light_group_1_on = true;
                                    if(light_group_1_brightness <= 0){
                                        light_group_1_brightness = 0;
                                        light_group_1_on = false;
                                    }else if(light_group_1_brightness > 254){
                                        light_group_1_brightness = 254;
                                    }
                                    light_group_1_Changed = true;
                                    break;
                        case 502: light_group_2_brightness = 254 - ((mouseY - 90) * 254)/225;
                                    light_group_2_on = true;
                                    if(light_group_2_brightness <= 0){
                                        light_group_2_brightness = 0;
                                        light_group_2_on = false;
                                    }else if(light_group_2_brightness > 254){
                                        light_group_2_brightness = 254;
                                    }
                                    light_group_2_Changed = true;
                                    break;
                        case 503: light_group_3_brightness = 254 - ((mouseY - 90) * 254)/225; 
                                    light_group_3_on = true;
                                    if(light_group_3_brightness <= 0){
                                        light_group_3_brightness = 0;
                                        light_group_3_on = false;
                                    }else if(light_group_3_brightness > 254){
                                        light_group_3_brightness = 254;
                                    }
                                    light_group_3_Changed = true;
                                    break;
                        case 504: light_group_4_brightness = 254 - ((mouseY - 90) * 254)/225;
                                    light_group_4_on = true;
                                    if(light_group_4_brightness <= 0){
                                        light_group_4_brightness = 0;
                                        light_group_4_on = false;
                                    }else if(light_group_4_brightness > 254){
                                        light_group_4_brightness = 254;
                                    }
                                    light_group_4_Changed = true;
                                    //system.println(light_group_4_brightness);
                                    break;
                        case 505: light_group_5_brightness = 254 - ((mouseY - 90) * 254)/225; 
                                    light_group_5_on = true;
                                    if(light_group_5_brightness <= 0){
                                        light_group_5_brightness = 0;
                                        light_group_5_on = false;
                                    }else if(light_group_5_brightness > 254){
                                        light_group_5_brightness = 254;
                                    }
                                    light_group_5_Changed = true;
                                    break;
                        case 506: light_group_6_brightness = 254 - ((mouseY - 90) * 254)/225; 
                                    light_group_6_on = true;
                                    if(light_group_6_brightness <= 0){
                                        light_group_6_brightness = 0;
                                        light_group_6_on = true;
                                    }else if(light_group_6_brightness > 254){
                                        light_group_6_brightness = 254;
                                    }
                                    light_group_6_Changed = true;
                                    break;
                    }
                }
                repaint();
            }
            if(section == 511 || section == 512 || section == 513 || section == 514 || section == 515 || section == 516){
                
                if(controlling_light1_slider || controlling_light2_slider || controlling_light3_slider || 
                   controlling_light4_slider || controlling_light5_slider || controlling_light6_slider){
                    switch(section){
                        case 511: light_group_1_brightness = 254 - ((mouseY - 90) * 254)/225; 
                                    light_group_1_on = true;
                                    if(light_group_1_brightness <= 0){
                                        light_group_1_brightness = 0;
                                        light_group_1_on = false;
                                    }else if(light_group_1_brightness > 254){
                                        light_group_1_brightness = 254;
                                    }
                                    light_group_1_Changed = true;
                                    //system.println(light_group_1_brightness);
                                    break;
                        case 512: light_group_2_brightness = 254 - ((mouseY - 90) * 254)/225;
                                    light_group_2_on = true;
                                    if(light_group_2_brightness <= 0){
                                        light_group_2_brightness = 0;
                                        light_group_2_on = false;
                                    }else if(light_group_2_brightness > 254){
                                        light_group_2_brightness = 254;
                                    }
                                    light_group_2_Changed = true;
                                    break;
                        case 513: light_group_3_brightness = 254 - ((mouseY - 90) * 254)/225; 
                                    light_group_3_on = true;
                                    if(light_group_3_brightness <= 0){
                                        light_group_3_brightness = 0;
                                        light_group_3_on = false;
                                    }else if(light_group_3_brightness > 254){
                                        light_group_3_brightness = 254;
                                    }
                                    light_group_3_Changed = true;
                                    break;
                        case 514: light_group_4_brightness = 254 - ((mouseY - 90) * 254)/225;
                                    light_group_4_on = true;
                                    if(light_group_4_brightness <= 0){
                                        light_group_4_brightness = 0;
                                        light_group_4_on = false;
                                    }else if(light_group_4_brightness > 254){
                                        light_group_4_brightness = 254;
                                    }
                                    light_group_4_Changed = true;
                                    //system.println(light_group_4_brightness);
                                    break;
                        case 515: light_group_5_brightness = 254 - ((mouseY - 90) * 254)/225; 
                                    light_group_5_on = true;
                                    if(light_group_5_brightness <= 0){
                                        light_group_5_brightness = 0;
                                        light_group_5_on = false;
                                    }else if(light_group_5_brightness > 254){
                                        light_group_5_brightness = 254;
                                    }
                                    light_group_5_Changed = true;
                                    break;
                        case 516: light_group_6_brightness = 254 - ((mouseY - 90) * 254)/225; 
                                    light_group_6_on = true;
                                    if(light_group_6_brightness <= 0){
                                        light_group_6_brightness = 0;
                                        light_group_6_on = false;
                                    }else if(light_group_6_brightness > 254){
                                        light_group_6_brightness = 254;
                                    }
                                    light_group_6_Changed = true;
                                    break;
                    }
                }
                repaint();
            }
        }
        public void mouseReleased (MouseEvent m){
            controlling_light1_slider = false;
            controlling_light2_slider = false;
            controlling_light3_slider = false;
            controlling_light4_slider = false;
            controlling_light5_slider = false;
            controlling_light6_slider = false;
            controlling_colorwheel = false;
            if(light_group_1_Changed){
                light_1_group_setting();
                light_group_1_Changed = false;
            }
            if(light_group_2_Changed){
                light_2_group_setting();
                light_group_2_Changed = false;
            }
            if(light_group_3_Changed){
                light_3_group_setting();
                light_group_3_Changed = false;
            }
            if(light_group_4_Changed){
                light_4_group_setting();
                light_group_4_Changed = false;
            }
            if(light_group_5_Changed){
                light_5_group_setting();
                light_group_5_Changed = false;
            }
            if(light_group_6_Changed){
                light_6_group_setting();
                light_group_6_Changed = false;
            }
        }
        private void light_1_setting(){
            //natural Lights
            light_setting_1_thread = new Thread(){
                public void run(){
                    polarBear.putLights(true, 57, 254, 11274, false, 8);
                    try{
                        Thread.sleep(400);
                    }catch(Exception e){
                    }
                    polarBear.putLights(true, 57, 254, 11274, false, 8);
                    //System.out.println("setting 1 fired");
                }
            };
            light_setting_1_thread.start();
            changeInLight = true;
        }   
        private void light_2_setting(){
            //reading lights
            light_setting_2_thread = new Thread(){
                public void run(){
                    polarBear.putLights(true, 0, 254, 0, false, 8);
                    try{
                        Thread.sleep(400);
                    }catch(Exception e){
                    }
                    polarBear.putLights(true, 0, 254, 0, false, 8);
                    //System.out.println("setting 2 fired");
                }
            };
            light_setting_2_thread.start();
            changeInLight = true;
        }
        private void light_3_setting(){
            light_setting_3_thread = new Thread(){
                public void run(){
                    polarBear.putLights(false, 57, 254, 11274, false, 8);
                    try{
                        Thread.sleep(400);
                    }catch(Exception e){
                    }
                    polarBear.putLights(true, 200, 40, 11274, false, 13);
                    //System.out.println("setting 3 fired");
                }
            };
            light_setting_3_thread.start();
            changeInLight = true;
        }
        private void light_4_setting(){
            light_setting_4_thread = new Thread(){
                public void run(){
                    polarBear.putLights(false, 57, 254, 11274, false, 8);
                    try{
                        Thread.sleep(400);
                    }catch(Exception e){
                    }
                    polarBear.putLights(true, 10, 40, 0, false, 10); //80
                    //System.out.println("setting 4 fired");
                }
            };
            light_setting_4_thread.start();
            changeInLight = true;
        }
        private void light_5_setting(){
            //disco lights
            light_setting_5_thread = new Thread(){
                public void run(){
                    polarBear.putLights(true, 200, 254, 11274, true, 8);
                    
                    //System.out.println("setting 5 fired");
                }
            };
            light_setting_5_thread.start();
            changeInLight = true;
        }
        private void light_6_setting(){
            light_setting_6_thread = new Thread(){
                public void run(){
                    polarBear.putLights(false, 57, 254, 11274, false, 8);
                    //System.out.println("setting 6 fired");
                }
            };
            light_setting_6_thread.start();
            changeInLight = true;
        }
    }
}
/*
 * Settings
 * polarBear.putLights(true, 10, 40, 0, false, 9); take out bed strip 13
 */

/*
 * 
                    case 1: tabSection+= easeIn(frame + 0.2,0,1,1.001) - easeIn(frame,0,1,1.001);
                    case 2: tabSection+= easeIn(frame + 0.2,0,2,1.001) - easeIn(frame,0,2,1.001);
                    case 3: tabSection+= easeIn(frame + 0.2,0,3,1.001) - easeIn(frame,0,3,1.001);
                    
                    case 1: tabSection+= easeIn(frame + 0.2,0,1,1.001) - easeIn(frame,0,1,1.001);
                    case 2: tabSection+= easeIn(frame + 0.2,0,1,0.71) - easeIn(frame,0,1,0.71);
                    case 3: tabSection+= easeIn(frame + 0.2,0,1,0.58) - easeIn(frame,0,1,0.58);
 */