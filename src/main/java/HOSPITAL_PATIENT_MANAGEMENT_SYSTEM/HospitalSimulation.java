package HOSPITAL_PATIENT_MANAGEMENT_SYSTEM;

import java.util.HashMap;
import java.util.Map;

public class HospitalSimulation{
    public static void main(String[] args) throws InterruptedException{
        printHeader();

        final int totalPatients = 30;
        final int numberOfShifts = 2;  // (1 day & 2 shifts)
        final long shiftDurationMs = 12000;   // 12 seconds per shift (simulated 12h)

        // 1 simulated hour = 1 real seconds
        // Therefore 23h shift = 12 seconds

        System.out.println(" Simulation Configuration:");
        System.out.println("   Total Patients: " + totalPatients);
        System.out.println("   Number of Shifts: " + numberOfShifts);
        System.out.println("   Shift Duration: " + (shiftDurationMs / 1000) + " seconds (simulated 12 hours)");
        System.out.println("   Time Scale: 1 simulated hour = 1 real second");
        System.out.println("   Specialities: Paediatrician, Surgeon, Cardiologist");
        System.out.println("   Consultants per shift: 3 (one per speciality)");

        // Performance Metrics
        PerformanceMetrics metrics = new PerformanceMetrics();
        metrics.startSimulation();
        System.out.println("\n---------------------------------------------------");
        System.out.println("[METRICS] Performance tracking initialized");
        System.out.println("  - NHS 4-hour target monitoring: ENABLED");
        System.out.println("  - Wait time tracking: ENABLED");
        System.out.println("  - Per-speciality statistics: ENABLED");
        System.out.println("===================================================");


        PatientQueue paediatricQueue = new PatientQueue("Paediatrician");
        PatientQueue surgeryQueue = new PatientQueue("Surgeon");
        PatientQueue cardiologyQueue = new PatientQueue("Cardiologist");

        // Starting producer thread (PatientArrival)
        PatientArrival patientArrival = new PatientArrival(paediatricQueue, surgeryQueue,cardiologyQueue,totalPatients,metrics);

        Thread producerThread = new Thread(patientArrival, "PatientArrival-Producer");
        producerThread.start();

        // Running shift manager (Manages Consumer threads)
        ShiftManagement shiftManager = new ShiftManagement(paediatricQueue, surgeryQueue, cardiologyQueue, shiftDurationMs, metrics);
        shiftManager.runShifts(numberOfShifts);

        // Stoping the producer thread
        System.out.println("\nStopping the patient arrival thread....");
        patientArrival.stop();
        producerThread.interrupt();
        producerThread.join(3000);  // waiting for producer to finish

        // stoping metrics tracking
        metrics.endSimulation();

        displayFinalStastics(paediatricQueue, surgeryQueue, cardiologyQueue);
        metrics.displayPerformanceReport();
        System.out.println("Hospital Simulation Completed!");
    }

    private static void printHeader(){
        System.out.println("\n===================================================");
        System.out.println("\n   ROYAL MANCHESTER HOSPITAL - PATIENT MANAGEMENT   ");
        System.out.println("   (Concurrent A&E system with Shift Management)");
        System.out.println();
        System.out.println("===================================================");
    }

    private static void displayFinalStastics(PatientQueue patientQueue, PatientQueue surgeryQueue, PatientQueue cardiologyQueue){
        System.out.println("\nFINAL QUEUE STATUS:");

        int paedWaiting = patientQueue.getSize();
        int surgeWaiting = surgeryQueue.getSize();
        int cardioWaiting = cardiologyQueue.getSize();
        int totalWaiting = paedWaiting + surgeWaiting + cardioWaiting;

        System.out.println("Paediatrician Queue: " + paedWaiting + " patients still waiting");
        System.out.println("Surgeon Queue:       " + surgeWaiting + " patients still waiting");
        System.out.println("Cardiologist Queue:  " + cardioWaiting + " patients still waiting");

        System.out.println("\nTotal patients still waiting: " + totalWaiting);
        System.out.println("--------------------------------------------------------");
    }
}
