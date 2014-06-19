/*== KOST-Converter ==================================================================================
The KOST-Converter application is used for convert PDF-Files to PDF/A-Files including 
validation and a automatic visual check. 
Copyright (C) 2014 Claire Röthlisberger (KOST-CECO)
-----------------------------------------------------------------------------------------------
KOST-Converter is a development of the KOST-CECO. All rights rest with the KOST-CECO. 
This application is free software: you can redistribute it and/or modify it under the 
terms of the GNU General Public License as published by the Free Software Foundation, 
either version 3 of the License, or (at your option) any later version. 
This application is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; 
without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
See the follow GNU General Public License for more details.
You should have received a copy of the GNU General Public License along with this program; 
if not, write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, 
Boston, MA 02110-1301 USA or see <http://www.gnu.org/licenses/>.
==============================================================================================*/

package ch.kostceco.tools.kostconverter.service.impl;

import java.io.File;
import java.net.URL;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.XMLConfiguration;

import ch.kostceco.tools.kostconverter.KOSTConverter;
import ch.kostceco.tools.kostconverter.logging.Logger;
import ch.kostceco.tools.kostconverter.service.ConfigurationService;
import ch.kostceco.tools.kostconverter.service.TextResourceService;

public class ConfigurationServiceImpl implements ConfigurationService
{

	private static final Logger	LOGGER	= new Logger(
												ConfigurationServiceImpl.class );
	XMLConfiguration			config	= null;
	private TextResourceService	textResourceService;

	public TextResourceService getTextResourceService()
	{
		return textResourceService;
	}

	public void setTextResourceService( TextResourceService textResourceService )
	{
		this.textResourceService = textResourceService;
	}

	private XMLConfiguration getConfig()
	{
		if ( this.config == null ) {

			try {

				String path = "configuration/KOSTConverter.conf.xml";

				URL locationOfJar = KOSTConverter.class.getProtectionDomain()
						.getCodeSource().getLocation();
				String locationOfJarPath = locationOfJar.getPath();

				if ( locationOfJarPath.endsWith( ".jar" ) ) {
					File file = new File( locationOfJarPath );
					String fileParent = file.getParent();
					path = fileParent + "/" + path;
				}

				config = new XMLConfiguration( path );

			} catch ( ConfigurationException e ) {
				LOGGER.logError( getTextResourceService().getText(
						MESSAGE_XML_MODUL_A_PDFA )
						+ getTextResourceService().getText(
								MESSAGE_XML_CONFIGURATION_ERROR_1 ) );
				LOGGER.logError( getTextResourceService().getText(
						MESSAGE_XML_MODUL_A_PDFA )
						+ getTextResourceService().getText(
								MESSAGE_XML_CONFIGURATION_ERROR_2 ) );
				LOGGER.logError( getTextResourceService().getText(
						MESSAGE_XML_MODUL_A_PDFA )
						+ getTextResourceService().getText(
								MESSAGE_XML_CONFIGURATION_ERROR_3 ) );
				System.exit( 1 );
			}
		}
		return config;
	}

	@Override
	public String getPathToWorkDir()
	{
		/**
		 * Gibt den Pfad des Arbeitsverzeichnisses zurück. Dieses Verzeichnis
		 * wird zum Entpacken des .zip-Files verwendet.
		 * 
		 * @return Pfad des Arbeitsverzeichnisses
		 */
		Object prop = getConfig().getProperty( "pathtoworkdir" );
		if ( prop instanceof String ) {
			String value = (String) prop;
			return value;
		}
		return null;
	}

	@Override
	public String getPathToLogfile()
	{
		/**
		 * Gibt den Pfad des Logverzeichnisses zurück.
		 * 
		 * @return Pfad des Logverzeichnisses
		 */
		Object prop = getConfig().getProperty( "pathtologfile" );
		if ( prop instanceof String ) {
			String value = (String) prop;
			return value;
		}
		return null;
	}

	/*--- PDF/A ---------------------------------------------------------------------*/
	@Override
	public String getPathToPdfaAppearance()
	{
		Object prop = getConfig().getProperty( "pdfa.pathtopdfaappearance" );

		if ( prop instanceof String ) {
			String value = (String) prop;
			return value;
		}
		return null;
	}

	@Override
	public String getPathToPdfaConverter()
	{
		Object prop = getConfig().getProperty( "pdfa.pathtopdfaconverter" );

		if ( prop instanceof String ) {
			String value = (String) prop;
			return value;
		}
		return null;
	}

	@Override
	public String getPathToPdfaValidator()
	{
		Object prop = getConfig().getProperty( "pdfa.pathtopdfavalidator" );

		if ( prop instanceof String ) {
			String value = (String) prop;
			return value;
		}
		return null;
	}

	@Override
	public String pdfa1()
	{
		Object prop = getConfig().getProperty( "pdfa.pdfa1" );

		if ( prop instanceof String ) {
			String value = (String) prop;
			return value;
		}
		return null;
	}

	@Override
	public String pdfa2()
	{
		Object prop = getConfig().getProperty( "pdfa.pdfa2" );

		if ( prop instanceof String ) {
			String value = (String) prop;
			return value;
		}
		return null;
	}

}
