package edu.unlv.cs.evol.integration;



import com.intellij.openapi.application.ApplicationStarter;
import edu.unlv.cs.evol.integration.database.DatabaseUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.javalite.activejdbc.Base;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.*;

/*
 * Evaluates RePatch and IntelliMerge. If in replication mode, The pipeline will attempt to replicate IntelliMerge's
 * results. If in comparison mode, the pipeline will run a comparison on RePatch, IntelliMerge, and Git. If in
 * stats mode, it will run RefMiner and collect merge scenarios that contain refactorings.
 */
public class IntegrationPipeline implements ApplicationStarter {

    @Override
    public String getCommandName() {
        return "integration";
    }

    @Override
    public void main(@NotNull List<String> args) {
        try {
            String mode = args.get(1);
            if(mode.equals("replication")) {
                DatabaseUtils.createDatabase(false);
                String path = System.getProperty("user.home") + "/" + args.get(2);
                //startIntelliMergeReplication(path);
            }
            else if(mode.equals("integration")) {
                DatabaseUtils.createDatabase(true);
                String path = System.getProperty("user.home") +"/" + args.get(2);
                String projectName = args.get(3);
                startEvaluation(path, projectName);
            }
        } catch(Throwable e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
        System.exit(0);
    }

    /*
     * Start the IntelliMerge replication.
     */
//    private void startIntelliMergeReplication(String path) {
//        try {
//            Base.open("com.mysql.jdbc.Driver", "jdbc:mysql://localhost/intelliMerge_replication?serverTimezone=UTC",
//                    "username", "password");
//            IntelliMergeReplication.runIntelliMergeReplication(path);
//            Base.close();
//        } catch (Throwable e) {
//            e.printStackTrace();
//        }
//
//    }

    /*
     * Start the head to head comparison between RePatch and IntelliMerge.
     * start integration
     * change db frame refactoring_aware_integration
     */
    private void startEvaluation(String path, String evaluationProject) {
        try {
            Base.open("com.mysql.jdbc.Driver", "jdbc:mysql://localhost/refactoring_aware_integration_repatch?serverTimezone=UTC",
                    "root", "student");
            RePatchIntegration evaluation = new RePatchIntegration();
            evaluation.runComparison(path, evaluationProject);
            Base.close();
        } catch (Throwable e) {
            e.printStackTrace();
        }

    }




}