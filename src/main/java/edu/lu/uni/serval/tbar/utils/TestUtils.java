package edu.lu.uni.serval.tbar.utils;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestUtils {

    private static Logger log = LoggerFactory.getLogger(TestUtils.class);

    public static int getFailTestNumInProject(String projectName, String defects4jPath, List<String> failedTests){
        final String project = projectName.substring(projectName.lastIndexOf('/')+1);
        final String database = project.substring(0, project.indexOf('_'));

        if(!database.equals("VUL4J")) {
            final String testResult = getDefects4jResult(projectName, defects4jPath, "test");
            return getFailTestNumDefects4j(testResult, failedTests);
        } else {
            final String testResult = getVul4jResult(projectName, defects4jPath, "test");
            try {
                return getFailTestNumVul4j(project, testResult, failedTests);
            } catch (JSONException e) {
                log.info(e.getMessage());
            }
        }
        return Integer.MAX_VALUE;
    }

    public static int getFailTestNumDefects4j(String testResult, List<String> failedTests) {
        if (testResult.equals("")){//error occurs in run
            return Integer.MAX_VALUE;
        }
        if (!testResult.contains("Failing tests:")){
            return Integer.MAX_VALUE;
        }
        int errorNum = 0;
        String[] lines = testResult.trim().split("\n");
        for (String lineString: lines){
            if (lineString.startsWith("Failing tests:")){
                errorNum =  Integer.valueOf(lineString.split(":")[1].trim());
                if (errorNum == 0) break;
            } else if (lineString.startsWith("Running ")) {
                break;
            } else {
                failedTests.add(lineString.trim());
            }
        }
        return errorNum;
    }

    public static int getFailTestNumVul4j(String projectName, String testResult, List<String> failedTests) throws JSONException {
        if (testResult.equals("")){//error occurs in run
            return Integer.MAX_VALUE;
        }

        StringBuilder fileContent = new StringBuilder("Failing tests: ");
        final JSONObject jsonObject = new JSONObject(testResult);
        final JSONObject tests = jsonObject.getJSONObject("tests");
        final JSONArray failures = tests.getJSONArray("failures");
        int errorNum = failures.length();
        fileContent.append(errorNum).append("\n");

        for (int i = 0; i < errorNum; i++){
            final JSONObject failure = failures.getJSONObject(i);
            final String failedTestClass = failure.getString("test_class").trim() + "::" + failure.getString("test_method");
            fileContent.append("  - ").append(failedTestClass).append("\n");
            failedTests.add(failedTestClass.trim());
        }
        if (errorNum == 0) {
            return Integer.MAX_VALUE;
        } else {
            FileHelper.outputToFile("FailedTestCases/"+projectName+".txt", fileContent, false);
            return errorNum;
        }
    }

    public static int compileProject(String projectName, String defects4jPath) {
        String compileResults;
        if (Objects.equals("VUL4J", projectName.split("_")[0].substring(projectName.split("_")[0].length() - 5))) {
            compileResults = getVul4jResult(projectName, defects4jPath, "compile");
            compileResults = (compileResults.isEmpty()) ? "Compiling...OK\nCompiled...OK" : compileResults;
        } else {
            compileResults = getDefects4jResult(projectName, defects4jPath, "compile");
        }
        String[] lines = compileResults.split("\n");
        if (lines.length != 2) return 1;
        for (String lineString: lines){
            if (!lineString.endsWith("OK")) return 1;
        }
        return 0;
    }

    private static String getDefects4jResult(String projectName, String defects4jPath, String cmdType) {
        try {
            String buggyProject = projectName.substring(projectName.lastIndexOf("/") + 1);
            //which java\njava -version\n
            String result = ShellUtils.shellRun(Arrays.asList("cd " + projectName + "\n", defects4jPath + "framework/bin/defects4j " + cmdType + "\n"), buggyProject, cmdType.equals("test") ? 2 : 1);//"defects4j " + cmdType + "\n"));//
            return result.trim();
        } catch (IOException e){
            e.printStackTrace();
            return "";
        }
    }

    private static String getVul4jResult(String projectName, String vul4jPath, String cmdType) {
        try {
            String buggyProject = projectName.substring(projectName.lastIndexOf("/") + 1);
            //which java\njava -version\n
            String result = ShellUtils.shellRun(Arrays.asList("cd " + projectName + "\n", "python " + vul4jPath + "vul4j/main.py " + cmdType + " -d " + projectName + "\n"), buggyProject, cmdType.equals("test") ? 2 : 1);
            return result.trim();
        } catch (IOException e){
            e.printStackTrace();
            return "";
        }
    }
    public static String recoverWithGitCmd(String projectName) {
        try {
            String buggyProject = projectName.substring(projectName.lastIndexOf("/") + 1);
            ShellUtils.shellRun(Arrays.asList("cd " + projectName + "\n", "git checkout -- ."), buggyProject, 1);
            return "";
        } catch (IOException e){
            return "Failed to recover.";
        }
    }

    public static String readPatch(String projectName) {
        try {
            String buggyProject = projectName.substring(projectName.lastIndexOf("/") + 1);
            return ShellUtils.shellRun(Arrays.asList("cd " + projectName + "\n", "git diff"), buggyProject, 1).trim();
        } catch (IOException e){
            return null;
        }
    }

    public static String checkout(String path, String projectName, int bugId, String databasePath) {
        try {
            String fullBuggyProjectName = projectName + "_" + bugId;
            String fullBuggyProjectPath = path + fullBuggyProjectName;
            String buggyProject = fullBuggyProjectPath.substring(fullBuggyProjectPath.lastIndexOf("/") + 1);
            if(!projectName.equals("VUL4J")) {
                return ShellUtils.shellRun(Arrays.asList("cd " + fullBuggyProjectPath + "\n", "git checkout -- ."), buggyProject, 1).trim();
            } else {
                String result = ShellUtils.shellRun(Arrays.asList(
                        "cd " + fullBuggyProjectPath + "\n", "python " + databasePath + "vul4j/main.py checkout --id "
                        + fullBuggyProjectName.replace('_', '-') + " -d /tmp/vul4j/VUL4J-10\n"),
                    buggyProject,
                    1);//"defects4j " + cmdType + "\n"));//
                return result.trim();
            }
        } catch (IOException e){
            return null;
        }
    }

}