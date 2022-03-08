package com.example.geektrust.user.transaction;

import com.example.geektrust.user.storage.UserGraph;
import com.example.geektrust.utility.Logger;

import java.util.*;

public class Transaction {

    static Map<String, Integer> balanceTransactionResult = new LinkedHashMap<>();
    private static final int REBALANCE_FACTOR = 3;

    public static void clearDue(String owes, String givenBy, int amount) {
        Map<String,String> clearDue = new HashMap<>();
        clearDue.put(owes, givenBy);
        if (amount > UserGraph.getUsersGraph().get(clearDue)) {
            Logger.incorrectPayment();
        } else {
            UserGraph.getUsersGraph().put(clearDue, UserGraph.getUsersGraph().get(clearDue) - amount);
            Logger.log(UserGraph.getUsersGraph().get(clearDue));
        }
    }

    public static boolean isEligibleForClearance(String member) {
        boolean isCleared = false;
        for (Map.Entry<Map<String, String>, Integer> set : UserGraph.getUsersGraph().entrySet()) {
            for (Map.Entry<String, String> user : set.getKey().entrySet()) {

                Map<String,String> map = new HashMap<>();
                map.put(user.getKey(), user.getValue());
                Map<String,String> mapgive = new HashMap<>();
                mapgive.put(user.getValue(), user.getKey());

                if ((user.getKey().equals(member) || user.getValue().equals(member))) {
                    if (UserGraph.getUsersGraph().get(map) == 0 && UserGraph.getUsersGraph().get(mapgive) == 0) {
                        isCleared = true;
                    } else return false;
                }
            }
        }
        return isCleared;
    }

    public static void balanceTransaction() {
        //total received - total sent
        for (Map.Entry<Map<String, String>, Integer> set : UserGraph.getUsersGraph().entrySet()) {
            for (Map.Entry<String, String> user : set.getKey().entrySet()) {

                Map<String,String> map = new HashMap<>();
                map.put(user.getKey(), user.getValue());
                Map<String,String> mapgive = new HashMap<>();
                mapgive.put(user.getValue(), user.getKey());

                int result = balanceTransactionResult.getOrDefault(user.getKey(), 0)
                        + UserGraph.getUsersGraph().getOrDefault(map, 0) - UserGraph.getUsersGraph().getOrDefault(mapgive, 0);

                balanceTransactionResult.put(user.getKey(), result);
            }
        }
    }

    // settle out , transaction for Nth/3rd  person to zero
    public static void settleTransaction() {

        LinkedList<String> getMinTransactions = getMinTransactions();

        for (int start = 0; start < 1; start++) {

            Map<String, String> adjustMap = new HashMap<>();
            adjustMap.put(getMinTransactions.get(start),
                    getMinTransactions.get(start + 1));
            UserGraph.getUsersGraph().put(adjustMap, 0);
            adjustMap = new HashMap<>();
            adjustMap.put(getMinTransactions.get(start + 1), getMinTransactions.get(start));
            UserGraph.getUsersGraph().put(adjustMap, 0);
        }
        adjustBalance(getMinTransactions);
    }

    public static void adjustBalance(List<String> getMinTransactions) {
        int size = getMinTransactions.size();
        Map<Map<String, String>, Integer> adjustUserTransaction = UserGraph.getUsersGraph();

        for (int start = 0; start < size - 1; start++) {

            for (int index = start; index < size - 1; index++) {
                Map<String, String> adjustMap = new HashMap<>();
                adjustMap.put(getMinTransactions.get(start),
                        getMinTransactions.get(index + 1));
                if (adjustUserTransaction.get(adjustMap) != 0) {
                    adjustUserTransaction.put(adjustMap, balanceTransactionResult.get(getMinTransactions.get(start)));
                }

                adjustMap = new HashMap<>();
                adjustMap.put(getMinTransactions.get(index + 1), getMinTransactions.get(start));
                if (adjustUserTransaction.get(adjustMap) != 0) {
                    adjustUserTransaction.put(adjustMap, balanceTransactionResult.get(getMinTransactions.get(start)));
                }
            }
        }
        UserGraph.setUsersGraph(adjustUserTransaction);
    }

    private static LinkedList<String> getMinTransactions() {

        for (Map.Entry<String, Integer> set : balanceTransactionResult.entrySet()) {
            int positive = balanceTransactionResult.get(set.getKey()) < 0 ? balanceTransactionResult.get(set.getKey()) * -1 : balanceTransactionResult.get(set.getKey());
            balanceTransactionResult.put(set.getKey(), positive);
        }

        LinkedList<String> sortedTransaction = new LinkedList<>();

        balanceTransactionResult.entrySet().stream()
                .sorted(Map.Entry.comparingByValue())
                .forEach(e -> sortedTransaction.add(String.valueOf(e.getKey())));

        return sortedTransaction;
    }


    public void transactionSpend(String owes, String givenBy, int amount) {
        if (UserGraph.getCurrentMemebers().contains(owes) && UserGraph.getCurrentMemebers().contains(givenBy)) {
            Map<String, String> user = new HashMap<>();
            user.put(owes, givenBy);
            UserGraph.getUsersGraph().put(user, amount);

            //handling case if link is already available b/w owe & giver - adjust balance
            Map<String,String> reverseUser = new HashMap<>();
            reverseUser.put(givenBy, owes);
            int existingAmount = UserGraph.getUsersGraph().getOrDefault(user, 0);
            int reverseAmount = UserGraph.getUsersGraph().getOrDefault(reverseUser, 0);
            if (reverseAmount != 0 && reverseAmount < existingAmount) {

                amount = existingAmount - reverseAmount;
                UserGraph.getUsersGraph().put(user, amount);
                UserGraph.getUsersGraph().put(reverseUser, 0);
            } else if (reverseAmount != 0 && reverseAmount > existingAmount) {
                amount = reverseAmount - existingAmount;
                UserGraph.getUsersGraph().put(reverseUser, amount);
                UserGraph.getUsersGraph().put(user, 0);
            }

            if (isRebalanceRequire()) {
                Transaction.balanceTransaction();
                Transaction.settleTransaction();
            }

        }
    }

    public boolean isRebalanceRequire() {
        boolean isEbligbleForReBalance = false;
        int rebalanceCount = 0;
        for (Map.Entry<Map<String, String>, Integer> set : UserGraph.getUsersGraph().entrySet()) {
            for (Map.Entry<String, String> user : set.getKey().entrySet()) {
                Map<String,String> map = new HashMap<>();
                map.put(user.getKey(), user.getValue());
                if (UserGraph.getUsersGraph().get(map) > 0) {
                    rebalanceCount++;
                }
            }
        }
        if (rebalanceCount == REBALANCE_FACTOR ) {
            isEbligbleForReBalance = true;
            return isEbligbleForReBalance;
        }
        return isEbligbleForReBalance;
    }

    //maintain order while returning result . order by amount ,if amount same then order by user name .
    public void getDues(String member) {
        Map<String, Integer> result = new HashMap<>();
        for (Map.Entry<Map<String, String>, Integer> set : UserGraph.getUsersGraph().entrySet()) {
            for (Map.Entry<String, String> user : set.getKey().entrySet()) {

                Map<String,String> map = new HashMap<>();
                map.put(user.getKey(), user.getValue());


                if ((user.getKey().equals(member)) && !user.getKey().equals(user.getValue())) {

                    result.putIfAbsent(user.getValue(), UserGraph.getUsersGraph().get(map));
                }
            }
        }
        result.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .forEach(user -> Logger.log(user.getKey() + " " + user.getValue()));

    }
}
