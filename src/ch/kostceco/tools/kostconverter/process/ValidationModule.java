/*== KOST-Converter ==================================================================================
The KOST-Converter application is used for validate TIFF, SIARD, PDF/A-Files and Submission 
Information Package (SIP). 
Copyright (C) 2012-2014 Claire R�thlisberger (KOST-CECO), Christian Eugster, Olivier Debenath, 
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

package ch.kostceco.tools.kostconverter.process;

import java.io.File;

import ch.kostceco.tools.kostconverter.exception.KOSTConverterException;
import ch.kostceco.tools.kostconverter.service.MessageService;
import ch.kostceco.tools.kostconverter.service.Service;

/**
 * Dies ist das Interface f�r alle Validierungs-Module und vereinigt alle
 * Funktionalit�ten, die den jeweiligen Modulen gemeinsam sind.
 * 
 * @author Rc Claire R�thlisberger, KOST-CECO
 */

public interface ValidationModule extends Service
{

	public boolean validate( File valDatei, File directoryOfLogfile )
			throws KOSTConverterException;

	public MessageService getMessageService();

}