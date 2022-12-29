/*
 * Exception intended to be generated when the temperature of a running Machine
 * rises too high, or drops too low. For Data Structures and Algorithms
 * Mid-Semester Test 2010.
 */

public class MachineTemperatureException extends RuntimeException
{
   public MachineTemperatureException(String message)
   {  super(message);
   }
}
