/**
 * Android Speech Diarization - Calculates the amount of time spent speaking by each speaker
 * 								in a conversation
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

public class Conversation {
	Turn[] turns;
	int numSpeakers;
	
	public Conversation(String pathToSegFile){
		FileReader segFile = null;
		numSpeakers = 0;
		
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
				StringTokenizer segFileEntry = new StringTokenizer(line);
				
				segFileEntry.nextToken(); //show not used
				segFileEntry.nextToken(); //channel not used
				turns[(numSpeakers-1)].start = Integer.parseInt(segFileEntry.nextToken());
				turns[(numSpeakers-1)].length = Integer.parseInt(segFileEntry.nextToken());
				segFileEntry.nextToken(); //gender not used
				segFileEntry.nextToken(); //band not used
				segFileEntry.nextToken(); //environment not used
				turns[(numSpeakers-1)].speaker = segFileEntry.nextToken();

				turns[(numSpeakers-1)].end = turns[(numSpeakers-1)].start + turns[(numSpeakers-1)].length - 1;
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
		
	}
	


}
