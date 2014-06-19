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

import static org.apache.commons.io.IOUtils.closeQuietly;

import ch.kostceco.tools.kostconverter.exception.modulepdfa.PdfaProcess2ConversionException;
import ch.kostceco.tools.kostconverter.process.ValidationModuleImpl;
import ch.kostceco.tools.kostconverter.process.modulepdfa.PdfaProcess2ConversionModule;
import ch.kostceco.tools.kostconverter.service.ConfigurationService;
import ch.kostceco.tools.kostconverter.util.StreamGobbler;
import ch.kostceco.tools.kostconverter.util.Util;

/**
 * PDFA Konvertierung mit PDFTron. 
 * 
 * @author Rc Claire Röthlisberger, KOST-CECO
 */

public class PdfaProcess2ConversionModuleImpl extends ValidationModuleImpl
		implements PdfaProcess2ConversionModule
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
			throws PdfaProcess2ConversionException
	{

		@SuppressWarnings("unused")
		boolean valid = false;
		boolean isValid = true;

		// Initialisierung PDFTron
		// überprüfen der Angaben: existiert die PdftronExe am
		// angebenen Ort?
		String pathToPdftronExe = getConfigurationService()
				.getPathToPdfaConverter();
		String pdfa1 = getConfigurationService().pdfa1();
		String pdfa2 = getConfigurationService().pdfa2();

		Integer pdfaVer1 = 0;
		Integer pdfaVer2 = 0;

		/*
		 * Nicht vergessen in
		 * "src/main/resources/config/applicationContext-services.xml" beim
		 * entsprechenden Modul die property anzugeben: <property
		 * name="configurationService" ref="configurationService" />
		 */
		
		String pathToOutput = getConfigurationService()
		.getPathToWorkDir();

		pathToPdftronExe = "\"" + pathToPdftronExe + "\"";
		/*
		 * Neu soll die Validierung mit PDFTron konfigurier bar sein Mögliche
		 * Werte 1A, 1B und no sowie 2A, 2B, 2U und no Da Archive beide
		 * Versionen erlauben können sind es 2 config einträge Es gibt mehre
		 * Möglichkeiten das PDF in der gewünschten Version zu testen -
		 * Unterscheidung anhand DROID --> braucht viel Zeit auch mit
		 * KaD_Signaturefile - Unterscheidung anhand PDF/A-Eintrag wie Droid
		 * aber selber programmiert --> ist viel schneller
		 */
		if ( pdfa2.equals( "2A" ) || pdfa2.equals( "2B" )
				|| pdfa2.equals( "2U" ) ) {
			// gültiger Konfigurationseintrag und V2 erlaubt
			pdfaVer2 = 2;
			valid = true;
		} else {
			// v2 nicht erlaubt oder falscher eintrag
			pdfa2 = "no";
		}
		if ( pdfa1.equals( "1A" ) || pdfa1.equals( "1B" ) || pdfa1.equals( "A" )
				|| pdfa1.equals( "B" ) ) {
			// gültiger Konfigurationseintrag und V1 erlaubt
			pdfaVer1 = 1;
			valid = true;
		} else {
			// v1 nicht erlaubt oder falscher eintrag
			pdfa1 = "no";
		}

		valid = true;
		// PDF-Datei an Pdftron übergeben
		if ( valid = true ) {
			try {
				// Start PDFTRON direkt auszulösen
				String level = "no";
				// Richtiges Level definieren
				if ( pdfaVer1 != 1 ) {
					// Level 1 nicht erlaubt --> Level 2
					level = pdfa2;
				} else if ( pdfaVer2 != 2 ) {
					// Level 2 nicht erlaubt --> Level 1
					level = pdfa1;
				} else {
					// Beide sind möglich --> Level je nach File auswählen
					pdfaVer1 = 0;
					pdfaVer2 = 0;
					BufferedReader in = new BufferedReader( new FileReader(
							valDatei ) );
					String line;
					while ( (line = in.readLine()) != null ) {
						// häufige Partangaben:
						// pdfaid:part>1< pdfaid:part='1' pdfaid:part="1"
						if ( line.contains( "pdfaid:part" ) ) {
							// pdfaid:part
							if ( line.contains( "pdfaid:part>1<" ) ) {
								level = pdfa1;
								pdfaVer1 = 1;
							} else if ( line.contains( "pdfaid:part='1'" ) ) {
								level = pdfa1;
								pdfaVer1 = 1;
							} else if ( line.contains( "pdfaid:part=\"1\"" ) ) {
								level = pdfa1;
								pdfaVer1 = 1;
							} else if ( line.contains( "pdfaid:part>2<" ) ) {
								level = pdfa2;
								pdfaVer2 = 2;
							} else if ( line.contains( "pdfaid:part='2'" ) ) {
								level = pdfa2;
								pdfaVer2 = 2;
							} else if ( line.contains( "pdfaid:part=\"2\"" ) ) {
								level = pdfa2;
								pdfaVer2 = 2;
							} else if ( line.contains( "pdfaid:part" )
									&& line.contains( "1" ) ) {
								// PDFA-Version = 1
								level = pdfa1;
								pdfaVer1 = 1;
							} else if ( line.contains( "pdfaid:part" )
									&& line.contains( "2" ) ) {
								// PDFA-Version = 2
								level = pdfa2;
								pdfaVer2 = 2;
							}
						}
						if ( pdfaVer1 == 0 && pdfaVer2 == 0 ) {
							// der Part wurde nicht gefunden --> Level 2
							level = pdfa2;
						}
					}
				}

				// Pfad zum Programm Pdftron
				File pdftronExe = new File( pathToPdftronExe );
				File output = new File( pathToOutput );
				if ( !output.exists() ) {
					output.mkdir();
				}

				StringBuffer command = new StringBuffer( pdftronExe + " " );
				command.append( "-x " );
				command.append( "-o " );
				command.append( "\"" );
				command.append( output.getAbsolutePath() );
				command.append( "\"" );
				command.append( " --suffix " + "\"\" " );
				command.append( "-l " + level + " " );
				command.append( "-c --nr " );
				command.append( "\"" );
				command.append( valDatei.getAbsolutePath() );
				command.append( "\"" );
				
				Process proc = null;
				Runtime rt = null;

				try {
					rt = Runtime.getRuntime();

					Util.switchOffConsole();

					proc = rt.exec( command.toString().split( " " ) );
					// .split(" ") ist notwendig wenn in einem Pfad ein
					// Doppelleerschlag vorhanden ist!

					// Fehleroutput holen
					StreamGobbler errorGobbler = new StreamGobbler(
							proc.getErrorStream(), "ERROR" );

					// Output holen
					StreamGobbler outputGobbler = new StreamGobbler(
							proc.getInputStream(), "OUTPUT" );

					// Threads starten
					errorGobbler.start();
					outputGobbler.start();

					// Warte, bis wget fertig ist
					proc.waitFor();
					
					Util.switchOnConsole();
					
					String valDateiName = valDatei.getName();
					valDatei = new File( pathToOutput, valDateiName );
				} catch ( Exception e ) {
					getMessageService().logError(
							getTextResourceService().getText(
									MESSAGE_XML_MODUL_B_PDFA )
									+ getTextResourceService().getText(
											ERROR_XML_PDFTRON_SERVICEFAILED ) );
					return false;
				} finally {
					if ( proc != null ) {
						closeQuietly( proc.getOutputStream() );
						closeQuietly( proc.getInputStream() );
						closeQuietly( proc.getErrorStream() );
					}
				}
				// Ende PDFTRON direkt auszulösen

			} catch ( Exception e ) {
				getMessageService().logError(
						getTextResourceService().getText(
								MESSAGE_XML_MODUL_B_PDFA )
								+ getTextResourceService().getText(
										ERROR_XML_UNKNOWN, e.getMessage() ) );
				return false;
			}
		}
		if ( !valDatei.exists() ) {
			getMessageService().logError(
					getTextResourceService().getText(
							MESSAGE_XML_MODUL_B_PDFA )
							+ getTextResourceService().getText(
									ERROR_XML_PDFTRON_SERVICEFAILED ) );
			return false;
		} else {
			getMessageService().logError(
					getTextResourceService().getText(
							MESSAGE_XML_MODUL_B_PDFA )
							+ getTextResourceService().getText(
									ERROR_XML_B_PDFA_CONVERTED ) );
		}

		return isValid;
	}

}
