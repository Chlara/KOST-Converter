/*== KOST-Converter ==================================================================================
The KOST-Converter application is used for validate TIFF, SIARD, PDF/A-Files and Submission 
Information Package (SIP). 
Copyright (C) 2012-2014 Claire Röthlisberger (KOST-CECO), Christian Eugster, Olivier Debenath, 
Peter Schneider (Staatsarchiv Aargau), Daniel Ludin (BEDAG AG)
-----------------------------------------------------------------------------------------------
KOST-Converter is a development of the KOST-CECO. All rights rest with the KOST-CECO. 
This application is free software: you can redistribute it and/or modify it under the 
terms of the GNU General Public License as published by the Free Software Foundation, 
either version 3 of the License, or (at your option) any later version. 
BEDAG AG and Daniel Ludin hereby disclaims all copyright interest in the program 
SIP-Val v0.2.0 written by Daniel Ludin (BEDAG AG). Switzerland, 1 March 2011.
This application is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; 
without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
See the follow GNU General Public License for more details.
You should have received a copy of the GNU General Public License along with this program; 
if not, write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, 
Boston, MA 02110-1301 USA or see <http://www.gnu.org/licenses/>.
==============================================================================================*/

package ch.kostceco.tools.kostconverter.process.modulepdfa.impl;

import static org.apache.commons.io.IOUtils.closeQuietly;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import ch.kostceco.tools.kostconverter.exception.SystemException;
import ch.kostceco.tools.kostconverter.exception.modulepdfa.PdfaProcess1ValidationValException;
import ch.kostceco.tools.kostconverter.process.ValidationModuleImpl;
import ch.kostceco.tools.kostconverter.process.modulepdfa.PdfaProcess1ValidationValModule;
import ch.kostceco.tools.kostconverter.service.ConfigurationService;
import ch.kostceco.tools.kostconverter.util.StreamGobbler;
import ch.kostceco.tools.kostconverter.util.Util;

/**
 * PDFA Validierungs mit PDFTron. Ist die vorliegende PDF-Datei eine valide
 * PDFA-Datei oder nicht?
 * 
 * Invalide PDF/A-Dateien werden in den ff Modulen konvertiert
 * 
 * @author Rc Claire Röthlisberger, KOST-CECO
 */

public class PdfaProcess1ValidationValModuleImpl extends ValidationModuleImpl
		implements PdfaProcess1ValidationValModule
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
			throws PdfaProcess1ValidationValException
	{

		// Start mit der Erkennung
		// Wurde bereits in PdfaProcess1ValidationErk gemacht
		
		boolean valid = true;
		boolean isValid = true;
		boolean erkennung = false;
		boolean encryption = false;

		// Initialisierung PDFTron überprüfen der Angaben: 
		// existiert die PdftronExe am angebenen Ort?
		String pathToPdftronExe = getConfigurationService()
				.getPathToPdfaValidator();
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
		} else {
			// v2 nicht erlaubt oder falscher eintrag
			pdfa2 = "no";
		}
		if ( pdfa1.equals( "1A" ) || pdfa1.equals( "1B" ) || pdfa1.equals( "A" )
				|| pdfa1.equals( "B" ) ) {
			// gültiger Konfigurationseintrag und V1 erlaubt
			pdfaVer1 = 1;
		} else {
			// v1 nicht erlaubt oder falscher eintrag
			pdfa1 = "no";
		}

		// PDF-Datei an Pdftron übergeben wenn die Erkennung erfolgreich
		erkennung = valid;
		if ( erkennung = true ) {
			try {
				// Start PDFTRON direkt auszulösen
				File report;
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
							// der Part wurde nicht gefunden --> Level 1
							level = pdfa1;
						}
					}
				}

				// Pfad zum Programm Pdftron
				File pdftronExe = new File( pathToPdftronExe );
				File output = directoryOfLogfile;
				String pathToPdftronOutput = output.getAbsolutePath();
				StringBuffer command = new StringBuffer( pdftronExe + " " );
				command.append( "-l " + level );
				command.append( " -o " );
				command.append( "\"" );
				command.append( output.getAbsolutePath() );
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

					// Output holen
					StreamGobbler outputGobbler = new StreamGobbler(
							proc.getInputStream(), "OUTPUT" );

					// Threads starten
					errorGobbler.start();
					outputGobbler.start();

					// Warte, bis wget fertig ist
					proc.waitFor();

					Util.switchOnConsole();

					// Der Name des generierten Reports lautet per default
					// report.xml und es scheint keine
					// Möglichkeit zu geben, dies zu übersteuern.
					report = new File( pathToPdftronOutput, "report.xml" );
					File newReport = new File( pathToPdftronOutput,
							valDatei.getName() + ".pdftron-val-log.xml" );

					// falls das File bereits existiert, z.B. von einem
					// vorhergehenden Durchlauf, löschen wir es
					if ( newReport.exists() ) {
						newReport.delete();
					}

					boolean renameOk = report.renameTo( newReport );
					if ( !renameOk ) {
						throw new SystemException(
								"Der Report konnte nicht umbenannt werden." );
					}
					report = newReport;

				} catch ( Exception e ) {
					getMessageService().logError(
							getTextResourceService().getText(
									MESSAGE_XML_MODUL_A_PDFA )
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

				String pathToPdftronReport = report.getAbsolutePath();
				BufferedInputStream bis = new BufferedInputStream(
						new FileInputStream( pathToPdftronReport ) );
				DocumentBuilderFactory dbf = DocumentBuilderFactory
						.newInstance();
				DocumentBuilder db = dbf.newDocumentBuilder();
				Document doc = db.parse( bis );
				doc.normalize();

				Integer passCount = new Integer( 0 );
				NodeList nodeLstI = doc.getElementsByTagName( "Pass" );

				// Valide pdfa-Dokumente enthalten
				// "<Validation> <Pass FileName..."
				// Anzahl pass = anzahl Valider pdfa
				for ( int s = 0; s < nodeLstI.getLength(); s++ ) {
					passCount = passCount + 1;
					// Valide PDFA-Datei
					isValid = true;
					getMessageService()
							.logError(
									getTextResourceService().getText(
											MESSAGE_XML_MODUL_A_PDFA )
											+ getTextResourceService().getText(
													ERROR_XML_A_PDFA_VALID,
													valDatei.getAbsolutePath(),
													level ) );
				}

				if ( passCount == 0 ) {
					// Invalide PDFA-Datei
					isValid = false;
					// Encrypted?
					BufferedReader in = new BufferedReader( new FileReader(
							valDatei ) );
					String line;
					while ( (line = in.readLine()) != null ) {
						if ( line.contains( "ncrypt" ) ) {
							encryption = true;
						}
					}
					// im nächsten Module konvertieren
					getMessageService()
							.logError(
									getTextResourceService().getText(
											MESSAGE_XML_MODUL_A_PDFA )
											+ getTextResourceService().getText(
													ERROR_XML_A_PDFA_INVALID,
													valDatei.getAbsolutePath(),
													level ) );
					
					NodeList nodeLstE = doc.getElementsByTagName( "Error" );

					// Valide pdfa-Dokumente enthalten
					// "<Validation> <Pass FileName..."
					// Anzahl pass = anzahl Valider pdfa
					for ( int s = 0; s < nodeLstE.getLength(); s++ ) {
						Node dateiNode = nodeLstE.item( s );
						NamedNodeMap nodeMap = dateiNode.getAttributes();
						Node errorNodeM = nodeMap.getNamedItem( "Code" );
						String errorMessage = errorNodeM.getNodeValue();
						String codeUnknown = "e_PDF_Unknown";
						if (errorMessage.equals( codeUnknown )&& encryption){
							getMessageService().logError(
								getTextResourceService().getText(
										MESSAGE_XML_MODUL_A_PDFA )
										+ getTextResourceService().getText(
												ERROR_XML_A_PDFA_ENCRYPTION ) );
						}

					}

				}
			} catch ( Exception e ) {
				getMessageService().logError(
						getTextResourceService().getText(
								MESSAGE_XML_MODUL_A_PDFA )
								+ getTextResourceService().getText(
										ERROR_XML_UNKNOWN, e.getMessage() ) );
				return false;
			}
		}
		valid = erkennung;

		return isValid;
	}

}
