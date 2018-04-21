/**
* Copyright (c) 2007 Ian Rae
* All Rights Reserved.
* Licensed under the Eclipse Public License - v 1.0
* For more information see http://www.eclipse.org/legal/epl-v10.html
*/
package org.speakright.core;
import java.io.Serializable;

/**
 * Each SpeakRight application has a project directory that contains
 * the various xml config files, and usually audio and grammar sub-dirs.
 * 
 * <pre>
 * c:\myapp            project dir
 *   |- audio          audio dir. contains audio files for the default
 *                     language, which is usually en-us.
 *      |- fr-ca       audio files for French Canadian locale
 *      |- sp-sp       audio files for Spanish locale
 *   |- grammar        grammar dir.  contains grammar files for the default
 *                     language which is usually en-us.
 *      |- fr-ca       grammar files for French Canadian locale
 *      |- sp-sp       grammar files for Spanish locale
 *   |- sro            xml files
 *      |- audio
 *      |- grammar 
 *  </pre>
 * @author Ian Rae
 *
 */
@SuppressWarnings("serial")
public class SRLocations implements Serializable {

	String m_projectDir;
	String m_promptDir;
	String m_gramDir;
	String m_sroDir;

	public transient String m_baseGrammarUrl = ""; //empty or address of grammars dir, such as "grammars/"
	public transient String m_basePromptUrl = ""; //empty or address of auiod file dir, such as "audio/"
	public transient String m_baseSROUrl = ""; //empty or address of grammars dir, such as "sro/"
	
	String m_currentLanguage = "en-us";
	
	
	public SRLocations()
	{
	    String currentDir = System.getProperty("user.dir");
		
	    setProjectDir(currentDir);
	}
	
	/**
	 * Get the current language 
	 * @return language, such as "en-us"
	 */
	public String language()
	{
		return m_currentLanguage;
	}
	/**
	 * Set the current language.
	 * @param s language, such as "en-us"
	 */
	public void setLanguage(String s)
	{
		m_currentLanguage = s;
	}

	/**
	 * Get the project dir.  Each SpeakRight application
	 * can define one directory as the 'project dir', which
	 * holds the application and its resources such as
	 * grammar, audio, or xml files.
	 * 
	 * Only used for unit testing?
	 * @return
	 */
	public String projectDir()
	{
		return m_projectDir;
	}
	
	
	/**
	 * Ensures path ends in / (or \ on windows)
	 * @param path
	 * @return
	 */
	static public String fixupDir(String path)
	{
		String slash = java.io.File.separator; //or backslash on windows
		if (! path.endsWith(slash))
		{
			return path + slash;
		}
		return path;
	}

	/**
	 * Resolve the path.  If it contains $sro$ then replace with the
	 * value of m_sroDir.
	 * @param path a full file path, such as "c:\\app3\\audio\\johnny.wav""
	 * @return a full file path with $sro$ resolved.
	 */
	public String resolvePath(String path)
	{
		//handle $sro$ first
		if (path.toLowerCase().startsWith("$sro$")) {
			String tmp = path.substring(5);
			if (tmp.startsWith("\\")) {
				tmp = tmp.substring(1);
			}
			return fixupDir(m_sroDir) + tmp;
		}
		return path;
	}

	/**
	 * Ensure url ends in '/'
	 * @param url
	 * @return url ending in '/'
	 */
	static public String fixupUrl(String url)
	{
		String slash = "/";
		if (! url.endsWith(slash))
		{
			return url + slash;
		}
		return url;
	}
	
	/**
	 * Convert the URL of a prompt into a full (absolute) URL (using m_basePromptURL).  
	 * If it's already a full URL then do nothing.  Resolve $sro$ if present.
	 * Typically prompts are in {project-dir}/audio
	 * @param url a relative or absolute URL.
	 * @return a full (absolute) URL.
	 */
	public String makeFullPromptUrl(String url)
	{
		return makeFullUrl(m_basePromptUrl, url);
	}
	/**
	 * Convert the URL of a grammar into a full (absolute) URL (using m_baseGrammarURL).  
	 * If it's already a full URL then do nothing.  Resolve $sro$ if present.
	 * Typically grammars are in {project-dir}/grammar
	 * @param url a relative or absolute URL.
	 * @return a full (absolute) URL.
	 */
	public String makeFullGrammarUrl(String url)
	{
		return makeFullUrl(m_baseGrammarUrl, url);
	}
	/**
	 * Convert the URL of a sro file or resource into a full (absolute) URL (using m_baseSROURL).  
	 * If it's already a full URL then do nothing.  Resolve $sro$ if present.
	 * Typically sro resourese are in {project-dir}/sro, with audio in {project-dir}/sro/audio,
	 * and grammar in {project-dir}/sro/grammar.
	 * @param url a relative or absolute URL.
	 * @return a full (absolute) URL.
	 */
	public String makeFullSROUrl(String url)
	{
		return makeFullUrl(m_baseSROUrl, url);
	}
	/**
	 * Returns an absolute URL for url.  If url is already an absolute url then it
	 * is simply returned.  Otherwise baseUrl is pre-pended.
	 * @param baseUrl the base url. can be null or "" if there is no base url.
	 * @param url a relative or absolute url.
	 * @return absolute URL for url.
	 */
	String makeFullUrl(String baseUrl, String url)
	{
		//handle $sro$ first
		if (url.toLowerCase().startsWith("$sro$")) {
			String tmp = url.substring(5);
			if (tmp.startsWith("/")) {
				tmp = tmp.substring(1);
			}
			return fixupUrl(m_baseSROUrl) + tmp;
		}
		
		if (baseUrl == null || baseUrl.length() == 0) {
			return url;
		}

		//if url already is a full url
		if (url.indexOf('/') == 0 || url.startsWith("http")) {
			return url;
		}
		else {
			return fixupUrl(baseUrl) + url;
		}
	}
	
	/**
	 * sets the project dir and
	 * the grammar and audio directories.
	 * @param path file path to the directory for this SpeakRight application
	 */
	public void setProjectDir(String path)
	{
		m_projectDir = fixupDir(path);
		m_promptDir = fixupDir(m_projectDir + "audio");
		m_gramDir = fixupDir(m_projectDir + "grammar");
		m_sroDir = fixupDir(m_projectDir + "sro");
	}

	/**
	 * sets the prompt dir. Call this after setProjectDir
	 * @param path file path to the directory containing audio files.
	 */
	public void setPromptDir(String path)
	{
		m_promptDir = fixupDir(path);
	}
	/**
	 * sets the grammar dir. Call this after setProjectDir
	 * @param path file path to the directory containing grammar files.
	 */
	public void setGrammarDir(String path)
	{
		m_gramDir = fixupDir(path);
	}
	/**
	 * sets the SRO dir. Call this after setProjectDir
	 * @param path file path to the directory containing SRO audio, xml, and grammar files
	 */
	public void setSRODir(String path)
	{
		m_sroDir = fixupDir(path);
	}
	
	public String getPromptPath(String language)
	{
		return getLangSpecificPath(m_promptDir, language);
	}
	public String getGrammarPath(String language)
	{
		return getLangSpecificPath(m_gramDir, language);
	}

	String getLangSpecificPath(String dir, String language)
	{
		if (language == "") //default lang?
			return dir;
		String path = fixupDir(dir + language);
		return path;
	}
}
