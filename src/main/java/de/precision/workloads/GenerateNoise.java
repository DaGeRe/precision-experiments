package de.precision.workloads;

import java.util.Random;

/**
 * To evaluate parallel usage with different cgroups, this class generates just noise
 * 
 * @author reichelt
 *
 */
public class GenerateNoise {

   public static final Random r = new Random();

   public static void main(String[] args) {
      int threads = 2;
      for (int i = 0; i < threads; i++) {
         Thread thread = new Thread(new Runnable() {
            
            @Override
            public void run() {
               while (true) {
                  int random = r.nextInt(5);

                  switch (random) {
                  case 0:
                  case 1:
                     executeAddWorkload();
                     break;
                  case 2:
                  case 3:
                     reserveRAM();
                     break;
                  case 4:
                     waitRandomly();
                     break;
                  }
               }
            }
         });
         thread.start();
      }
   }

   private static void waitRandomly() {
      try {
         final int waitDuration = 10 * 1000 + r.nextInt(1000 * 60 * 2);
         System.out.println("Waiting: " + waitDuration);
         Thread.sleep(waitDuration);
      } catch (InterruptedException e) {
         e.printStackTrace();
      }
   }

   private static void reserveRAM() {
      int randomRAM = 1000000 + new Random().nextInt(100000);
      System.out.println("Reserving RAM: " + randomRAM);
      final ReserveRAM reserveRAM = new ReserveRAM(randomRAM);
      reserveRAM.reserveRAM();
      final int[][] ints = reserveRAM.getInts();
      final int[] lastRow = ints[ints.length - 1];
      System.out.println(lastRow[lastRow.length - 1]);
   }

   private static void executeAddWorkload() {
      long randomAddition = 10000000 + new Random().nextInt(10000000);
      System.out.println("Adding: " + randomAddition);
      final AddRandomNumbers addWorkload = new AddRandomNumbers();
      for (int i = 0; i < randomAddition; i++) {
         addWorkload.addSomething();
      }
      System.out.println(addWorkload.getValue());
   }
}
