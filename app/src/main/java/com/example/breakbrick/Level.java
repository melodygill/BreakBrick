package com.example.breakbrick;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;

public class Level {

    //instance variables
    private ArrayList<Block> blocks;
    private ArrayList<Ball> balls;
    int levelNum;

    //constructor
    public Level(int num, Context context) {
        //levelNum determines which file to read from
        levelNum = num;

        balls = new ArrayList<Ball>();
        blocks = new ArrayList<Block>();

        //Use AssetManager to open assets folder to find level file
        AssetManager assetManager = context.getAssets();

        try {
            InputStream input = assetManager.open("levels/level"+num+".txt");
            BufferedReader r = new BufferedReader(new InputStreamReader(input));

            StringBuilder total = new StringBuilder();
            for (String line; (line = r.readLine()) != null; ) {
                total.append(line).append('\n');
            }
            String result = total.toString();
            String[] fileLines = result.split("\n");

            //Log.d("Melody", Arrays.toString(fileLines));

            //Remove empty lines and comments
            ArrayList<String> info = new ArrayList<>();
            for (String line : fileLines) {
                line = line.trim();
                if (line.length() > 0 && line.charAt(0) != '#') {
                    info.add(line);
                }
            }
            //TODO remove duplicate blocks

            //There should be ball info and at least 1 block
            if (info.size() < 2) {
                throw new IllegalArgumentException();
            }

            //Log.d("Melody", info.toString());
            //call helper methods that parse file into balls/blocks arraylists
            parseBalls(info);
            parseBlocks(info);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //Methods to read from file and edit arraylists
    public void parseBalls(ArrayList<String> info) {
        int numBalls = Integer.parseInt(info.get(0));
        for (int i = 0; i < numBalls; i++) {
            balls.add(new Ball());
        }
    }

    public void parseBlocks(ArrayList<String> info) {
        //Start loop at 1 because line 0 is the number of balls
        for (int i = 1; i < info.size(); i++) {
            String[] blockInfo = info.get(i).split(" ");
            int[] blockNums = new int[blockInfo.length];
            for (int j = 0; j < blockInfo.length; j++) {
                blockNums[j] = Integer.parseInt(blockInfo[j]);
            }
            blocks.add(new Block(blockNums[0], blockNums[1], blockNums[2]));
        }
    }

    //getters
    public ArrayList<Ball> getBalls() {
        return balls;
    }

    public ArrayList<Block> getBlocks() {
        return blocks;
    }

    public int getLevelNum() {
        return levelNum;
    }
}
