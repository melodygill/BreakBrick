package com.example.breakbrick;

import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.media.MediaPlayer;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import java.util.ArrayList;
import java.util.Arrays;
//TODO
// Make it so that I don't have to add minYPixels every time i want to draw something
// Add color codes as Utility constants
// Align the block text properly within each block
// Update floor collision detection so that it doesn't immediately detect a collision when the ball is still being launched initially
// > I got around that by moving the ball's initial position up in ball constructor but should prob fix that
// > Also change the y-value that the ball gets reset to when it collides with the floor
// fix delty in player input
// make all activities portrait, fullscreen, etc in manifest

/*
Bugs to fix
- Balls phase through block corners. (No longer count double hits; just looks weird/wrong)
- Once the blocks moved down even though the balls had not returned (try to replicate? check vids)
- Figure out how to handle incorrect file input for levels. Rn program is throwing exception
- Balls can get between blocks, or b/w blocks and ceilings, causing many collisions

*- Hit sounds lag out. Not all of them play and the game gets slow. Currently I only have sounds on
   for bottom of block hits but it's still slow. How to fix that? try mediaplayer.prepare()? But the
   MP i'm using now would only call prepare() once.

Improvements
- Teleporting balls to correct place post collision is working correctly physics-wise, but it looks weird. Fix that
- Scale the buttons and text so they look ok on all device sizes
- Next level button?
- Fix button spacing. Just do custom spacing maybe? Since I want the return balls button to be on the bottom
- More levels
- Better file processing for levels - input checking (ex. remove duplicate blocks)
- Mark when levels are beat (use shared preferences to store data)
- Consider diff way of representing high score data / whether level is beat on menu activity
- Sounds (+credit Mixkit in about)
- Change the color scheme + fonts - consider using colors.xml
- Credits/about page
- Infinity mode?
- Settings page w/ ball speed changer
- Custom background for home page

- Refactor code
  > Put repetitive code into methods
  > compartmentalize or whatever the word is. Abstract (verb) ?
 */

public class GameView extends SurfaceView implements Runnable {

    //INSTANCE VARIABLES
    Thread gameThread = null;
    volatile boolean playing;
    private Context context;
    private Level gameLevel;
    private int levelNum;

    //screen resolution
    private int screenX;
    private int screenY;
    //find playable area
    private int minYPixels; //top of playable area
    private int maxYPixels; //bottom of playable area

    //drawing objects
    private Paint paint;
    private Canvas canvas;
    private SurfaceHolder holder;

    //turn control variables
    private boolean begOfTurn; //whether or not it is the beginning of a turn
    private int numBallsReturned;
    private double inputAngle;
    private boolean gameEnded;
    private boolean gameWon;
    private int numLaunched;
    private boolean gameEndSoundPlayed;

    //player input coords
    private float shotX; //stores the coordinates of the player's final shot
    private float shotY;
    private float previewX; //stores the coordinates of the player's click when they haven't committed to a shot
    private float previewY;

    //lists of Block and Ball objects for the current level
    private ArrayList<Block> blocks;
    private ArrayList<Ball> balls;

    //list of buttons
    private ArrayList<Button> buttons;

    //score variables
    private int score;
    private int streak; //Number of blocks player destroys in one turn
    private boolean newHighScore;

    //MediaPlayer to play hit sounds
    private MediaPlayer mp;


    //METHODS

    //Constructor
    GameView(Context context, int x, int y, int num) {
        super(context);
        this.context = context;
        levelNum = num;

        screenX = x;
        screenY = y;

        //Find playable area
        double temp = Utility.metersToPixels((double)Utility.playableLength, screenX); //y-length of playable area in pixels
        minYPixels = (int) ((screenY-temp)/2);
        maxYPixels = minYPixels + (int)temp;

        //Initialize drawing objects
        holder = getHolder();
        paint = new Paint();

        //Create MediaPlayer so that it's faster to play hit sounds
        int index = Arrays.asList(Utility.soundEvents).indexOf("hit");
        Log.d("Melody", "index: " + index);
        assert index != -1 : "Sound event 'hit' was not found";
        mp = MediaPlayer.create(context, Utility.soundIDs[index]);

        startGame();
    }

    private void startGame() {
        //create Level object + read its block and ball ArrayLists
        //initialize the lists blocks and balls
        gameLevel = new Level(levelNum, context);
        balls = gameLevel.getBalls();
        numBallsReturned = balls.size();
        blocks = gameLevel.getBlocks();
        //set gameWon, gameEnded, beginning of turn variables
        begOfTurn = true;
        inputAngle = -1;
        gameEnded = false;
        gameWon = false;
        numLaunched = 0;
        gameEndSoundPlayed = false;
        //Set player input coord variables to -1 (no player input yet)
        shotX = -1;
        shotY = -1;
        previewX = -1;
        previewY = -1;

        //TODO initialize buttons - locations in pixels
        buttons = new ArrayList<Button>();
        buttons.add(new Button(0, 0, screenX, minYPixels, "Menu"));
        buttons.add(new Button(0, 0, screenX, minYPixels, "Restart"));
        buttons.add(new Button(0, maxYPixels, screenX, minYPixels, "Return balls"));

        //Space out the buttons
        int xcoord = 0;  // keeps track of where buttons will be drawn
        int ycoord = minYPixels / Utility.buttonHeightFactor;  //buttons drawn slightly lower than top of screen
        for (Button button : buttons) {
            button.setHitbox(new Rect(button.getHitbox().left + xcoord, button.getHitbox().top + ycoord,
                    button.getHitbox().right + xcoord, button.getHitbox().bottom + ycoord));
            xcoord += button.getHitbox().right; //add length of this button so next button is spaced correct
            xcoord += screenX/((float)Utility.buttonLengthFactor*2);  //add margin
        }

        //Initialize score control variables
        score = 0;
        streak = 0;
        newHighScore = false;

        //DEBUG
        mp.start();
        mp.start();
        //Utility.setHighScore(context, levelNum+"", 5);
    }

    @Override
    public void run() {
        while (playing) {
            update();
            detectCollisions();
            draw();
            control();
            /* consider:
            * check for collisions more often than new frames are drawn
            * -> would have to update the balls in smaller increments. Like divide the ball speed
            *    by a certain number (like 3) and then check collision 3x as often
            * This would hopefully catch collisions earlier to avoid intersections of ball and block
            * */
        }
    }

    private void update() {
        if (begOfTurn && !gameEnded) {
            //if player input is not already set, get player input
            if (inputAngle == -1) {
                if (shotX != -1 && shotY != -1) {
                    inputAngle = PlayerInput.findAngle(shotX, shotY, balls.get(0), screenX, minYPixels);
                    numBallsReturned = 0;
                    //reset coords
                    shotX = -1;
                    shotY = -1;
                }
            }
            //if player input is already set, then turn is in launching phase
            else {
                boolean ballLaunched = false;
                //find the first ball that has launched set to false
                //give that ball updated velocity according to player input
                //reset ball variables to show that it's been launched but not returned
                //break (only launch 1 ball per tick)
                for (Ball ball : balls) {
                    if (!ball.isLaunched()) {
                        ball.setXvel(Utility.findXvel(inputAngle));
                        ball.setYvel(Utility.findYvel(inputAngle));
                        ball.setLaunched(true);
                        ball.setReturned(false);
                        ballLaunched = true;
                        numLaunched++;
                        break;
                    }
                }
                //if all the balls have been launched, it's no longer the beginning of the turn
                /*boolean allLaunched = true;
                for (Ball ball : balls) {
                    if (!ball.isLaunched()) {
                        allLaunched = false;
                    }
                }*/
                if (numLaunched >= balls.size()) { //prev allLaunched) {
                    begOfTurn = false;
                    inputAngle = -1;
                    Log.d("Melody", "begOfTurn=false; numLaunched: " + numLaunched);
                    numLaunched = 0;
                }
            }
        }

        //The following updating is done whether or not it's the beginning of the turn

        //update balls + blocks
        for (Ball ball : balls) {
            ball.update();
        }
        ArrayList<Integer> deadBlocks = new ArrayList<Integer>(); //holds indexes of blocks w/ value 0
        for (int i = 0; i < blocks.size(); i++) {
            if (blocks.get(i).getValue() <= 0) {
                deadBlocks.add(i);
                //Count number of blocks player destroys in one turn
                //Score increases faster when more blocks are destroyed in one turn
                streak++;
                score += streak;
            }
        }
        //Remove blocks w/ value 0. Iterate backwards so the indexes aren't messed up
        for (int i = deadBlocks.size()-1; i >= 0; i--) {
            int index = deadBlocks.get(i);
            blocks.remove(blocks.get(index));
        }

        //Log.d("Melody", "streak: " + streak + "; score: " + score);

        //check to reset beginning of turn - if all the balls have returned to floor
        //move blocks down
        if (numBallsReturned >= balls.size() && !begOfTurn) {
            begOfTurn = true;
            for (Block block : blocks) {
                block.moveDown();
            }
            streak = 0; //Reset streak
            Log.d("Melody", "ball x coord: " + Utility.metersToPixels(balls.get(0).getXpos(), screenX));
            Log.d("Melody", "numBallsReturned: "+numBallsReturned + "; balls.size(): "+balls.size());
        }

        //check if player won (all blocks destroyed)
        if (blocks.size() == 0) {
            gameEnded = true;
            gameWon = true;
            streak = 0;
            //Update high score
            if (score > Utility.getHighScore(context, levelNum+"")) {
                Utility.setHighScore(context, levelNum+"", score);
                newHighScore = true;
            }
            //Play win sound
            if (!gameEndSoundPlayed) {
                Utility.playSound(context, "win");
                gameEndSoundPlayed = true;
            }
        }
        //check if player lost (a block has made it past the lower bound)
        for (Block block : blocks) {
            if (block.getYposGrid() >= Utility.numBlockRows) {
                gameEnded = true;
                streak = 0;
                if (!gameEndSoundPlayed) {
                    Utility.playSound(context, "loss");
                    gameEndSoundPlayed = true;
                }
                break;
            }
        }
        //If game is over, reset balls to the values given in constructor so they don't keep updating
        if (gameEnded) {
            for (Ball ball : balls) {
                ball.setXpos((double) Utility.playableWidth / 2);
                ball.setYpos(Utility.playableLength - Utility.ballDiameter * 2);
                ball.setXvel(0);
                ball.setYvel(0);
            }
        }
    }

    private void detectCollisions() {
        //Loop through balls list
        for (Ball ball : balls) {
            //Check for collision with ceiling
            if (Utility.metersToPixels(ball.getHitbox().top, screenX) <= 0) {
                ball.setYvel(-1*ball.getYvel());
                //Teleport ball to within bounds so it never leaves the screen
                ball.setYpos(1);
            }

            //Check for collision with left wall
            if (Utility.metersToPixels(ball.getHitbox().left, screenX) <= 0) {
                ball.setXvel(-1*ball.getXvel());
                ball.setXpos(1);
            }
            //Right wall
            if (Utility.metersToPixels(ball.getHitbox().right, screenX) >= Utility.metersToPixels(Utility.playableWidth, screenX)) {
                ball.setXvel(-1*ball.getXvel());
                ball.setXpos(Utility.playableWidth - Utility.ballDiameter - 1);
            }

            //Check for collision with floor - if so, ball gets collected on bottom
            if (Utility.metersToPixels(ball.getHitbox().bottom, screenX) >= maxYPixels - minYPixels) {
                numBallsReturned += 1;
                ball.setReturned(true);
                ball.setLaunched(false);
                ball.setXvel(0);
                ball.setYvel(0);
                ball.setYpos(Utility.playableLength - (Utility.ballDiameter+1));
                //Check if this ball is the first one to be returned. If so, keep its x-pos
                //If not, make its x-pos the same as the other returned balls
                for (Ball ball2 : balls) {
                    if (ball2.isReturned() && ball != ball2) {
                        ball.setXpos(ball2.getXpos());
                        break;
                    }
                }
            }

            //Check for collision with blocks
            for (Block block : blocks) {
                if (Rect.intersects(ball.getHitbox(), block.getHitbox())) {
                    //Log.d("Melody", "intersection " + block.getValue());
                    //draw a line on the ball's path and see which of the block's sides it intersects with
                    //essentially recreating the path that the ball took

                    //invert ball's velocity
                    double lineXvel = -1 * ball.getXvel();
                    double lineYvel = -1 * ball.getYvel();
                    //lengthen velocity vector so that it's definitely long enough to hit the block sides
                    //multiply by ratio of block's length to vel vector length: blockLength/ballSpeed
                    //add 1 just to be sure :^)
                    double vectorFactor = (double) Utility.blockLength / (double) Utility.ballSpeed;
                    lineXvel *= vectorFactor;
                    lineYvel *= vectorFactor;
                    lineXvel += 1;
                    lineYvel += 1;
                    //use the new velocities to make an endpoint to the line I'm making (start point is ball's location)
                    double xCoord = ball.getXpos() + lineXvel;
                    double yCoord = ball.getYpos() + lineYvel;
                    //convert these points as well as ball's location to pixels
                    xCoord = Utility.metersToPixels(xCoord, screenX);
                    yCoord = Utility.metersToPixels(yCoord, screenX);
                    double ballX = Utility.metersToPixels(ball.getXpos(), screenX);
                    double ballY = Utility.metersToPixels(ball.getYpos(), screenX);

                    //Create GFG.Point objects for each start and end point of the lines I'm testing
                    //Ball's path
                    GFG.Point ballP1 = new GFG.Point((int) ballX, (int) ballY);
                    GFG.Point ballP2 = new GFG.Point((int) xCoord, (int) yCoord);
                    //Each side of the block needs a start and end point too, to represent the line segment
                    double blockX = block.getXposMeters();
                    double blockY = block.getYposMeters();
                    double blockTopRightX = blockX + Utility.blockLength;
                    double blockBottomLeftY = blockY + Utility.blockLength;
                    blockX = Utility.metersToPixels(blockX, screenX);
                    blockY = Utility.metersToPixels(blockY, screenX);
                    blockTopRightX = Utility.metersToPixels(blockTopRightX, screenX);
                    blockBottomLeftY = Utility.metersToPixels(blockBottomLeftY, screenX);
                    GFG.Point blockTopLeft = new GFG.Point((int)blockX, (int)blockY);
                    GFG.Point blockTopRight = new GFG.Point((int)blockTopRightX, (int)blockY);
                    GFG.Point blockBottomLeft = new GFG.Point((int)blockX, (int)blockBottomLeftY);
                    GFG.Point blockBottomRight = new GFG.Point((int)blockTopRightX, (int)blockBottomLeftY);

                    //use algorithm from Geeks For Geeks to test collision w/ each side of block
                    if (GFG.doIntersect(ballP1, ballP2, blockTopLeft, blockTopRight)) {
                        ball.setYvel(-1*ball.getYvel());
                        //Teleport the ball to just outside the block so it doesn't get stuck in the block
                        ball.setYpos(block.getYposMeters() - Utility.ballDiameter);
                        block.decreaseValue();
                        mp.start();
                        Log.d("Melody", block.getValue() + " top");
                    }
                    else if (GFG.doIntersect(ballP1, ballP2, blockBottomLeft, blockBottomRight)) {
                        ball.setYvel(-1*ball.getYvel());
                        ball.setYpos(block.getYposMeters() + Utility.blockLength);
                        block.decreaseValue();

                        //Utility.playHit();
                        //Utility.playSound(context, "hit");
                        mp.start();
                        Log.d("Melody", "Mediaplayer reference: " + mp.toString());

                        Log.d("Melody", block.getValue() + " bottom");
                    }
                    else if (GFG.doIntersect(ballP1, ballP2, blockTopRight, blockBottomRight)) {
                        ball.setXvel(-1*ball.getXvel());
                        ball.setXpos(block.getXposMeters() + Utility.blockLength);
                        block.decreaseValue();
                        mp.start();
                        Log.d("Melody", block.getValue() + " right");
                    }
                    else if (GFG.doIntersect(ballP1, ballP2, blockTopLeft, blockBottomLeft)) {
                        ball.setXvel(-1*ball.getXvel());
                        ball.setXpos(block.getXposMeters() - Utility.ballDiameter);
                        block.decreaseValue();
                        mp.start();
                        Log.d("Melody", block.getValue() + " left");
                    }
                    //TODO throw exception if no collision with any side
                    else {
                        Log.d("Melody", "No collision with any sides of " + block.getValue());
                        //if it's not colliding with the sides then what is it colliding with??
                        //this algorithm doesnt handle CORNER cases
                        //only decrease block value if algorithm detects a side was hit?
                    }
                }
            }
        }
    }

    private void draw() {
        if (holder.getSurface().isValid()) {
            canvas = holder.lockCanvas();
            //cover the last frame
            canvas.drawColor(getResources().getColor(R.color.blue_gray));

            //debug - draw ball hitbox
            /*
            paint.setColor(Color.argb(255, 255, 255, 255));
            //Convert to pixels before drawing
            for (Ball ball : balls) {
                canvas.drawRect(Utility.makeDrawable(ball.getHitbox(), screenX, minYPixels), paint);
            }
            //Log.d("Melody", "ball1 hitbox top in pixels: "+ Utility.metersToPixels(ball1.getHitbox().top, screenX));
             */

            //Lines indicate playable area bounds
            paint.setColor(getResources().getColor(R.color.white));
            paint.setStrokeWidth(2);
            canvas.drawLine(0, minYPixels, screenX, minYPixels, paint);
            canvas.drawLine(0, maxYPixels, screenX, maxYPixels, paint);


            //Draw the shot preview line
            //If it's the beginning of the turn and the coords for preview line are set
            //That means that the player is holding down their finger on the screen
            //Draw a line from the balls' position on the floor to the input coords to show where the balls will go
            if (begOfTurn && previewX != -1 && previewY !=-1) {
                Ball tempBall = balls.get(0);
                float xposPixels = (float) Utility.metersToPixels(tempBall.getXCenter(), screenX);
                float yposPixels = (float) Utility.metersToPixels(tempBall.getYCenter(), screenX);
                yposPixels += minYPixels; //make sure it's at the right height on screen
                canvas.drawLine(xposPixels, yposPixels, previewX, previewY, paint);
            }

            //DRAW BLOCKS:
            //Loop through block list
            //Convert each position in meters to pixels using utility class methods
            //Draw the block w/ its number value on top
            for (Block block : blocks) {
                if (block.getYposGrid() >= 0 && block.getValue() > 0) {
                    paint.setColor(getResources().getColor(Utility.getBlockColor(block)));
                    canvas.drawRect(Utility.makeDrawable(block.getHitbox(), screenX, minYPixels), paint);
                    paint.setStyle(Paint.Style.STROKE);
                    paint.setColor(getResources().getColor(R.color.black));
                    canvas.drawRect(Utility.makeDrawable(block.getHitbox(), screenX, minYPixels), paint);
                    paint.setStyle(Paint.Style.FILL);
                    //canvas.draw
                    //add number value on top
                    paint.setColor(getResources().getColor(R.color.black));
                    paint.setTextSize(Utility.blockLength);
                    float xposPixels = (float) Utility.metersToPixels(block.getXposMeters(), screenX);
                    float yposPixels = (float) Utility.metersToPixels(block.getYposMeters(), screenX);
                    canvas.drawText(block.getValue() + "", xposPixels, yposPixels + minYPixels + Utility.blockLength, paint);
                }
            }

            //DRAW BALLS:
            //Loop through ball list
            //Convert position to pixels
            //Draw ball (circle)
            paint.setColor(getResources().getColor(R.color.bright_yellow));
            float numxpos = -1;  //stores position of returned balls

            for (Ball ball : balls) {
                float xposPixels = (float) Utility.metersToPixels(ball.getXCenter(), screenX);
                float yposPixels = (float) Utility.metersToPixels(ball.getYCenter(), screenX);
                yposPixels += minYPixels; //make sure it's at the right height on the screen
                //Log.d("Melody", "x : " + xposPixels);
                //Log.d("Melody", "y : " + yposPixels);
                int radius = (int) (Utility.metersToPixels(Utility.ballDiameter, screenX)/2);

                //Draw solid color ball
                canvas.drawCircle(xposPixels, yposPixels, radius, paint);

                //draw outline
                /*
                paint.setStyle(Paint.Style.STROKE);
                paint.setColor(getResources().getColor(R.color.black));
                canvas.drawCircle(xposPixels, yposPixels, radius, paint);
                paint.setStyle(Paint.Style.FILL);
                paint.setColor(getResources().getColor(R.color.teal_200));
                */

                if (ball.isReturned()) {
                    numxpos = (float) Utility.metersToPixels(ball.getXpos(), screenX);
                }
            }
            //Display number of returned balls at bottom, underneath returned ball
            paint.setColor(getResources().getColor(R.color.white));
            paint.setTextSize(Utility.blockLength);

            if (numxpos == -1) {
                numxpos = screenX / 2;
            }
            canvas.drawText(numBallsReturned + "", numxpos, maxYPixels + Utility.blockLength, paint);

            //Draw score and high score
            canvas.drawText("Score: "+score, screenX/2, minYPixels/2, paint);
            canvas.drawText("High score: "+Utility.getHighScore(context, levelNum+""), screenX/2, minYPixels/4, paint);

            //IF GAME HAS ENDED
            //Draw either victory screen or death screen, according to value of gameWon
            if (gameEnded) {
                paint.setColor(getResources().getColor(R.color.white));
                paint.setTextSize(150);
                if (gameWon) {
                    canvas.drawText("YOU WON!", screenX/4, screenY/2, paint);
                    Log.d("Melody", "won :)");
                    paint.setTextSize(70);
                    if (newHighScore) {
                        canvas.drawText("New high score!", screenX / 4, 3 * screenY / 4, paint);
                    }
                }
                else {
                    canvas.drawText("YOU LOST", screenX/4, screenY/2, paint);
                    Log.d("Melody", "lost :)");
                }
            }

            // DRAW BUTTONS
            for (Button button : buttons) {
                paint.setColor(getResources().getColor(R.color.light_blue));
                canvas.drawRect(button.getHitbox(), paint);

                paint.setColor(getResources().getColor(R.color.black));
                paint.setTextSize((button.getHitbox().bottom - button.getHitbox().top)/2);
                float xpos = button.getHitbox().left;
                float ypos = button.getHitbox().top;
                canvas.drawText(button.getText(), xpos, ypos + 60, paint);
            }

            //Unlock and draw the scene
            holder.unlockCanvasAndPost(canvas);
        }
    }

    private void control() {
        try {
            gameThread.sleep(17); //60 fps
        } catch (InterruptedException e) {

        }
    }

    //Handle player input: button presses and shots
    @Override
    public boolean onTouchEvent(MotionEvent motionEvent) {
        float x = motionEvent.getX();
        float y = motionEvent.getY();

        switch (motionEvent.getAction() & MotionEvent.ACTION_MASK) {
            //Player touched screen and lifted finger up
            case MotionEvent.ACTION_UP:

                //Check if the player clicked a button
                for (Button button : buttons) {
                    if (button.getHitbox().contains((int)x, (int) y)) {
                        switch (button.getText()) {
                            case "Menu":
                                Log.d("Melody", "menu button pressed");
                                Utility.playSound(context, "button");
                                Intent startMenuIntent = new Intent(context, MenuActivity.class);
                                context.startActivity(startMenuIntent);
                                //context.finish();
                                break;
                            case "Restart":
                                Log.d("Melody", "restart button pressed");
                                Utility.playSound(context, "button");
                                startGame();
                                break;
                            case "Return balls":
                                //Return balls to the floor
                                Log.d("Melody", "return balls button pressed");
                                Utility.playSound(context, "button");
                                //Code within loop copy/pasted from collision detection method
                                //Return the ball to the floor if it's not already returned
                                for (Ball ball : balls) {
                                    if (!ball.isReturned()) {
                                        numBallsReturned += 1;
                                        ball.setReturned(true);
                                        ball.setLaunched(false);
                                        ball.setXvel(0);
                                        ball.setYvel(0);
                                        ball.setYpos(Utility.playableLength - (Utility.ballDiameter + 1));
                                        //Check if this ball is the first one to be returned. If so, keep its x-pos
                                        //If not, make its x-pos the same as the other returned balls
                                        for (Ball ball2 : balls) {
                                            if (ball2.isReturned() && ball != ball2) {
                                                ball.setXpos(ball2.getXpos());
                                                break;
                                            }
                                        }
                                    }
                                }
                                break;
                        }
                    }
                }
        }

        //Listen for these events (player input) - but only if it's the beginning of turn
        if (begOfTurn && inputAngle == -1 && y >= minYPixels && y <= maxYPixels) {
            switch (motionEvent.getAction() & MotionEvent.ACTION_MASK) {
                //Player lifted up finger
                case MotionEvent.ACTION_UP:
                    //find coordinates of event
                    //store coordinates so update method can send them to playerinput
                    //clear the variable/s that store coordinates of event so the preview line is not drawn
                    shotX = x;
                    shotY = y;

                    previewX = -1;
                    previewY = -1;
                    break;

                //Player drags finger while holding down OR player has touched screen
                case MotionEvent.ACTION_MOVE:

                case MotionEvent.ACTION_DOWN:
                    //find coordinates of event
                    //store in x/y draw variables
                    //so that the draw method can draw line from position of balls on floor to player touch
                    previewX = x;
                    previewY = y;
                    break;

            }
        }
        return true;
    }

    //Methods to pause/resume game thread
    public void pause() {
        playing = false;
        try {
            gameThread.join();
        } catch (InterruptedException e) {

        }
    }

    public void resume() {
        playing = true;
        gameThread = new Thread(this);
        gameThread.start();
    }
}
