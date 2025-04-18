package org.project;

public class GitHubActivityRunner {
    public static void main(String[] args) {
        try{
            String userArgs = args[0];
            new GettingUserActivity().UsingAndGettingData(userArgs);
        }catch (Exception e){
            System.out.println("The username cannot be blank");
        }
        }
    }
