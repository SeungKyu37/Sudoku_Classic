package com.sudoku.Sudoku

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.FrameLayout
import android.widget.GridLayout
import android.widget.ImageButton
import android.widget.TextView
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.OnUserEarnedRewardListener
import com.google.android.gms.ads.rewarded.RewardItem
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback
import kotlin.random.Random

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        difficulty = intent.getIntExtra("difficulty", 0)
        if(difficulty != 0){
            InitGrid(solvedgrid)
            ShuffleGrid(solvedgrid, 3)
            CreateRiddleGrid(difficulty)
        } else {
            solvedgrid = intent.getSerializableExtra("loadedSolvedGrid") as Array<IntArray>
            riddleGrid = intent.getSerializableExtra("loadedSavedGrid") as Array<IntArray>
            savedGrid = riddleGrid
            cntAnswer = intent.getIntExtra("loadedCntAnswer", 0)
            difficulty = intent.getIntExtra("loadedDifficulty", 0)
        }


        setContentView(R.layout.activity_main)


//        displayGrid()

        loadRewardedAd()
        updateGrid()

//        setMemoImage()
//
//        updateMemoGrid()

        val btn_Title: ImageButton = findViewById(R.id.btnTitle)



        btn_Title.setOnClickListener{
            saveData(solvedgrid, savedGrid, cntAnswer, difficulty)
            val intent = Intent(this,TitleActivity::class.java)
            startActivity(intent)
        }
    }

    private var isMemoOn: Boolean = false

    var solvedgrid = Array(9) { IntArray(9) }
    var savedGrid = Array(9) { IntArray(9) }
    var riddleGrid = Array(9) { IntArray(9) }
    var colorGrid = Array(9) { IntArray(9) }

    var difficulty = 0
    var cntAnswer = 0
    val redTextColorCells: MutableSet<Pair<Int, Int>> = mutableSetOf()

    var erasable: Boolean = true

    var hintable: Boolean = true

    private var rewardedAd: RewardedAd? = null



    private fun displayGrid() {
        val adView: AdView = findViewById(R.id.adView)
        val adRequest: AdRequest = AdRequest.Builder().build()
        adView.loadAd(adRequest)

        var layout_side : Int = 0

        val gridLayout: GridLayout = findViewById(R.id.gridLayout)

        for (i in 0 until 9) {
            layout_side += 110
            for (j in 0 until 9) {

            }
        }

    }

    private fun loadRewardedAd() {
        val adRequest = AdRequest.Builder().build()

        RewardedAd.load(this,"ca-app-pub-5730535650243784/9088876125", adRequest, object : RewardedAdLoadCallback() {
            override fun onAdFailedToLoad(adError: LoadAdError) {

                rewardedAd = null
            }

            override fun onAdLoaded(ad: RewardedAd) {

                rewardedAd = ad
            }
        })
    }

    private fun showRewardedAd(btnHint: ImageButton) {
        rewardedAd?.fullScreenContentCallback = object: FullScreenContentCallback() {
            override fun onAdDismissedFullScreenContent() {
                rewardedAd = null
                loadRewardedAd()
            }

            override fun onAdShowedFullScreenContent() {
                rewardedAd = null
            }
        }
        rewardedAd?.let { ad ->
            ad.show(this, OnUserEarnedRewardListener { rewardItem ->
                hintable = true
                btnHint.setImageResource(R.drawable.hint_icon_1)
            })
        } ?: run {
        }
    }


    private fun updateGrid() {
        val adView: AdView = findViewById(R.id.adView)
        val adRequest: AdRequest = AdRequest.Builder().build()
        adView.loadAd(adRequest)

        val gridLayout: GridLayout = findViewById(R.id.gridLayout)
        gridLayout.removeAllViews()

        var layout_side : Int = 0

        val btn1: Button = findViewById(R.id.btn1)
        val btn2: Button = findViewById(R.id.btn2)
        val btn3: Button = findViewById(R.id.btn3)
        val btn4: Button = findViewById(R.id.btn4)
        val btn5: Button = findViewById(R.id.btn5)
        val btn6: Button = findViewById(R.id.btn6)
        val btn7: Button = findViewById(R.id.btn7)
        val btn8: Button = findViewById(R.id.btn8)
        val btn9: Button = findViewById(R.id.btn9)

        val buttons = listOf(btn1, btn2, btn3, btn4, btn5, btn6, btn7, btn8, btn9)

        val btnErase: ImageButton = findViewById(R.id.btnErase)

        val btnHint: ImageButton = findViewById(R.id.btnHint)

        for (i in 0 until 9) {
            layout_side += 110
            for (j in 0 until 9) {
                val value = riddleGrid[i][j]

                val textView = TextView(this)

                if (value != 0) {
                    textView.text = value.toString()
                }
                textView.textAlignment = TextView.TEXT_ALIGNMENT_CENTER
                textView.textSize = 30f

                val params = FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.WRAP_CONTENT,
                    FrameLayout.LayoutParams.WRAP_CONTENT
                )
                params.width = 110
                params.height = 110
                params.gravity = Gravity.CENTER
                textView.layoutParams = params

                val frameLayout = FrameLayout(this)
                frameLayout.layoutParams = params
                frameLayout.addView(textView)

                val gridLayoutParams = GridLayout.LayoutParams()
                gridLayoutParams.width = 110
                gridLayoutParams.height = 110
                gridLayoutParams.rowSpec = GridLayout.spec(i)
                gridLayoutParams.columnSpec = GridLayout.spec(j)


                frameLayout.layoutParams = gridLayoutParams

                if (Pair(i, j) in redTextColorCells) {
                    textView.setTextColor(Color.RED)
                    if(colorGrid[i][j] == 1){
                        frameLayout.setBackgroundColor(Color.argb(85,173, 216, 230))
                    }
                    if(colorGrid[i][j] == 2){
                        frameLayout.setBackgroundColor(Color.rgb(173, 216, 230))
                    }
                }

                val borderView = View(this)
                borderView.layoutParams = FrameLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )

                if(i==0&&j==0){
                    borderView.setBackgroundResource(R.drawable.top_left_border)
                    frameLayout.addView(borderView)
                }
                else if((i==2||i==5||i==8)&&(j==2||j==5||j==8)){
                    borderView.setBackgroundResource(R.drawable.right_bottom_border)
                    frameLayout.addView(borderView)
                }
                else if(i==0&&(j==2||j==5||j==8)){
                    borderView.setBackgroundResource(R.drawable.top_right_border)
                    frameLayout.addView(borderView)
                }
                else if(j==0&&(i==2||i==5||i==8)){
                    borderView.setBackgroundResource(R.drawable.left_bottom_border)
                    frameLayout.addView(borderView)
                }
                else if(j==2 || j==5 || j==8) {
                    borderView.setBackgroundResource(R.drawable.right_border)
                    frameLayout.addView(borderView)
                }
                else if(i==2 || i==5 || i==8){
                    borderView.setBackgroundResource(R.drawable.bottom_border)
                    frameLayout.addView(borderView)
                }
                else if(i==0){
                    borderView.setBackgroundResource(R.drawable.top_border)
                    frameLayout.addView(borderView)
                }
                else if(j==0){
                    borderView.setBackgroundResource(R.drawable.left_border)
                    frameLayout.addView(borderView)
                }
                else{
                    borderView.setBackgroundResource(R.drawable.border)
                    frameLayout.addView(borderView)
                }

                frameLayout.setOnClickListener {
                    if (value == 0 || (value !=0 && Pair(i, j) in redTextColorCells)) {

                        setColor(i,j)

                        erasable = true

                        btnErase.setOnClickListener {
                            if(erasable){
                                riddleGrid[i][j] = 0
                                setColor(i,j)
                                updateGrid()
                            }
                        }

                        btnHint.setOnClickListener {
                            if(hintable){
                                if(riddleGrid[i][j] == 0) {
                                    hintable = false
                                    riddleGrid[i][j] = solvedgrid[i][j]
                                    setColor(i,j)
                                    updateGrid()
                                    btnHint.setImageResource(R.drawable.hint_icon_ad)
                                }
                            } else {
                                showRewardedAd(btnHint)
                            }
                        }

                        for (button in buttons) {
                            button.setOnClickListener {
                                if (!isMemoOn){
                                    val buttonText: CharSequence = button.text
                                    val intValue: Int = buttonText.toString().toInt()

                                    // 셀이 이미 올바른 값을 가지고 있는지 확인
                                    if (riddleGrid[i][j] == solvedgrid[i][j]) {
                                        // 이미 올바른 값을 가지고 있다면 변경을 허용하지 않음
                                        return@setOnClickListener
                                    }

                                    if (checkGrid(intValue, i, j)) {
                                        riddleGrid[i][j] = intValue
                                        savedGrid[i][j] = intValue
                                        redTextColorCells.remove(Pair(i, j))
                                    } else {
                                        riddleGrid[i][j] = intValue
                                        redTextColorCells.add(Pair(i, j))
                                    }
                                    setColor(i,j)
                                    updateGrid()
                                }
                            }
                        }
                        updateGrid()
                    }

                }

                // 현재 클릭된 프레임 레이아웃의 배경색 변경
                if(colorGrid[i][j] == 1){
                    frameLayout.setBackgroundColor(Color.argb(40,173, 216, 230))
                }
                if(colorGrid[i][j] == 2){
                    frameLayout.setBackgroundColor(Color.rgb(173, 216, 230))
                }

                gridLayout.addView(frameLayout)

            }
        }
        gridLayout.layoutParams.width = layout_side + 30
        gridLayout.layoutParams.height = layout_side + 30
        initcolor()
    }

//    private fun setMemoImage() {
//        val btnMemo: ImageButton = findViewById(R.id.btnMemo)
//
//        // isMemoOn 값에 따라 이미지 설정
//        if (isMemoOn) {
//            btnMemo.setImageResource(R.drawable.memo_on)
//        } else {
//            btnMemo.setImageResource(R.drawable.memo_off)
//        }
//    }
//
//    private fun updateMemoGrid(){
//
//        val btnMemo: ImageButton = findViewById(R.id.btnMemo)
//
//        btnMemo.setOnClickListener {
//            // 버튼 클릭 시 상태를 토글하고 이미지 업데이트
//            isMemoOn = !isMemoOn
//            setMemoImage()
//        }
//
//        if (isMemoOn) {
//            for (i in 0 until 9) {
//                for (j in 0 until 9) {
//
//                }
//            }
//        }
//    }

    private fun setColor(i: Int,j: Int){
        if(i<3&&j<3){
            for (a in 0 until 3){
                for (b in 0 until 3){
                    colorGrid[a][b] = 1
                }
            }
        }

        if(i>=3&&i<6&&j<3){
            for (a in 3 until 6){
                for (b in 0 until 3){
                    colorGrid[a][b] = 1
                }
            }
        }

        if(i>=6&&i<9&&j<3){
            for (a in 6 until 9){
                for (b in 0 until 3){
                    colorGrid[a][b] = 1
                }
            }
        }

        if(i<3&&j>=3&&j<6){
            for (a in 0 until 3){
                for (b in 3 until 6){
                    colorGrid[a][b] = 1
                }
            }
        }

        if(i>=3&&i<6&&j>=3&&j<6){
            for (a in 3 until 6){
                for (b in 3 until 6){
                    colorGrid[a][b] = 1
                }
            }
        }

        if(i>=6&&i<9&&j>=3&&j<6){
            for (a in 6 until 9){
                for (b in 3 until 6){
                    colorGrid[a][b] = 1
                }
            }
        }

        if(i<3&&j>=6&&j<9){
            for (a in 0 until 3){
                for (b in 6 until 9){
                    colorGrid[a][b] = 1
                }
            }
        }

        if(i>=3&&i<6&&j>=6&&j<9){
            for (a in 3 until 6){
                for (b in 6 until 9){
                    colorGrid[a][b] = 1
                }
            }
        }

        if(i>=6&&i<9&&j>=6&&j<9){
            for (a in 6 until 9){
                for (b in 6 until 9){
                    colorGrid[a][b] = 1
                }
            }
        }

        for (a in 0 until 9) {
            colorGrid[i][a] = 1
            colorGrid[a][j] = 1
            colorGrid[i][j] = 2
        }
    }

    private fun initcolor(){
        for (i in 0 until 9) {
            for (j in 0 until 9) {
                colorGrid[i][j] = 0
            }
        }
    }


    private fun checkGrid(answer: Int, i: Int, j: Int): Boolean{
        if(solvedgrid[i][j] == answer){
            erasable = false
            cntAnswer++
            if(difficulty == cntAnswer){
                gameEnd()
            }
            return true
        }else{
            return false
        }
    }

    private fun gameEnd(){
        val builder = AlertDialog.Builder(this)

        builder.setTitle("게임 종료") // 다이얼로그 제목
        builder.setMessage("축하합니다. 게임이 종료 되었습니다.") // 다이얼로그 내용

        // Positive 버튼
        builder.setPositiveButton("확인") { dialog, which ->
            val intent = Intent(this@MainActivity, TitleActivity::class.java)
            startActivity(intent)
        }

        builder.show()
    }


    fun InitGrid(grid: Array<IntArray>) {
        for (i in 0 until 9) {
            for (j in 0 until 9) {
                grid[i][j] = (i * 3 + i / 3 + j) % 9 + 1
            }
        }
    }

    fun ShuffleGrid(grid: Array<IntArray>, shuffleAmount: Int) {
        for (i in 0 until shuffleAmount) {
            val value1 = Random.nextInt(1, 10);
            val value2 = Random.nextInt(1, 10);

            MixTwoGridCells(grid, value1, value2)
        }
    }

    fun MixTwoGridCells(grid: Array<IntArray>, value1: Int, value2: Int){
        var x1 = 0;
        var x2 = 0;
        var y1 = 0;
        var y2 = 0;

        for (i in 0 until 9 step 3) {
            for (j in 0 until 9 step 3) {
                for (k in 0 until 3) {
                    for (l in 0 until 3) {
                        if(grid[i+k][j+l] == value1){
                            x1 = i + k;
                            y1 = j + l;
                        }

                        if(grid[i+k][j+l] == value2){
                            x2 = i + k;
                            y2 = j + l;
                        }
                    }
                }
                grid[x1][y1] = value2;
                grid[x2][y2] = value1;
            }
        }
    }

    fun CreateRiddleGrid(piecesToErase:Int){
        for (i in 0 until 9){
            for (j in 0 until 9){
                riddleGrid[i][j] = solvedgrid[i][j]
            }
        }

        for(i in 0 until piecesToErase){
            var x1 = Random.nextInt(0, 9);
            var y1 = Random.nextInt(0, 9);

            while (riddleGrid[x1][y1] == 0) {
                x1 = Random.nextInt(0, 9);
                y1 = Random.nextInt(0, 9);
            }

            riddleGrid[x1][y1] = 0;

        }
        for (i in 0 until 9){
            for (j in 0 until 9){
                savedGrid[i][j] = riddleGrid[i][j]
            }
        }

    }



    fun saveData(solvedgrid: Array<IntArray>, savedGrid: Array<IntArray>, cntAnswer: Int, difficulty: Int) {
        val sharedPreferences: SharedPreferences = getSharedPreferences("sudoku", Context.MODE_PRIVATE)
        val editor: SharedPreferences.Editor = sharedPreferences.edit()

        // solvedgrid, savedGrid, cntAnswer를 SharedPreferences에 저장
        editor.putInt("cntAnswer", cntAnswer)
        editor.putInt("difficulty", difficulty)
        for (i in 0 until 9) {
            for (j in 0 until 9) {
                editor.putInt("solvedgrid_$i$j", solvedgrid[i][j])
                editor.putInt("savedGrid_$i$j", savedGrid[i][j])
            }
        }
        editor.apply()
    }

    override fun onDestroy() {
        saveData(solvedgrid, savedGrid, cntAnswer, difficulty)

        super.onDestroy()
    }

}