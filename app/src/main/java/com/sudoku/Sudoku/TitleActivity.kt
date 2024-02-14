package com.sudoku.Sudoku

import android.animation.ObjectAnimator
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import androidx.appcompat.app.AlertDialog

class TitleActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_title)

        val myImageView = findViewById<ImageView>(R.id.myImageView)

        val sudokuData: SudokuData = loadData()

        val loadedSolvedGrid: Array<IntArray> = sudokuData.loadedSolvedGrid
        val loadedSavedGrid: Array<IntArray> = sudokuData.loadedSavedGrid
        val loadedCntAnswer: Int = sudokuData.loadedCntAnswer
        val loadedDifficulty: Int = sudokuData.loadedDifficulty

        // ObjectAnimator를 사용하여 이미지뷰 크기 애니메이션 설정
        val scaleXAnimator = ObjectAnimator.ofFloat(myImageView, "scaleX", 1f, 1.5f, 1f)
        val scaleYAnimator = ObjectAnimator.ofFloat(myImageView, "scaleY", 1f, 1.5f, 1f)

        // 애니메이션 시간 설정 (1초 동안 실행)
        scaleXAnimator.duration = 1000
        scaleYAnimator.duration = 1000

        // 애니메이션을 반복하도록 설정
        scaleXAnimator.repeatCount = ObjectAnimator.INFINITE
        scaleYAnimator.repeatCount = ObjectAnimator.INFINITE

        // 애니메이션 시작
        scaleXAnimator.start()
        scaleYAnimator.start()

        val btn_newgame: Button = findViewById(R.id.btn_newgame)

        btn_newgame.setOnClickListener{
            val sharedPreferences: SharedPreferences = getSharedPreferences("sudoku", MODE_PRIVATE)
            val editor: SharedPreferences.Editor = sharedPreferences.edit()
            editor.clear()
            editor.apply()

            setupDifficulty()
        }

        val btn_continue: Button = findViewById(R.id.btn_continue)

        val allZeros = loadedSavedGrid.all { row -> row.all { it == 0 } }

        if (allZeros) {
            // loadedCntAnswer가 0이면 버튼을 숨김
            btn_continue.visibility = View.GONE
        } else {
            // loadedCntAnswer가 0이 아니면 버튼을 보이게 함
            btn_continue.visibility = View.VISIBLE
        }

        btn_continue.setOnClickListener{
            intent = Intent(this@TitleActivity, MainActivity::class.java)
            intent.putExtra("loadedSolvedGrid", loadedSolvedGrid)
            intent.putExtra("loadedSavedGrid", loadedSavedGrid)
            intent.putExtra("loadedCntAnswer", loadedCntAnswer)
            intent.putExtra("loadedDifficulty", loadedDifficulty)
            startActivity(intent)
        }

    }



    private  fun setupDifficulty() {
        // Create a dark overlay view
        val darkOverlay = View(this)
        darkOverlay.layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        darkOverlay.setBackgroundColor(Color.parseColor("#80000000"))

        // Add the overlay to the root view of the activity
        val rootView = window.decorView.findViewById<ViewGroup>(android.R.id.content)
        rootView.addView(darkOverlay)


        // 다이얼로그 레이아웃을 불러옴
        val dialogView = layoutInflater.inflate(R.layout.difficulty, null)

        // 다이얼로그 생성
        val dialog = AlertDialog.Builder(this, R.style.dialog_difficulty)
            .setView(dialogView)
            .setCancelable(true)
            .create()

        dialog.setOnCancelListener {
            rootView.removeView(darkOverlay)
        }

        // 각 버튼에 대한 클릭 이벤트 처리
        val btn_easy = dialogView.findViewById<Button>(R.id.btn_easy)
        val btn_medium = dialogView.findViewById<Button>(R.id.btn_medium)
        val btn_hard = dialogView.findViewById<Button>(R.id.btn_hard)
        val btn_expert = dialogView.findViewById<Button>(R.id.btn_expert)

        var intent: Intent

        btn_easy.setOnClickListener {
            intent = Intent(this@TitleActivity, MainActivity::class.java)
            intent.putExtra("difficulty", 40)
            startActivity(intent)
            dialog.dismiss()
            rootView.removeView(darkOverlay)
        }

        btn_medium.setOnClickListener {
            intent = Intent(this@TitleActivity, MainActivity::class.java)
            intent.putExtra("difficulty", 45)
            startActivity(intent)
            dialog.dismiss()
            rootView.removeView(darkOverlay)
        }

        btn_hard.setOnClickListener {
            intent = Intent(this@TitleActivity, MainActivity::class.java)
            intent.putExtra("difficulty", 55)
            startActivity(intent)
            dialog.dismiss()
            rootView.removeView(darkOverlay)
        }

        btn_expert.setOnClickListener {
            intent = Intent(this@TitleActivity, MainActivity::class.java)
            intent.putExtra("difficulty", 65)
            startActivity(intent)
            dialog.dismiss()
            rootView.removeView(darkOverlay)
        }

        dialog.show()
    }

    fun loadData(): SudokuData {
        val sharedPreferences: SharedPreferences = getSharedPreferences("sudoku", Context.MODE_PRIVATE)

        // SharedPreferences에서 데이터를 불러와서 반환
        val loadedCntAnswer = sharedPreferences.getInt("cntAnswer", 0)
        val loadedDifficulty = sharedPreferences.getInt("difficulty", 0)
        val loadedSolvedGrid = Array(9) { IntArray(9) }
        val loadedSavedGrid = Array(9) { IntArray(9) }

        for (i in 0 until 9) {
            for (j in 0 until 9) {
                loadedSolvedGrid[i][j] = sharedPreferences.getInt("solvedgrid_$i$j", 0)
                loadedSavedGrid[i][j] = sharedPreferences.getInt("savedGrid_$i$j", 0)
            }
        }

        return SudokuData(loadedSolvedGrid, loadedSavedGrid, loadedCntAnswer, loadedDifficulty)
    }

    data class SudokuData(
        val loadedSolvedGrid: Array<IntArray>,
        val loadedSavedGrid: Array<IntArray>,
        val loadedCntAnswer: Int,
        val loadedDifficulty: Int
    )
}