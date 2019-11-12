package it.cnr.istc.stlab.refact;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import org.apache.commons.io.FilenameUtils;
import org.apache.jena.query.Dataset;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.ReadWrite;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.tdb.TDBFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.jsonldjava.shaded.com.google.common.collect.Sets;

import it.cnr.istc.stlab.refact.utils.FileUtils;

public class DataRefactor {

	private static Logger logger = LoggerFactory.getLogger(DataRefactor.class);
	private RefactConfiguration conf;

	public DataRefactor(RefactConfiguration c) {
		conf = c;
	}

	public void refactorize() throws FileNotFoundException {

		if (conf.getOutputGraph() != null) {
			refactorizeTDB();
		} else {
			refactorizeModel();
		}

	}

	private void refactorizeTDB() throws FileNotFoundException {

		logger.info("Refactorize using a TDB");

		new File(conf.getTmpDir() + "/tdb").mkdirs();

		Dataset ds = TDBFactory.createDataset(conf.getTmpDir() + "/tdb");

		if (conf.getInputType().equals(RefactConfiguration.InputType.SPARQL_ENDPOINT)) {
			for (String step : conf.getRefactoringRulesFolders()) {
				for (String r : FileUtils.getFilesUnderTreeRec(step)) {

					if (!FilenameUtils.getExtension(r).equals("sparql"))
						continue;

					logger.trace("Applaying rule in {}", r);
					QueryExecution qexec = QueryExecutionFactory.sparqlService(conf.getInput(),
							QueryFactory.create(FileUtils.readFile(r, true)));
					ds.begin(ReadWrite.WRITE);
					ds.getNamedModel(conf.getOutputGraph()).add(qexec.execConstruct());
					ds.commit();
					ds.end();
				}
			}
		} else {
			Model model_in = ModelFactory.createDefaultModel();
			RDFDataMgr.read(model_in, conf.getInput());
			for (String step : conf.getRefactoringRulesFolders()) {
				for (String r : FileUtils.getFilesUnderTreeRec(step)) {
					if (!FilenameUtils.getExtension(r).equals("sparql"))
						continue;
					logger.trace("Applaying rule in {}", r);
					QueryExecution qexec = QueryExecutionFactory
							.create(QueryFactory.create(FileUtils.readFile(r, true)), model_in);
					ds.begin(ReadWrite.WRITE);
					ds.getNamedModel(conf.getOutputGraph()).add(qexec.execConstruct());
					ds.commit();
					ds.end();

				}
			}
		}

		logger.info("Dumping the dataset");
		ds.begin(ReadWrite.READ);
		RDFDataMgr.write(new FileOutputStream(new File(conf.getOutputFile())), ds,
				conf.getOutputFormat().equalsIgnoreCase("TRIG") ? Lang.TRIG : Lang.NQUADS);
		ds.end();
		logger.info("Output file written!");

	}

	private void refactorizeModel() throws FileNotFoundException {

		logger.info("Refactorize using an in-memory model");

		Model out = ModelFactory.createDefaultModel();

		if (conf.getInputType().equals(RefactConfiguration.InputType.SPARQL_ENDPOINT)) {
			for (String step : conf.getRefactoringRulesFolders()) {
				for (String r : FileUtils.getFilesUnderTreeRec(step)) {

					if (!FilenameUtils.getExtension(r).equals("sparql"))
						continue;

					logger.trace("Applaying rule in {}", r);
					QueryExecution qexec = QueryExecutionFactory.sparqlService(conf.getInput(),
							QueryFactory.create(FileUtils.readFile(r, true)));
					out.add(qexec.execConstruct());
				}
			}
		} else {
			Model model_in = ModelFactory.createDefaultModel();
			File f = new File(conf.getInput());
			if (f.isDirectory()) {
				for (String r : FileUtils.getFilesUnderTreeRec(conf.getInput())) {
					if (FilenameUtils.isExtension(r, Sets.newHashSet("rdf", "ttl", "nt", "owl", "jsonld"))) {
						RDFDataMgr.read(model_in, r);
					}
				}
			} else {
				RDFDataMgr.read(model_in, conf.getInput());
			}
			for (String step : conf.getRefactoringRulesFolders()) {
				for (String r : FileUtils.getFilesUnderTreeRec(step)) {
					if (!FilenameUtils.getExtension(r).equals("sparql"))
						continue;
					logger.trace("Applaying rule in {}", r);
					QueryExecution qexec = QueryExecutionFactory
							.create(QueryFactory.create(FileUtils.readFile(r, true)), model_in);
					out.add(qexec.execConstruct());
				}
			}
			out.setNsPrefixes(model_in.getNsPrefixMap());
		}

		logger.info("Writing output file");
		if (conf.getOutputFormat() == null) {
			out.write(new FileOutputStream(new File(conf.getOutputFile())));
		} else {
			out.write(new FileOutputStream(new File(conf.getOutputFile())), conf.getOutputFormat());
		}
		logger.info("Output file written!");
	}
}
