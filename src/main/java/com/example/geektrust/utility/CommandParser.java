package com.example.geektrust.utility;

import com.example.geektrust.user.storage.UserGraph;
import com.example.geektrust.user.transaction.Transaction;


public class CommandParser {

    UserGraph userGraph = new UserGraph();
    Transaction transaction = new Transaction();
    String[] commandParam;

    public CommandParser() {
        this.transaction.setUserGraph(userGraph);
        this.userGraph.setTransaction(transaction);
    }


    public void parse(String command) {
        this.commandParam = command.split(" ");
        String commandType = commandParam[0];

        switch (commandType) {
            case "MOVE_IN":
                processMoveInCommand();
                break;
            case "MOVE_OUT":
                processMoveOutCommand();
                break;
            case "SPEND":
                processSpendCommand(command);
                break;
            case "DUES":
                processDueCommand();
                break;
            case "CLEAR_DUE":
                processClearDueCommand();
                break;
            default:
                Logger.log("COMMAND_NOT_SUPPORTED");
        }
    }

    private void processDueCommand() {
        String member = commandParam[1];
        if (this.isMemberExist(member)) {
            this.transaction.getDues(member);
        } else {
            Logger.memberNotFound();
        }
    }

    private void processClearDueCommand() {
        String owes = commandParam[1];
        String givenBy = commandParam[2];

        if (this.isMemberExist(owes) && isMemberExist(givenBy)) {
            this.transaction.clearDue(owes, givenBy, Integer.parseInt(commandParam[3]));
        } else {
            Logger.memberNotFound();
        }

    }

    private void processMoveInCommand() {
        this.userGraph.moveIn(commandParam[1]);
    }

    private void processMoveOutCommand() {
        String member = commandParam[1];
        if (this.isMemberExist(member)) {
            this.userGraph.moveOut(commandParam[1]);
        } else {
            Logger.memberNotFound();
        }
    }

    private void processSpendCommand(String command) {

        String[] spendParam = command.split(" ");
        int spendParamSize = spendParam.length;
        int amountToShare = Integer.parseInt(spendParam[1]) / (spendParamSize - 2);
        String givenByUser = spendParam[2];
        boolean isValidUser = userGraph.getCurrentMemebers().contains(givenByUser);

        for (int index = 3; index < spendParamSize; index++) {
            String owe = spendParam[index];
            if (!userGraph.getCurrentMemebers().contains(owe)) {
                isValidUser = false;
            } else {
                this.transaction.transactionSpend(spendParam[index], givenByUser, amountToShare);

            }
        }
        if (isValidUser) {
            Logger.success();
        } else {
            Logger.memberNotFound();
        }
    }

    private boolean isMemberExist(String member) {
        return userGraph.getCurrentMemebers().contains(member);
    }
}
