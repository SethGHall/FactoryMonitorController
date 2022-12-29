/*******************************************************************************************
PROGRAM NAME:   MID SEMESTER TEST PRACTICAL
FILE:           FactoryControl.java
AUTHER:         Seth Hall
MODIFIED BY:
DATE:           6 May 2010
DESCRIPTION:    Creates the Gui that draws a graph of machines that are increasing in temperate
                that coolers are responsible for cooling. Complete the code in comments below
*******************************************************************************************/
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Toolkit;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JRadioButton;
import javax.swing.JPanel;
import javax.swing.ButtonGroup;
import javax.swing.JFrame;
import java.util.Collection;
import java.util.ArrayList;
import javax.swing.Timer;

public class FactoryControl extends JPanel implements ActionListener
{
    private JPanel monitorPanel;
    private JRadioButton startButton,stopButton;
    private Timer timer;
    private Collection<Machine> machines;
    private Collection<MonitoringCooler> coolers;
    public final int MAX_TEMP = 250;
    public final int MIN_TEMP = 0;
    public final int NUM_MACHINES = 50;
    public final int NUM_COOLERS = 20;
    public final int COOLING_FACTOR = 25;

    public FactoryControl()
    {   super(new BorderLayout());
        //CREATE MACHINES OPERATING BETWEEN MIN_TEMP AND MAX_TEMP
        machines = new ArrayList<Machine>();
        for(int i=0;i<NUM_MACHINES;i++)
            machines.add(new Machine(MIN_TEMP,MAX_TEMP));
        //CREATE MONITORING COOLERS WITH A COOLING_FACTOR THAT EACH MONITOR
        //THE ENTIRE COLLECTION OF MACHINES
        coolers = new ArrayList<MonitoringCooler>();
        for(int i=0;i<NUM_COOLERS;i++)
            coolers.add(new MonitoringCooler(machines,COOLING_FACTOR));

        //TIMER TO GENERATE ACTION EVENTS EVERY 50ms
        timer = new Timer(20,this);

        monitorPanel = new MonitorPanel();          //innerclass that draws graph
        JPanel radioPanel = new JPanel();           //panel to hold radioButtons
        //radio buttons and group them and add action Listener to them
        startButton = new JRadioButton("start");
        stopButton = new JRadioButton("stop",true);
        ButtonGroup group = new ButtonGroup();
        startButton.addActionListener(this);
        stopButton.addActionListener(this);
        group.add(startButton);
        group.add(stopButton);
        //adds the radio buttons to the panel and then add the panels to the frame
        radioPanel.add(startButton);
        radioPanel.add(stopButton);
        add(radioPanel,BorderLayout.NORTH);
        add(monitorPanel,BorderLayout.CENTER);

    }
    //method that is called every timer ActionEvent generated or a radio
    //button has been pressed to start or stop the machines
    public void actionPerformed(ActionEvent e)
    {
        Object source = e.getSource();   //get source of the ActionEvent
        if(source == startButton)
        {
           //START THE TIMER AND START MACHINE AND MONITORING COOLER THREADS
            timer.start();

            for(Machine machine:machines)
                machine.startMachine();

            for(MonitoringCooler cooler:coolers)
            {
                cooler.startCooler();
            }
        }
        if(source == stopButton)
        {
           //TERMINATE THE RUNNING OF THE TIMER, MONITORING COOLER THREADS AND
           //MACHINE THREADS, SHOULD ALSO DISCONNECT ANY COOLER FROM A MACHINE

            for(MonitoringCooler cooler:coolers)
                cooler.requestStop();
            //cycles through each machine stoping it and disconnecting it from any cooler
            for(Machine machine:machines)
            {
                machine.disconnectCooler();
                machine.stopMachine();
            }
            timer.stop();      //stop timer
        }
        if(source == timer)
        {
            monitorPanel.repaint();     //repaint panel every 500ms
        }
    }
    //inner class that deals with drawing the graph of the machines on a panel
    public class MonitorPanel extends JPanel
    {   public final int PANEL_HEIGHT = MAX_TEMP*2+50;
        public final int PANEL_WIDTH = NUM_MACHINES*12+50;
        public MonitorPanel()
        {
            setPreferredSize(new Dimension(PANEL_WIDTH,PANEL_HEIGHT));
            setBackground(Color.WHITE);
        }
        //this method draws the graph in their appropriate labels and color
        @Override
        public void paintComponent(Graphics g)
        {
            super.paintComponent(g);
            g.drawString("GRAPH OF MACHINES TEMPERATURES IN C",getWidth()/3,15);
            int i=0;
            for(Machine machine:machines)
            {
                if(machine.getCurrentTemp() >= machine.getMaxTemp()-Cooler.DANGER_ZONE)
                    g.setColor(Color.RED);
                else if(machine.getCurrentTemp() <= machine.getMinTemp()+Cooler.DANGER_ZONE)
                    g.setColor(Color.BLUE);
                else
                    g.setColor(Color.YELLOW);
                g.fillRect(i*12,PANEL_HEIGHT-(machine.getCurrentTemp()*2)-10,
                            10,machine.getCurrentTemp()*2);
                //draw appropriate symbol under graph if connected or not.
                g.setColor(Color.BLACK);
                if(machine.isCoolerConnected())
                    g.drawString("-",(i*12)+2,PANEL_HEIGHT-2);
                else
                    g.drawString("+",(i*12)+2,PANEL_HEIGHT-2);
                i++;
            }
            g.drawLine(0,40,getWidth()-25,40);
            g.drawLine(0,PANEL_HEIGHT-(MIN_TEMP*2)-10,getWidth()-25,PANEL_HEIGHT-(MIN_TEMP*2)-10);
            int safeTemp = (MAX_TEMP+MIN_TEMP)/2;
            g.drawLine(0,PANEL_HEIGHT-(safeTemp*2)-10,getWidth()-25,PANEL_HEIGHT-(safeTemp*2)-10);
           // //draw lables for the temperatures
            g.drawString(""+MAX_TEMP,getWidth()-22,40);
            g.drawString(""+MIN_TEMP,getWidth()-22,PANEL_HEIGHT-(MIN_TEMP*2)-20);
            
            g.drawString(""+((MAX_TEMP+MIN_TEMP)/2),getWidth()-22,PANEL_HEIGHT-(safeTemp*2)-10);

             g.setColor(Color.BLUE);
             int y = PANEL_HEIGHT-(MIN_TEMP*2)-Cooler.DANGER_ZONE*2-10;
             g.drawLine(0,y,getWidth()-25,y);
             g.drawString(""+(MIN_TEMP+Cooler.DANGER_ZONE),getWidth()-22,y);
            // g.drawLine(0,PANEL_HEIGHT-(MIN_TEMP*2)-10,getWidth()-25,PANEL_HEIGHT-(MIN_TEMP*2)-10);
            
            g.setColor(Color.red);
            y = PANEL_HEIGHT-(MIN_TEMP*2)-(MAX_TEMP*2)+Cooler.DANGER_ZONE*2-10;
            g.drawString(""+(MAX_TEMP-Cooler.DANGER_ZONE),getWidth()-22,y);
            g.drawLine(0,y,getWidth()-25,y);
        }
    }
    public static void main(String[] args)
    {
        JFrame frame = new JFrame("GRAPH OF MACHINES, BEING COOLED");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().add(new FactoryControl());
        //gets the dimensions for screen width and height to calculate center
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        Dimension dimension = toolkit.getScreenSize();
        int screenHeight = dimension.height;
        int screenWidth = dimension.width;
        frame.pack();      //resize frame apropriately for its content
        //positions frame in center of screen
        frame.setLocation(new java.awt.Point((screenWidth/2)-(frame.getWidth()/2),
            (screenHeight/2)-(frame.getHeight()/2)));
        frame.setVisible(true);     //show the frame
    }
}
