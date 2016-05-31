package com.example.asad.quizapplication;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class CreateQuiz extends AppCompatActivity {


    Button nextBtn, insertQuestionBtn;
    EditText quizNameEditText, noOfQuestionEditText, questionEditText, answerEditText,
            optionAEditText,optionBEditText, optionCEditText, optionDEditText;

    String quizName;
    int numberOfQuestions;
    private QuizDatabase db;
    private int quizID;
    private int questionCounter = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_quiz);

        db = new QuizDatabase(CreateQuiz.this);

        nextBtn = (Button) findViewById(R.id.nextBtn);
        insertQuestionBtn = (Button) findViewById(R.id.insertQuestionBtn);

        quizNameEditText = (EditText) findViewById(R.id.quizNameEditText);
        noOfQuestionEditText = (EditText) findViewById(R.id.noOfQuestionsEditText);

        questionEditText = (EditText) findViewById(R.id.questionEditText);
        answerEditText = (EditText) findViewById(R.id.answerEditText);
        optionAEditText = (EditText) findViewById(R.id.optionAEditText);
        optionBEditText = (EditText) findViewById(R.id.optionBEditText);
        optionCEditText = (EditText) findViewById(R.id.optionCEditText);
        optionDEditText = (EditText) findViewById(R.id.optionDEditText);

        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!(quizNameEditText.getText().toString().equals("") || noOfQuestionEditText.getText().toString().equals(""))){
                    quizName = quizNameEditText.getText().toString();
                    numberOfQuestions = Integer.parseInt(noOfQuestionEditText.getText().toString());

                    quizID = db.AddQuiz(quizName, numberOfQuestions);

                    quizNameEditText.setVisibility(View.GONE);
                    noOfQuestionEditText.setVisibility(View.GONE);
                    nextBtn.setVisibility(View.GONE);

                    questionEditText.setVisibility(View.VISIBLE);
                    answerEditText.setVisibility(View.VISIBLE);
                    optionAEditText.setVisibility(View.VISIBLE);
                    optionBEditText.setVisibility(View.VISIBLE);
                    optionCEditText.setVisibility(View.VISIBLE);
                    optionDEditText.setVisibility(View.VISIBLE);
                    insertQuestionBtn.setVisibility(View.VISIBLE);

                    questionEditText.setHint("Enter Question # 1");

                }else{
                    Toast.makeText(CreateQuiz.this, "Please Insert Both Quiz Name and Number Of Questions", Toast.LENGTH_SHORT).show();
                }
            }
        });


        insertQuestionBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!(questionEditText.getText().toString().equals("") ||
                        answerEditText.getText().toString().equals("") ||
                        optionAEditText.getText().toString().equals("") ||
                        optionBEditText.getText().toString().equals("") ||
                        optionCEditText.getText().toString().equals("") ||
                        optionDEditText.getText().toString().equals("")
                )){

                    String question = questionEditText.getText().toString();
                    String answer = answerEditText.getText().toString();
                    String optionA = optionAEditText.getText().toString();
                    String optionB = optionBEditText.getText().toString();
                    String optionC = optionCEditText.getText().toString();
                    String optionD = optionDEditText.getText().toString();

                    boolean questionAdded = db.AddQuestion(quizID, question, answer, new String[]{
                       optionA, optionB, optionC, optionD
                    });

                    if (questionAdded){

                        if (questionCounter == numberOfQuestions){
                            Toast.makeText(CreateQuiz.this, "Quiz Created!", Toast.LENGTH_SHORT).show();
                            CreateQuiz.this.finish();
                        }else{
                            questionEditText.setText("");
                            questionEditText.setHint("Enter Question # " + ++questionCounter);
                            answerEditText.setText("");
                            optionAEditText.setText("");
                            optionBEditText.setText("");
                            optionCEditText.setText("");
                            optionDEditText.setText("");

                        }

                    }else{
                        Toast.makeText(CreateQuiz.this, "Error in Adding Question", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

    }
}
