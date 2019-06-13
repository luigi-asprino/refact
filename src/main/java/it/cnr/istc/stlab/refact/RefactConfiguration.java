package it.cnr.istc.stlab.refact;

import org.apache.commons.configuration2.Configuration;
import org.apache.commons.configuration2.builder.fluent.Configurations;
import org.apache.commons.configuration2.ex.ConfigurationException;

public class RefactConfiguration {

	private static RefactConfiguration instance;
	private static String CONFIG_FILE = "config.properties";

	public enum InputType {
		SPARQL_ENDPOINT, TTL_FILE, NT_FILE, RDF_XML
	}

	private String input, outputFile, outputFormat, outputGraph, tmpDir;
	private String[] refactoringRulesFolders;
	public InputType inputType;

	private RefactConfiguration() {

		try {
			Configurations configs = new Configurations();
			Configuration config = configs.properties(CONFIG_FILE);

			input = config.getString("input");
			outputFile = config.getString("outputFile");
			outputFormat = config.getString("outputFormat");
			outputGraph = config.getString("outputGraph");
			refactoringRulesFolders = config.getStringArray("ruleFolders");
			tmpDir = config.getString("tmpDir","tmp");

			String inputT = config.getString("inputType");
			if (inputT.equalsIgnoreCase("SPARQL")) {
				inputType = InputType.SPARQL_ENDPOINT;
			} else if (inputT.equalsIgnoreCase("TTL")) {
				inputType = InputType.TTL_FILE;
			} else if (inputT.equalsIgnoreCase("NT")) {
				inputType = InputType.NT_FILE;
			} else if (inputT.equalsIgnoreCase("RDF/XML")) {
				inputType = InputType.RDF_XML;
			}

		} catch (ConfigurationException e) {
			e.printStackTrace();
		}

	}

	public static RefactConfiguration getInstance(String configFile) {
		if (instance == null) {
			if (configFile != null) {
				CONFIG_FILE = configFile;
			}
			instance = new RefactConfiguration();
		}
		return instance;
	}


	public static String getCONFIG_FILE() {
		return CONFIG_FILE;
	}

	public String getInput() {
		return input;
	}

	public String getOutputFile() {
		return outputFile;
	}

	public String[] getRefactoringRulesFolders() {
		return refactoringRulesFolders;
	}

	public InputType getInputType() {
		return inputType;
	}

	public String getOutputFormat() {
		return outputFormat;
	}
	
	public String getOutputGraph() {
		return outputGraph;
	}

	public String getTmpDir() {
		return tmpDir;
	}

}
