package com.company;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Random;

public class Main {

    public static void main(String[] args) {
        //initializes test problem
        int number_of_jobs_time1 = 55;
        int number_of_jobs_time2 = 30;
        int number_of_machines = 20;
        int time1_low = 10;
        int time1_high = 990;
        int time2_low = 100;
        int time2_high = 200;
        Create_Problem p = new Create_Problem(number_of_jobs_time1, number_of_jobs_time2, number_of_machines, time1_low, time1_high, time2_low, time2_high);
        p.print_jobs_list();
        //Initializer1 i = new Initializer1(p.getJobsList(),5,p.getNumberOfMachines());
        //i.print_initial_pop();
        // for some reason enabeling the line 20 does mess with line 19 and i start to get some errors which i wasnt able to debug yet

    }

    //generates a problem with given parameters as object
    public static class Create_Problem {

        Random r = new Random();
        private int number_of_jobs_time1;
        private int number_of_jobs_time2;
        private int number_of_machines;
        private int time1_low;
        private int time1_high;
        private int time2_low;
        private int time2_high;
        private ArrayList<Job> jobs_list = new ArrayList<>();
        private int i = 0;


        public Create_Problem(int number_of_jobs_time1, int number_of_jobs_time2, int number_of_machines, int time1_low, int time1_high, int time2_low, int time2_high)

        {

            this.number_of_jobs_time1 = number_of_jobs_time1;
            this.number_of_jobs_time2 = number_of_jobs_time2;
            this.number_of_machines = number_of_machines;
            this.time1_low = time1_low;
            this.time1_high = time1_high;
            this.time2_low = time2_low;
            this.time2_high = time2_high;

            //generates random jobs according to first time limits and adds them to the jobs_list
            for (int y = 0; y < this.number_of_jobs_time1; y++) {
                jobs_list.add(new Job(this.i, r.nextInt(this.time1_high) + this.time1_low));
                this.i++;
            }
            //generates random jobs according to second time limits and adds them to the jobs_list
            for (int x = this.i; x < this.number_of_jobs_time2 + this.number_of_jobs_time1; x++) {
                jobs_list.add(new Job(this.i, r.nextInt(this.time2_high) + this.time2_low));
                this.i++;
            }

        }

        public ArrayList<Job> getJobsList() {
            return this.jobs_list;
        }

        public int getNumberOfMachines() {
            return this.number_of_machines;
        }

        public void print_jobs_list() {
            for (Job j : this.jobs_list) {
                System.out.println(j);
            }
        }

    }

    //Job as object with duration and a number to enable comparing different assignments of jobs in chromosomes
    public static class Job {

        private int job_number;
        private int job_duration;

        public Job(int job_number, int job_duration) {

            this.job_number = job_number;
            this.job_duration = job_duration;
        }

        public int getJob_number() {
            return this.job_number;
        }

        public int getJob_duration() {
            return this.job_duration;
        }

        public String toString() {
            return "Jobnumber: " + getJob_number() + ", Jobduration: " + getJob_duration();
        }

    }
    //Machine class as an arraylist containing jobs
    public static class Machine {
        private ArrayList<Job> jobs_list_machine;
        private int machine_number;

        public Machine(ArrayList<Job> jobs_list_machine, int machine_number) {

            this.jobs_list_machine = jobs_list_machine;
            this.machine_number = machine_number;
        }

        public ArrayList<Job> getJobs_list_machine() {
            return this.jobs_list_machine;
        }

        public int getMachine_number() {
            return this.machine_number;
        }

        public void addJob(Job job) {
            this.jobs_list_machine.add(job);
        }
        public void print_machine() {
            for (Job j : this.jobs_list_machine) {
                System.out.println(j);
            }
        }
    }
    //Chromosome class as arraylist containing machines which selmselfs contain their jobs
    public static class Chromosome {
        private ArrayList<Machine> machines_list;
        private int fitness;

        public Chromosome(ArrayList<Machine> machines_list, int fitness) {
            this.machines_list = machines_list;

        }

        public int getFitness() {
            return this.fitness;
        }
        public void print_chromosome() {
            for (Machine m : machines_list) {
                m.print_machine();
            }
        }
    }
    //Initializer that gets a jobslist generated by the create_problem together with population size and number of machines
    //generates pop_size-many chromosomes with feasable random solutions and collects them in the arraylist initial_pop
    public static class Initializer1 {
        private int pop_size;
        private int machine_number;
        private ArrayList<Job> jobs_list;
        private ArrayList<Chromosome> initial_pop;


        public Initializer1(ArrayList<Job> jobs_list, int pop_size, int machine_number) {
            for (int i = 0; i < pop_size; i++) {
                ArrayList<Machine> create_chromosome = new ArrayList<>();
                int j = 0; //number to identify machines
                int y = 0;
                int fitness = 0;
                Random r = new Random();
                do {
                    //duration adds up the durations of jobs in one machine to evaluate fitness
                    int duration = 0;
                    ArrayList<Job> machine = new ArrayList<>();
                    //loop that creates a machine with a random amount(boundery is length of list) of random jobs from jobs_list
                    for (y = 0; y < r.nextInt(1) + jobs_list.size()-1; y++) {
                        //safety stop so the last remaining jobs will be filled into the last remaining machine
                        if (jobs_list.size() > 1) {
                            //transfers job from jobs_list into helper-variable
                            Job k = jobs_list.remove(r.nextInt(1) + jobs_list.size()-1);
                            //adds up durations
                            duration = duration+k.getJob_duration();
                            //adds the job to the current machine
                            machine.add(k);
                        }
                    }
                    //checks for slowest machine
                    if (duration > fitness) {
                        fitness = duration;
                    }
                    //adds the machine to the new chromosome_list and keeps track of number of machines used already
                    Machine m = new Machine(machine, j);
                    create_chromosome.add(m);
                    j++;
                    machine_number--;
                //safety stop so the last remaining jobs will be filled into the last remaining machine
                } while (!jobs_list.isEmpty() && machine_number > 1);
                //fills last machine and adds it to chromosome_list
                ArrayList<Job> machine = new ArrayList<>();
                for (int x = 0; x < jobs_list.size(); x++) {
                    machine.add(jobs_list.remove(x));
                }
                Machine m = new Machine(machine, j);
                create_chromosome.add(m);
                //creates chromosome out of chromosome_list and fitness
                Chromosome c = new Chromosome(create_chromosome, fitness);
                //adds new chromosome to the initial population
                initial_pop.add(c);
            }


        }
        public ArrayList<Chromosome> getInitial_pop(){return initial_pop;}
        public void print_initial_pop() {
            for (Chromosome c : initial_pop) {
                 c.print_chromosome();
            }
        }
    }
}
