package HOSPITAL_PATIENT_MANAGEMENT_SYSTEM;

import java.util.Random;

public class Consultant implements Runnable{
    // This is the Consumer class
    private final String name;
    private final String speciality;
    private final String shift;
    private final PatientQueue queue;
    private final PerformanceMetrics metrics;  // Tracking metrics
    private volatile boolean running = true;   // Volatile for thread visibility
    private final Random random = new Random();
    private int patientTreated = 0;

    public Consultant(String name, String speciality, PatientQueue queue, String shift, PerformanceMetrics metrics) {
        this.name = name;
        this.speciality = speciality;
        this.shift = shift;
        this.queue = queue;
        this.metrics = metrics;
    }

    @Override
    public void run(){
        System.out.println("[CONSULTANT] " + name + " (" + speciality + ") started " + shift + " shift");
        Patient patient = null;
        try{
            while(running){
                patient = queue.getPatient();
                if (patient != null){
                    patient.setTreatmentStartTime();  // Recording the treatment start time

                    // Starting to treat the patient
                    long waitTime = patient.getWaitingTimeSeconds();
                    System.out.println();
                    System.out.println("[TREATING] " + name + " started treating " + patient + " (waited " + waitTime + "s / " + (waitTime / 60) + " mins)");

                    // Simulating the treatment time
                    int treatmentTime = 1000 + random.nextInt(2000);
                    Thread.sleep(treatmentTime);

                    //Recording the treatment end time
                    patient.setTreatmentEndTime();
                    patientTreated++;

                    // Recording the treatment in metrics
                    metrics.recordTreatment(patient);

                    // checking if the patient met NHS target
                    String targetStatus = patient.metNHSTarget() ? " Met 4h target" : "Breached 4h target";
                    System.out.println("[Done] " + name + " finished treating " + patient + " | " + targetStatus + " | Total treated: " + patientTreated);
                    System.out.println();
                    patient = null;
                }
            }
        } catch (InterruptedException e){
            if (patient != null){
                try{
                    // Re-queueing the patients so they aren't lost during the shift change
                    queue.addPatient(patient);
                    System.out.println("[HANDOVER] " + name + " returned " + patient + " to queue.");
                } catch (InterruptedException ex){
                    Thread.currentThread().interrupt();
                }
            }
            // Shift ended or Thread gets interrupted
            System.out.println("[CONSULTANT] " + name + " (" + shift + " shift) ended - " + "treated " + patientTreated + " patients");
            Thread.currentThread().interrupt();
        }
    }

    // Stoping the consultant thread (called at shift end)
    public void stop(){
        running = false;
    }
    // No. of patients treated
    public int getPatientTreated(){
        return patientTreated;
    }
    // Getting consultant's name
    public String getName(){
        return name;
    }
}
