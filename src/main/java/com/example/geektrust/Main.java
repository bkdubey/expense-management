package com.example.geektrust;

import com.example.geektrust.utility.CommandParser;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Stream;

public class Main {


    public static void main(String[] args) {

        String fileName = args[0]; fileName = "D:\\L̥project\\contest\\intuit\\java-maven-starter-kit-master\\sample_input\\input1.txt";
        fileName = "D:\\project\\contest\\intuit\\java-maven-starter-kit-master\\sample_input\\input1.txt";

        new Main().processInputFile(fileName);

    }


    private void processInputFile(String fileName) {

        try (Stream<String> stream = Files.lines(Paths.get(fileName))) {

            CommandParser commandParser = new CommandParser();
            stream.forEach(commandParser::parse);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
