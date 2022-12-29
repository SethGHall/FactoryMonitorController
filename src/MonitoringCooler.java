/**
    PROGRAM NAME:   MID SEMESTER TEST PRACTICAL
    FILE:           MonitoringCooler.java
    AUTHER:         Seth Hall
    MODIFIED BY:
    DATE:           6 May 2010
*/
import java.util.Collection;
import java.util.Iterator;

public class MonitoringCooler implements Cooler, Runnable
{
   private Collection<Machine> machines;
   private Machine currentConnect;
   private boolean stopRequested;
   private int operatingTemp;
   
   public MonitoringCooler(Collection<Machine> machines,int operatingTemp)
   {  this.machines = machines;
      this.operatingTemp = operatingTemp;
      stopRequested = false;
      currentConnect = null;
   }
   public void startCooler()
   {
      Thread thread = new Thread(this);
      thread.start();
   }
   // returns the current operating temperature of this cooler
   public int getCoolingFactor()
   {  return operatingTemp;
   }
   // returns whether this cooler is currently connected to a machine
   public boolean isConnectedToMachine()
   {  return (currentConnect != null);
   }

   public void run()
   {  stopRequested = false;
      if (machines.size()==0) // no machines to monitor
         return;

      Iterator<Machine> machineIterator = machines.iterator();
      while(!stopRequested)
       {    //if no more elements get new iterator
            if(!machineIterator.hasNext())
                machineIterator = machines.iterator();
            //get first element in iterator
            Machine machine = machineIterator.next();
            
            if(currentConnect == null && machine.isRunning())
            {
                //checks if machines current temp is getting close to the max
                //temp that it can run, if true this Cooler connects to it
                if((machine.getCurrentTemp() >= (machine.getMaxTemp()
                        -Cooler.DANGER_ZONE)) && !machine.isCoolerConnected())
                {    //keeps track of currentConnection
                    System.out.println("WE NEED TO CONNECT COOLER");
                    if(machine.connectCooler(this))
                        currentConnect = machine;
                    else
                       currentConnect = null;
                }
            }
            //checks if the current connected machines temp is getting close to
            //the min temp that it can run, if true this Cooler disconnects
            else if(currentConnect != null && machine.isRunning())
            {
               
                if(currentConnect.getCurrentTemp() <=
                        (currentConnect.getMinTemp()+Cooler.DANGER_ZONE))
                {
                    currentConnect.disconnectCooler();
                    currentConnect = null;         //rester currentConnection
                }
            }
            try
            {
                Thread.sleep(10);     //puts this tread to sleep for 5ms
            }
            catch(InterruptedException e){}  //catch exception
        }
   }   
   public void requestStop()
   {  stopRequested = true;
   }
}
