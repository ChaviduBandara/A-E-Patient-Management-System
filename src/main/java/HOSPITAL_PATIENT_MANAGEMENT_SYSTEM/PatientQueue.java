package HOSPITAL_PATIENT_MANAGEMENT_SYSTEM;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class PatientQueue {
    private final String speciality;
    private final BlockingQueue<Patient> queue;

    public PatientQueue(String speciality) {
        this.speciality = speciality;
        this.queue = new LinkedBlockingQueue<>();
    }

    // Adding a patient to the queue (called by the producer)
    public void addPatient(Patient patient) throws InterruptedException{
        int expectedSize = queue.size() + 1;
        System.out.println("\t[QUEUE] Patient - " + patient.getPatientID() + " added to " + speciality + " queue. Queue size " + expectedSize);
        queue.put(patient);

    }

    // Getting next patient rom the queue (called by the consumer)
    public Patient getPatient() throws InterruptedException {
        return queue.take();  // Blocks if it is empty
    }

    // Checking if the queue is empty
    public boolean isEmpty(){
        return queue.isEmpty();
    }

    // getting queue size
    public int getSize(){
        return queue.size();
    }

    // speciality type
    public String getSpeciality(){
        return speciality;
    }
}
