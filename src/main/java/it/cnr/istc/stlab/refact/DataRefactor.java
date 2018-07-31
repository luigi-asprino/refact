package it.cnr.istc.stlab.refact;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.List;

import org.apache.commons.io.FilenameUtils;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.riot.RDFDataMgr;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.cnr.istc.stlab.refact.utils.FileUtils;

public class DataRefactor {

	private static Logger logger = LoggerFactory.getLogger(Refact.class);

	private String inFile, outFile;
	private String[] refacotringRules;
	private String format = "TTL";

	public DataRefactor(String inF, String outF, String[] refacotringRules) {
		this.inFile = inF;
		this.outFile = outF;
		this.refacotringRules = refacotringRules;
	}

	public void setFormat(String format) {
		this.format = format;
	}

	public void refactorize() throws FileNotFoundException {

		Model in = ModelFactory.createDefaultModel();
		Model out = ModelFactory.createDefaultModel();
		RDFDataMgr.read(in, inFile);

		for (String step : refacotringRules) {

			List<String> rules = FileUtils.getFilesUnderTreeRec(step);

			for (String r : rules) {

				if (!FilenameUtils.getExtension(r).equals("sparql"))
					continue;

				logger.trace("Applaying rule in {}", r);
				String rule = FileUtils.readFile(r, true);
				Query query = QueryFactory.create(rule);
				QueryExecution qexec = QueryExecutionFactory.create(query, in);
				out.add(qexec.execConstruct());
			}
		}

		out.setNsPrefixes(in.getNsPrefixMap());
		out.write(new FileOutputStream(new File(outFile)), format);

	}
}
