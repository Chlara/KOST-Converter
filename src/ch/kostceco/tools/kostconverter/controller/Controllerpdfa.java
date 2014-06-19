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

package ch.kostceco.tools.kostconverter.controller;

import java.io.File;

import ch.kostceco.tools.kostconverter.exception.modulepdfa.PdfaProcess1ValidationErkException;
import ch.kostceco.tools.kostconverter.exception.modulepdfa.PdfaProcess1ValidationValException;
import ch.kostceco.tools.kostconverter.exception.modulepdfa.PdfaProcess3ConversionException;
import ch.kostceco.tools.kostconverter.exception.modulepdfa.PdfaProcess4RevalidationException;
import ch.kostceco.tools.kostconverter.exception.modulepdfa.PdfaProcess5AppearanceException;
import ch.kostceco.tools.kostconverter.logging.Logger;
import ch.kostceco.tools.kostconverter.logging.MessageConstants;
import ch.kostceco.tools.kostconverter.process.modulepdfa.PdfaProcess1ValidationErkModule;
import ch.kostceco.tools.kostconverter.process.modulepdfa.PdfaProcess1ValidationValModule;
import ch.kostceco.tools.kostconverter.process.modulepdfa.PdfaProcess3ConversionModule;
import ch.kostceco.tools.kostconverter.process.modulepdfa.PdfaProcess4RevalidationModule;
import ch.kostceco.tools.kostconverter.process.modulepdfa.PdfaProcess5AppearanceModule;
import ch.kostceco.tools.kostconverter.service.TextResourceService;

/**
 * KOSTConverter -->
 * 
 * Der Controller ruft die benötigten Module zur Validierung der SIARD-Datei in
 * der benötigten Reihenfolge auf.
 * 
 * Die Validierungs-Module werden mittels Spring-Dependency-Injection
 * eingebunden.
 */

public class Controllerpdfa implements MessageConstants
{

	private static final Logger				LOGGER	= new Logger(
															Controllerpdfa.class );
	private TextResourceService				textResourceService;

	private PdfaProcess1ValidationErkModule	pdfaProcess1ValidationErkModule;
	private PdfaProcess1ValidationValModule	pdfaProcess1ValidationValModule;
	private PdfaProcess3ConversionModule	pdfaProcess3ConversionModule;
	private PdfaProcess4RevalidationModule	pdfaProcess4RevalidationModule;
	private PdfaProcess5AppearanceModule	pdfaProcess5AppearanceModule;

	public PdfaProcess1ValidationErkModule getPdfaProcess1ValidationErkModule()
	{
		return pdfaProcess1ValidationErkModule;
	}

	public void setPdfaProcess1ValidationErkModule(
			PdfaProcess1ValidationErkModule pdfaProcess1ValidationErkModule )
	{
		this.pdfaProcess1ValidationErkModule = pdfaProcess1ValidationErkModule;
	}

	public PdfaProcess1ValidationValModule getPdfaProcess1ValidationValModule()
	{
		return pdfaProcess1ValidationValModule;
	}

	public void setPdfaProcess1ValidationValModule(
			PdfaProcess1ValidationValModule pdfaProcess1ValidationValModule )
	{
		this.pdfaProcess1ValidationValModule = pdfaProcess1ValidationValModule;
	}

	public PdfaProcess3ConversionModule getPdfaProcess3ConversionModule()
	{
		return pdfaProcess3ConversionModule;
	}

	public void setPdfaProcess3ConversionModule(
			PdfaProcess3ConversionModule pdfaProcess3ConversionModule )
	{
		this.pdfaProcess3ConversionModule = pdfaProcess3ConversionModule;
	}

	public PdfaProcess4RevalidationModule getPdfaProcess4RevalidationModule()
	{
		return pdfaProcess4RevalidationModule;
	}

	public void setPdfaProcess4RevalidationModule(
			PdfaProcess4RevalidationModule pdfaProcess4RevalidationModule )
	{
		this.pdfaProcess4RevalidationModule = pdfaProcess4RevalidationModule;
	}

	public void setPdfaProcess5AppearanceModule(
			PdfaProcess5AppearanceModule pdfaProcess5AppearanceModule )
	{
		this.pdfaProcess5AppearanceModule = pdfaProcess5AppearanceModule;
	}

	public PdfaProcess5AppearanceModule getPdfaProcess5AppearanceModule()
	{
		return pdfaProcess5AppearanceModule;
	}

	public TextResourceService getTextResourceService()
	{
		return textResourceService;
	}

	public void setTextResourceService( TextResourceService textResourceService )
	{
		this.textResourceService = textResourceService;
	}

	public boolean executeMandatory( File valDatei, File directoryOfLogfile )
	{
		boolean valid = true;

		// 1 Validation -> a) Erkennung
		try {
			if ( this.getPdfaProcess1ValidationErkModule().validate( valDatei,
					directoryOfLogfile ) ) {
				this.getPdfaProcess1ValidationErkModule().getMessageService()
						.print();
			} else {
				this.getPdfaProcess1ValidationErkModule().getMessageService()
						.print();
				return false;
			}
		} catch ( PdfaProcess1ValidationErkException e ) {
			LOGGER.logError( getTextResourceService().getText(
					MESSAGE_XML_MODUL_A_PDFA )
					+ getTextResourceService().getText( ERROR_XML_UNKNOWN,
							e.getMessage() ) );
			this.getPdfaProcess1ValidationErkModule().getMessageService().print();
			return false;
		} catch ( Exception e ) {
			LOGGER.logError( getTextResourceService().getText(
					MESSAGE_XML_MODUL_A_PDFA )
					+ getTextResourceService().getText( ERROR_XML_UNKNOWN,
							e.getMessage() ) );
			return false;
		}
		return valid;

	}

	public boolean executeTriage( File valDatei, File directoryOfLogfile )
	{
		boolean valid = true;

		// 1 Validation -> b) PDFTRON Validierung
		try {
			if ( this.getPdfaProcess1ValidationValModule().validate( valDatei,
					directoryOfLogfile ) ) {
				this.getPdfaProcess1ValidationValModule().getMessageService()
						.print();
			} else {
				this.getPdfaProcess1ValidationValModule().getMessageService()
						.print();
				return false;
			}
		} catch ( PdfaProcess1ValidationValException e ) {
			LOGGER.logError( getTextResourceService().getText(
					MESSAGE_XML_MODUL_A_PDFA )
					+ getTextResourceService().getText( ERROR_XML_UNKNOWN,
							e.getMessage() ) );
			this.getPdfaProcess1ValidationValModule().getMessageService().print();
			return false;
		} catch ( Exception e ) {
			LOGGER.logError( getTextResourceService().getText(
					MESSAGE_XML_MODUL_A_PDFA )
					+ getTextResourceService().getText( ERROR_XML_UNKNOWN,
							e.getMessage() ) );
			return false;
		}
		return valid;

	}

	public boolean executeProcess( File valDatei, File directoryOfLogfile )
	{
		boolean valid = true;
		// 3 Conversion
		try {
			if ( this.getPdfaProcess3ConversionModule().validate( valDatei,
					directoryOfLogfile ) ) {
				this.getPdfaProcess3ConversionModule().getMessageService()
						.print();
			} else {
				this.getPdfaProcess3ConversionModule().getMessageService()
						.print();
				return false;
			}
		} catch ( PdfaProcess3ConversionException e ) {
			LOGGER.logError( getTextResourceService().getText(
					MESSAGE_XML_MODUL_C_PDFA )
					+ getTextResourceService().getText( ERROR_XML_UNKNOWN,
							e.getMessage() ) );
			this.getPdfaProcess3ConversionModule().getMessageService().print();
			return false;
		} catch ( Exception e ) {
			LOGGER.logError( getTextResourceService().getText(
					MESSAGE_XML_MODUL_C_PDFA )
					+ getTextResourceService().getText( ERROR_XML_UNKNOWN,
							e.getMessage() ) );
			return false;
		}

		// 4 Revalidation
		try {
			if ( this.getPdfaProcess4RevalidationModule().validate( valDatei,
					directoryOfLogfile ) ) {
				this.getPdfaProcess4RevalidationModule().getMessageService()
						.print();
			} else {
				this.getPdfaProcess4RevalidationModule().getMessageService()
						.print();
				return false;
			}
		} catch ( PdfaProcess4RevalidationException e ) {
			LOGGER.logError( getTextResourceService().getText(
					MESSAGE_XML_MODUL_D_PDFA )
					+ getTextResourceService().getText( ERROR_XML_UNKNOWN,
							e.getMessage() ) );
			this.getPdfaProcess4RevalidationModule().getMessageService()
					.print();
			return false;
		} catch ( Exception e ) {
			LOGGER.logError( getTextResourceService().getText(
					MESSAGE_XML_MODUL_D_PDFA )
					+ getTextResourceService().getText( ERROR_XML_UNKNOWN,
							e.getMessage() ) );
			return false;
		}

		// 5 Appearance
		try {
			if ( this.getPdfaProcess5AppearanceModule().validate( valDatei,
					directoryOfLogfile ) ) {
				this.getPdfaProcess5AppearanceModule().getMessageService()
						.print();
			} else {
				this.getPdfaProcess5AppearanceModule().getMessageService()
						.print();
				return false;
			}
		} catch ( PdfaProcess5AppearanceException e ) {
			LOGGER.logError( getTextResourceService().getText(
					MESSAGE_XML_MODUL_E_PDFA )
					+ getTextResourceService().getText( ERROR_XML_UNKNOWN,
							e.getMessage() ) );
			this.getPdfaProcess5AppearanceModule().getMessageService()
					.print();
			return false;
		} catch ( Exception e ) {
			LOGGER.logError( getTextResourceService().getText(
					MESSAGE_XML_MODUL_E_PDFA )
					+ getTextResourceService().getText( ERROR_XML_UNKNOWN,
							e.getMessage() ) );
			return false;
		}
		return valid;
	}

}
