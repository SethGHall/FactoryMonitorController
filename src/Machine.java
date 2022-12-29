
/*
    PROGRAM NAME:   MID SEMESTER TEST PRACTICAL
    FILE:           Machine.java
    AUTHER:         Seth Hall
    DATE:           6 May 2010
*/
import java.util.Random;




public class Machine implements Runnable
{
   private int minTemp, maxTemp;
   private int currentTemp;
   private Cooler connectedCooler;
   private final int SLEEP_MS = 200;
   private boolean isRunning;

   public Machine(int minTemp, int maxTemp)
   {
      this.minTemp = minTemp;
      this.maxTemp = maxTemp;
      currentTemp = 20;
      connectedCooler = null;
      isRunning = false;
   }
   public void startMachine()
   {  isRunning = true;
      Thread thread = new Thread(this);
      thread.start();
   }
   public void stopMachine()
   {  isRunning = false;
   }
   public synchronized boolean connectCooler(Cooler cooler)
   {
      if(connectedCooler == null)
      {  System.out.println("COOLER CONNECTED");
         connectedCooler = cooler;
         return true;
      }
      else return false;
   }
   public int getCurrentTemp()
   {
      return currentTemp;
   }
   public int getMaxTemp()
   {  return maxTemp;
   }
   public int getMinTemp()
   {  return minTemp;
   }
   public boolean isRunning()
   {  return isRunning;
   }
   public synchronized boolean isCoolerConnected()
   {  return (connectedCooler != null);
   }
   public synchronized void disconnectCooler()
   {  connectedCooler = null;
   }
   public void run()
   {  Random gen = new Random();
      while(isRunning)
      {  if(!isCoolerConnected())
         {  currentTemp+=gen.nextInt(5)+1;
         }
         else
         {  synchronized(this)
            {
               currentTemp -= connectedCooler.getCoolingFactor();
            }
         }
         if(currentTemp < minTemp)
         {  isRunning = false;
            disconnectCooler();
             throw new MachineTemperatureException("MACHINE TOO COLD ");
         }
         if(currentTemp > maxTemp)
         {  isRunning = false;
            disconnectCooler();
            throw new MachineTemperatureException("MACHINE TOO HOT ");
         }
         try
         {  Thread.sleep(SLEEP_MS);
         }catch(Exception e){}
      }
   }
}
