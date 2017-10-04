package com.oxygenxml.resources.batch.converter.converters;

import java.io.Reader;
import java.io.StringWriter;
import java.net.URL;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamResult;

import org.xml.sax.InputSource;

import com.elovirta.dita.markdown.MarkdownReader;
import com.oxygenxml.resources.batch.converter.trasformer.TransformerFactoryCreator;

/**
 * Class that use com.elovirta.dita.markdown.MarkdownReader for convert Markdown
 * to DITA.
 * 
 * @author intern4
 *
 */
public class MarkdownToDitaTransformer implements com.oxygenxml.resources.batch.converter.converters.Converter {

	/**
	 * Convert the markdown document from the given URL in DITA.
	 * 
	 * @param originalFileLocation
	 *          The URL location of document.
	 * @param contentReader
	 *          Reader of the document.
	 * @return The conversion in DITA.
	 * @throws TransformerException
	 */
	@Override
	public String convert(URL originalFileLocation, Reader contentReader, TransformerFactoryCreator transformerCreator) throws TransformerException {
		// content to return
		String toReturn = null;

		Transformer transformer = transformerCreator.createTransformer(null);

		// get the trasformFactory property
		String property = System.getProperty("javax.xml.transform.TransformerFactory");

		// set the trasformFactory property to
		// "net.sf.saxon.TransformerFactoryImpl"
		System.setProperty("javax.xml.transform.TransformerFactory", "net.sf.saxon.TransformerFactoryImpl");
		
		// reader for markdown document
		final MarkdownReader r = new MarkdownReader();

		try {

			// input source of document to convert
			final InputSource i = new InputSource(originalFileLocation.toString());

			StringWriter sw = new StringWriter();
			StreamResult res = new StreamResult(sw);

			// convert the document
			//TODO problema cu C:\Users\intern4\Documents\OxygenXMLEditor\samples\dita\thunderbird\README.md
			transformer.transform(new SAXSource(r, i), res);

			System.out.println("aici4");
			// get the converted content
			toReturn = sw.toString();

		}catch (Exception e) {
			if(e instanceof TransformerException){
				
				throw new TransformerException(((TransformerException)e).getException().getMessage() , 
						((TransformerException)e).getException().getCause());
			}
			else{
				e.printStackTrace();
				throw new TransformerException("Document cannot be converted.");
			}
		} finally {
			// return the initial property of trasformerFactory
			if (property == null) {
				System.getProperties().remove("javax.xml.transform.TransformerFactory");
			} else {
				System.setProperty("javax.xml.transform.TransformerFactory", property);
			}
		}

		return toReturn;
	}

}