package HOSPITAL_PATIENT_MANAGEMENT_SYSTEM;

import java.util.ArrayList;
import java.util.List;

public class ShiftManagement {
    // This is the controller class
    private final PatientQueue paediatricQueue;
    private final PatientQueue surgeryQueue;
    private final PatientQueue cardioQueue;

    private final long shiftDurationMs;
    private final PerformanceMetrics metrics;

    public ShiftManagement(PatientQueue paediatricQueue, PatientQueue surgeryQueue, PatientQueue cardioQueue, long shiftDurationMs, PerformanceMetrics metrics) {
        this.paediatricQueue = paediatricQueue;
        this.surgeryQueue = surgeryQueue;
        this.cardioQueue = cardioQueue;
        this.metrics = metrics;
        this.shiftDurationMs = shiftDurationMs;
    }

    // Running multiple shifts with automatic rotations (Day and Night shifts automatically)
    public void runShifts(int numberOfShifts) throws InterruptedException{
        for (int shiftNumber = 1; shiftNumber<= numberOfShifts; shiftNumber++){
            // Automatic alternates between day and night
            String shiftType = (shiftNumber % 2 ==1) ? "Day" : "Night";
            System.out.println("\n---------------------------------------------------");
            System.out.println("SHIFT " + shiftNumber + ": " + shiftType + " Shift starting...");
            System.out.println("---------------------------------------------------");

            // Starting the shift (creating and starting 3 consultant threads)
            List<Thread> consultantThreads = startShift(shiftType, shiftNumber);

            // shift runs for specified duration
            //System.out.println("[SHIFT] Running for " + (shiftDurationMs / 1000) + " seconds");
            Thread.sleep(shiftDurationMs);

            // Ending the shift (stoping the consultant threads)
            System.out.println("\n[SHIFT] " + shiftType + " Shift " + shiftNumber + " ending");
            System.out.println("---------------------------------------------------");
            endShift(consultantThreads);

            // Adding pauses between shifts
            if (shiftNumber < numberOfShifts){
                Thread.sleep(1000);
            }
        }
    }

    // Starting a new shift (creating and starting consultant threads)
    private List<Thread> startShift(String shiftType, int shiftNumber){
        List<Thread> threads = new ArrayList<>();

        // Creating 3 consultant threads (one per speciality) which runs simultaneously

        // 1. Paediatrician
        String paedName = "Dr. PAE-" + shiftType.charAt(0) + shiftNumber;
        Consultant paediatrician = new Consultant(paedName, "Paediatrician", paediatricQueue, shiftType, metrics);
        Thread paedThread = new Thread(paediatrician);
        paedThread.start();
        threads.add(paedThread);

        // 1. Surgeon
        String surgeonName = "Dr. SUR-" + shiftType.charAt(0) + shiftNumber;
        Consultant surgeon = new Consultant(surgeonName, "Surgeon", surgeryQueue, shiftType, metrics);
        Thread surgeonThread = new Thread(surgeon);
        surgeonThread.start();
        threads.add(surgeonThread);

        // 1. Cardiologist
        String cardioName = "Dr. CAD-" + shiftType.charAt(0) + shiftNumber;
        Consultant cardiologist = new Consultant(cardioName, "Cardiologist", cardioQueue, shiftType, metrics);
        Thread cardioThread = new Thread(cardiologist);
        cardioThread.start();
        threads.add(cardioThread);

        System.out.println("[SHIFT] " + threads.size() + " consultants started for " + shiftType + " shift");
        return threads;
    }

    // End shift (using interrupt stopping the consultant threads gracefully)
    private void endShift(List<Thread> threads) throws InterruptedException{
        System.out.println();
        // interrupting all the consultant threads to signal the shift end
        for (Thread thread : threads){
            thread.interrupt();
        }

        // waiting for all the threads to be stop executing
        for (Thread thread : threads){
            thread.join(2000);
        }
        System.out.println("[SHIFT] all consultants have ended their shift");
    }

    public void displayQueueStatus() {
        System.out.println("QUEUE STATUS:");
        System.out.println("=".repeat(60));
        System.out.println("Paediatrician queue: " + paediatricQueue.getSize() + " patients waiting");
        System.out.println("Surgeon queue: " + surgeryQueue.getSize() + " patients waiting");
        System.out.println("Cardiologist queue: " + cardioQueue.getSize() + " patients waiting");
    }

}
