package com.yahoo.labs.samoa.topology.impl;

/*
 * #%L
 * SAMOA
 * %%
 * Copyright (C) 2013 Yahoo! Inc.
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import com.github.javacliparser.ClassOption;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.yahoo.labs.samoa.tasks.Task;

/**
 * Utility class for samoa-storm project. It is used by StormDoTask to process its arguments.
 * @author Arinto Murdopo
 *
 */
public class StormSamoaUtils {
	
	private static final Logger logger = LoggerFactory.getLogger(StormSamoaUtils.class);

	static final String KEY_FIELD = "key";
	static final String CONTENT_EVENT_FIELD = "content_event";
		
	static Properties getProperties() throws IOException{
		Properties props = new Properties();
		InputStream is = null;
		
		File f = new File("src/main/resources/samoa-storm-cluster.properties");
		is = new FileInputStream(f);
		
		try {
			props.load(is);
		} catch (IOException e1) {
			System.out.println("Fail to load property file");
			return null;
		} finally{
			is.close();
		}
		
		return props;
	}
	
	public static StormTopology argsToTopology(String[] args){
		StringBuilder cliString = new StringBuilder();
		for (int i = 0; i < args.length; i++) {
			cliString.append(" ").append(args[i]);
		}
		logger.debug("Command line string = {}", cliString.toString());

		Task task = getTask(cliString.toString());
		
		//TODO: remove setFactory method with DynamicBinding
		task.setFactory(new StormComponentFactory());
		task.init();
			
		StormTopology stormTopo = (StormTopology)task.getTopology();
		return stormTopo;
	}
	
	public static int numWorkers(List<String> tmpArgs){
		int position = tmpArgs.size() - 1;
		int numWorkers = -1;
		
		try {
			numWorkers = Integer.parseInt(tmpArgs.get(position));
			tmpArgs.remove(position);
		} catch (NumberFormatException e) {
			numWorkers = 4;
		}
		
		return numWorkers;
	}

    public static Task getTask(String cliString) {
        Task task = null;
        try {
            logger.debug("Providing task [{}]", cliString);
            task = (Task) ClassOption.cliStringToObject(cliString, Task.class, null);
        } catch (Exception e) {
            logger.warn("Fail in initializing the task!");
            e.printStackTrace();
        }
        return task;
    }
}
