package com.example.brainhub;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Random;

public class WordOfTheDayActivity extends AppCompatActivity {

    TextView tvWord, tvGrammar, tvMeaning;
    Button btnCreatePost, btnHomePage;


    ArrayList<Word> wordsList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wordoftheday);


        tvWord = findViewById(R.id.tvWord);
        tvGrammar = findViewById(R.id.tvGrammar);
        tvMeaning = findViewById(R.id.tvMeaning);
        btnCreatePost = findViewById(R.id.btnCreatePost);
        btnHomePage = findViewById(R.id.btnHomePage);

        wordsList.add(new Word("Cunning", "Adjective", "Having the ability to achieve one's goals by using cleverness, deceit, or trickery. It often involves manipulating situations or people through shrewd tactics to outsmart others, sometimes with an element of concealment or subtlety."));
        wordsList.add(new Word("Devious", "Adjective", "Characterized by the use of underhanded or dishonest tactics to achieve a particular goal. A devious person often employs secretive, indirect methods that are designed to mislead or manipulate others without them realizing their true intent."));
        wordsList.add(new Word("Calculating", "Adjective", "Marked by careful and deliberate planning, often with a focus on achieving personal gain. A calculating person tends to make decisions based on what will benefit them most, frequently considering the costs and consequences with cold logic and without concern for morality or ethics."));
        wordsList.add(new Word("Conniving", "Adjective", "Secretly plotting or scheming in a deceitful or manipulative way, often for personal gain. A conniving person works behind the scenes, employing trickery or underhanded methods to get what they want while keeping their true intentions hidden from others."));
        wordsList.add(new Word("Scheming", "Adjective", "Involving secret or deceptive planning, often for personal benefit. A scheming individual usually works in the background to orchestrate actions or events that will help them gain an advantage, often with hidden or malicious motives."));
        wordsList.add(new Word("Sly", "Adjective", "Demonstrating cleverness and craftiness, often in a dishonest or secretive manner. A sly person is skilled at achieving their goals in ways that are not immediately obvious, using trickery or subtle manipulation to outwit others or conceal their true intentions."));
        wordsList.add(new Word("Ruthless", "Adjective", "Showing no pity or compassion, particularly in the pursuit of one's goals. A ruthless individual is willing to act in a harsh, unforgiving manner, often disregarding the feelings or well-being of others in order to achieve their objectives without mercy or hesitation."));
        wordsList.add(new Word("Clever", "Adjective", "Quick-witted and able to think and act with ingenuity. A clever person demonstrates sharp intelligence and resourcefulness, often using creative or subtle methods to solve problems or outmaneuver others, which can sometimes involve manipulation or cunning tactics."));
        wordsList.add(new Word("Wily", "Adjective", "Skilled at gaining an advantage through cleverness, deceit, or trickery. A wily individual uses their intelligence and craftiness to outsmart others, often employing indirect or stealthy methods to achieve their goals without being detected or opposed."));
        wordsList.add(new Word("Shrewd", "Adjective", "Having sharp judgment and keen insight, particularly when it comes to understanding situations or people. A shrewd person is astute and perceptive, using their intelligence and awareness to make decisions that maximize their personal benefit, often at the expense of others."));
        wordsList.add(new Word("Duplicitous", "Adjective", "Marked by deceitfulness in speech or behavior, often involving the presentation of one thing while secretly intending another. A duplicitous individual engages in two-faced actions, saying one thing but doing another in order to manipulate or deceive others for personal advantage."));
        wordsList.add(new Word("Manipulative", "Adjective", "Characterized by the tendency to control or influence others through indirect, deceptive, or unfair means. A manipulative person often seeks to exploit situations or people to achieve their own goals, often without regard for others' well-being or desires."));
        wordsList.add(new Word("Machiavellian", "Adjective", "Relating to or characteristic of the political philosophy of Niccolò Machiavelli, often involving manipulation, deceit, and cunning in the pursuit of power. A Machiavellian person is skilled at using underhanded tactics to achieve their objectives, often at the expense of others."));
        wordsList.add(new Word("Schemer", "Noun", "A person who engages in secretive or deceitful planning, often to gain an advantage or achieve personal goals. A schemer typically works behind the scenes, manipulating circumstances or people in their favor, often without others realizing their true intentions."));
        wordsList.add(new Word("Guileful", "Adjective", "Having or displaying deceitful intelligence; skillfully crafty in achieving goals, often through manipulation or trickery. A guileful person uses their cleverness to mislead or deceive others for personal gain, often through subtle and indirect means."));
        wordsList.add(new Word("Insidious", "Adjective", "Proceeding in a gradual, subtle way but with harmful effects. An insidious person or action often involves hidden dangers or deceptive tactics that slowly undermine others, making it difficult to detect until significant damage has been done."));
        wordsList.add(new Word("Underhanded", "Adjective", "Characterized by dishonest, unfair, or deceitful actions, especially in a secretive or concealed manner. Underhanded actions are typically intended to gain an unfair advantage or to achieve goals without the knowledge of others involved."));
        wordsList.add(new Word("Deceitful", "Adjective", "Marked by a tendency to mislead, misrepresent, or conceal the truth to gain an advantage. A deceitful person uses falsehoods or omissions to manipulate others or to cover up their true intentions or actions."));
        wordsList.add(new Word("Subversive", "Adjective", "Seeking or intending to undermine the power, authority, or system in place, often through covert or deceptive means. A subversive individual works behind the scenes to destabilize or disrupt existing norms or authorities to achieve their personal or ideological goals."));

        setRandomWord();


        btnCreatePost.setOnClickListener(v -> {
            Intent intent = new Intent(WordOfTheDayActivity.this, CreatePost.class);
            startActivity(intent);
        });

        btnHomePage.setOnClickListener(v->{
            Intent intent = new Intent(WordOfTheDayActivity.this, FeedActivity.class);
            startActivity(intent);
        });
    }


    private void setRandomWord() {
        Random random = new Random();
        int randomIndex = random.nextInt(wordsList.size());
        Word selectedWord = wordsList.get(randomIndex);


        tvWord.setText(selectedWord.getWord());
        tvGrammar.setText(selectedWord.getGrammar());
        tvMeaning.setText(selectedWord.getMeaning());
    }


    static class Word {
        private String word;
        private String grammar;
        private String meaning;

        public Word(String word, String grammar, String meaning) {
            this.word = word;
            this.grammar = grammar;
            this.meaning = meaning;
        }

        public String getWord() {
            return word;
        }

        public String getGrammar() {
            return grammar;
        }

        public String getMeaning() {
            return meaning;
        }
    }
}
