/**
   A interface that describes the required behaviour of a cooling unit
   Written for Data Structures and Algorithms Mid Semester Test 2010
*/

public interface Cooler
{
   public final static int DANGER_ZONE = 50; // 20 degrees within limit
   // returns cooling factor of this cooler, used to decrease machine temperature
   public int getCoolingFactor();
   // returns whether this cooler is currently connected to a machine
   public boolean isConnectedToMachine();
}
