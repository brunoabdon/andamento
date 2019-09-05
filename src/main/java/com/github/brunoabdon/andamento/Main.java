package com.github.brunoabdon.andamento;

import java.io.IOException;

import org.jline.reader.EndOfFileException;
import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import org.jline.reader.UserInterruptException;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;

public class Main {

	
	public static void main(String[] args) throws IOException {
		  final Terminal terminal = 
			  TerminalBuilder.builder().name("andamento").build();
		  
		  final LineReader reader = 
			  LineReaderBuilder
			  	.builder()
			  	.appName("Andamento")
                .terminal(terminal)
                .build();		  
		  
		    String prompt = "#";
		    while (true) {
		        String line = null;
		        try {
		            line = reader.readLine(prompt);
		        } catch (final UserInterruptException e) {
		            // Ignore
		        } catch (final EndOfFileException e) {
		            return;
		        }
		    }		  
	}
	
	
}
