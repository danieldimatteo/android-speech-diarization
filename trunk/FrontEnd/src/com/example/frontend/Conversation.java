/**
 * Android Speech Diarization - Calculates the amount of time spent speaking
 * 								by each speaker in a conversation
 * 
 * Copyright (C) 2011  Daniel Di Matteo
 *
 * 
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * 
 */

package com.example.frontend;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.StringTokenizer;
import java.util.Vector;


public class Conversation {
	public Vector<Turn> turns;
	int numSpeakers;
	int totalLength;
	
	public Conversation(String pathToSegFile){
		turns = new Vector<Turn>();
		FileReader segFile = null;
		numSpeakers = 0;
		totalLength = 0;
		
		try {
			segFile = new FileReader(pathToSegFile);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		BufferedReader in = new BufferedReader(segFile);
		String line;
		
		try {
			while( (line = in.readLine()) != null ){
				numSpeakers++;
				
				Turn turnFromSegFile = new Turn();
				
				StringTokenizer segFileEntry = new StringTokenizer(line);
				
				segFileEntry.nextToken(); //show not used
				segFileEntry.nextToken(); //channel not used
				turnFromSegFile.start = Integer.parseInt(segFileEntry.nextToken());
				turnFromSegFile.length = Integer.parseInt(segFileEntry.nextToken());
				segFileEntry.nextToken(); //gender not used
				segFileEntry.nextToken(); //band not used
				segFileEntry.nextToken(); //environment not used
				turnFromSegFile.speaker = segFileEntry.nextToken();

				turnFromSegFile.end = turnFromSegFile.start + turnFromSegFile.length - 1;
				
				totalLength += turnFromSegFile.length;

				turns.add(turnFromSegFile);	
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
		
		//Go back and add % spoke (can't do until we have total length):
    	for (int i = 0; i < numSpeakers; i++ ) {
    		turns.get(i).percentSpeaking = (100 * turns.get(i).length) / totalLength;
    	}
				
	}
	


}
