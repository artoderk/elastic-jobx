package com.dangdang.ddframe.job.internal.monitor;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * 
 * @author xiong.j support jdk1.6
 *
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class SocketUtils {
    
    public static String sendCommand(final String command, final int monitorPort) throws IOException {
    	Socket socket = null;
    	BufferedReader reader = null;
    	BufferedWriter writer = null;
        try {
        	socket = new Socket("127.0.0.1", monitorPort);
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            
            writer.write(command);
            writer.newLine();
            writer.flush();
            return reader.readLine();
        } finally {
        	if (socket != null) {
        		socket.close();
        	}
        	if (reader != null) {
        		reader.close();
        	}
        	if (writer != null) {
        		writer.close();
        	}
        }
    }
}
