/*== KOST-Converter ==================================================================================
The KOST-Converter application is used for convert PDF-Files to PDF/A-Files including 
validation and a automatic visual check. 
Copyright (C) 2014 Claire R�thlisberger (KOST-CECO)
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

package ch.kostceco.tools.kostconverter.service;

// import java.util.Map;

/**
 * Service Interface f�r die Konfigurationsdatei.
 * 
 * @author Rc Claire R�thlisberger, KOST-CECO
 */
public interface ConfigurationService extends Service
{

	/**
	 * Gibt den Pfad des Arbeitsverzeichnisses zur�ck. Dieses Verzeichnis wird
	 * z.B. zum Entpacken des .zip-Files verwendet.
	 * 
	 * @return Pfad des Arbeitsverzeichnisses
	 */
	String getPathToWorkDir();

	/**
	 * Gibt den Pfad des Logverzeichnisses zur�ck.
	 * 
	 * @return Pfad des Logverzeichnisses
	 */
	String getPathToLogfile();

	/**
	 * Gibt an welche Konformit�t mindestens erreicht werden muss 1a oder 1b
	 * oder no
	 */
	String pdfa1();

	/**
	 * Gibt an welche Konformit�t mindestens erreicht werden muss 2a oder 2b
	 * oder 2u oder no
	 */
	String pdfa2();

	/**
	 * Gibt den Pfad zum PDF/A-Validator zur�ck
	 * 
	 * @return Pfad zum PDF/A-Validator
	 */
	String getPathToPdfaValidator();

	/**
	 * Gibt den Pfad zum PDF/A-Converter zur�ck.
	 * 
	 * @return Pfad zum PDF/A-Converter
	 */
	String getPathToPdfaConverter();

	/**
	 * Gibt den Pfad zum PDF/A-Vergleicher zur�ck.
	 * 
	 * @return Pfad zum PDF/A-Vergleicher
	 */
	String getPathToPdfaAppearance();

}
