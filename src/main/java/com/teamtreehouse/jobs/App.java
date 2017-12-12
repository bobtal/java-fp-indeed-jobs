package com.teamtreehouse.jobs;

import com.teamtreehouse.jobs.model.Job;
import com.teamtreehouse.jobs.service.JobService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public class App {

    public static void main(String[] args) {
        JobService service = new JobService();
        boolean shouldRefresh = false;
        try {
            if (shouldRefresh) {
                service.refresh();
            }
            List<Job> jobs = service.loadJobs();
            System.out.printf("Total jobs:  %d %n %n", jobs.size());
            explore(jobs);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void explore(List<Job> jobs) {
        // Your amazing code below...
        getCaptionsStream(jobs).forEach(System.out::println);
    }

    private static boolean isJuniorJob(Job job) {
        String title = job.getTitle().toLowerCase();
        return title.contains("junior") || title.contains("jr");
    }

    // "Senior Dev", "Jr. Java Engineer", "Java Evangelist", "Junior Java Dev",
    // "Sr. Java Wizard Ninja", "Junior Java Wizard Ninja", "Full Stack Java Engineer"
    private static List<Job> getThreeJuniorJobsStream(List<Job> jobs) {
        return jobs.stream()
                .filter(App::isJuniorJob)
                .limit(3) //stateful short circuiting intermediate operation
                .collect(Collectors.toList());

        // Another option. This is an example of a method relying on a side effect
//    List<Job> juniorJobs = new ArrayList<>();
//    jobs.stream()
//            .filter(App::isJuniorJob)
//            .limit(3) //stateful short circuiting intermediate operation
//            .forEach(juniorJobs::add);
//            // this forEach requires something that's out of the stream (juniorJobs)
//            // although it works, it's bad form, because it could be solved
//            // without side effects
//    return juniorJobs;
    }

    private static List<Job> getThreeJuniorJobsInmperatively(List<Job> jobs) {
        List<Job> juniorJobs = new ArrayList<>();
        for (Job job : jobs) {
            if (isJuniorJob(job)) {
                juniorJobs.add(job);
                if (juniorJobs.size() >= 3) {
                    break;
                }
            }
        }
        return juniorJobs;
    }

    private static List<String> getCaptionsStream(List<Job> jobs) {
        return jobs.stream()
                .filter(App::isJuniorJob) // this is static method which accepts a Job param
                .map(Job::getCaption) // this is instance method which doesn't accept a param
                // syntax is the same, because in the case of the instance method
                // it's assumed that the instance on which the method is called
                // will be a parameter (the job object from the stream)
                // This is -Method reference (type) inference-
                .limit(3) //stateful short circuiting intermediate operation
                .collect(Collectors.toList());

    }

    private static List<String> getCaptionsImperatively(List<Job> jobs) {
        List<String> captions = new ArrayList<>();
        for (Job job : jobs) {
            if (isJuniorJob(job)) {
                captions.add(job.getCaption());
                if (captions.size() >= 3) {
                    break;
                }
            }
        }
        return captions;
    }

    private static void printPortlandJobsImperatively(List<Job> jobs) {
        for (Job job : jobs) {
            if (job.getState().equals("OR") && job.getCity().equals("Portland")) {
                System.out.println(job);
            }
        }
    }

    private static void printPortlandJobsStream(List<Job> jobs) {
        jobs.stream()
                .filter(job -> job.getState().equals("OR"))
                .filter(job -> job.getCity().equals("Portland"))
                .forEach(System.out::println);
    }
}
