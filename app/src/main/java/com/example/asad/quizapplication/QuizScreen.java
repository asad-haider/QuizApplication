package com.example.asad.quizapplication;

import android.content.Context;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class QuizScreen extends AppCompatActivity {


    TextView questionTextView, timerTextView, questionCounterTextView;
    RadioButton optionABtn, optionBBtn, optionCBtn, optionDBtn;
    Button submitButton;
    String[] answers;
    int questionCounter = 0;
    private QuizDatabase.Question question;
    private QuizDatabase.Quiz quiz;
    private QuizDatabase db;
    private ListView quizListView;
    private LinearLayout questionsLinearLayout;
    private ArrayList<QuizDatabase.Question> questions;
    private String answer;
    private CountDownTimer timer;

    public class QuizAdapter extends ArrayAdapter<QuizDatabase.Quiz>{

        private final LayoutInflater mInflater;

        public QuizAdapter(Context context, ArrayList<QuizDatabase.Quiz> quizArrayList) {
            super(context, R.layout.quiz_listview_item, quizArrayList);
            mInflater = LayoutInflater.from(context);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            if (convertView == null){
                convertView = mInflater.inflate(R.layout.quiz_listview_item, null);

                QuizDatabase.Quiz quiz = getItem(position);

                System.out.println(quiz.quizName);
                System.out.println(quiz.numberOfQuestions);

                ((TextView)convertView.findViewById(R.id.quizName)).setText(quiz.quizName);
                ((TextView)convertView.findViewById(R.id.numberOfQuestion)).setText("" + quiz.numberOfQuestions);
            }

            return convertView;
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (timer != null){
            timer.cancel();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_screen);



        questionTextView = (TextView) findViewById(R.id.questionTextView);
        timerTextView = (TextView) findViewById(R.id.timerTextView);
        questionCounterTextView = (TextView) findViewById(R.id.questionCounter);
        quizListView = (ListView) findViewById(R.id.quizListView);
        questionsLinearLayout = (LinearLayout) findViewById(R.id.questionsLinearLayout);

        quizListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                quiz = (QuizDatabase.Quiz) parent.getItemAtPosition(position);
                quizListView.setVisibility(View.GONE);
                questionsLinearLayout.setVisibility(View.VISIBLE);

                questions = quiz.questions;
                question = questions.get(0);

                answers = new String[quiz.numberOfQuestions];

                questionTextView.setText(question.question);
                questionCounterTextView.setText(+questionCounter + 1 + "/" + quiz.numberOfQuestions);
                optionABtn.setText(question.option.optionA);
                optionBBtn.setText(question.option.optionB);
                optionCBtn.setText(question.option.optionC);
                optionDBtn.setText(question.option.optionD);


                timer = new CountDownTimer(120000, 1000) { // adjust the milli seconds here

                    public void onTick(long millisUntilFinished) {
                        timerTextView.setText(""+String.format("%d min, %d sec",
                                TimeUnit.MILLISECONDS.toMinutes( millisUntilFinished),
                                TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) -
                                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished))));
                    }

                    public void onFinish() {
                        for (int i = questionCounter; i < answers.length; i++) {
                            if (answers[i] == null){
                                answers[i] = "";
                            }
                        }

                        int score = db.CheckResult(quiz.pkQuizId, 1, answers);
                        Toast.makeText(QuizScreen.this, "Times Up: Your Score: " + score, Toast.LENGTH_LONG).show();
                        QuizScreen.this.finish();

                    }
                };

                timer.start();

            }
        });

        optionABtn = (RadioButton) findViewById(R.id.optionABtn);
        optionBBtn = (RadioButton) findViewById(R.id.optionBBtn);
        optionCBtn = (RadioButton) findViewById(R.id.optionCBtn);
        optionDBtn = (RadioButton) findViewById(R.id.optionDBtn);

        submitButton = (Button) findViewById(R.id.submitBtn);
        db = new QuizDatabase(this);

        ArrayList<QuizDatabase.Quiz> quizs = db.GetAllQuizes();

        QuizAdapter adapter = new QuizAdapter(this, quizs);
        quizListView.setAdapter(adapter);

        RadioGroup rGroup = (RadioGroup)findViewById(R.id.radioGroup);
        RadioButton checkedRadioButton = (RadioButton)rGroup.findViewById(rGroup.getCheckedRadioButtonId());
        answer = checkedRadioButton.getText().toString();

        rGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
        {
            public void onCheckedChanged(RadioGroup rGroup, int checkedId)
            {
                RadioButton checkedRadioButton = (RadioButton)rGroup.findViewById(checkedId);
                boolean isChecked = checkedRadioButton.isChecked();
                if (isChecked) {
                    answer = checkedRadioButton.getText().toString();
                }
            }
        });

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                answers[questionCounter++] = answer;

                if (questionCounter == quiz.numberOfQuestions){
                    int score = db.CheckResult(quiz.pkQuizId, 1, answers);
                    Toast.makeText(QuizScreen.this, "Score: " + score, Toast.LENGTH_SHORT).show();
                    QuizScreen.this.finish();
                }else{
                    question = questions.get(questionCounter);
                    questionTextView.setText(question.question);
                    questionCounterTextView.setText(questionCounter + 1 + "/" + quiz.numberOfQuestions);
                    optionABtn.setText(question.option.optionA);
                    optionBBtn.setText(question.option.optionB);
                    optionCBtn.setText(question.option.optionC);
                    optionDBtn.setText(question.option.optionD);
                }
            }
        });
    }
}
