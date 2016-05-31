package com.example.asad.quizapplication;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

import java.util.ArrayList;

public class QuizDatabase extends SQLiteAssetHelper {

    private static final String DATABASE_NAME = "quizgamedb.sqlite";
    private static final int DATABASE_VERSION = 1;
    public Context context;

    public QuizDatabase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    public void AddPlayer(String name, String id, String password){
        QuizDatabase quizDatabase = new QuizDatabase(context);
        SQLiteDatabase db = quizDatabase.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("name", name);
        values.put("id", id);
        values.put("password", password);
        db.insert("Player", null, values);

        db.close();
    }

    public void DeletePlayer(String id){
        QuizDatabase quizDatabase = new QuizDatabase(context);
        SQLiteDatabase db = quizDatabase.getWritableDatabase();
        db.execSQL("delete from Player where id='"+id+"'");
        db.close();
    }

    public boolean CheckPlayer(String id, String password){
        QuizDatabase quizDatabase = new QuizDatabase(context);
        SQLiteDatabase db = quizDatabase.getWritableDatabase();
        String query = "SELECT  * FROM Player WHERE id='"+id+"' & password='"+password+"'";
        Cursor cursor = db.rawQuery(query, null);
        db.close();
        return cursor.moveToFirst();
    }

    public int AddQuiz(String quizName, int numberOfQuestions){
        QuizDatabase quizDatabase = new QuizDatabase(context);
        SQLiteDatabase db = quizDatabase.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("quizName", quizName);
        values.put("numberOfQuestions", numberOfQuestions);
        long quizId = db.insert("Quiz", null, values);

        db.close();
        return (int) quizId;
    }

    public boolean AddQuestion(int fkQuizId, String question, String answer, String[] options){
        QuizDatabase quizDatabase = new QuizDatabase(context);
        SQLiteDatabase db = quizDatabase.getWritableDatabase();

        String[] optionsName = {"A", "B", "C", "D"};

        ContentValues values = new ContentValues();

        for (int i = 0; i < options.length; i++) {
            values.put("option" + optionsName[i], options[i]);
        }

        long pkOptionId = db.insert("Option", null, values);
        values.clear();

        values.put("fkQuizId", fkQuizId);
        values.put("fkOptionId", pkOptionId);
        values.put("question", question);
        values.put("answer", answer);
        long quizId = db.insert("Question", null, values);

        if (quizId == -1){
            return false;
        }else{
            return true;
        }
    }

    public ArrayList<Quiz> GetAllQuizes(){
        QuizDatabase quizDatabase = new QuizDatabase(context);
        SQLiteDatabase db = quizDatabase.getReadableDatabase();

        String query = "SELECT  * FROM Quiz";
        Cursor quizCursor = db.rawQuery(query, null);

        ArrayList<Quiz> quizs = new ArrayList<>();

        if (quizCursor.moveToFirst()) {
            do {
                Quiz quiz = new Quiz();
                quiz.pkQuizId = quizCursor.getInt(0);
                quiz.quizName = quizCursor.getString(1);
                quiz.numberOfQuestions = quizCursor.getInt(2);

                query = "SELECT  * FROM Question where fkQuizId='"+quiz.pkQuizId+"'";
                Cursor questionCursor = db.rawQuery(query, null);

                ArrayList<Question> questions = new ArrayList<>();

                if (questionCursor.moveToFirst()){
                    do{

                        Question question = new Question();
                        question.fkOptionId = questionCursor.getInt(2);
                        question.question = questionCursor.getString(3);
                        question.answer = questionCursor.getString(4);

                        query = "SELECT  * FROM Option where pkOptionId='"+question.fkOptionId+"'";
                        Cursor optionCursor = db.rawQuery(query, null);

                        Option option = new Option();

                        if (optionCursor.moveToFirst()){
                            do{
                                option.optionA = optionCursor.getString(1);
                                option.optionB = optionCursor.getString(2);
                                option.optionC = optionCursor.getString(3);
                                option.optionD = optionCursor.getString(4);

                            }while (optionCursor.moveToNext());
                        }

                        question.option = option;

                        questions.add(question);

                    }while (questionCursor.moveToNext());
                }

                quiz.questions = questions;

                quizs.add(quiz);

            } while (quizCursor.moveToNext());
        }

        db.close();

        return  quizs;
    }

    public int CheckResult(int fkQuizId, int fkPlayerId, String[] answers){

        QuizDatabase quizDatabase = new QuizDatabase(context);
        SQLiteDatabase db = quizDatabase.getWritableDatabase();
        String query = "SELECT * FROM Question WHERE fkQuizId='"+fkQuizId+"'";
        Cursor cursor = db.rawQuery(query, null);

        int score = 0;
        int counter = 0;

        if (cursor.moveToFirst()){
            do{
                if (answers[counter++].equals(cursor.getString(4))){
                    score++;
                }
            }while (cursor.moveToNext());
        }

        ContentValues values = new ContentValues();
        values.put("fkQuizId", fkQuizId);
        values.put("fkPlayerId", fkPlayerId);
        values.put("score", score);
        db.insert("Result", null, values);

        cursor.close();
        db.close();

        return score;
    }


    public class Quiz{
        int pkQuizId;
        String quizName;
        int numberOfQuestions;
        ArrayList<Question> questions;
    }

    public class Question{
        int fkOptionId;
        String question;
        String answer;
        Option option;
    }

    public class Option{
        String optionA;
        String optionB;
        String optionC;
        String optionD;
    }
}