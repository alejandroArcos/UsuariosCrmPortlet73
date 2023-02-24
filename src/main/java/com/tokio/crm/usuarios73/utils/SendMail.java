/**
 * 
 */
package com.tokio.crm.usuarios73.utils;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.tokio.crm.crmservices73.Util.SendMailJavaMail;


/**
 * @author jonathanfviverosmoreno
 *
 */
public class SendMail {
	
	private static final Log _log = LogFactoryUtil.getLog(SendMail.class);
	
	public void SendMAil(String[] mails, String body,  String subject){
		
		/*
		try {
			String fromMail = "portal_agentes@tokiomarine.com.mx";
			MailMessage ms = new MailMessage();
			
			
			InternetAddress fromAddress = new InternetAddress(fromMail);
			InternetAddress toAddress = null;
			InternetAddress[] bulkAddresses = new InternetAddress[mails.length];

			ms.setFrom(fromAddress);
			ms.setSubject(subject);
			ms.setBody(body);
			ms.setHTMLFormat(true);

			for (int i = 0; i < mails.length; i++) {
				toAddress = new InternetAddress(mails[i]);
				bulkAddresses[i] = toAddress;
			}
			ms.setBulkAddresses(bulkAddresses);

			MailServiceUtil.sendEmail(ms);
			_log.debug("Envio de mail: " + subject);
			
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		*/
		
		SendMailJavaMail smjm = new SendMailJavaMail(mails);
		
		smjm.setBody(body);
		smjm.addBody();
		smjm.setSubject(subject);
		smjm.send();
	}

}
