package org.project;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class GettingUserActivity {
    /**
     * Fetches recent GitHub events for the specified username using the GitHub API.
     * Parses the JSON response into an array and displays the event types.
     * Returns the first event in the response as a JSONObject.
     *
     * @param username GitHub username whose public events are to be fetched.
     * @return The first event as a JSONObject, or null if the request fails or an error occurs.
     */

    public JSONObject UsingAndGettingData(String username) {
        String api = "https://api.github.com/users/" + username + "/events";
        try {
            HttpURLConnection apiConnection = fetchApiResponse(api);
            if (apiConnection.getResponseCode() != 200) {
                System.out.println("Could not connect to the API");
                return null;
            } else {
                String jsonResponse = readApiResponse(apiConnection);
                JSONParser jsonParser = new JSONParser();

                JSONArray eventsArray = (JSONArray) jsonParser.parse(jsonResponse);
                displayTypeOfEvents(eventsArray);

                return (JSONObject) eventsArray.get(0);
            }
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
        return null;
    }
    /**
     * Opens an HTTP connection to the given URL using the GET method.
     * This method does not initiate the connection or check availability;
     * it only prepares the connection for further use.
     *
     * @param urlString The URL to connect to.
     * @return A configured HttpURLConnection object, or null if an error occurs.
     */

    private HttpURLConnection fetchApiResponse(String urlString){
        try{
            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            return conn;

        }
        catch (Exception e){
            System.out.println(e);
        }
        return null;

    }
    /**
     * Reads the response body from the given HttpURLConnection.
     * Returns the response content as a single JSON-formatted string.
     *
     * @param apiConnection The established HttpURLConnection from which to read the response.
     * @return The full API response as a String, or null if an error occurs.
     */

    private String readApiResponse(HttpURLConnection apiConnection){
        try {
            StringBuilder jsonGathered = new StringBuilder();
            Scanner scanner = new Scanner(apiConnection.getInputStream());

            while (scanner.hasNext()){
                jsonGathered.append(scanner.nextLine());
            }
            scanner.close();
            return jsonGathered.toString();

        }catch (Exception e){
            System.out.println(e);
        }
    return null;
    }
    public String capitalize(String str) {
        if (str == null || str.isEmpty()) return str;
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }
    /**
     * Displays details about different types of GitHub events in the command-line interface.
     * For each event in the array, it prints relevant information such as:
     * - Number of commits pushed per repository
     * - Issue titles and actions
     * - Pull request titles and actions
     * - Forks, stars, creations, deletions, and other event types
     *
     * @param eventsArray JSONArray of GitHub events to be processed and displayed.
     */

    private void displayTypeOfEvents(JSONArray eventsArray){

        Map<String, Integer> commitsByRepo = new HashMap<>();

        for (Object obj : eventsArray) {
            JSONObject event = (JSONObject) obj;
            String eventType = (String) event.get("type");
            JSONObject payload = (JSONObject) event.get("payload");
            JSONObject repo = (JSONObject) event.get("repo");

            String repoName = (String) repo.get("name");

            switch (eventType) {
                case "PushEvent":
                    JSONArray commits = (JSONArray) payload.get("commits");
                    int commitCount = commits != null ? commits.size() : 0;
                    commitsByRepo.put(repoName, commitsByRepo.getOrDefault(repoName, 0) + commitCount);
                    break;

                case "IssuesEvent":
                    String issueAction = (String) payload.get("action");
                    JSONObject issue = (JSONObject) payload.get("issue");
                    String issueTitle = (String) issue.get("title");
                    System.out.println("- " + capitalize(issueAction) + " an issue in " + repoName + ": \"" + issueTitle + "\"");
                    break;

                case "PullRequestEvent":
                    String prAction = (String) payload.get("action");
                    JSONObject pr = (JSONObject) payload.get("pull_request");
                    String prTitle = (String) pr.get("title");
                    System.out.println("- " + capitalize(prAction) + " a pull request in " + repoName + ": \"" + prTitle + "\"");
                    break;

                case "ForkEvent":
                    JSONObject forkee = (JSONObject) payload.get("forkee");
                    String forkFullName = (String) forkee.get("full_name");
                    System.out.println("- Forked " + repoName + " to " + forkFullName);
                    break;

                case "WatchEvent":
                    System.out.println("- Starred " + repoName);
                    break;

                case "CreateEvent":
                    String refType = (String) payload.get("ref_type");
                    String ref = (String) payload.get("ref");
                    if (ref != null) {
                        System.out.println("- Created " + refType + " '" + ref + "' in " + repoName);
                    } else {
                        System.out.println("- Created " + refType + " in " + repoName);
                    }
                    break;

                case "DeleteEvent":
                    String deletedRefType = (String) payload.get("ref_type");
                    String deletedRef = (String) payload.get("ref");
                    System.out.println("- Deleted " + deletedRefType + " '" + deletedRef + "' in " + repoName);
                    break;

                default:

                    System.out.println("- Performed " + eventType + " on " + repoName);
                    break;
            }

        }
        for (Map.Entry<String, Integer> entry : commitsByRepo.entrySet()) {
            System.out.println("- Pushed " + entry.getValue() + " commit(s) to " + entry.getKey());
        }


    }

}
