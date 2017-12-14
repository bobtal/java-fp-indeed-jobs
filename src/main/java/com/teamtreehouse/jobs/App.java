package com.teamtreehouse.jobs;

import com.teamtreehouse.jobs.model.Job;
import com.teamtreehouse.jobs.service.JobService;

import java.io.IOException;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

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
        Job firstOne = jobs.get(0);
        System.out.println("First job: " + firstOne);
        Predicate<Job> caJobChecker = job -> job.getState().equals("CA");

        Job caJob = jobs.stream()
//                .filter(job -> job.getState().equals("CA"))
                .filter(caJobChecker)
                .findFirst()
                .orElseThrow(NullPointerException::new);

        emailIfMatches(firstOne, caJobChecker);
//        emailIfMatches(caJob, caJobChecker);
        // let's modify the call above to actually check for jobs in CA
        // which are Junior level jobs by just adding
        // "and" to our caJobChecker predicate.
        // Note: isJuniorJob isn't a predicate function, it just matches the
        // signature of the predicate function (accepts an object returns a boolean)
        // which is why we are able to use it this way.
        emailIfMatches(caJob, caJobChecker.and(App::isJuniorJob));

    }

    // method emailIfMatches is a Higher Order Function, because it accepts a
    // function as a parameter
    // we can pass any predicate to this method without ever touching the method
    // implementation and it will check whatever we want to check...just WOW!!!
    public static void emailIfMatches(Job job, Predicate<Job> checker) {
        if (checker.test(job)) {
            System.out.println("I am sending an email about " + job);
        }
    }

    private static void displayCompaniesMenuImperatively(List<String> companies) {
        for (int i = 0; i < 20; i++) {
            System.out.printf("%d. %s %n", i + 1, companies.get(i));
        }
    }

    // declaratively
    private static void displayCompaniesMenuUsingRange(List<String> companies) {
        IntStream.rangeClosed(1, 20)
                .mapToObj(i -> String.format("%d. %s", i, companies.get(i - 1)))
                .forEach(System.out::println);
    }

    private static Optional<Job> luckySearchJob(List<Job> jobs, String searchTerm) {
        return jobs.stream()
                    .filter(job -> job.getTitle().contains(searchTerm))
                    .findFirst();
    }

    public static Map<String, Long> getSnippetWordCountsStream(List<Job> jobs) {

        return jobs.stream()
                .map(Job::getSnippet) // same as map(job -> job.getSnippet())
                // replacing a Job object with a String (the snippet)

                .map(snippet -> snippet.split("\\W+"))
                // replacing a snippet with an array of words

                .flatMap(Stream::of) // same as words -> Stream.of(words)
                // ^^ this will make the array words into a stream (flattening)
                // so we don't get multiple "dimensions" in the initial stream so to speak

                .filter(word -> word.length() > 0)
                .map(String::toLowerCase) // same as word -> word.toLowerCase()
                .collect(Collectors.groupingBy(
                        Function.identity(), // instead of word -> word
                        Collectors.counting()
                ));
    }

    public static Map<String, Long> getSnippetWordCountsImperatively(List<Job> jobs) {

        Map<String, Long> wordCounts = new HashMap<>();

        for (Job job : jobs) {
            String[] words = job.getSnippet().split("\\W+");
            for (String word : words) {
                if (word.length() == 0) {
                    continue;
                }
                String lWord = word.toLowerCase();
                Long count = wordCounts.get(lWord);
                if (count == null) {
                    count = 0L;
                }
                wordCounts.put(lWord, ++count);
            }
        }
        return wordCounts;
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
