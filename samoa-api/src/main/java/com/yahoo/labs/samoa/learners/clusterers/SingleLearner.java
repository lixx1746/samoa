package com.yahoo.labs.samoa.learners.clusterers;

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

/**
 * License
 */


import com.github.javacliparser.ClassOption;
import com.github.javacliparser.Configurable;
import com.yahoo.labs.samoa.core.Processor;
import com.yahoo.labs.samoa.instances.Instances;
import com.yahoo.labs.samoa.learners.Learner;
import com.yahoo.labs.samoa.topology.Stream;
import com.yahoo.labs.samoa.topology.TopologyBuilder;
/**
 * 
 * Learner that contain a single learner.
 * 
 */
public final class SingleLearner implements Learner, Configurable {

	private static final long serialVersionUID = 684111382631697031L;
	
	private LocalClustererProcessor  learnerP;
		
	private Stream resultStream;
	
	private Instances dataset;

	public ClassOption learnerOption = new ClassOption("learner", 'l',
			"Learner to train.", LocalClustererAdapter.class, MOAClustererAdapter.class.getName());
	
	private TopologyBuilder builder;

        private int parallelism;

	@Override
	public void init(TopologyBuilder builder, Instances dataset, int parallelism){
		this.builder = builder;
		this.dataset = dataset;
                this.parallelism = parallelism;
		this.setLayout();
	}


	protected void setLayout() {		
		learnerP = new LocalClustererProcessor();
                LocalClustererAdapter learner = (LocalClustererAdapter) this.learnerOption.getValue();
                learner.setDataset(this.dataset);
		learnerP.setLearner(learner);
                
		this.builder.addProcessor(learnerP, this.parallelism);
		resultStream = this.builder.createStream(learnerP);
		
		learnerP.setOutputStream(resultStream);
	}

	/* (non-Javadoc)
	 * @see samoa.classifiers.Classifier#getInputProcessingItem()
	 */
       @Override
	public Processor getInputProcessor() {
		return learnerP;
	}
		
	/* (non-Javadoc)
	 * @see samoa.classifiers.Classifier#getResultStream()
	 */
	@Override
	public Stream getResultStream() {
		return resultStream;
	}
}
