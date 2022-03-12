package com.example.geektrust.user.transaction;

import com.example.geektrust.user.storage.UserGraph;
import com.example.geektrust.utility.Logger;

import java.util.*;

public class Transaction {

    private static final int REBALANCE_FACTOR = 3;
    static Map<String, Integer> balanceTransactionResult = new LinkedHashMap<>();

    public void setUserGraph(UserGraph userGraph) {
        this.userGraph = userGraph;
    }

    UserGraph userGraph;



    public Transaction() {
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

    // settle out , transaction for Nth/3rd  person to zero
    public void settleTransaction() {

        LinkedList<String> getMinTransactions = getMinTransactions();

        for (int start = 0; start < 1; start++) {

            Map<String, String> adjustMap = new HashMap<>();
            adjustMap.put(getMinTransactions.get(start),
                    getMinTransactions.get(start + 1));
            this.userGraph.getUsersGraph().put(adjustMap, 0);
            adjustMap = new HashMap<>();
            adjustMap.put(getMinTransactions.get(start + 1), getMinTransactions.get(start));
            this.userGraph.getUsersGraph().put(adjustMap, 0);
        }
        adjustBalance(getMinTransactions);
    }

    public void clearDue(String owes, String givenBy, int amount) {
        Map<String, String> clearDue = new HashMap<>();
        clearDue.put(owes, givenBy);
        if (amount > (Integer) this.userGraph.getUsersGraph().get(clearDue)) {
            Logger.incorrectPayment();
        } else {
            this.userGraph.getUsersGraph().put(clearDue, (Integer) this.userGraph.getUsersGraph().get(clearDue) - amount);
            Logger.log((Integer) this.userGraph.getUsersGraph().get(clearDue));
        }
    }

    public boolean isEligibleForClearance(String member) {
        boolean isCleared = false;

        for (Iterator<Map.Entry<Map<String, String>, Integer>> it = this.userGraph.getUsersGraph().entrySet().iterator(); it.hasNext(); ) {
            Map.Entry<Map<String, String>, Integer> set = it.next();
            for (Iterator<Map.Entry<String, String>> iter = set.getKey().entrySet().iterator(); iter.hasNext(); ) {
                Map.Entry<String, String> user = iter.next();

                Map<String, String> userPair = new HashMap<>();
                userPair.put(user.getKey(), user.getValue());

                Map<String, String> reversePair = new HashMap<>();
                reversePair.put(user.getValue(), user.getKey());

                if ((user.getKey().equals(member) || user.getValue().equals(member))) {
                    if ((Integer) this.userGraph.getUsersGraph().get(userPair) == 0 && (Integer) this.userGraph.getUsersGraph().get(reversePair) == 0) {
                        isCleared = true;
                    } else return false;
                }

            }

        }
        return isCleared;
    }


    public void balanceTransaction() {

        for (Iterator<Map.Entry<Map<String, String>, Integer>> it = this.userGraph.getUsersGraph().entrySet().iterator(); it.hasNext(); ) {
            Map.Entry<Map<String, String>, Integer> set = it.next();
            for (Iterator<Map.Entry<String, String>> iter = set.getKey().entrySet().iterator(); iter.hasNext(); ) {

                Map.Entry<String, String> user = iter.next();

                Map<String, String> userPair = new HashMap<>();
                userPair.put(user.getKey(), user.getValue());

                Map<String, String> reversePair = new HashMap<>();
                reversePair.put(user.getValue(), user.getKey());

                int result = balanceTransactionResult.getOrDefault(user.getKey(), 0)
                        + (Integer) this.userGraph.getUsersGraph().getOrDefault(userPair, 0) -
                        (Integer) this.userGraph.getUsersGraph().getOrDefault(reversePair, 0);
                balanceTransactionResult.put(user.getKey(), result);

            }
        }

    }

    public void adjustBalance(List<String> getMinTransactions) {
        int size = getMinTransactions.size();
        Map<Map<String, String>, Integer> adjustUserTransaction = this.userGraph.getUsersGraph();

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
        this.userGraph.setUsersGraph(adjustUserTransaction);
    }

    public void transactionSpend(String owes, String givenBy, int amount) {
        if (userGraph.getCurrentMemebers().contains(owes) && userGraph.getCurrentMemebers().contains(givenBy)) {
            Map<String, String> user = new HashMap<>();
            user.put(owes, givenBy);
            this.userGraph.getUsersGraph().put(user, amount);

            //handling case if link is already available b/w owe & giver - adjust balance
            Map<String, String> reverseUser = new HashMap<>();
            reverseUser.put(givenBy, owes);
            int existingAmount = (Integer) this.userGraph.getUsersGraph().getOrDefault(user, 0);
            int reverseAmount = (Integer) this.userGraph.getUsersGraph().getOrDefault(reverseUser, 0);
            if (reverseAmount != 0 && reverseAmount < existingAmount) {

                amount = existingAmount - reverseAmount;
                this.userGraph.getUsersGraph().put(user, amount);
                this.userGraph.getUsersGraph().put(reverseUser, 0);
            } else if (reverseAmount != 0 && reverseAmount > existingAmount) {
                amount = reverseAmount - existingAmount;
                this.userGraph.getUsersGraph().put(reverseUser, amount);
                this.userGraph.getUsersGraph().put(user, 0);
            }

            if (isRebalanceRequire()) {
                this.balanceTransaction();
                settleTransaction();
            }

        }
    }


    public boolean isRebalanceRequire() {
        boolean isEbligbleForReBalance = false;
        int rebalanceCount = 0;

        for (Iterator<Map.Entry<Map<String, String>, Integer>> it = this.userGraph.getUsersGraph().entrySet().iterator(); it.hasNext(); ) {
            Map.Entry<Map<String, String>, Integer> set = it.next();
            for (Iterator<Map.Entry<String, String>> iter = set.getKey().entrySet().iterator(); iter.hasNext(); ) {
                Map.Entry<String, String> user = iter.next();

                Map<String, String> userPair = new HashMap<>();
                userPair.put(user.getKey(), user.getValue());

                if ((Integer) this.userGraph.getUsersGraph().get(userPair) > 0) {
                    rebalanceCount++;
                }

            }

        }
        if (rebalanceCount == REBALANCE_FACTOR) {
            isEbligbleForReBalance = true;
            return isEbligbleForReBalance;
        }
        return isEbligbleForReBalance;
    }


    //maintain order while returning result . order by amount ,if amount same then order by user name .

    public void getDues(String member) {
        Map<String, Integer> result = new HashMap<>();

        for (Iterator<Map.Entry<Map<String, String>, Integer>> it = this.userGraph.getUsersGraph().entrySet().iterator(); it.hasNext(); ) {
            Map.Entry<Map<String, String>, Integer> set = it.next();
            for (Iterator<Map.Entry<String, String>> iter = set.getKey().entrySet().iterator(); iter.hasNext(); ) {
                Map.Entry<String, String> user = iter.next();

                Map<String, String> userPair = new HashMap<>();
                userPair.put(user.getKey(), user.getValue());

                if ((user.getKey().equals(member)) && !user.getKey().equals(user.getValue())) {

                    result.putIfAbsent(user.getValue(), (Integer) this.userGraph.getUsersGraph().get(userPair));
                }


            }

        }
        result.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .forEach(user -> Logger.log(user.getKey() + " " + user.getValue()));

    }

}
