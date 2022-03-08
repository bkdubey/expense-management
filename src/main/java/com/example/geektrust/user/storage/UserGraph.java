package com.example.geektrust.user.storage;

import com.example.geektrust.user.transaction.Transaction;
import com.example.geektrust.utility.Logger;

import java.util.*;

public class UserGraph implements Resident {

    private static final int HOUSE_MAX_CAPACITY = 3;
    private static List<String> currentMemebers  = new LinkedList<>();
    private static Map<Map<String, String>, Integer> usersGraph = new HashMap<>();


    public static Map<Map<String, String>, Integer> getUsersGraph() {
        return usersGraph;
    }

    public static void setUsersGraph(Map<Map<String, String>, Integer> usersGraph) {
        UserGraph.usersGraph = usersGraph;
    }

    public static int getHouseMaxCapacity() {
        return HOUSE_MAX_CAPACITY;
    }

    public static List<String> getCurrentMemebers() {
        return currentMemebers;
    }

    public static void printUserGraph() {

        for (Map.Entry<Map<String, String>, Integer> set : usersGraph.entrySet()) {
            for (Map.Entry<String, String> user : set.getKey().entrySet()) {
                Map<String,String> map = new HashMap<>();
                map.put(user.getKey(), user.getValue());
                Logger.log(user.getKey() + "," + user.getValue() + " = " + usersGraph.get(map));
            }
            Logger.log("");
        }
        Logger.log("size of usergraph -- " + usersGraph.size());
    }


    @Override
    public void moveIn(String memberToAdd) {
        if (currentMemebers.size() >= HOUSE_MAX_CAPACITY) {
            Logger.houseful();
        } else {
            currentMemebers.add(memberToAdd);
            initGraph(currentMemebers);
            Logger.success();
        }
    }

    @Override
    public void moveOut(String memberToExit) {
        if (currentMemebers.contains(memberToExit)) {
            if (Transaction.isEligibleForClearance(memberToExit)) {
                currentMemebers.remove(memberToExit);
                reInitGraph(memberToExit);
                Logger.success();
            } else {
                Logger.failure();
            }
        } else {
            Logger.memberNotFound();
        }


    }

    private void initGraph(List<String> userList) {
        for (String user : userList) {
            for (String user2 : userList) {
                Map<String, String> temp = new HashMap<>();
                temp.put(user, user2);
                usersGraph.putIfAbsent(temp, 0);
            }
        }
    }

    private void reInitGraph(String member) {

        for (Iterator<Map.Entry<Map<String, String>, Integer>> it = usersGraph.entrySet().iterator(); it.hasNext(); ) {
            Map.Entry<Map<String, String>, Integer> set = it.next();
            for (Iterator<Map.Entry<String, String>> iter = set.getKey().entrySet().iterator(); iter.hasNext(); ) {
                Map.Entry<String, String> user = iter.next();
                Map<String,String> map = new HashMap<>();
                map.put(user.getKey(), user.getValue());
                if (user.getKey().contains(member) || user.getValue().contains(member)) {
                    it.remove();
                }
            }

        }

    }
}
