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
 *  </pre>
 * @author Ian Rae
 *
 */
@SuppressWarnings("serial")
public class SRLocations implements Serializable {

	String m_projectDir;
	String m_promptDir;
	String m_gramDir;
	
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
	 * Returns an absolute URL for url.  If url is already an absolute url then it
	 * is simply returned.  Otherwise baseUrl is pre-pended.
	 * @param baseUrl the base url. can be null or "" if there is no base url.
	 * @param url a relative or absolute url.
	 * @return absolute URL for url.
	 */
	static public String makeFullUrl(String baseUrl, String url)
	{
		if (baseUrl == null || baseUrl.length() == 0) {
			return url;
		}

		//if url already is a full url
		if (url.indexOf('/') == 0 || url.startsWith("http")) {
			return url;
		}
		else {
			return SRLocations.fixupUrl(baseUrl) + url;
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
