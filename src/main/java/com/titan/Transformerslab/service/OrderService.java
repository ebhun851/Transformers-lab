package com.titan.Transformerslab.service;

import java.io.ByteArrayOutputStream;

import javax.xml.soap.MessageFactory;
import javax.xml.soap.MimeHeaders;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPConnection;
import javax.xml.soap.SOAPConnectionFactory;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;
import javax.xml.soap.SOAPPart;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Service;

import com.titan.Transformerslab.utils.SoapMessageUtils;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Getter
@Setter
@ConfigurationProperties("eom_service")
public class OrderService {

	private String coUrl;

	
	private String orderNsUri;

	@Autowired
	public EomTokenService eomTokenService;

	private  void createCustomerOrderSoapEnvelope(SOAPMessage soapMessage, String token, String OrderNumber) throws SOAPException {
		SOAPPart soapPart = soapMessage.getSOAPPart();

		String myNamespace = "impl";

		SOAPEnvelope envelope = soapPart.getEnvelope();
		envelope.addNamespaceDeclaration(myNamespace, orderNsUri);

		SOAPBody soapBody = envelope.getBody();
		SOAPElement soapBodyElem = soapBody.addChildElement("getCustomerOrderDetails", myNamespace);
		SOAPElement soapBodyElem1 = soapBodyElem.addChildElement("arg0");
		soapBodyElem1.addTextNode(token);
		SOAPElement soapBodyElem2 = soapBodyElem.addChildElement("arg1");
		soapBodyElem2.addTextNode(SoapMessageUtils.getOrderInputXml(OrderNumber));
	}
	
	private  void callSoapWebService(String soapAction, String orderNumber) {
		try {

			String token = eomTokenService.getEomToken();
			SOAPConnectionFactory soapConnectionFactory = SOAPConnectionFactory.newInstance();
			SOAPConnection soapConnection = soapConnectionFactory.createConnection();

			SOAPMessage orderRequest = createOrderSOAPRequest(soapAction, token, orderNumber);
			
			SOAPMessage orderResponse = soapConnection.call(orderRequest, coUrl);
//			orderResponse.writeTo(System.out);
			ByteArrayOutputStream baos = new ByteArrayOutputStream();  
			orderResponse.writeTo(baos);  
			log.info(baos.toString());
			
			soapConnection.close();
		} catch (Exception e) {
			log.error(
					"\nError occurred while sending SOAP Request to Server!\nMake sure you have the correct endpoint URL and SOAPAction!\n" + e.getMessage());
		}
	}
	
	
	private  SOAPMessage createOrderSOAPRequest(String soapAction,String token, String OrderNumber) throws Exception {
		MessageFactory messageFactory = MessageFactory.newInstance();
		SOAPMessage soapMessage = messageFactory.createMessage();

		createCustomerOrderSoapEnvelope(soapMessage, token,  OrderNumber);

		MimeHeaders headers = soapMessage.getMimeHeaders();
		headers.addHeader("SOAPAction", soapAction);

		soapMessage.saveChanges();

		soapMessage.writeTo(System.out);

		return soapMessage;
	}

	public void getOrderDetails(String orderNumber) {
		String soapAction = "";
		callSoapWebService(soapAction,orderNumber);
	}

}