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

import static org.apache.commons.io.IOUtils.closeQuietly;

import java.io.File;
import java.util.Scanner;

import ch.kostceco.tools.kostconverter.exception.modulepdfa.PdfaProcess5AppearanceException;
import ch.kostceco.tools.kostconverter.process.ValidationModuleImpl;
import ch.kostceco.tools.kostconverter.process.modulepdfa.PdfaProcess5AppearanceModule;
import ch.kostceco.tools.kostconverter.service.ConfigurationService;
import ch.kostceco.tools.kostconverter.util.StreamGobbler;
import ch.kostceco.tools.kostconverter.util.Util;

/**
 * Stimmt die Konvertierte Datei optisch mit dem Original überein?
 * 
 * @author Rc Claire Röthlisberger, KOST-CECO
 */

public class PdfaProcess5AppearanceModuleImpl extends ValidationModuleImpl
		implements PdfaProcess5AppearanceModule
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
			throws PdfaProcess5AppearanceException
	{

		@SuppressWarnings("unused")
		boolean valid = true;
		boolean isValid = false;

		// Initialisierung comparepdf
		// überprüfen der Angaben: existiert comparepdf am
		// angebenen Ort?
		String pathToComparepdfExe = getConfigurationService()
				.getPathToPdfaAppearance();

		/*
		 * Nicht vergessen in
		 * "src/main/resources/config/applicationContext-services.xml" beim
		 * entsprechenden Modul die property anzugeben: <property
		 * name="configurationService" ref="configurationService" />
		 */

		String pathToOutput = getConfigurationService().getPathToWorkDir();

		String pathValDatei = valDatei.getParent();
		String pathToOutputNok = pathValDatei + "\\_convert_invalid";
		String pathToOutputBak = pathValDatei + "\\_backup_original";
		File outputBak = new File( pathToOutputBak );
		if ( !outputBak.exists() ) {
			outputBak.mkdir();
		}

		String valDateiName = valDatei.getName();
		File oriDatei = valDatei;
		valDatei = new File( pathToOutput, valDateiName );

		File fComparepdfExe = new File( pathToComparepdfExe );
		if ( !fComparepdfExe.exists()
				|| !fComparepdfExe.getName().equals( "comparepdf.exe" ) ) {
			getMessageService().logError(
					getTextResourceService().getText( MESSAGE_XML_MODUL_E_PDFA )
							+ getTextResourceService().getText(
									ERROR_XML_COMPAREPDF_MISSING ) );
			valid = false;
			return false;
		}

		pathToComparepdfExe = "\"" + pathToComparepdfExe + "\"";

		// PDF-Dateien an Comparepdf übergeben
		if ( valid = true ) {
			try {
				// Start Comparepdf direkt auszulösen

				// Pfad zum Programm Comparepdf
				File comparepdfExe = new File( pathToComparepdfExe );
				StringBuffer command = new StringBuffer( comparepdfExe + " " );
				command.append( "-ca -v2 " );
				command.append( "\"" );
				command.append( oriDatei.getAbsolutePath() );
				command.append( "\"" );
				command.append( " " );
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

					// Threads starten
					errorGobbler.start();

					// input stream holen und sichern in String outputComparepdf
					Scanner s = new Scanner( proc.getInputStream() )
							.useDelimiter( "\\Z" );
					String outputComparepdf = "" + s.next();
					s.close();

					// Warte, bis wget fertig ist
					proc.waitFor();

					Util.switchOnConsole();

					// System.out.println( outputComparepdf );

					/*
					 * InputStream (Output): Files look different. No
					 * differences detected.
					 */

					String outputIo = "No differences detected.";

					if ( outputComparepdf.equals( outputIo ) ) {
						isValid = true;
						// Original Datei in _backup_original verschieben
						File ziel = new File( pathToOutputBak,
								oriDatei.getName() );
						Util.copyFile( oriDatei, ziel );
						// Util.deleteFile( oriDatei );
						// Konvertierte Datei an OriginalOrt verschieben
						Util.copyFile( valDatei, oriDatei );
						Util.deleteFile( valDatei );

						getMessageService().logError(
								getTextResourceService().getText(
										MESSAGE_XML_MODUL_E_PDFA )
										+ getTextResourceService().getText(
												ERROR_XML_E_PDFA_EQUALS ) );
					} else {
						isValid = false;
						// Konvertierte Datei in _convert_invalid verschieben
						File ziel = new File( pathToOutputNok,
								valDatei.getName() );
						Util.copyFile( valDatei, ziel );
						Util.deleteFile( valDatei );
						valDatei = ziel;

						getMessageService().logError(
								getTextResourceService().getText(
										MESSAGE_XML_MODUL_E_PDFA )
										+ getTextResourceService().getText(
												ERROR_XML_E_PDFA_DIFFERENT ) );
					}

				} catch ( Exception e ) {
					getMessageService()
							.logError(
									getTextResourceService().getText(
											MESSAGE_XML_MODUL_E_PDFA )
											+ getTextResourceService()
													.getText(
															ERROR_XML_COMPAREPDF_SERVICEFAILED ) );
					return false;
				} finally {
					if ( proc != null ) {
						closeQuietly( proc.getOutputStream() );
						closeQuietly( proc.getInputStream() );
						closeQuietly( proc.getErrorStream() );
					}
				}
				// Ende COMPAREPDF direkt auszulösen
			} catch ( Exception e ) {
				getMessageService().logError(
						getTextResourceService().getText(
								MESSAGE_XML_MODUL_E_PDFA )
								+ getTextResourceService().getText(
										ERROR_XML_UNKNOWN, e.getMessage() ) );
				return false;
			}
		}
		return isValid;
	}

}
