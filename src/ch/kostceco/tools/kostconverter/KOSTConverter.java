/*== KOST-Converter ==================================================================================
The KOST-Converter v0.0.2 application is used for convert PDF-Files to PDF/A-Files including 
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

package ch.kostceco.tools.kostconverter;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import ch.kostceco.tools.kostconverter.controller.Controllerpdfa;
import ch.kostceco.tools.kostconverter.logging.LogConfigurator;
import ch.kostceco.tools.kostconverter.logging.Logger;
import ch.kostceco.tools.kostconverter.logging.MessageConstants;
import ch.kostceco.tools.kostconverter.service.ConfigurationService;
import ch.kostceco.tools.kostconverter.service.TextResourceService;
import ch.kostceco.tools.kostconverter.util.Util;

/**
 * Dies ist die Starter-Klasse, verantwortlich für das Initialisieren des
 * Controllers, des Loggings und das Parsen der Start-Parameter.
 * 
 * @author Rc Claire Röthlisberger, KOST-CECO
 */
public class KOSTConverter implements MessageConstants
{

	private static final Logger		LOGGER	= new Logger( KOSTConverter.class );

	private TextResourceService		textResourceService;
	private ConfigurationService	configurationService;

	public TextResourceService getTextResourceService()
	{
		return textResourceService;
	}

	public void setTextResourceService( TextResourceService textResourceService )
	{
		this.textResourceService = textResourceService;
	}

	public ConfigurationService getConfigurationService()
	{
		return configurationService;
	}

	public void setConfigurationService(
			ConfigurationService configurationService )
	{
		this.configurationService = configurationService;
	}

	/**
	 * Die Eingabe besteht aus 2 oder 3 Parameter: [0] Format [1] Pfad zur
	 * Val-File/-Folder [2] option: Verbose
	 * 
	 * @param args
	 * @throws IOException
	 */
	@SuppressWarnings("unused")
	public static void main( String[] args ) throws IOException
	{
		ApplicationContext context = new ClassPathXmlApplicationContext(
				"classpath:config/applicationContext.xml" );

		// Zeitstempel Start
		java.util.Date nowStart = new java.util.Date();
		java.text.SimpleDateFormat sdfStart = new java.text.SimpleDateFormat(
				"dd.MM.yyyy HH:mm:ss" );
		String ausgabeStart = sdfStart.format( nowStart );

		// TODO: siehe Bemerkung im applicationContext-services.xml bezüglich
		// Injection in der Superklasse aller Impl-Klassen
		// ValidationModuleImpl validationModuleImpl = (ValidationModuleImpl)
		// context.getBean("validationmoduleimpl");

		KOSTConverter KOSTConverter = (KOSTConverter) context
				.getBean( "kostconverter" );

		// Ueberprüfung des Parameters (Log-Verzeichnis)
		String pathToLogfile = KOSTConverter.getConfigurationService()
				.getPathToLogfile();

		File directoryOfLogfile = new File( pathToLogfile );

		if ( !directoryOfLogfile.exists() ) {
			directoryOfLogfile.mkdir();
		}

		// Im Logverzeichnis besteht kein Schreibrecht
		if ( !directoryOfLogfile.canWrite() ) {
			System.out.println( KOSTConverter.getTextResourceService().getText(
					ERROR_LOGDIRECTORY_NOTWRITABLE, directoryOfLogfile ) );
			System.exit( 1 );
		}

		if ( !directoryOfLogfile.isDirectory() ) {
			System.out.println( KOSTConverter.getTextResourceService().getText(
					ERROR_LOGDIRECTORY_NODIRECTORY ) );
			System.exit( 1 );
		}

		// Ist die Anzahl Parameter (mind. 2) korrekt?
		if ( args.length < 2 ) {
			System.out.println( KOSTConverter.getTextResourceService().getText(
					ERROR_PARAMETER_USAGE ) );
			System.exit( 1 );
		}

		File valDatei = new File( args[1] );
		File logDatei = null;
		logDatei = valDatei;

		// Konfiguration des Loggings, ein File Logger wird
		// zusätzlich erstellt
		LogConfigurator logConfigurator = (LogConfigurator) context
				.getBean( "logconfigurator" );
		String logFileName = logConfigurator.configure(
				directoryOfLogfile.getAbsolutePath(), logDatei.getName() );
		File logFile = new File( logFileName );
		// Ab hier kann ins log geschrieben werden...
		LOGGER.logError( KOSTConverter.getTextResourceService().getText(
				MESSAGE_XML_HEADER ) );
		LOGGER.logError( KOSTConverter.getTextResourceService().getText(
				MESSAGE_XML_START, ausgabeStart ) );
		LOGGER.logError( KOSTConverter.getTextResourceService().getText(
				MESSAGE_XML_END ) );
		LOGGER.logError( KOSTConverter.getTextResourceService().getText(
				MESSAGE_XML_INFO ) );
		System.out.println( "KOST-Converter" );
		System.out.println( "" );

		File xslOrig = new File( "resources\\kost-converter.xsl" );
		File xslCopy = new File( directoryOfLogfile.getAbsolutePath()
				+ "\\kost-converter.xsl" );
		if ( !xslCopy.exists() ) {
			Util.copyFile( xslOrig, xslCopy );
		}

		// Ist die Anzahl Parameter (mind. 2) korrekt?
		/*
		 * if ( args.length < 2 ) { LOGGER.logError(
		 * KOSTConverter.getTextResourceService().getText( ERROR_IOE,
		 * KOSTConverter.getTextResourceService().getText( ERROR_PARAMETER_USAGE
		 * ) ) ); System.out.println(
		 * KOSTConverter.getTextResourceService().getText( ERROR_PARAMETER_USAGE
		 * ) ); System.exit( 1 ); }
		 */

		// Informationen zum Arbeitsverzeichnis holen
		String pathToWorkDir = KOSTConverter.getConfigurationService()
				.getPathToWorkDir();
		/*
		 * Nicht vergessen in
		 * "src/main/resources/config/applicationContext-services.xml" beim
		 * entsprechenden Modul die property anzugeben: <property
		 * name="configurationService" ref="configurationService" />
		 */

		File tmpDir = new File( pathToWorkDir );

		// bestehendes Workverzeichnis Abbruch, da am Schluss das
		// Workverzeichnis gelöscht wird und entsprechend bestehende Dateien
		// gelöscht werden können
		if ( tmpDir.exists() ) {
			LOGGER.logError( KOSTConverter.getTextResourceService().getText(
					ERROR_IOE,
					KOSTConverter.getTextResourceService().getText(
							ERROR_WORKDIRECTORY_EXISTS, pathToWorkDir ) ) );
			System.out.println( KOSTConverter.getTextResourceService().getText(
					ERROR_WORKDIRECTORY_EXISTS, pathToWorkDir ) );
			System.exit( 1 );
		}

		// die Anwendung muss mindestens unter Java 6 laufen
		String javaRuntimeVersion = System.getProperty( "java.vm.version" );
		if ( javaRuntimeVersion.compareTo( "1.6.0" ) < 0 ) {
			LOGGER.logError( KOSTConverter.getTextResourceService().getText(
					ERROR_IOE,
					KOSTConverter.getTextResourceService().getText(
							ERROR_WRONG_JRE ) ) );
			System.out.println( KOSTConverter.getTextResourceService().getText(
					ERROR_WRONG_JRE ) );
			System.exit( 1 );
		}

		// bestehendes Workverzeichnis wieder anlegen
		if ( !tmpDir.exists() ) {
			tmpDir.mkdir();
		}

		// Im workverzeichnis besteht kein Schreibrecht
		if ( !tmpDir.canWrite() ) {
			LOGGER.logError( KOSTConverter.getTextResourceService().getText(
					ERROR_IOE,
					KOSTConverter.getTextResourceService().getText(
							ERROR_WORKDIRECTORY_NOTWRITABLE, tmpDir ) ) );
			System.out.println( KOSTConverter.getTextResourceService().getText(
					ERROR_WORKDIRECTORY_NOTWRITABLE, tmpDir ) );
			System.exit( 1 );
		}

		// Ueberprüfung des optionalen Parameters (2 -v --> im Verbose-mode
		// werden die originalen Logs nicht gelöscht (PDFTron, Jhove & Co.)
		boolean verbose = false;
		if ( args.length > 2 ) {
			if ( !(args[2].equals( "-v" )) ) {
				LOGGER.logError( KOSTConverter.getTextResourceService()
						.getText(
								ERROR_IOE,
								KOSTConverter.getTextResourceService().getText(
										ERROR_PARAMETER_OPTIONAL_1 ) ) );
				System.out.println( KOSTConverter.getTextResourceService()
						.getText( ERROR_PARAMETER_OPTIONAL_1 ) );
				System.exit( 1 );
			} else {
				verbose = true;
			}
		}

		// Ueberprüfung des Parameters (Val-Datei): existiert die Datei?
		if ( !valDatei.exists() ) {
			LOGGER.logError( KOSTConverter.getTextResourceService().getText(
					ERROR_IOE,
					KOSTConverter.getTextResourceService().getText(
							ERROR_CONFILE_FILENOTEXISTING ) ) );
			System.out.println( KOSTConverter.getTextResourceService().getText(
					ERROR_CONFILE_FILENOTEXISTING ) );
			System.exit( 1 );
		}
		String valPath = valDatei.getAbsolutePath();
		File valDir = new File( valPath);

		// Im Verzeichnis der zu konvertierenden Datei besteht kein Schreibrecht
		if ( !valDir.canWrite() ) {
			LOGGER.logError( KOSTConverter.getTextResourceService().getText(
					ERROR_IOE,
					KOSTConverter.getTextResourceService().getText(
							ERROR_VALDIRECTORY_NOTWRITABLE, valDir ) ) );
			System.out.println( KOSTConverter.getTextResourceService().getText(
					ERROR_VALDIRECTORY_NOTWRITABLE, valDir ) );
			System.exit( 1 );
		}
		

		if ( args[0].equals( "--pdfa" ) ) {
			LOGGER.logError( KOSTConverter.getTextResourceService().getText(
					MESSAGE_XML_FORMAT1 ) );
			Integer countNio = 0;
			Integer countSummaryNio = 0;
			Integer count = 0;
			Integer pdfaCountIo = 0;
			Integer pdfaCountNio = 0;
			Integer siardCountIo = 0;
			Integer siardCountNio = 0;
			Integer tiffCountIo = 0;
			Integer tiffCountNio = 0;

			// TODO: Formatvalidierung/-konvertierung an einer Datei
			// --> erledigt --> nur Marker
			if ( !valDatei.isDirectory() ) {

				boolean valFile = valFile( valDatei, logFileName,
						directoryOfLogfile, verbose );

				LOGGER.logError( KOSTConverter.getTextResourceService()
						.getText( MESSAGE_XML_FORMAT2 ) );

				// Löschen des Arbeitsverzeichnisses, falls eines
				// angelegt wurde
				if ( tmpDir.exists() ) {
					Util.deleteDir( tmpDir );
				}

				LOGGER.logError( KOSTConverter.getTextResourceService()
						.getText( MESSAGE_XML_LOGEND ) );
				// Zeitstempel End
				java.util.Date nowEnd = new java.util.Date();
				java.text.SimpleDateFormat sdfEnd = new java.text.SimpleDateFormat(
						"dd.MM.yyyy HH:mm:ss" );
				String ausgabeEnd = sdfEnd.format( nowEnd );
				ausgabeEnd = "<End>" + ausgabeEnd + "</End>";
				Util.valEnd( ausgabeEnd, logFile );
				Util.amp( logFile );

				if ( valFile ) {
					// Löschen des Arbeitsverzeichnisses, falls eines
					// angelegt wurde
					if ( tmpDir.exists() ) {
						Util.deleteDir( tmpDir );
					}
					// Validierte Datei valide
					System.exit( 0 );
				} else {
					// Löschen des Arbeitsverzeichnisses, falls eines
					// angelegt wurde
					if ( tmpDir.exists() ) {
						Util.deleteDir( tmpDir );
					}
					// Fehler in Validierte Datei --> invalide
					System.exit( 2 );

				}
			} else {
				// TODO: Formatvalidierung/-konvertierung über ein Ordner
				// --> erledigt --> nur Marker
				Map<String, File> fileMap = Util.getFileMap( valDatei, false );
				Set<String> fileMapKeys = fileMap.keySet();
				for ( Iterator<String> iterator = fileMapKeys.iterator(); iterator
						.hasNext(); ) {
					String entryName = iterator.next();
					File newFile = fileMap.get( entryName );
					if ( !newFile.isDirectory() ) {
						valDatei = newFile;
						count = count + 1;

						if ( ((valDatei.getName().endsWith( ".pdf" ) || valDatei
								.getName().endsWith( ".pdfa" ))) ) {

							boolean valFile = valFile( valDatei, logFileName,
									directoryOfLogfile, verbose );

							// Löschen des Arbeitsverzeichnisses, falls eines
							// angelegt wurde
							if ( tmpDir.exists() ) {
								Util.deleteDir( tmpDir );
							}
							if ( valFile ) {
								pdfaCountIo = pdfaCountIo + 1;
								// Löschen des Arbeitsverzeichnisses, falls
								// eines angelegt wurde
								if ( tmpDir.exists() ) {
									Util.deleteDir( tmpDir );
								}
							} else {
								pdfaCountNio = pdfaCountNio + 1;
								// Löschen des Arbeitsverzeichnisses, falls
								// eines angelegt wurde
								if ( tmpDir.exists() ) {
									Util.deleteDir( tmpDir );
								}
							}

						} else {
							countNio = countNio + 1;
						}
					}
				}

				if ( countNio == count ) {
					// keine Dateien Validiert
					LOGGER.logError( KOSTConverter.getTextResourceService()
							.getText( ERROR_INCORRECTFILEENDINGS ) );
					System.out.println( KOSTConverter.getTextResourceService()
							.getText( ERROR_INCORRECTFILEENDINGS ) );
				}

				LOGGER.logError( KOSTConverter.getTextResourceService()
						.getText( MESSAGE_XML_FORMAT2 ) );

				LOGGER.logError( KOSTConverter.getTextResourceService()
						.getText( MESSAGE_XML_LOGEND ) );
				// Zeitstempel End
				java.util.Date nowEnd = new java.util.Date();
				java.text.SimpleDateFormat sdfEnd = new java.text.SimpleDateFormat(
						"dd.MM.yyyy HH:mm:ss" );
				String ausgabeEnd = sdfEnd.format( nowEnd );
				ausgabeEnd = "<End>" + ausgabeEnd + "</End>";
				Util.valEnd( ausgabeEnd, logFile );
				Util.amp( logFile );

				if ( countNio == count ) {
					// keine Dateien Validiert
					// bestehendes Workverzeichnis ggf. löschen
					if ( tmpDir.exists() ) {
						Util.deleteDir( tmpDir );
					}
					System.exit( 1 );
				} else if ( countSummaryNio == 0 ) {
					// bestehendes Workverzeichnis ggf. löschen
					if ( tmpDir.exists() ) {
						Util.deleteDir( tmpDir );
					}
					// alle Validierten Dateien valide
					System.exit( 0 );
				} else {
					// bestehendes Workverzeichnis ggf. löschen
					if ( tmpDir.exists() ) {
						Util.deleteDir( tmpDir );
					}
					// Fehler in Validierten Dateien --> invalide
					System.exit( 2 );
				}
			}
			LOGGER.logError( KOSTConverter.getTextResourceService().getText(
					MESSAGE_XML_FORMAT2 ) );

		} else if ( args[0].equals( "--tiff" ) ) {
		} else {
			// Ueberprüfung des Parameters (Val-Typ): pdfa / tiff
			// args[0] ist nicht "--pdfa" oder "--tiff" --> INVALIDE
			LOGGER.logError( KOSTConverter.getTextResourceService().getText(
					ERROR_IOE,
					KOSTConverter.getTextResourceService().getText(
							ERROR_PARAMETER_USAGE ) ) );
			System.out.println( KOSTConverter.getTextResourceService().getText(
					ERROR_PARAMETER_USAGE ) );
			if ( tmpDir.exists() ) {
				Util.deleteDir( tmpDir );
				tmpDir.deleteOnExit();
			}
			System.exit( 1 );
		}
	}

	// TODO: ValFile --> Formatvalidierung/-konvertierung einer Datei
	// --> erledigt --> nur Marker
	private static boolean valFile( File valDatei, String logFileName,
			File directoryOfLogfile, boolean verbose ) throws IOException
	{
		ApplicationContext context = new ClassPathXmlApplicationContext(
				"classpath:config/applicationContext.xml" );

		KOSTConverter KOSTConverter = (KOSTConverter) context
				.getBean( "kostconverter" );
		String originalValName = valDatei.getAbsolutePath();
		boolean valFile = false;

		if ( (valDatei.getAbsolutePath().toLowerCase().endsWith( ".tiff" ) || valDatei
				.getAbsolutePath().toLowerCase().endsWith( ".tif" )) ) {
			// evtl später
		} else if ( (valDatei.getName().endsWith( ".pdf" ) || valDatei
				.getName().endsWith( ".pdfa" )) ) {
			LOGGER.logError( KOSTConverter.getTextResourceService().getText(
					MESSAGE_XML_CONERGEBNIS ) );
			LOGGER.logError( KOSTConverter.getTextResourceService().getText(
					MESSAGE_XML_CONTYPE,
					KOSTConverter.getTextResourceService().getText(
							MESSAGE_PDFACONVERSION ) ) );
			LOGGER.logError( KOSTConverter.getTextResourceService().getText(
					MESSAGE_XML_CONFILE, originalValName ) );
			System.out.println( KOSTConverter.getTextResourceService().getText(
					MESSAGE_PDFACONVERSION ) );
			System.out.println( originalValName );
			Controllerpdfa controller3 = (Controllerpdfa) context
					.getBean( "controllerpdfa" );
			boolean okMandatory = false;
			boolean okTriage = false;
			boolean ok = false;

			okMandatory = controller3.executeMandatory( valDatei,
					directoryOfLogfile );
			// nur wenn die Erkennung (1Erk) bestanden wurden, werden die
			// restlichen Prozessschritte (Validierung...) ausgeführt.

			if ( okMandatory ) {
				// PDF-Datei weitere Schritte möglich
				okTriage = controller3.executeTriage( valDatei,
						directoryOfLogfile );
				// nur wenn die Validierung (1) nicht bestanden wurden, werden
				// die
				// restlichen
				// Prozessschritte ausgeführt.

				if ( okTriage ) {
					// valide PDFA-Datei keine weiteren Schritte notwendig
					ok = true;
				} else {
					ok = controller3.executeProcess( valDatei,
							directoryOfLogfile );
				}
			} else {
				// keine weiteren Schritte möglich -> Abbruch
				ok = false;
			}

			valFile = ok;

			if ( ok ) {
				// Validierte Datei valide
				LOGGER.logError( KOSTConverter.getTextResourceService()
						.getText( MESSAGE_XML_VALERGEBNIS_VALID ) );
				LOGGER.logError( KOSTConverter.getTextResourceService()
						.getText( MESSAGE_XML_VALERGEBNIS_CLOSE ) );
				System.out.println( "Valid" );
				System.out.println( "" );
			} else {
				// Validierte Datei invalide
				LOGGER.logError( KOSTConverter.getTextResourceService()
						.getText( MESSAGE_XML_VALERGEBNIS_INVALID ) );
				LOGGER.logError( KOSTConverter.getTextResourceService()
						.getText( MESSAGE_XML_VALERGEBNIS_CLOSE ) );
				System.out.println( "Invalid" );
				System.out.println( "" );
			}

			// Ausgabe der Pfade zu den Pdftron Reports, falls welche
			// generiert wurden (-v) oder Pdftron Reports löschen
			File pdftronValReport = new File( directoryOfLogfile,
					valDatei.getName() + ".pdftron-val-log.xml" );
			File pdftronRevalReport = new File( directoryOfLogfile,
					valDatei.getName() + ".pdftron-reval-log.xml" );
			File pdftronXsl = new File( directoryOfLogfile, "report.xsl" );

			if ( pdftronValReport.exists() ) {
				if ( verbose ) {
					// optionaler Parameter --> PDFTron-Report lassen
				} else {
					// kein optionaler Parameter --> PDFTron-Report
					// loeschen!
					pdftronValReport.delete();
					pdftronXsl.delete();
				}
			}
			if ( pdftronRevalReport.exists() ) {
				if ( verbose ) {
					// optionaler Parameter --> PDFTron-Report lassen
				} else {
					// kein optionaler Parameter --> PDFTron-Report
					// loeschen!
					pdftronRevalReport.delete();
				}
			}

		}
		return valFile;
	}

}
