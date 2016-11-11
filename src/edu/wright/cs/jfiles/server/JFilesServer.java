/*
 * Copyright (C) 2016 - WSU CEG3120 Students
 * 
 * Roberto C. Sánchez <roberto.sanchez@wright.edu>
 * Matthew T. Trippel <trippel.3@wright.edu>
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

package edu.wright.cs.jfiles.server;

import edu.wright.cs.jfiles.common.Error;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.w3c.dom.Document;

import edu.wright.cs.jfiles.common.XmlHandler2;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Properties;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactoryConfigurationError;

/**
 * The main class of the JFiles server application.
 * 
 * @author Roberto C. Sánchez &lt;roberto.sanchez@wright.edu&gt;
 *
 */
public class JFilesServer implements Runnable {

	static final Logger logger = LogManager.getLogger(JFilesServer.class);
	private static int PORT;
	private static int MAXTHREADS;
	private final ServerSocket serverSocket;
	private static final String UTF_8 = "UTF-8";

	/**
	 * Handles allocating resources needed for the server.
	 * 
	 * @throws IOException
	 *             If there is a problem binding to the socket
	 */
	
	private static void init() throws IOException {	
		//Array of strings containing possible paths to check for config files
		ArrayList<String> configPaths = new ArrayList<String>();
		configPaths.add("/usr/local/etc/jfiles/serverConfig.xml");
		configPaths.add("/usr/local/etc/jfiles/serverConfig.xml");
		configPaths.add("/opt/etc/jfiles/serverConfig.xml");
		configPaths.add("/etc/jfiles/serverConfig.xml");
		configPaths.add(System.getProperty("user.home") + "/jfiles/serverConfig.xml");
		configPaths.add(System.getProperty("user.home") + "/.jfiles/serverConfig.xml");
		
		FileInputStream fis = null;
		File config = null;
				
		//Checking location(s) for the config file);
		for (int i = 0; i < configPaths.size(); i++) {
			if (new File(configPaths.get(i)).exists()) {
				config = new File(configPaths.get(i));
				break;
			}
		}
		
		Properties prop = new Properties();
		
		//Output location where the config file was found. Otherwise warn and use defaults.
		if (config == null) {		
			logger.info("No config file found. Using default values.");
		} else {
			logger.info("Config file found in " + config.getPath());
			//Read file
			try {
				//Reads xmlfile into prop object as key value pairs
				fis = new FileInputStream(config);
				prop.loadFromXML(fis);			
			} catch (IOException e) {
				logger.error(Error.IOEXCEPTION1.getDescription(), e);
			} finally {
				if (fis != null) {
					fis.close();
				}
			}
		}
	
		//Add setters here. First value is the key name and second is the default value.
		//Default values are require as they are used if the config file cannot be found OR if
		// the config file doesn't contain the key.
		PORT = Integer.parseInt(prop.getProperty("Port","9786"));
		logger.info("Config set to port " + PORT);
		
		MAXTHREADS = Integer.parseInt(prop.getProperty("maxThreads","10"));
		logger.info("Config set max threads to " + MAXTHREADS);		
	}
	
	/**
	 * This is a Javadoc comment to statisfy Checkstyle.
	 * @throws IOException When bad things happen
	 */
	public JFilesServer() throws IOException {
		serverSocket = new ServerSocket(PORT);
	}
	
	


	@Override
	public void run() {
		String dir = System.getProperty("user.dir");
		try (Socket server = serverSocket.accept()) {
			logger.info("Received connection from" + server.getRemoteSocketAddress());
			InputStreamReader isr = new InputStreamReader(server.getInputStream(), UTF_8);
			BufferedReader in = new BufferedReader(isr);
			String cmd;
			while (null != (cmd = in.readLine())) {
				if ("".equals(cmd)) {
					break;
				}
				OutputStreamWriter osw = new OutputStreamWriter(server.getOutputStream(), UTF_8);
				BufferedWriter out = new BufferedWriter(osw);
				String[] baseCommand = cmd.split(" ");
				if ("LIST".equalsIgnoreCase(baseCommand[0])) {
					try (DirectoryStream<Path> directoryStream = 
							Files.newDirectoryStream(Paths.get(dir))) {
						for (Path path : directoryStream) {
							out.write(path.toString() + "\n");
						}
					}
				}
				// start Search block
				if ("FIND".equalsIgnoreCase(baseCommand[0])) {
					try (DirectoryStream<Path> directoryStream = 
							Files.newDirectoryStream(Paths.get(dir))) {
						for (Path path : directoryStream) {
							// out.write(path.toString() + "\n");
							if (path.toString().contains(baseCommand[1])) {
								out.write(path.toString() + "\n");
							}
						}
					}
				} else { // End search block
					logger.error(Error.UNKNOWN_COMMAND.toString());
				}
				out.flush();
			}
		} catch (IOException e) {
			//TODO Auto-generated catch block
			//e.printStackTrace();
			logger.error("Some error occurred", e);
		}
	}

	/**
	 * The main entry point to the program.
	 * 
	 * @throws IOException
	 * If there is a problem binding to the socket
	 */
	public static void main(String[] args) {
		try {
			init();
			XmlHandler2 aaa = new XmlHandler2(Paths.get("/home/brian/git/jfiles"));
			OutputStreamWriter osw = new OutputStreamWriter(new FileOutputStream(new File("test.xml")));
			aaa.sendXml(osw);
			logger.info("Starting the server");
			XmlHandler handler = new XmlHandler(logger);
			try {
				Document doc = handler.createXml("fileSystem");
				handler.parseXml(doc);
			} catch (TransformerFactoryConfigurationError e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (TransformerException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			//Thread thread = new Thread(jf);
			//thread.start();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
