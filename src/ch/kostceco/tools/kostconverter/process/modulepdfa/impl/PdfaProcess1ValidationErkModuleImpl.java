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

package ch.kostceco.tools.kostconverter.process.modulepdfa.impl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.Arrays;

import ch.kostceco.tools.kostconverter.exception.modulepdfa.PdfaProcess1ValidationErkException;
import ch.kostceco.tools.kostconverter.process.ValidationModuleImpl;
import ch.kostceco.tools.kostconverter.process.modulepdfa.PdfaProcess1ValidationErkModule;
import ch.kostceco.tools.kostconverter.service.ConfigurationService;

/**
 * PDF Erkennung. Ist die vorliegende Datei ein PDF? 
 * 
 * @author Rc Claire Röthlisberger, KOST-CECO
 */

public class PdfaProcess1ValidationErkModuleImpl extends ValidationModuleImpl
		implements PdfaProcess1ValidationErkModule
{

	private ConfigurationService	configurationService;

	public static String			NEWLINE	= System.getProperty( "line.separator" );

	public ConfigurationService getConfigurationService()
	{
		return configurationService;
	}

	public void setConfigurationService(
			ConfigurationService configurationService )
	{
		this.configurationService = configurationService;
	}

	@Override
	public boolean validate( File valDatei, File directoryOfLogfile )
			throws PdfaProcess1ValidationErkException
	{

		// Start mit der Erkennung
		boolean valid = false;

		// Eine PDF Datei (.pdf / .pdfa) muss mit %PDF [25504446]
		// beginnen
		if ( valDatei.isDirectory() ) {
			getMessageService().logError(
					getTextResourceService().getText( MESSAGE_XML_MODUL_A_PDFA )
							+ getTextResourceService().getText(
									ERROR_XML_A_PDFA_ISDIRECTORY ) );
			return false;
		} else if ( (valDatei.getAbsolutePath().toLowerCase().endsWith( ".pdf" ) || valDatei
				.getAbsolutePath().toLowerCase().endsWith( ".pdfa" )) ) {

			FileReader fr = null;

			try {
				fr = new FileReader( valDatei );
				BufferedReader read = new BufferedReader( fr );

				// Hex 25 in Char umwandeln
				String str1 = "25";
				int i1 = Integer.parseInt( str1, 16 );
				char c1 = (char) i1;
				// Hex 50 in Char umwandeln
				String str2 = "50";
				int i2 = Integer.parseInt( str2, 16 );
				char c2 = (char) i2;
				// Hex 44 in Char umwandeln
				String str3 = "44";
				int i3 = Integer.parseInt( str3, 16 );
				char c3 = (char) i3;
				// Hex 46 in Char umwandeln
				String str4 = "46";
				int i4 = Integer.parseInt( str4, 16 );
				char c4 = (char) i4;

				// auslesen der ersten 4 Zeichen der Datei
				int length;
				int i;
				char[] buffer = new char[4];
				length = read.read( buffer );
				for ( i = 0; i != length; i++ )
					;

				// die beiden charArrays (soll und ist) mit einander
				// vergleichen IST = c1c2c3c4
				char[] charArray1 = buffer;
				char[] charArray2 = new char[] { c1, c2, c3, c4 };

				if ( Arrays.equals( charArray1, charArray2 ) ) {
					// höchstwahrscheinlich ein PDF da es mit
					// 25504446 respektive %PDF beginnt
					valid = true;
				} else {
					getMessageService().logError(
							getTextResourceService().getText(
									MESSAGE_XML_MODUL_A_PDFA )
									+ getTextResourceService().getText(
											ERROR_XML_A_PDFA_INCORRECTFILE ) );
					return false;
				}
			} catch ( Exception e ) {
				getMessageService().logError(
						getTextResourceService().getText(
								MESSAGE_XML_MODUL_A_PDFA )
								+ getTextResourceService().getText(
										ERROR_XML_A_PDFA_INCORRECTFILE ) );
				return false;
			}
		} else {
			// die Datei endet nicht mit pdf oder pdfa -> Fehler
			getMessageService().logError(
					getTextResourceService().getText( MESSAGE_XML_MODUL_A_PDFA )
							+ getTextResourceService().getText(
									ERROR_XML_A_PDFA_INCORRECTFILEENDING ) );
			return false;
		}
		// Ende der Erkennung
		
		// Initialisierung PDFTron überprüfen der Angaben: 
		// existiert die PdftronExe am angebenen Ort?
		String pathToPdftronExe = getConfigurationService()
				.getPathToPdfaValidator();
		String pdfa1 = getConfigurationService().pdfa1();
		String pdfa2 = getConfigurationService().pdfa2();

		/*
		 * Nicht vergessen in
		 * "src/main/resources/config/applicationContext-services.xml" beim
		 * entsprechenden Modul die property anzugeben: <property
		 * name="configurationService" ref="configurationService" />
		 */

		File fPdftronExe = new File( pathToPdftronExe );
		if ( !fPdftronExe.exists()
				|| !fPdftronExe.getName().equals( "pdfa.exe" ) ) {
			getMessageService().logError(
					getTextResourceService().getText( MESSAGE_XML_MODUL_A_PDFA )
							+ getTextResourceService().getText(
									ERROR_XML_PDFTRON_MISSING ) );
			valid = false;
			return false;
		}

		pathToPdftronExe = "\"" + pathToPdftronExe + "\"";

		// ist in der Konfig eine PDF/A-Version erlaubt
		if ( pdfa2.equals( "2A" ) || pdfa2.equals( "2B" )
				|| pdfa2.equals( "2U" ) ) {
			// gültiger Konfigurationseintrag und V2 erlaubt
		} else {
			// v2 nicht erlaubt oder falscher eintrag
			pdfa2 = "no";
		}
		if ( pdfa1.equals( "1A" ) || pdfa1.equals( "1B" ) || pdfa1.equals( "A" )
				|| pdfa1.equals( "B" ) ) {
			// gültiger Konfigurationseintrag und V1 erlaubt
		} else {
			// v1 nicht erlaubt oder falscher eintrag
			pdfa1 = "no";
		}
		if ( pdfa1 == "no" && pdfa2 == "no" ) {
			// keine Validierung möglich
			// keine PDFA-Versionen konfiguriert
			getMessageService().logError(
					getTextResourceService().getText( MESSAGE_XML_MODUL_A_PDFA )
							+ getTextResourceService().getText(
									ERROR_XML_PDFA_NOCONFIG ) );
			return false;
		}

		return valid;
	}

}
