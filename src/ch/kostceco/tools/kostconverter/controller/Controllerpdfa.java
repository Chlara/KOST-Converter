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

package ch.kostceco.tools.kostconverter.controller;

import java.io.File;

import ch.kostceco.tools.kostconverter.exception.modulepdfa.PdfaProcess1ValidationErkException;
import ch.kostceco.tools.kostconverter.exception.modulepdfa.PdfaProcess1ValidationValException;
import ch.kostceco.tools.kostconverter.exception.modulepdfa.PdfaProcess2ConversionException;
import ch.kostceco.tools.kostconverter.exception.modulepdfa.PdfaProcess3RevalidationException;
import ch.kostceco.tools.kostconverter.exception.modulepdfa.PdfaProcess4ReconversionException;
import ch.kostceco.tools.kostconverter.exception.modulepdfa.PdfaProcess5AppearanceException;
import ch.kostceco.tools.kostconverter.logging.Logger;
import ch.kostceco.tools.kostconverter.logging.MessageConstants;
import ch.kostceco.tools.kostconverter.process.modulepdfa.PdfaProcess1ValidationErkModule;
import ch.kostceco.tools.kostconverter.process.modulepdfa.PdfaProcess1ValidationValModule;
import ch.kostceco.tools.kostconverter.process.modulepdfa.PdfaProcess2ConversionModule;
import ch.kostceco.tools.kostconverter.process.modulepdfa.PdfaProcess3RevalidationModule;
import ch.kostceco.tools.kostconverter.process.modulepdfa.PdfaProcess4ReconversionModule;
import ch.kostceco.tools.kostconverter.process.modulepdfa.PdfaProcess5AppearanceModule;
import ch.kostceco.tools.kostconverter.service.TextResourceService;

/**
 * KOST-Converter -->
 * 
 * Der Controller ruft die benötigten Module in der benötigten Reihenfolge auf.
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
	private PdfaProcess2ConversionModule	pdfaProcess2ConversionModule;
	private PdfaProcess3RevalidationModule	pdfaProcess3RevalidationModule;
	private PdfaProcess4ReconversionModule	pdfaProcess4ReconversionModule;
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

	public PdfaProcess2ConversionModule getPdfaProcess2ConversionModule()
	{
		return pdfaProcess2ConversionModule;
	}

	public void setPdfaProcess2ConversionModule(
			PdfaProcess2ConversionModule pdfaProcess2ConversionModule )
	{
		this.pdfaProcess2ConversionModule = pdfaProcess2ConversionModule;
	}

	public PdfaProcess3RevalidationModule getPdfaProcess3RevalidationModule()
	{
		return pdfaProcess3RevalidationModule;
	}

	public void setPdfaProcess3RevalidationModule(
			PdfaProcess3RevalidationModule pdfaProcess3RevalidationModule )
	{
		this.pdfaProcess3RevalidationModule = pdfaProcess3RevalidationModule;
	}

	public PdfaProcess4ReconversionModule getPdfaProcess4ReconversionModule()
	{
		return pdfaProcess4ReconversionModule;
	}

	public void setPdfaProcess4ReconversionModule(
			PdfaProcess4ReconversionModule pdfaProcess4ReconversionModule )
	{
		this.pdfaProcess4ReconversionModule = pdfaProcess4ReconversionModule;
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
		// 2 Conversion
		try {
			if ( this.getPdfaProcess2ConversionModule().validate( valDatei,
					directoryOfLogfile ) ) {
				this.getPdfaProcess2ConversionModule().getMessageService()
						.print();
			} else {
				this.getPdfaProcess2ConversionModule().getMessageService()
						.print();
				return false;
			}
		} catch ( PdfaProcess2ConversionException e ) {
			LOGGER.logError( getTextResourceService().getText(
					MESSAGE_XML_MODUL_B_PDFA )
					+ getTextResourceService().getText( ERROR_XML_UNKNOWN,
							e.getMessage() ) );
			this.getPdfaProcess2ConversionModule().getMessageService().print();
			return false;
		} catch ( Exception e ) {
			LOGGER.logError( getTextResourceService().getText(
					MESSAGE_XML_MODUL_B_PDFA )
					+ getTextResourceService().getText( ERROR_XML_UNKNOWN,
							e.getMessage() ) );
			return false;
		}

		// 3 Revalidation
		try {
			if ( this.getPdfaProcess3RevalidationModule().validate( valDatei,
					directoryOfLogfile ) ) {
				this.getPdfaProcess3RevalidationModule().getMessageService()
						.print();
			} else {
				this.getPdfaProcess3RevalidationModule().getMessageService()
						.print();
				// 4 Reconversion & Validation
				try {
					if ( this.getPdfaProcess4ReconversionModule().validate( valDatei,
							directoryOfLogfile ) ) {
						this.getPdfaProcess4ReconversionModule().getMessageService()
								.print();
					} else {
						this.getPdfaProcess4ReconversionModule().getMessageService()
								.print();
						return false;
					}
				} catch ( PdfaProcess4ReconversionException e ) {
					LOGGER.logError( getTextResourceService().getText(
							MESSAGE_XML_MODUL_D_PDFA )
							+ getTextResourceService().getText( ERROR_XML_UNKNOWN,
									e.getMessage() ) );
					this.getPdfaProcess3RevalidationModule().getMessageService()
							.print();
					return false;
				} catch ( Exception e ) {
					LOGGER.logError( getTextResourceService().getText(
							MESSAGE_XML_MODUL_D_PDFA )
							+ getTextResourceService().getText( ERROR_XML_UNKNOWN,
									e.getMessage() ) );
					return false;
				}
			}
		} catch ( PdfaProcess3RevalidationException e ) {
			LOGGER.logError( getTextResourceService().getText(
					MESSAGE_XML_MODUL_C_PDFA )
					+ getTextResourceService().getText( ERROR_XML_UNKNOWN,
							e.getMessage() ) );
			this.getPdfaProcess3RevalidationModule().getMessageService()
					.print();
			return false;
		} catch ( Exception e ) {
			LOGGER.logError( getTextResourceService().getText(
					MESSAGE_XML_MODUL_C_PDFA )
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
