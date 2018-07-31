package it.cnr.istc.stlab.refact;

import java.io.FileNotFoundException;

import org.apache.commons.configuration2.Configuration;
import org.apache.commons.configuration2.builder.fluent.Configurations;
import org.apache.commons.configuration2.ex.ConfigurationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Refact {

	private static Logger logger = LoggerFactory.getLogger(Refact.class);

	public static void main(String[] args) {
		try {
			Configurations configs = new Configurations();
			Configuration config = configs.properties(args[0]);

			logger.info("Refact");

			DataRefactor dr = new DataRefactor(config.getString("inputFile"), config.getString("outputFile"), config.getStringArray("ruleFolders"));
			dr.setFormat(config.getString("outputFormat", "TTL"));
			dr.refactorize();

		} catch (ConfigurationException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
}
