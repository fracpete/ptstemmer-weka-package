/*
 *    This program is free software; you can redistribute it and/or modify
 *    it under the terms of the GNU General Public License as published by
 *    the Free Software Foundation; either version 2 of the License, or
 *    (at your option) any later version.
 *
 *    This program is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *    GNU General Public License for more details.
 *
 *    You should have received a copy of the GNU General Public License
 *    along with this program; if not, write to the Free Software
 *    Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
 */

/*
 * PTStemmer.java
 * Copyright (C) 2009 University of Waikato, Hamilton, New Zealand
 *
 */

package weka.core.stemmers;

import java.io.File;
import java.util.Enumeration;
import java.util.Vector;

import ptstemmer.exceptions.PTStemmerException;
import ptstemmer.implementations.OrengoStemmer;
import ptstemmer.implementations.PorterStemmer;
import ptstemmer.implementations.SavoyStemmer;
import ptstemmer.support.PTStemmerUtilities;
import weka.core.Option;
import weka.core.OptionHandler;
import weka.core.RevisionUtils;
import weka.core.SelectedTag;
import weka.core.Tag;
import weka.core.Utils;

/**
 <!-- globalinfo-start -->
 * A wrapper for PTStemmer (developed by Pedro Oliveira):<br/>
 * http://code.google.com/p/ptstemmer/
 * <p/>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * Valid options are: <p/>
 *
 * <pre> -S &lt;ORENGO|PORTER|SAVOY&gt;
 *  The type of stemmer algorithm to use:
 *  ORENGO = Orengo
 *  PORTER = Porter
 *  SAVOY = Savoy
 *  (default: ORENGO)</pre>
 *
 * <pre> -N &lt;file&gt;
 *  The file with the named entities to ignore (optional).
 *  File format: simple text file with one entity per line.
 *  (default: none)
 * </pre>
 *
 * <pre> -W &lt;file&gt;
 *  The file with the stopwords (optional).
 *  File format: simple text file with one stopword per line.
 *  (default: none)
 * </pre>
 *
 * <pre> -C &lt;int&gt;
 *  The size of the cache. Disable with 0.
 *  (default: 1000)
 * </pre>
 *
 <!-- options-end -->
 *
 * @author  FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision: 1179 $
 */
public class PTStemmer
  implements Stemmer, OptionHandler {

  /** for serialization. */
  static final long serialVersionUID = -6113024782588197L;

  /** orengo stemmer. */
  public static final int STEMMER_ORENGO = 0;

  /** porter stemmer. */
  public static final int STEMMER_PORTER = 1;
  
  /** savoy stemmer. */
  public static final int STEMMER_SAVOY = 2;

  /** stemmers. */
  public static final Tag[] TAGS_STEMMERS = {
    new Tag(STEMMER_ORENGO, "orengo", "Orengo"),
    new Tag(STEMMER_PORTER, "porter", "Porter"),
    new Tag(STEMMER_SAVOY, "savoy", "Savoy")
  };

  /** the type of stemmer to use. */
  protected int m_Stemmer = STEMMER_ORENGO;

  /** the named entities. */
  protected File m_NamedEntities = new File(".");

  /** the stopwords. */
  protected File m_Stopwords = new File(".");

  /** the cache size. */
  protected int m_Cache = 1000;

  /** the actual stemmer. */
  protected ptstemmer.Stemmer m_ActualStemmer;

  /**
   * Returns a string describing the stemmer.
   *
   * @return 		a description suitable for displaying in the
   * 			explorer/experimenter gui
   */
  public String globalInfo() {
    return
        "A wrapper for PTStemmer (developed by Pedro Oliveira):\n"
      + "http://code.google.com/p/ptstemmer/";
  }

  /**
   * Returns an enumeration describing the available options.
   *
   * @return an enumeration of all the available options.
   */
  public Enumeration listOptions() {
    Vector<Option>	result;
    String		desc;
    SelectedTag		tag;
    int			i;

    result = new Vector<Option>();
    desc  = "";
    for (i = 0; i < TAGS_STEMMERS.length; i++) {
      tag = new SelectedTag(TAGS_STEMMERS[i].getID(), TAGS_STEMMERS);
      desc  +=   "\t" + tag.getSelectedTag().getIDStr()
      	       + " = " + tag.getSelectedTag().getReadable()
      	       + "\n";
    }
    result.addElement(new Option(
	"\tThe type of stemmer algorithm to use:\n"
	+ desc
	+ "\t(default: " + new SelectedTag(STEMMER_ORENGO, TAGS_STEMMERS) + ")",
	"S", 1, "-S " + Tag.toOptionList(TAGS_STEMMERS)));

    result.addElement(new Option(
        "\tThe file with the named entities to ignore (optional).\n"
        + "\tFile format: simple text file with one entity per line.\n"
        + "\t(default: none)\n",
        "N", 1, "-N <file>"));

    result.addElement(new Option(
        "\tThe file with the stopwords (optional).\n"
        + "\tFile format: simple text file with one stopword per line.\n"
        + "\t(default: none)\n",
        "W", 1, "-W <file>"));

    result.addElement(new Option(
        "\tThe size of the cache. Disable with 0.\n"
        + "\t(default: 1000)\n",
        "C", 1, "-C <int>"));

    return result.elements();
  }

  /**
   * Parses the options. <p/>
   *
   <!-- options-start -->
   * Valid options are: <p/>
   *
   * <pre> -S &lt;ORENGO|PORTER|SAVOY&gt;
   *  The type of stemmer algorithm to use:
   *  ORENGO = Orengo
   *  PORTER = Porter
   *  SAVOY = Savoy
   *  (default: ORENGO)</pre>
   *
   * <pre> -N &lt;file&gt;
   *  The file with the named entities to ignore (optional).
   *  File format: simple text file with one entity per line.
   *  (default: none)
   * </pre>
   *
   * <pre> -W &lt;file&gt;
   *  The file with the stopwords (optional).
   *  File format: simple text file with one stopword per line.
   *  (default: none)
   * </pre>
   *
   * <pre> -C &lt;int&gt;
   *  The size of the cache. Disable with 0.
   *  (default: 1000)
   * </pre>
   *
   <!-- options-end -->
   *
   * @param options	the options to parse
   * @throws Exception 	if parsing fails
   */
  public void setOptions(String[] options) throws Exception {
    String	tmpStr;

    tmpStr = Utils.getOption('S', options);
    if (tmpStr.length() != 0)
      setStemmer(new SelectedTag(tmpStr, TAGS_STEMMERS));
    else
      setStemmer(new SelectedTag(STEMMER_ORENGO, TAGS_STEMMERS));

    tmpStr = Utils.getOption('N', options);
    if (tmpStr.length() != 0)
      setNamedEntities(new File(tmpStr));
    else
      setNamedEntities(new File("."));

    tmpStr = Utils.getOption('W', options);
    if (tmpStr.length() != 0)
      setStopwords(new File(tmpStr));
    else
      setStopwords(new File("."));

    tmpStr = Utils.getOption('C', options);
    if (tmpStr.length() != 0)
      setCache(Integer.parseInt(tmpStr));
    else
      setCache(1000);
  }

  /**
   * Gets the current settings of the classifier.
   *
   * @return an array of strings suitable for passing to setOptions
   */
  public String[] getOptions() {
    Vector<String>	result;

    result = new Vector<String>();

    result.add("-S");
    result.add("" + getStemmer());

    result.add("-N");
    result.add("" + getNamedEntities());

    result.add("-W");
    result.add("" + getStopwords());

    result.add("-C");
    result.add("" + getCache());

    return result.toArray(new String[result.size()]);
  }

  /**
   * Sets the stemmer type to use
   *
   * @param value 	the type
   */
  public void setStemmer(SelectedTag value) {
    if (value.getTags() == TAGS_STEMMERS) {
      m_Stemmer = value.getSelectedTag().getID();
      invalidate();
    }
  }

  /**
   * Gets the stemmer type to use.
   *
   * @return 		the type
   */
  public SelectedTag getStemmer() {
    return new SelectedTag(m_Stemmer, TAGS_STEMMERS);
  }

  /**
   * Returns the tip text for this property
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the explorer/experimenter gui
   */
  public String stemmerTipText() {
    return "Sets the type of stemmer to use.";
  }

  /**
   * Sets the file for the named entities.
   *
   * @param value	the file.
   */
  public void setNamedEntities(File value) {
    m_NamedEntities = value;
    invalidate();
  }

  /**
   * Gets the file for the named entities.
   *
   * @return 		the file.
   */
  public File getNamedEntities() {
    return m_NamedEntities;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the explorer/experimenter gui
   */
  public String namedEntitiesTipText() {
    return
        "The file with the named entities to ignore (format: text file with "
      + "one entity per line); gets ignored if pointing to a directory.";
  }

  /**
   * Sets the file for the stopwords.
   *
   * @param value	the file.
   */
  public void setStopwords(File value) {
    m_Stopwords = value;
    invalidate();
  }

  /**
   * Gets the file for the named entities.
   *
   * @return 		the file.
   */
  public File getStopwords() {
    return m_Stopwords;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the explorer/experimenter gui
   */
  public String stopwordsTipText() {
    return
        "The file with the stopwords to ignore (format: text file with "
      + "one stopword per line); gets ignored if pointing to a directory.";
  }

  /**
   * Sets the size of the cache.
   *
   * @param value	the size.
   */
  public void setCache(int value) {
    m_Cache = value;
    invalidate();
  }

  /**
   * Gets the size of the cache.
   *
   * @return 		the size.
   */
  public int getCache() {
    return m_Cache;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the explorer/experimenter gui
   */
  public String cacheTipText() {
    return "The size of the cache (use 0 to disable caching).";
  }

  /**
   * Invalidates the stemmer object.
   */
  protected void invalidate() {
    m_ActualStemmer = null;
  }

  /**
   * Returns the stemmer to use.
   *
   * @return		the stemmer to use
   */
  protected synchronized ptstemmer.Stemmer getActualStemmer() throws PTStemmerException {
    if (m_ActualStemmer == null) {
      // stemmer algorithm
      if (m_Stemmer == STEMMER_ORENGO)
	m_ActualStemmer = new OrengoStemmer();
      else if (m_Stemmer == STEMMER_PORTER)
	m_ActualStemmer = new PorterStemmer();
      else if (m_Stemmer == STEMMER_SAVOY)
	m_ActualStemmer = new SavoyStemmer();
      else
	throw new IllegalStateException("Unhandled stemmer type: " + m_Stemmer);

      // named entities
      if (!m_NamedEntities.isDirectory())
	m_ActualStemmer.ignore(PTStemmerUtilities.fileToSet(m_NamedEntities.getAbsolutePath()));

      // stopwords
      if (!m_Stopwords.isDirectory())
	m_ActualStemmer.ignore(PTStemmerUtilities.fileToSet(m_Stopwords.getAbsolutePath()));

      // cache
      if (m_Cache > 0)
	m_ActualStemmer.enableCaching(m_Cache);
      else
	m_ActualStemmer.disableCaching();
    }

    return m_ActualStemmer;
  }

  /**
   * Returns the stemmed version of the given word.
   * Word is converted to lower case before stemming.
   *
   * @param word 	a string consisting of a single word
   * @return 		the stemmed word
   */
  public String stem(String word) {
	String ret = null;
    try {
	  ret = getActualStemmer().getWordStem(word);
	}
    catch (PTStemmerException e) {
      e.printStackTrace();
	}
    return ret;
  }

  /**
   * returns a string representation of the stemmer.
   *
   * @return a string representation of the stemmer
   */
  public String toString() {
    return getClass().getName();
  }

  /**
   * Returns the revision string.
   *
   * @return		the revision
   */
  public String getRevision() {
    return RevisionUtils.extract("$Revision: 1179 $");
  }

  /**
   * Runs the stemmer with the given options.
   *
   * @param args      the options
   */
  public static void main(String[] args) {
    try {
      Stemming.useStemmer(new PTStemmer(), args);
    }
    catch (Exception e) {
      e.printStackTrace();
    }
  }
}
