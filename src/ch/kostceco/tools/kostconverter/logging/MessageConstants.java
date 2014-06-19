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

package ch.kostceco.tools.kostconverter.logging;

/**
 * Interface für den Zugriff auf Resourcen aus dem ResourceBundle.
 * 
 * @author Rc Claire Röthlisberger, KOST-CECO
 */
public interface MessageConstants
{

	// Initialisierung und Parameter-Ueberpruefung
	String	ERROR_IOE										= "error.ioe";
	String	ERROR_PARAMETER_USAGE							= "error.parameter.usage";
	String	ERROR_LOGDIRECTORY_NODIRECTORY					= "error.logdirectory.nodirectory";
	String	ERROR_LOGDIRECTORY_NOTWRITABLE					= "error.logdirectory.notwritable";
	String	ERROR_VALDIRECTORY_NOTWRITABLE					= "error.valdirectory.notwritable";
	String	ERROR_WORKDIRECTORY_NOTDELETABLE				= "error.workdirectory.notdeletable";
	String	ERROR_WORKDIRECTORY_NOTWRITABLE					= "error.workdirectory.notwritable";
	String	ERROR_WORKDIRECTORY_EXISTS						= "error.workdirectory.exists";
	String	ERROR_CONFILE_FILENOTEXISTING					= "error.confile.filenotexisting";
	String	ERROR_LOGGING_NOFILEAPPENDER					= "error.logging.nofileappender";
	String	ERROR_CANNOTCREATEZIP							= "error.cannotcreatezip";
	String	ERROR_JHOVECONF_MISSING							= "error.jhoveconf.missing";
	String	ERROR_PARAMETER_OPTIONAL_1						= "error.parameter.optional.1";
	String	ERROR_INCORRECTFILEENDING						= "error.incorrectfileending";
	String	ERROR_INCORRECTFILEENDINGS						= "error.incorrectfileendings";
	String	ERROR_WRONG_JRE									= "error.wrong.jdk";

	// Globale Meldungen
	String	MESSAGE_XML_SUMMARY_3C							= "message.xml.summary.3c";
	String	MESSAGE_XML_CONFILE								= "message.xml.confile";
	String	MESSAGE_XML_HEADER								= "message.xml.header";
	String	MESSAGE_XML_START								= "message.xml.start";
	String	MESSAGE_XML_END									= "message.xml.end";
	String	MESSAGE_XML_INFO								= "message.xml.info";
	String	MESSAGE_TIFFCONVERSION							= "message.tiffconversion";
	String	MESSAGE_PDFACONVERSION							= "message.pdfaconversion";
	String	MESSAGE_XML_CONERGEBNIS							= "message.xml.conergebnis";
	String	MESSAGE_XML_CONTYPE								= "message.xml.contype";
	String	MESSAGE_XML_FORMAT1								= "message.xml.format1";
	String	MESSAGE_XML_FORMAT2								= "message.xml.format2";
	String	MESSAGE_XML_LOGEND								= "message.xml.logend";
	String	MESSAGE_XML_VALERGEBNIS_VALID					= "message.xml.valergebnis.valid";
	String	MESSAGE_XML_VALERGEBNIS_INVALID					= "message.xml.valergebnis.invalid";
	String	MESSAGE_XML_VALERGEBNIS_CLOSE					= "message.xml.valergebnis.close";

	String	MESSAGE_XML_MODUL_A_PDFA						= "message.xml.modul.a.pdfa";
	String	MESSAGE_XML_MODUL_B_PDFA						= "message.xml.modul.b.pdfa";
	String	MESSAGE_XML_MODUL_C_PDFA						= "message.xml.modul.c.pdfa";
	String	MESSAGE_XML_MODUL_D_PDFA						= "message.xml.modul.d.pdfa";
	String	MESSAGE_XML_MODUL_E_PDFA						= "message.xml.modul.e.pdfa";

	String	MESSAGE_XML_CONFIGURATION_ERROR_1				= "message.xml.configuration.error.1";
	String	MESSAGE_XML_CONFIGURATION_ERROR_2				= "message.xml.configuration.error.2";
	String	MESSAGE_XML_CONFIGURATION_ERROR_3				= "message.xml.configuration.error.3";

	String	MESSAGE_XML_CONFIGURATION_ERROR_NO_SIGNATURE	= "message.xml.configuration.error.no.signature";
	String	ERROR_XML_CANNOT_INITIALIZE_DROID				= "error.xml.cannot.initialize.droid";

	String	ERROR_XML_UNKNOWN								= "error.xml.unknown";

	// *************PDFA-Meldungen*************************************************************************
	// Allgemein
	String	ERROR_XML_PDFTRON_SERVICEFAILED					= "error.xml.pdftron.servicefailed";
	String	ERROR_XML_PDFTRON_MISSING						= "error.xml.pdftron.missing";
	String	ERROR_XML_PDFTRON_INIT							= "error.xml.pdftron.init";
	String	ERROR_XML_PDFA_NOCONFIG							= "error.xml.pdfa.noconfig";
	String	ERROR_XML_COMPAREPDF_SERVICEFAILED				= "error.xml.comparepdf.servicefailed";
	String	ERROR_XML_COMPAREPDF_MISSING					= "error.xml.comparepdf.missing";

	// Modul A Meldungen
	String	ERROR_XML_A_PDFA_INCORRECTFILEENDING			= "error.xml.a.pdfa.incorrectfileending";
	String	ERROR_XML_A_PDFA_INCORRECTFILE					= "error.xml.a.pdfa.incorrectfile";
	String	ERROR_XML_A_PDFA_ISDIRECTORY					= "error.xml.a.pdfa.isdirectory";
	String	ERROR_XML_A_PDFA_VALID							= "error.xml.a.pdfa.valid";
	String	ERROR_XML_A_PDFA_INVALID						= "error.xml.a.pdfa.invalid";
	String	ERROR_XML_A_PDFA_ENCRYPTION						= "error.xml.a.pdfa.encryption";

	// Modul C Meldungen
	String	ERROR_XML_C_PDFA_CONVERTED						= "error.xml.c.pdfa.converted";

	// Modul D Meldungen
	String	ERROR_XML_D_PDFA_VALID							= "error.xml.d.pdfa.valid";
	String	ERROR_XML_D_PDFA_INVALID						= "error.xml.d.pdfa.invalid";

	// Modul E Meldungen
	String	ERROR_XML_E_PDFA_EQUALS							= "error.xml.e.pdfa.equals";
	String	ERROR_XML_E_PDFA_DIFFERENT						= "error.xml.e.pdfa.diffrent";
	
	String	ERROR_XML_AJ_PDFA_ERRORMESSAGE					= "error.xml.aj.pdfa.errormessage";

}
