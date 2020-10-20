package de.precision.analysis.IterationEvolution;

import javax.xml.bind.JAXBException;

public interface CoVLoader {

   public VMExecution[] getResults();

   long getIterations();

   void load() throws JAXBException;

}