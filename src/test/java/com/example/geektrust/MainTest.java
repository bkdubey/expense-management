package com.example.geektrust;

import com.example.geektrust.user.storage.UserGraph;
import com.example.geektrust.user.transaction.Transaction;
import com.example.geektrust.utility.CommandParser;
import junit.framework.Assert;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;


public class MainTest {

    UserGraph userGraph = new UserGraph();

    MainTest() {

    }

    @Test
    void testPipeline() {
        this.userGraph.moveIn("ANDY");
        this.userGraph.moveIn("WOODY");
        this.userGraph.moveIn("BO");
        this.userGraph.moveIn("REX");


        userGraph.printUserGraph();

        //use case 1
        new Transaction().transactionSpend("WOODY", "ANDY", 1000);
        new Transaction().transactionSpend("BO", "ANDY", 1000);
        new Transaction().transactionSpend("BO", "WOODY", 150);

        //use case 2
        /*new Transaction().addTransaction( "ANDY","WOODY",2000);
        new Transaction().addTransaction( "BO","WOODY",2000);
        new Transaction().addTransaction( "BO","ANDY",3000);*/

        //use case 3
        /*this.userGraph.moveIn("A0");
        this.userGraph.moveIn("A1");
        this.userGraph.moveIn("A2");
        UserGraph.printUserGraph();
        new Transaction().addTransaction( "A0","A1",1000);
        new Transaction().addTransaction( "A0","A2",2000);
        new Transaction().addTransaction( "A1","A2",5000);*/


        userGraph.printUserGraph();
        Map user = new HashMap<>();
        user.putIfAbsent("ANDY", "ANDY");
        Assert.assertEquals(0, Integer.parseInt(userGraph.getUsersGraph().get(user).toString()));
    }

    @Test
    void testCompletePipeline() {
        CommandParser commandParser = new CommandParser();
        commandParser.parse("MOVE_IN ANDY");
        commandParser.parse("MOVE_IN WOODY");
        commandParser.parse("MOVE_IN BO");
        commandParser.parse("MOVE_IN REX");


        commandParser.parse("SPEND 3000 ANDY WOODY BO");
        commandParser.parse("SPEND 300 WOODY BO");
        commandParser.parse("SPEND 300 WOODY REX");

        //Transaction.balanceTransaction();
        //Transaction.settleTransaction();

        commandParser.parse("DUES BO");
        commandParser.parse("DUES WOODY");
        /*new Transaction().getDues("BO");
        new Transaction().getDues("WOODY");*/

        commandParser.parse("CLEAR_DUE BO ANDY 500");
        commandParser.parse("CLEAR_DUE BO ANDY 2500");

        /*Transaction.clearDue("BO","ANDY",500);
        Transaction.clearDue("BO","ANDY",2500);*/

        commandParser.parse("MOVE_OUT ANDY");
        commandParser.parse("MOVE_OUT WOODY");
        commandParser.parse("MOVE_OUT BO");
        commandParser.parse("CLEAR_DUE BO ANDY 650");

        commandParser.parse("MOVE_OUT BO");
        commandParser.parse("MOVE_OUT REX");

        Map user = new HashMap<>();
        user.putIfAbsent("ANDY", "ANDY");
        Assert.assertEquals(0, Integer.parseInt(userGraph.getUsersGraph().get(user).toString()));

    }

    @Test
    void testPipeLinewithoutParser() {
        CommandParser commandParser = new CommandParser();
        commandParser.parse("MOVE_IN ANDY");
        commandParser.parse("MOVE_IN WOODY");
        commandParser.parse("MOVE_IN BO");

        commandParser.parse("SPEND 6000 WOODY ANDY BO");
        commandParser.parse("SPEND 6000 ANDY BO");

        commandParser.parse("DUES ANDY");
        commandParser.parse("DUES BO");
        commandParser.parse("CLEAR_DUE BO ANDY 500");
        commandParser.parse("CLEAR_DUE BO ANDY 2500");

        commandParser.parse("MOVE_OUT ANDY");
        commandParser.parse("MOVE_OUT WOODY");
        commandParser.parse("MOVE_OUT BO");
        commandParser.parse("CLEAR_DUE BO ANDY 650");

        commandParser.parse("MOVE_OUT BO");
        commandParser.parse("MOVE_OUT REX");

        Map user = new HashMap<>();
        user.putIfAbsent("ANDY", "ANDY");
        Assert.assertEquals(0, Integer.parseInt(userGraph.getUsersGraph().get(user).toString()));

    }


/*
    @Test
    void testMoveIn() {
        this.house.moveIn("ANDY");
        this.house.moveIn("WOODY");
        this.house.moveIn("BO");
        this.house.moveIn("REX");
        Assert.assertEquals(house.getCurrentMemebers().get(0), "ANDY");
    }


    @Test
    void testSpendAppender() {
        String[] owesList = { "WOODY" , "BO" };
        paymentHandler.spendAppender(3000,"ANDY",  Arrays.asList(owesList));
    }

    @Test
    void testMoveInAndSpendAppender() {
        this.house.moveIn("ANDY");
        this.house.moveIn("WOODY");
        this.house.moveIn("BO");
        this.house.moveIn("REX");

        String[] owesList = { "WOODY" , "BO" };

        paymentHandler.spendAppender(3000,"ANDY",  Arrays.asList(owesList));

        String[] owesList2 = { "WOODY" , "REX" };
        paymentHandler.spendAppender(3000,"ANDY",  Arrays.asList(owesList2));

    }
*/

}