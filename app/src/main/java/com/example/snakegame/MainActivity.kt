package com.example.snakegame

import android.annotation.SuppressLint
import android.graphics.Rect
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer

import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import java.util.Random
import kotlin.math.abs

class MainActivity : AppCompatActivity(),GestureDetector.OnGestureListener {

    private lateinit var newGame:Button
    private lateinit var resume:Button
    private lateinit var board:RelativeLayout
    private lateinit var pause:Button
    private lateinit var rLayout:RelativeLayout
    private lateinit var score:TextView
    private lateinit var playAgain:Button

    private lateinit var gestureDetector:GestureDetector
    private var x1:Float = 0.0f
    private var x2:Float = 0.0f
    private var y1:Float = 0.0f
    private var y2:Float = 0.0f

    companion object{
        const val MIN_Distance = 100
    }

    private lateinit var meat:ImageView
    private lateinit var snake:ImageView
    private lateinit var snakeSegments:MutableList<ImageView>
    private var currentDirection = "pause"
    private var scoreForTextView = 0
    private var flag = false
    private var countDownInterval = 30L

    private  val timer = object: CountDownTimer(100000000000,countDownInterval) {
        override fun onTick(millisUntilFinished: Long) {
            move()
            meatCollision()
        }
        override fun onFinish() {}
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        gestureDetector = GestureDetector(this,this)

        newGame = findViewById(R.id.buttonNewGame)
        resume = findViewById(R.id.buttonResume)
        board = findViewById(R.id.gameBoard)

        pause = findViewById(R.id.buttonPause)
        rLayout = findViewById(R.id.RLayot)
        score  = findViewById(R.id.ScoreTextView)
        playAgain = findViewById(R.id.PlayAgain)
        resume.visibility = View.INVISIBLE
        playAgain.visibility = View.INVISIBLE
        snake = ImageView(this)
        meat = ImageView(this)
        snakeSegments = mutableListOf(snake)

        timer.start()
        newGame.setOnClickListener{
            flag = true

            board.visibility = View.VISIBLE
            newGame.visibility = View.INVISIBLE
            resume.visibility = View.INVISIBLE

            snake.setImageResource(R.drawable.snakehead)
            snake.layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            board.addView(snake)
            snakeSegments.add(snake)
            snake.x = (board.width/2).toFloat()
            snake.y = (board.height/2).toFloat()

            meat.setImageResource(R.drawable.meat)
            meat.layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            board.addView(meat)
            val random = Random()
            val randomX = random.nextInt(404) - 0
            val randomY = random.nextInt(358) - 0
            meat.x = randomX.toFloat()
            meat.y = randomY.toFloat()
        }

        pause.setOnClickListener{
            if(flag)
            {
                currentDirection = "Pause"
                flag = false
                board.visibility = View.INVISIBLE
                resume.visibility = View.VISIBLE
            }

        }
        playAgain.setOnClickListener{
            recreate()
        }
        resume.setOnClickListener {
            board.visibility = View.VISIBLE
            currentDirection = "Pause"
            flag = true
        }
    }

    @SuppressLint("SuspiciousIndentation")
    override fun onTouchEvent(event: MotionEvent): Boolean {

        gestureDetector.onTouchEvent(event)

        when(event.action){
            0->{
                x1 = event.x
                y1 = event.y
            }
            1->{
                x2 = event.x
                y2 = event.y

                val valueX:Float = x2-x1
                val valueY:Float = y2-y1

                if(abs(valueX) > MIN_Distance) {
                    if(x2 > x1){
                        if(flag)
                        currentDirection = "Right"
                    }
                    else{
                        if(flag)
                        currentDirection = "Left"
                    }
                }
                else if(abs(valueY) > MIN_Distance) {
                    if(y2 > y1){
                        if(flag)
                        currentDirection = "Down"
                    }
                    else{
                        if(flag)
                        currentDirection = "Up"
                    }
                }

            }
        }

        return super.onTouchEvent(event)
    }

    fun move() {


        for (i in snakeSegments.size - 1 downTo 1) {
            snakeSegments[i].visibility = View.VISIBLE
            snakeSegments[i-1].visibility = View.VISIBLE
            snakeSegments[i].x = snakeSegments[i - 1].x
            snakeSegments[i].y = snakeSegments[i - 1].y
        }

        when (currentDirection) {

            "Right" -> {
                snake.x += 10
                if (snake.x > 990){
                    snake.x = 990f
                    endGame()
                }
            }
            "Left" -> {
                snake.x -= 10
                if (snake.x < 5){
                    snake.x = 5f
                    endGame()

                }
            }
            "Up" -> {
                snake.y -= 10
                if (snake.y < 4f){
                    snake.y = 5f
                    endGame()
                }
            }
            "Down" -> {
                snake.y += 10
                if (snake.y > 1544){
                    snake.y = 1544f
                    endGame()
                }
            }
            "Pause" -> {
                snake.x += 0
                snake.translationX = snake.x
            }
        }

    }

    @SuppressLint("SetTextI18n")
    fun endGame(){
        rLayout.setBackgroundColor(getColor(R.color.red))
        currentDirection = "Pause"
        score.setText("Score:${scoreForTextView}")
        playAgain.visibility = View.VISIBLE
        flag = false
    }

    @SuppressLint("SuspiciousIndentation", "SetTextI18n")
    fun meatCollision(){

        val meatRect = Rect(meat.x.toInt(), meat.y.toInt(), (meat.x+meat.width).toInt(), (meat.y+meat.height).toInt())
        val snakeRect = Rect(snake.x.toInt(), snake.y.toInt(), (snake.x+snake.width).toInt(), (snake.y+snake.height).toInt())
        if(snakeRect.intersect(meatRect)){
            scoreForTextView++
            countDownInterval--
            score.setText("score: ${scoreForTextView}")
            val random = Random()
            val randomX = random.nextInt(990) - 0
            val randomY = random.nextInt(1544) - 0
            meat.x = randomX.toFloat()
            meat.y = randomY.toFloat()
            val newSnake = ImageView(this)
                newSnake.setImageResource(R.drawable.snake)
            newSnake.layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            snakeSegments.add(newSnake)
            newSnake.visibility = View.INVISIBLE
            board.addView(newSnake)

        }
    }


    //Everything below does not apply to the program but don't touch this

    override fun onDown(e: MotionEvent): Boolean {
        return false
    }

    override fun onShowPress(e: MotionEvent) {
        return
    }

    override fun onSingleTapUp(e: MotionEvent): Boolean {
        return false
    }

    override fun onScroll(
        e1: MotionEvent?,
        e2: MotionEvent,
        distanceX: Float,
        distanceY: Float
    ): Boolean {
        return false
    }

    override fun onLongPress(e: MotionEvent) {
        return
    }

    override fun onFling(
        e1: MotionEvent?,
        e2: MotionEvent,
        velocityX: Float,
        velocityY: Float
    ): Boolean {
        return false
    }

}