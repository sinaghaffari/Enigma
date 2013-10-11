package main;

import java.io.*;
import java.util.ArrayList;
import java.util.StringTokenizer;

public class HighScores {

	private PrintWriter outFile;
	private BufferedReader inFile;
	private ArrayList<Integer> scores= new ArrayList<Integer>();
	private ArrayList<String> scorenames= new ArrayList<String>();

	public HighScores() throws IOException{

		inFile=new BufferedReader(new FileReader("highscores.hs"));
		StringTokenizer st;
		String at=inFile.readLine();

		while(at!=null){ //add all the scores in the file to parallel arraylists
			st=new StringTokenizer(at);
			scorenames.add(st.nextToken());	
			scores.add(Integer.parseInt(st.nextToken()));								
			at=inFile.readLine();
		}

	}	

	private void updateFile() throws IOException{
		outFile=new PrintWriter(new FileWriter("highscores.hs"));

		for(int i=0;i<scores.size();++i){
			outFile.println(scorenames.get(i)+" "+scores.get(i));		
		}

		outFile.close();
	}

	public void addScore(int inscore, String inname){


		if(scores.size()==0){
			scores.add(inscore);
			scorenames.add(inname);
		}
		else{	

			for(int i=0;i<scores.size();++i){
				if(inscore>scores.get(i)){
					scores.add(i, inscore);
					scorenames.add(i, inname);
					break;
				}
			}
		}

		try {
			updateFile();
		} catch (IOException e) {}

	}

	public String[] getScores(){
		String[] out=new String[scores.size()];

		for(int i=0;i<scores.size();++i){
			out[i]=scorenames.get(i)+" "+scores.get(i);		
		}		
		return out;
	}


	public void printScores(){
		for(int i=0;i<scores.size();++i){
			System.out.println(scorenames.get(i)+" "+scores.get(i));		
		}
	}


}
