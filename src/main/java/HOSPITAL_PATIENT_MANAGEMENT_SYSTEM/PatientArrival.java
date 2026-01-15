package HOSPITAL_PATIENT_MANAGEMENT_SYSTEM;

import java.util.Random;

public class PatientArrival implements Runnable{
    // This is the Producer class
    private final PatientQueue paediatricQueue;
    private final PatientQueue surgeryQueue;
    private final PatientQueue cardioQueue;

    private final String[] specialities = {"Paediatrician", "Surgeon", "Cardiologist"};
    private final PerformanceMetrics metrics;  // tracks the metrics
    private volatile boolean running = true;
    private final Random random = new Random();
    private int patientIdCounter = 1;
    private final int totalPatientsToGenerate;

    public PatientArrival(PatientQueue paediatricQueue, PatientQueue surgeryQueue, PatientQueue cardioQueue, int totalPatientsToGenerate, PerformanceMetrics metrics) {
        this.paediatricQueue = paediatricQueue;
        this.surgeryQueue = surgeryQueue;
        this.cardioQueue = cardioQueue;
        this.metrics = metrics;
        this.totalPatientsToGenerate = totalPatientsToGenerate;
    }

    @Override
    public void run(){
        System.out.println("\n[PRODUCER] Patient arrival thread started simulating continuous arrivals");
        try {
            while(running && patientIdCounter <= totalPatientsToGenerate){
                // Generating new patients with random speciality
                String randomSpeciality = specialities[random.nextInt(specialities.length)];
                Patient patient = new Patient(patientIdCounter, "Patient-" + patientIdCounter, randomSpeciality);
                patientIdCounter++;

                System.out.println(">>> [ARRIVAL] " + patient + " arrived - needs a "  + randomSpeciality);

                // recording the arrival in metrics
                metrics.recordArrival(randomSpeciality);

                // Adding to appropriate queues
                if (randomSpeciality.equals("Paediatrician")){
                    paediatricQueue.addPatient(patient);
                } else if (randomSpeciality.equals("Surgeon")) {
                    surgeryQueue.addPatient(patient);
                } else if (randomSpeciality.equals("Cardiologist")) {
                    cardioQueue.addPatient(patient);
                }


                // Adding random delays between arrivals (simulates unpredictable patient arrival times)
                int delay = 500 + random.nextInt(1500);
                Thread.sleep(delay);
            }
            System.out.println("[PRODUCER] Patient arrival generation completed (" + (patientIdCounter - 1) + " patients generated");

        } catch (InterruptedException e){
            Thread.currentThread().interrupt();
        }
    }

    // stopping the producer thread gracefully
    public void stop(){
        running = false;
    }
}
